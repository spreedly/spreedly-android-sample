package com.spreedly.backend;


import com.spreedly.backend.resources.Continue;
import com.spreedly.backend.resources.GatewayListing;
import com.spreedly.backend.resources.GatewayWithRoot;
import com.spreedly.backend.resources.TransactionWithRoot;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IClient {
    // Transaction Based Requests
    @POST("v1/gateways/{gateway}/purchase.json")
    Call<TransactionWithRoot> createGatewayPurchase(@Path("gateway") String gateway, @Body TransactionWithRoot body);

    @POST("v1/gateways/{gateway}/authorize.json")
    Call<TransactionWithRoot> createGatewayAuthorize(@Path("gateway") String gateway, @Body TransactionWithRoot body);

    @POST("v1/transactions/{token}/continue.json")
    Call<TransactionWithRoot> continueTransaction(@Path("token") String token, @Body Continue body);

    // Gateway Based Requests
    @GET("v1/gateways/{gateway}.json")
    Call<GatewayWithRoot> showGateway(@Path("gateway") String gateway);

    @GET("v1/gateways.json")
    Call<GatewayListing> listGateways();
}
