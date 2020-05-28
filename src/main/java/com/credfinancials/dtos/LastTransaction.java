package com.credfinancials.dtos;

import java.text.ParseException;

/***
 * DTO to store transaction details fetched from card_transactions_lookup table.
 */
public class LastTransaction {
    private long cardId;
    private int postcode;
    private String transactionDt;
    private double ucl;
    private int score;

    public LastTransaction(int postcode, String transactionDt, double ucl, int score) throws ParseException {
        this.postcode = postcode;
        this.ucl = ucl;
        this.score = score;
        this.transactionDt = transactionDt;
    }

    public LastTransaction(String postcode, String transactionDt, String ucl, String score) throws ParseException {
        this.postcode = Integer.parseInt(postcode);
        this.ucl = Double.parseDouble(ucl);
        this.score = Integer.parseInt(score);
        this.transactionDt = transactionDt;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public String getTransactionDt() {
        return transactionDt;
    }

    public void setTransactionDt(String transactionDt) {
        this.transactionDt = transactionDt;
    }

    public double getUcl() {
        return ucl;
    }

    public void setUcl(double ucl) {
        this.ucl = ucl;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
