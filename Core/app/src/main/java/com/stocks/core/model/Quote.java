package com.stocks.core.model;

import java.util.List;

public class Quote {
    private List<Double> open;
    private List<Double> high;
    private List<Double> low;
    private List<Double> close;
    private List<Long> volume;

    public List<Double> getOpen() {
        return open;
    }

    public List<Double> getHigh() {
        return high;
    }

    public List<Double> getLow() {
        return low;
    }

    public List<Double> getClose() {
        return close;
    }

    public List<Long> getVolume() {
        return volume;
    }
}