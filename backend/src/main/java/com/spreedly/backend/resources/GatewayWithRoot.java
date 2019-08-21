package com.spreedly.backend.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GatewayWithRoot implements Serializable {
    @SerializedName("gateway")
    @Expose
    private Gateway gateway;

    public Gateway getGateway() {
        return gateway;
    }
}
