package com.stocks.core.model;

import com.google.gson.annotations.SerializedName;

public class Period {
    @SerializedName("timezone")
    private String timezone;

    @SerializedName("start")
    private long start;

    @SerializedName("end")
    private long end;

    @SerializedName("gmtoffset")
    private int gmtoffset;

    public String getTimezone() {
        return timezone;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public int getGmtoffset() {
        return gmtoffset;
    }
}