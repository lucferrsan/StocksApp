package com.stocks.core.api;

import com.stocks.core.model.StockModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("v8/finance/chart/{symbol}")
    Call<StockModel> getChartData(
            @Path("symbol") String symbol,
            @Query("range") String range,
            @Query("interval") String interval
    );

    @GET("v8/finance/chart/{symbol}")
    Call<StockModel> getStockData(
            @Path("symbol") String symbol
    );
}

