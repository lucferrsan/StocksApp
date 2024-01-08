package com.stocks.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chart {
    @SerializedName("result")
    private List<Result> result;

    public List<Result> getResult() {
        return result;
    }
}