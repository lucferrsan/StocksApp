package com.stocks.core.network;

import com.stocks.core.api.ApiService;
import com.stocks.core.model.StockModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://query2.finance.yahoo.com/";

    private final ApiService apiService;

    public ApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void fetchChartData(String symbol, String range, String interval, Callback<StockModel> callback) {
        Call<StockModel> call = apiService.getChartData(symbol, range, interval);
        call.enqueue(callback);
    }

    public void fetchStockData(String symbol, Callback<StockModel> callback) {
        Call<StockModel> call = apiService.getStockData(symbol);
        call.enqueue(callback);
    }
}
