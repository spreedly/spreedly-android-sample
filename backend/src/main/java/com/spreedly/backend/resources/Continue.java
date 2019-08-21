package com.spreedly.backend.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Continue extends Resource implements Serializable {
    @Expose @SerializedName("three_ds_data") private final String threeDsData;

    public Continue(String threeDsData) {
        this.threeDsData = threeDsData;
    }
}
