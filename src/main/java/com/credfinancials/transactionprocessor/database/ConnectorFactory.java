package com.credfinancials.transactionprocessor.database;

import com.credfinancials.dtos.HbaseConnectionInfo;

import static com.credfinancials.Constants.Constants.CARD_TRANSACTIONS_TABLE_NAME;
import static com.credfinancials.Constants.Constants.LOOKUP_TABLE_NAME;

/***
 * Factory class to create connections to one of the two tables:
 * card_transactions_staging and card_transactions_lookup.
 */
public class ConnectorFactory {
    private static DbOperations cardTransactionsTable;
    private static DbOperations lookupTable;

    private ConnectorFactory() {
    }

    public static DbOperations getDatabaseConnection(HbaseConnectionInfo hbaseConnectionInfo, String table) throws Exception {
        if (table.equalsIgnoreCase(CARD_TRANSACTIONS_TABLE_NAME)) {
            if (cardTransactionsTable == null) {
                cardTransactionsTable = new DbOperations(hbaseConnectionInfo, table);
            }
            return cardTransactionsTable;
        } else if (table.equalsIgnoreCase(LOOKUP_TABLE_NAME)) {
            if (lookupTable == null) {
                lookupTable = new DbOperations(hbaseConnectionInfo, table);
            }
            return lookupTable;
        }
        throw new Exception("Not a valid table");
    }
}
