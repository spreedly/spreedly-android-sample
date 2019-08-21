package com.spreedly.backend.resources;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class GatewayListing extends Resource implements Serializable {
    @Expose private List<Gateway> gateways;

    public List<Gateway> getGateways() {
        return gateways;
    }
}
