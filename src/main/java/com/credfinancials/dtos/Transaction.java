package com.credfinancials.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.text.ParseException;

/***
 * DTO to store new transaction details fetched from kafka topic.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction implements Serializable {
    private long cardId;
    private long memberId;
    private int amount;
    private long posId;
    private int postCode;
    private String transactionDt;

    public Transaction(String cardId, String memberId, String amount, String posId, String postCode, String transactionDt) throws ParseException {
        this.cardId = Long.parseLong(cardId);
        this.memberId = Long.parseLong(memberId);
        this.amount = Integer.parseInt(amount);
        this.posId = Long.parseLong(posId);
        this.postCode = Integer.parseInt(postCode);
        this.transactionDt = transactionDt;
    }

    public Transaction(TransactionGeneric transactionGeneric) throws ParseException {
        this.cardId = Long.parseLong(transactionGeneric.getCard_id());
        this.memberId = Long.parseLong(transactionGeneric.getMember_id());
        this.amount = Integer.parseInt(transactionGeneric.getAmount());
        this.posId = Long.parseLong(transactionGeneric.getPos_id());
        this.postCode = Integer.parseInt(transactionGeneric.getPostcode());
        this.transactionDt = transactionGeneric.getTransaction_dt();
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getPosId() {
        return posId;
    }

    public void setPosId(long posId) {
        this.posId = posId;
    }

    public int getPostCode() {
        return postCode;
    }

    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    public String getTransactionDt() {
        return transactionDt;
    }

    public void setTransactionDt(String transactionDt) {
        this.transactionDt = transactionDt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "cardId=" + cardId +
                ", memberId=" + memberId +
                ", amount=" + amount +
                ", posId=" + posId +
                ", postCode=" + postCode +
                ", transactionDt=" + transactionDt +
                '}';
    }
}
