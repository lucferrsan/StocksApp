package com.stocks.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @SerializedName("meta")
    private Meta meta;

    @SerializedName("timestamp")
    private List<Long> timestamp;

    @SerializedName("indicators")
    private Indicators indicators;

    public Meta getMeta() {
        return meta;
    }

    public List<Long> getTimestamp() {
        return timestamp;
    }

    public Indicators getIndicators() {
        return indicators;
    }
}