package com.credfinancials.transactionprocessor;

import com.credfinancials.dtos.HbaseConnectionInfo;
import com.credfinancials.dtos.KafkaTopicInfo;
import com.credfinancials.dtos.Transaction;
import com.credfinancials.dtos.TransactionGeneric;
import com.credfinancials.transactionprocessor.database.ConnectorFactory;
import com.credfinancials.transactionprocessor.database.DbOperations;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.credfinancials.Constants.Constants.CARD_TRANSACTIONS_TABLE_NAME;
import static com.credfinancials.Constants.Constants.LOOKUP_TABLE_NAME;

/***
 * Handles new transactions by categorising them between GENUINE and FRAUD.
 * All new transactions are updated in card_transactions_staging but only genuine
 * transactions are updated in the lookup table.
 */
public class TransactionHandler implements Serializable {
    private HbaseConnectionInfo hbaseConnectionInfo;

    public TransactionHandler(HbaseConnectionInfo hbaseConnectionInfo) {
        this.hbaseConnectionInfo = hbaseConnectionInfo;
    }

    public void startProcessing(KafkaTopicInfo topicInfo) throws InterruptedException {
        Logger.getLogger("org").setLevel(Level.ERROR);
        Logger.getLogger("akka").setLevel(Level.ERROR);

        SparkConf sparkConf = new SparkConf().setAppName("TransactionHandler").setMaster("local");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(1));
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", topicInfo.getServer() + ":" + topicInfo.getPort());
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", topicInfo.getConsumerGroupId());
        kafkaParams.put("auto.offset.reset", "earliest");
        kafkaParams.put("enable.auto.commit", true);

        Collection<String> topics = Arrays.asList(topicInfo.getTopic());
        JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams));

        stream.foreachRDD((VoidFunction<JavaRDD<ConsumerRecord<String, String>>>) rdds -> {

            rdds.foreach((VoidFunction<ConsumerRecord<String, String>>) newTransaction -> {
                ObjectMapper objectMapper = new ObjectMapper();
                TypeReference<TransactionGeneric> mapType = new TypeReference<TransactionGeneric>() {
                };

                DbOperations lookupTable = ConnectorFactory.getDatabaseConnection(hbaseConnectionInfo, LOOKUP_TABLE_NAME);
                DbOperations cardTransactionsTable = ConnectorFactory.getDatabaseConnection(hbaseConnectionInfo, CARD_TRANSACTIONS_TABLE_NAME);
                System.out.println();
                System.out.print(newTransaction.value());
                TransactionGeneric transactionGeneric = objectMapper.readValue(newTransaction.value(), mapType);
                Transaction transaction = new Transaction(transactionGeneric);

                TransactionValidator transactionValidator = new TransactionValidator(hbaseConnectionInfo, transaction);
                boolean isValid = transactionValidator.validateOnAllParameters();
                cardTransactionsTable.addNewTransaction(transaction, isValid);
                System.out.println();

                if (isValid) {
                    lookupTable.updateTransactionForCard(transaction);
                }
            });
        });

        jssc.start();
        jssc.awaitTermination();
    }
}
