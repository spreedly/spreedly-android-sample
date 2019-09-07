package com.spreedly.backend.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransactionCheckoutForm implements Serializable {
    @Expose @SerializedName("cdata") private String cdata;

    public String getCdata() {
        return this.cdata;
    }
}
