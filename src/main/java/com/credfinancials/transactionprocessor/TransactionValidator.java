package com.credfinancials.transactionprocessor;

import com.credfinancials.dtos.HbaseConnectionInfo;
import com.credfinancials.dtos.LastTransaction;
import com.credfinancials.dtos.Transaction;
import com.credfinancials.transactionprocessor.database.ConnectorFactory;
import com.credfinancials.transactionprocessor.database.DbOperations;
import com.credfinancials.utility.DistanceUtility;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import static com.credfinancials.Constants.Constants.LOOKUP_TABLE_NAME;

/***
 * Validates transactions on the basis of 3 parameters i.e., transaction amount,
 * UCL and postcode.
 */
public class TransactionValidator implements Serializable {
    private Transaction transaction;
    private DbOperations dbOperationsConn;
    private LastTransaction lastTransactionInfo;

    public TransactionValidator(HbaseConnectionInfo hbaseConnectionInfo,Transaction transaction) throws Exception {
        this.transaction = transaction;
        if (dbOperationsConn == null) {
            dbOperationsConn = ConnectorFactory.getDatabaseConnection(hbaseConnectionInfo,LOOKUP_TABLE_NAME);
            lastTransactionInfo = dbOperationsConn.getLastTransactionForCard(transaction.getCardId());
        }
    }

    public boolean validateTransactionAmount() {
        boolean isValid = false;
        if (lastTransactionInfo.getUcl() > transaction.getAmount()) {
            isValid = true;
        }
        return isValid;
    }

    public boolean validateCreditScore() throws Exception {
        boolean isValid = false;
        if (lastTransactionInfo.getScore() > 200) {
            isValid = true;
        }
        return isValid;
    }

    public boolean validateZipCode() throws Exception {
        boolean isValid = false;
        DistanceUtility disUtil = new DistanceUtility();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String lastTransactionPostCode = String.valueOf(lastTransactionInfo.getPostcode());
        String currentTransactionPostCode = String.valueOf(transaction.getPostCode());
        double distanceViaZipCode = disUtil.getDistanceViaZipCode(lastTransactionPostCode, currentTransactionPostCode);
        long durationInMilliSeconds = Math.abs(dateFormat.parse(transaction.getTransactionDt()).getTime() - dateFormat.parse(lastTransactionInfo.getTransactionDt()).getTime());
        long durationInSeconds = durationInMilliSeconds / 1000;
        double speed = distanceViaZipCode / durationInSeconds;
        if (speed < 0.25) {
            isValid = true;
        }
        return isValid;
    }

    public boolean validateOnAllParameters() throws Exception {
        return validateTransactionAmount() && validateCreditScore() && validateZipCode();
    }
}
