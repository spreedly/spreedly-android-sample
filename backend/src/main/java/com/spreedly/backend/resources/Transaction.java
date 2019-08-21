package com.spreedly.backend.resources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Transaction extends Resource implements Serializable {
    @Expose private String token;
    @Expose private String state;
    @Expose private Integer amount;
    @Expose private String channel;
    @Expose private Boolean succeeded;

    @Expose @SerializedName("attempt_3dsecure") private Boolean attempt3dSecure;
    @Expose @SerializedName("three_ds_version") private String threeDsVersion;
    @Expose @SerializedName("redirect_url") private String redirectUrl;
    @Expose @SerializedName("callback_url") private String callbackUrl;
    @Expose @SerializedName("browser_info") String browserInfo;
    @Expose @SerializedName("device_info") Map<String, Object> deviceInfo;

    @Expose @SerializedName("gateway_specific_fields") HashMap<String, HashMap<String, Object>> gatewaySpecificFields;

    @Expose @SerializedName("payment_method_token") private String paymentMethodToken;
    @Expose @SerializedName("required_action") private String requiredAction;
    @Expose @SerializedName("currency_code") private String currencyCode;
    @Expose @SerializedName("three_ds_context") private String threeDsContext;

    @Expose @SerializedName("payment_method") private PaymentMethod paymentMethod;
    @Expose @SerializedName("credit_card") private CreditCard creditCard;

    // Constructor with Payment Method

    public Transaction(Integer amount, String currencyCode, PaymentMethod paymentMethod) {
       this.amount = amount;
       this.currencyCode = currencyCode;

       if (paymentMethod instanceof CreditCard) {
           this.creditCard = (CreditCard) paymentMethod;
       } else {
           this.paymentMethod = paymentMethod;
       }
    }

     public Transaction(Integer amount, String currencyCode, PaymentMethod paymentMethod, Boolean attempt3dSecure, String threeDsVersion) {
        this(amount, currencyCode, paymentMethod);
        this.attempt3dSecure = attempt3dSecure;
        this.threeDsVersion = threeDsVersion;
    }

    public Transaction(Integer amount, String currencyCode, PaymentMethod paymentMethod, Boolean attempt3dSecure, String threeDsVersion, String browserInfo) {
       this(amount, currencyCode, paymentMethod, attempt3dSecure, threeDsVersion);
       this.browserInfo = browserInfo;
    }

    public Transaction(Integer amount, String currencyCode, PaymentMethod paymentMethod, Boolean attempt3dSecure, String threeDsVersion, Map<String, Object> deviceInfo) {
        this(amount, currencyCode, paymentMethod, attempt3dSecure, threeDsVersion);
        this.deviceInfo = deviceInfo;
    }

    // Constructor with Payment Method Token

    public Transaction(Integer amount, String currencyCode, String paymentMethodToken) {
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.paymentMethodToken = paymentMethodToken;
    }

    public Transaction(Integer amount, String currencyCode, String paymentMethodToken, Boolean attempt3dSecure, String threeDsVersion) {
        this(amount, currencyCode, paymentMethodToken);
        this.attempt3dSecure = attempt3dSecure;
        this.threeDsVersion = threeDsVersion;
    }

    public Transaction(Integer amount, String currencyCode, String paymentMethodToken, Boolean attempt3dSecure, String threeDsVersion, String browserInfo) {
        this(amount, currencyCode, paymentMethodToken, attempt3dSecure, threeDsVersion);
        this.browserInfo = browserInfo;
    }

    public Transaction(Integer amount, String currencyCode, String paymentMethodToken, Boolean attempt3dSecure, String threeDsVersion, Map<String, Object> deviceInfo) {
        this(amount, currencyCode, paymentMethodToken, attempt3dSecure, threeDsVersion);
        this.deviceInfo = deviceInfo;
    }

    public Transaction(CreditCard creditCard) {
        this.amount = 0;
        this.currencyCode = "USD";
        this.attempt3dSecure = false;
        this.creditCard = creditCard;
    }

    // Getters

    public String getToken() {
        return token;
    }

    public String getState() {
        return state;
    }

    public String getRequiredAction() {
        return requiredAction;
    }

    public Integer getAmount() {
        return amount;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public String getPaymentMethodToken() {
        if ((paymentMethodToken == null || paymentMethodToken.trim().isEmpty()) && paymentMethod != null) {
            return paymentMethod.getToken();
        }
        return paymentMethodToken;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public CreditCard getCreditCard() {
        return this.creditCard;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public Boolean getAttempt3dSecure() {
        return this.attempt3dSecure;
    }

    public String getThreeDsVersion() {
        return this.threeDsVersion;
    }

    public String getBrowserInfo() {
        return this.browserInfo;
    }

    public Map<String, Object> getDeviceInfo() {
        return this.deviceInfo;
    }

    public String getChannel() {
        return channel;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getThreeDsContext() {
        return threeDsContext;
    }

    // Setters

    public void setPaymentMethodToken(String token) {
        paymentMethodToken = token;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setAttempt3dSecure(boolean attempt3dSecure) {
        this.attempt3dSecure = attempt3dSecure;
    }

    public void setThreeDsVersion(String threeDsVersion) {
        this.threeDsVersion = threeDsVersion;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void setThreeDsContext(String threeDsContext) {
        this.threeDsContext = threeDsContext;
    }
}
