package com.stocks.core.model;

import java.util.List;

public class StockModels {
    private Chart chart;

    public Chart getChart() {
        return chart;
    }

    public static class Chart {
        private List<Result> result;
        private String dataGranularity; // Novo campo

        public List<Result> getResult() {
            return result;
        }

        public String getDataGranularity() {
            return dataGranularity;
        }

        public static class Result {
            private Meta meta;
            private List<Long> timestamp;
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
    }

    public static class Meta {
        private List<String> validRanges;
        private String granularity;
        private double regularMarketPrice;
        private double chartPreviousClose;

        public double getRegularMarketPrice() {
            return regularMarketPrice;
        }

        public double getChartPreviousClose() {
            return chartPreviousClose;
        }

        // Adicione getters para validRanges e granularity
        public List<String> getValidRanges() {
            return validRanges;
        }

        public String getGranularity() {
            return granularity;
        }
    }

    public static class Indicators {
        private List<Quote> quote;

        public List<Quote> getQuote() {
            return quote;
        }

        public static class Quote {
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
    }
}
