package com.stocks.core.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Meta {
    @SerializedName("currency")
    private String currency;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("exchangeName")
    private String exchangeName;

    @SerializedName("instrumentType")
    private String instrumentType;

    @SerializedName("firstTradeDate")
    private long firstTradeDate;

    @SerializedName("regularMarketTime")
    private long regularMarketTime;

    @SerializedName("gmtoffset")
    private int gmtoffset;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("exchangeTimezoneName")
    private String exchangeTimezoneName;

    @SerializedName("regularMarketPrice")
    private double regularMarketPrice;

    @SerializedName("chartPreviousClose")
    private double chartPreviousClose;

    @SerializedName("previousClose")
    private double previousClose;

    @SerializedName("scale")
    private int scale;

    @SerializedName("priceHint")
    private int priceHint;

    @SerializedName("currentTradingPeriod")
    private TradingPeriod currentTradingPeriod;

    @SerializedName("tradingPeriods")
    private List<List<TradingPeriod>> tradingPeriods;

    @SerializedName("dataGranularity")
    private String dataGranularity;

    @SerializedName("range")
    private String range;

    @SerializedName("validRanges")
    private List<String> validRanges;

    public String getCurrency() {
        return currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public long getFirstTradeDate() {
        return firstTradeDate;
    }

    public long getRegularMarketTime() {
        return regularMarketTime;
    }

    public int getGmtoffset() {
        return gmtoffset;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getExchangeTimezoneName() {
        return exchangeTimezoneName;
    }

    public double getRegularMarketPrice() {
        return regularMarketPrice;
    }

    public double getChartPreviousClose() {
        return chartPreviousClose;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public int getScale() {
        return scale;
    }

    public int getPriceHint() {
        return priceHint;
    }

    public TradingPeriod getCurrentTradingPeriod() {
        return currentTradingPeriod;
    }

    public List<List<TradingPeriod>> getTradingPeriods() {
        return tradingPeriods;
    }

    public String getDataGranularity() {
        return dataGranularity;
    }

    public String getRange() {
        return range;
    }

    public List<String> getValidRanges() {
        return validRanges;
    }
}