package com.stocks.core.model;

import com.google.gson.annotations.SerializedName;

public class TradingPeriod {
    @SerializedName("pre")
    private Period pre;

    @SerializedName("regular")
    private Period regular;

    @SerializedName("post")
    private Period post;

    public Period getPre() {
        return pre;
    }

    public Period getRegular() {
        return regular;
    }

    public Period getPost() {
        return post;
    }
}