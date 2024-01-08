package com.stocks.core.model;

import com.google.gson.annotations.SerializedName;

public class StockModel {
    @SerializedName("chart")
    private Chart chart;

    public Chart getChart() {
        return chart;
    }
}
