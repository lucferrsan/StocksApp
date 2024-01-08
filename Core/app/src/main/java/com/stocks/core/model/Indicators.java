package com.stocks.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Indicators {
    @SerializedName("quote")
    private List<Quote> quote;

    public List<Quote> getQuote() {
        return quote;
    }

    public static class Quote {
        @SerializedName("open")
        private List<Double> open;

        @SerializedName("low")
        private List<Double> low;

        @SerializedName("high")
        private List<Double> high;

        @SerializedName("close")
        private List<Double> close;

        @SerializedName("volume")
        private List<Long> volume;

        public List<Double> getOpen() {
            return open;
        }

        public List<Double> getLow() {
            return low;
        }

        public List<Double> getHigh() {
            return high;
        }

        public List<Double> getClose() {
            return close;
        }

        public List<Long> getVolume() {
            return volume;
        }
    }
}
