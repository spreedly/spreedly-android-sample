package com.spreedly.backend.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Gateway implements Serializable {
    @SerializedName("token") @Expose private String token;
    @SerializedName("gateway_type") @Expose private String gatewayType;
    @SerializedName("name") @Expose private String name;
    @SerializedName("state") @Expose private String state;
    @SerializedName("redacted") @Expose private Boolean redacted;
    @SerializedName("characteristics") @Expose private List<String> characteristics;
    @SerializedName("payment_methods") @Expose private List<String> paymentMethods;
    @SerializedName("gateway_specific_fields") @Expose private List<String> gatewaySpecificFields;

    public String getToken() {
        return token;
    }

    public String getGatewayType() {
        return gatewayType;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public Boolean getRedacted() {
        return redacted;
    }

    public List<String> getCharacteristics() {
        return characteristics;
    }

    public List<String> getPaymentMethods() {
        return paymentMethods;
    }

    public List<String> getGatewaySpecificFields() {
        return gatewaySpecificFields;
    }
}
