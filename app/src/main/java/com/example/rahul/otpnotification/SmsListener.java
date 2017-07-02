package com.example.rahul.otpnotification;

/**
 * SmsListener interface : this interface implements the messageRecived method
 *
 */
public interface SmsListener{
    void MessageReceived(String messageText , String messageSender);
}
