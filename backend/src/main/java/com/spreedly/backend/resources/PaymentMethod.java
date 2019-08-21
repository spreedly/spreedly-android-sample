package com.spreedly.backend.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentMethod implements Serializable {
    @Expose private String token;
    @Expose private String number;
    @Expose private String month;
    @Expose private String year;
    @Expose @SerializedName("full_name") private String fullName;
    @Expose @SerializedName("verification_value") private String verificationValue;

    // Getters

    public String getToken() {
        return token;
    }

    public String getNumber() {
        return number;
    }

    public String getFullName() {
        return fullName;
    }

    public String getVerificationValue() {
        return verificationValue;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    // Setters

    public void setToken(String token) {
        this.token = token;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setVerificationValue(String verificationValue) {
        this.verificationValue = verificationValue;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
