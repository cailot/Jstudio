package com.creapple.tms.mobiledriverconsole.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.firebase.client.ServerValue;

import java.io.Serializable;

/**
 * Created by jinseo on 2016. 7. 7..
 */
public class TransactionVO implements Serializable{

    private int UUID;

    private int adultNo;

    private int adultPrice;

    private String destinationId;

    private String destinationName;

    private String originName;

    private String originId;

//    private int passengerTotal;

    private String paymentType;

    @JsonProperty
    private Object updated;

    public int getAdultNo() {
        return adultNo;
    }

    public void setAdultNo(int adultNo) {
        this.adultNo = adultNo;
    }

    public int getAdultPrice() {
        return adultPrice;
    }

    public void setAdultPrice(int adultPrice) {
        this.adultPrice = adultPrice;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

//    public int getPassengerTotal() {
//        return passengerTotal;
//    }

//    public void setPassengerTotal(int passengerTotal) {
//        this.passengerTotal = passengerTotal;
//    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @JsonIgnore
    public Long getUpdatedLong() {
        if(updated instanceof Long){
            return (Long) updated;
        }else{
            return null;
        }
    }

    public int getUUID() {
        return UUID;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    public TransactionVO() {
        updated = ServerValue.TIMESTAMP;
    }
}
