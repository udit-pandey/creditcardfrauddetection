package com.credfinancials.transactionprocessor.database;

import com.credfinancials.Constants.Status;
import com.credfinancials.dtos.HbaseConnectionInfo;
import com.credfinancials.dtos.LastTransaction;
import com.credfinancials.dtos.Transaction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/***
 * All the operations that can be performed in the hbase tables.
 */
public class DbOperations implements Serializable {
    private String table;
    private HTable htable;
    private Connection con;
    private HbaseConnectionInfo hbaseConnectionInfo;
    private Admin admin;

    public DbOperations(HbaseConnectionInfo hbaseConnectionInfo, String table) throws IOException {
        this.table = table;
        this.hbaseConnectionInfo = hbaseConnectionInfo;
        initiateConnection();
    }

    /***
     * Initiating connections to the table as supplied in the constructor.
     * @throws IOException
     */
    public void initiateConnection() throws IOException {
        Logger.getRootLogger().setLevel(Level.INFO);

        Configuration conf = HBaseConfiguration.create();
        conf.setInt("timeout", 1200);
        conf.set("hbase.master", hbaseConnectionInfo.getHbaseMaster());
        conf.set("hbase.zookeeper.quorum", hbaseConnectionInfo.getHbaseZookeeperQuoram());
        conf.set("hbase.zookeeper.property.clientPort", hbaseConnectionInfo.getHbaseZookeeperClientPort());
        conf.set("zookeeper.znode.parent", hbaseConnectionInfo.getZookeeperZnodeParent());

        System.out.println("Connecting to the server...");
        con = ConnectionFactory.createConnection(conf);
        System.out.println("Connected");
        admin = con.getAdmin();
        if (!admin.tableExists(TableName.valueOf(table))) {
            throw new IOException("The HBase Table named '" + table + "' doesn't exists.");
        }
        htable = new HTable(admin.getConfiguration(), table);
    }

    /***
     * Method to get the last transaction for the card id from lookup table.
     * @param cardId
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public LastTransaction getLastTransactionForCard(long cardId) throws IOException, ParseException {
        Get get = new Get(Bytes.toBytes(String.valueOf(cardId)));
        Result result = htable.get(get);

        String postcode = Bytes.toString(result.getValue(Bytes.toBytes("transaction"), Bytes.toBytes("postcode")));
        String transactionDt = Bytes.toString(result.getValue(Bytes.toBytes("transaction"), Bytes.toBytes("transaction_dt")));
        String transactionUcl = Bytes.toString(result.getValue(Bytes.toBytes("transaction"), Bytes.toBytes("ucl")));
        String transactionScore = Bytes.toString(result.getValue(Bytes.toBytes("transaction"), Bytes.toBytes("score")));

        LastTransaction lastTransaction = new LastTransaction(postcode, transactionDt, transactionUcl, transactionScore);
        lastTransaction.setCardId(cardId);
        return lastTransaction;
    }

    /***
     * Method to update the transaction in the lookup table with the postcode and
     * transaction date of the latest genuine transaction.
     * @param transaction
     * @throws IOException
     * @throws ParseException
     */
    public void updateTransactionForCard(Transaction transaction) throws IOException, ParseException {
        String date = String.valueOf(transaction.getTransactionDt());
        String postcode = String.valueOf(transaction.getPostCode());
        Put newTransaction = new Put(Bytes.toBytes(transaction.getCardId()));
        newTransaction.addColumn(Bytes.toBytes("transaction"), Bytes.toBytes("postcode"), Bytes.toBytes(postcode));
        newTransaction.addColumn(Bytes.toBytes("transaction"), Bytes.toBytes("transaction_dt"), Bytes.toBytes(date));
        htable.put(newTransaction);
    }

    /***
     * Add new transactions to the card_transactions_staging table irrespective of
     * whether they are GENUINE or FRAUD.
     * @param transaction
     * @param success
     * @throws IOException
     */
    public void addNewTransaction(Transaction transaction, boolean success) throws IOException, ParseException {
        String status;
        if (success) {
            status = Status.GENUINE.toString();
            System.out.print(" --> " + Status.GENUINE);
        } else {
            status = Status.FRAUD.toString();
            System.out.print(" --> " + Status.FRAUD);
        }
        Date parsedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(transaction.getTransactionDt());
        String dateInHiveSupportedFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parsedDate);

        String cardId = String.valueOf(transaction.getCardId());
        String memberId = String.valueOf(transaction.getMemberId());
        String amt = String.valueOf(transaction.getAmount());
        String postcode = String.valueOf(transaction.getPostCode());
        String posId = String.valueOf(transaction.getPosId());
        Put newTransaction = new Put(Bytes.toBytes(cardId + UUID.randomUUID()));
        newTransaction.addColumn(Bytes.toBytes("transaction"), Bytes.toBytes("card_id"), Bytes.toBytes(cardId));
        newTransaction.addColumn(Bytes.toBytes("transaction"), Bytes.toBytes("member_id"), Bytes.toBytes(memberId));
        newTransaction.addColumn(Bytes.toBytes("transaction"), Bytes.toBytes("amount"), Bytes.toBytes(amt));
        newTransaction.addColumn(Bytes.toBytes("transaction"), Bytes.toBytes("postcode"), Bytes.toBytes(postcode));
        newTransaction.addColumn(Bytes.toBytes("transaction"), Bytes.toBytes("pos_id"), Bytes.toBytes(posId));
        newTransaction.addColumn(Bytes.toBytes("transaction"), Bytes.toBytes("transaction_dt"), Bytes.toBytes(dateInHiveSupportedFormat));
        newTransaction.addColumn(Bytes.toBytes("transaction"), Bytes.toBytes("status"), Bytes.toBytes(status));
        htable.put(newTransaction);
    }

    /**
     * Close the connection to the table when done.
     * @throws IOException
     */
    public void closeConnection() throws IOException {
        con.close();
    }
}
