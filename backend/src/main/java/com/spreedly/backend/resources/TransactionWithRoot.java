package com.spreedly.backend.resources;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class TransactionWithRoot extends Resource implements Serializable {
    @Expose private Transaction transaction;

    // Constructors
    public TransactionWithRoot(Transaction transaction) {
        this.transaction = transaction;
    }

    // Getters

    public Transaction getTransaction() {
        return transaction;
    }
}
