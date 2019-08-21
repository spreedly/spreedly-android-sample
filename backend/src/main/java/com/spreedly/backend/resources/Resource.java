package com.spreedly.backend.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Resource {
    @SerializedName("errors")
    @Expose
    private List<String> errors;

    public boolean isSuccess() {
        if (errors == null) {
            return true;
        }

        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public String toJSON() {
        Gson serializer = new GsonBuilder().create();
        return serializer.toJson(this, this.getClass());
    }
}
