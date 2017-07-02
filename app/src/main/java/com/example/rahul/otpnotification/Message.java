package com.example.rahul.otpnotification;

/**
 * Created by rahul on 2/7/17.
 *
 * Message class for storing the otp messages
 * uid :  unique identifier
 * message :  the body of the received message
 * otp : the otp parsed from the message
 *
 * //to be added :
 * 1.sender : the sender of the message
 * 2.time : the time message was received
 *
 * //to be implemented
 * 1. parcelable
 */

public class Message {
    private String uid;
    private String message;
    private String otp;
    private String sender;

    public String getMessage(){
        return this.message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getUid(){
        return this.uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getOtp(){
        return this.otp;
    }

    public void setOtp(String otp){
        this.otp = otp;
    }

    public String getSender(){
        return this.sender;
    }
    public void setSender(String sender){
        this.sender = sender ;
    }
}
