package com.credfinancials.dtos;

import java.io.Serializable;

/***
 * DTO to store new transaction details fetched from kafka topic as string.
 */
public class TransactionGeneric implements Serializable {
    private String card_id;
    private String member_id;
    private String amount;
    private String pos_id;
    private String postcode;
    private String transaction_dt;

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPos_id() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTransaction_dt() {
        return transaction_dt;
    }

    public void setTransaction_dt(String transaction_dt) {
        this.transaction_dt = transaction_dt;
    }
}
