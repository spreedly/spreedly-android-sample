package com.spreedly.backend;

import com.spreedly.backend.resources.*;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Client implements Serializable {
    private final boolean debug;
    private String environmentKey;
    private String environmentSecret;
    private String host;
    private IClient client;

    public Client(String environmentKey, String environmentSecret) {
        this.environmentKey = environmentKey;
        this.environmentSecret = environmentSecret;
        this.host = "https://core.spreedly.com";
        this.debug = false;
    }

    public Client(String environmentKey, String environmentSecret, String host) {
        this.environmentKey = environmentKey;
        this.environmentSecret = environmentSecret;
        this.host = host;
        this.debug = false;
    }
    
    public Client(String environmentKey, String environmentSecret, String host, Boolean debug) {
        this.environmentKey = environmentKey;
        this.environmentSecret = environmentSecret;
        this.host = host;
        this.debug = debug;
    }

    public String getHost() {
        return host;
    }

    public String getEnvironmentKey() {
        return this.environmentKey;
    }

    public String getEnvironmentSecret() {
        return this.environmentSecret;
    }

    public IClient getClient() {
        if (this.client == null) {
            this.client = setupClient();
        }
        return this.client;
    }

    public List<Gateway> listGateways() throws IOException {
        IClient client = getClient();
        retrofit2.Call<GatewayListing> call = client.listGateways();
        GatewayListing listing = call.execute().body();
        if (listing != null) {
            List<Gateway> gateways = listing.getGateways();
            List<Gateway> filteredGateways = new ArrayList<>();

            for (Gateway gateway: gateways) {
                if (!gateway.getRedacted()) {
                    filteredGateways.add(gateway);
                }
            }

            return filteredGateways;
        }
        return new ArrayList<>();
    }

    public Transaction createGatewayPurchase(String gatewayToken, Transaction transaction) throws IOException {
        IClient client = getClient();
        retrofit2.Call<TransactionWithRoot> call = client.createGatewayPurchase(gatewayToken, new TransactionWithRoot(transaction));
        retrofit2.Response<TransactionWithRoot> execute = call.execute();
        TransactionWithRoot root = execute.body();
        if (root != null) {
            return root.getTransaction();
        }

        return null;
    }

    public Transaction continueTransaction(Transaction transaction, String threeDsData) throws IOException {
        IClient client = getClient();
        retrofit2.Call<TransactionWithRoot> call = client.continueTransaction(transaction.getToken(), new Continue(threeDsData));
        retrofit2.Response<TransactionWithRoot> execute = call.execute();
        TransactionWithRoot root = execute.body();
        if (root != null) {
            return root.getTransaction();
        }

        return null;
    }

    private IClient setupClient() {
        Authenticator authenticator = new Authenticator() {
            @Override
            public Request authenticate(Route route, okhttp3.Response response) throws IOException {
                String credential = Credentials.basic(environmentKey, environmentSecret);
                return response.request().newBuilder()
                        .header("Authorization", credential)
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .build();
            }
        };
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .authenticator(authenticator);

        if (debug) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(loggingInterceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(clientBuilder.build())
                .baseUrl(this.host)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(IClient.class);
    }
}
