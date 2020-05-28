package com.credfinancials;

import com.credfinancials.dtos.HbaseConnectionInfo;
import com.credfinancials.dtos.KafkaTopicInfo;
import com.credfinancials.transactionprocessor.TransactionHandler;

/***
 * Driver class to run the spark application.
 * Takes 4 compulsory arguments:
 * 1. kafka broker
 * 2. kafka topic
 * 3. consumer group ID
 * 4. ec2 public hostname
 */
public class TransactionProcessor {

    public static void main(String[] args) throws Exception {
        KafkaTopicInfo kafkaTopic = new KafkaTopicInfo(args[0],args[1],args[2]);
        HbaseConnectionInfo hbaseConnectionInfo = new HbaseConnectionInfo(args[3]);
        TransactionHandler transactionHandler = new TransactionHandler(hbaseConnectionInfo);
        transactionHandler.startProcessing(kafkaTopic);
    }
}
