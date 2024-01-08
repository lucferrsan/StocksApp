package com.stocks.core;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.gson.Gson;
import com.stocks.core.model.Chart;
import com.stocks.core.model.Meta;
import com.stocks.core.model.Result;
import com.stocks.core.model.StockModel;
import com.stocks.core.network.ApiClient;
import com.stocks.core.utils.CustomMarker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private static final String FLUTTER_CHANNEL = "stocks_channel";
    private MethodChannel flutterMethodChannel;
    private ApiClient apiClient;
    private final String selectedInterval1 = "max";
    private final String selectedInterval2 = "1mo";
    private final List<Button> buttons = new ArrayList<>();
    private TextView txtStockPercent;
    private TextView txtStockPrice;
    private TextView txtStockID;
    private Button btnHistory;
    private ProgressBar progressBar;
    private LinearLayout modalLoad;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int updateInterval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new ApiClient();

        modalLoad = findViewById(R.id.modalLoad);
        progressBar = findViewById(R.id.progressBar);
        txtStockPercent = findViewById(R.id.txtStockPercent);
        txtStockPrice = findViewById(R.id.txtStockPrice);
        txtStockID = findViewById(R.id.txtStockID);
        btnHistory = findViewById(R.id.btnHistory);

        fetchAndUpdateStockData();

        FlutterEngine flutterEngine = new FlutterEngine(MainActivity.this);
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );

        FlutterEngineCache.getInstance().put(FLUTTER_CHANNEL, flutterEngine);

        flutterMethodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), FLUTTER_CHANNEL);

        btnHistory.setOnClickListener(v -> {
            startActivity(FlutterActivity.withCachedEngine(FLUTTER_CHANNEL)
                    .build(this));
        });


        fetchData(selectedInterval1, selectedInterval2);
        //startDataUpdate();
    }

    private void startDataUpdate() {
        final Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                fetchAndUpdateStockData();

                handler.postDelayed(this, updateInterval);
            }
        };

        handler.post(updateRunnable);
    }

    private void fetchAndUpdateStockData() {
        progressBar.setVisibility(View.VISIBLE);
        modalLoad.setVisibility(View.VISIBLE);
        apiClient.fetchStockData("PETR4.SA", new Callback<StockModel>() {
            @Override
            public void onResponse(@NonNull Call<StockModel> call, @NonNull retrofit2.Response<StockModel> response) {
                progressBar.setVisibility(View.GONE);
                modalLoad.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    StockModel stockModels = response.body();
                    Chart chart = stockModels.getChart();
                    Result result = chart.getResult().get(0);

                    processStockData(result);

                    sendDataToFlutter(stockModels.getChart().getResult().get(0).getTimestamp(), stockModels.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose());
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<StockModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                modalLoad.setVisibility(View.GONE);
            }
        });
    }

    private void sendDataToFlutter(List<Long> timestamps, List<Double> closes) {
        Map<String, Object> stockData = new HashMap<>();
        stockData.put("timestamps", timestamps);
        stockData.put("closes", closes);

        flutterMethodChannel.invokeMethod("updateStockData", stockData);
    }

    private void setupRangeButtons(Meta meta) {
        List<String> validRanges = meta.getValidRanges();
        int totalButtons = validRanges.size();

        addButton(selectedInterval1, meta.getDataGranularity(), 100, 15, true);

        for (int i = 0; i < totalButtons; i++) {
            String range = validRanges.get(i);
            String granularity = meta.getDataGranularity();

            int marginLeft = (i == 0) ? 15 : 15;

            int marginRight = (i == totalButtons - 1) ? 100 : 15;

            Button existingButton = findButtonByRange(range);
            if (existingButton != null && !range.equals(selectedInterval1)) {
                updateButtonState(existingButton, range, granularity, marginLeft, marginRight);
            } else if (!range.equals(selectedInterval1)) {
                addButton(range, granularity, marginLeft, marginRight, false);
            }
        }
    }

    private void processStockData(Result result) {
        if (result != null) {
            double regularMarketPrice = result.getMeta().getRegularMarketPrice();
            double previousClose = result.getMeta().getChartPreviousClose();
            String symbol = result.getMeta().getSymbol();

            double changePercentage = ((regularMarketPrice - previousClose) / previousClose) * 100;

            txtStockPercent.setText(String.format(Locale.getDefault(), "%.2f%%", changePercentage));
            txtStockPrice.setText(String.format(Locale.getDefault(), "R$ %.2f", regularMarketPrice));
            txtStockID.setText(symbol);

            if (changePercentage < 0) {
                txtStockPercent.setTextColor(Color.RED);
            } else if (changePercentage > 0) {
                txtStockPercent.setTextColor(Color.GREEN);
            } else {
            }
        }
    }

    private Button findButtonByRange(String range) {
        for (Button button : buttons) {
            if (mapRangeToText(range).equals(button.getText().toString())) {
                return button;
            }
        }
        return null;
    }

    private void updateButtonState(Button button, String range, String granularity, int marginLeft, int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.setMargins(marginLeft, 0, marginRight, 0);
        button.setLayoutParams(params);

        if (range.equals(selectedInterval1)) {
            button.setBackgroundResource(R.drawable.rounded_button_active);
        } else {
            button.setBackgroundResource(R.drawable.rounded_button);
        }

        button.setOnClickListener(v -> {
            fetchData(range, granularity);
            clearButtonStates();
            button.setBackgroundResource(R.drawable.rounded_button_active);
        });
    }

    private String mapRangeToText(String range) {
        switch (range) {
            case "1d":
                return "1 dia";
            case "5d":
                return "5 dias";
            case "1mo":
                return "1 mês";
            case "3mo":
                return "3 meses";
            case "6mo":
                return "6 meses";
            case "1y":
                return "1 ano";
            case "2y":
                return "2 anos";
            case "5y":
                return "5 anos";
            case "10y":
                return "10 anos";
            case "ytd":
                return "Este ano";
            case "max":
                return "Tudo";
            default:
                return "";
        }
    }

    private void addButton(String range, String granularity, int marginLeft, int marginRight, boolean isActive) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(marginLeft, 0, marginRight, 0);

        Button button = new Button(this);
        button.setText(mapRangeToText(range));
        button.setTextColor(Color.WHITE);
        button.setLayoutParams(params);
        button.setHeight(20);
        button.setBackgroundResource(isActive ? R.drawable.rounded_button_active : R.drawable.rounded_button);

        button.setOnClickListener(v -> {
            fetchData(range, "1mo");
            clearButtonStates();
            button.setBackgroundResource(R.drawable.rounded_button_active);
        });

        buttons.add(button);

        LinearLayout buttonContainer = findViewById(R.id.lnlListBtn);
        buttonContainer.addView(button);
    }

    private void clearButtonStates() {
        for (Button btn : buttons) {
            btn.setBackgroundResource(R.drawable.rounded_button);
            btn.setTextColor(Color.WHITE);
        }
    }

    private void fetchData(String range, String granularity) {
        progressBar.setVisibility(View.VISIBLE);
        modalLoad.setVisibility(View.VISIBLE);
        apiClient.fetchChartData("PETR4.SA", range, granularity, new Callback<StockModel>() {
            @Override
            public void onResponse(@NonNull Call<StockModel> call, @NonNull retrofit2.Response<StockModel> response) {
                progressBar.setVisibility(View.GONE);
                modalLoad.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    StockModel stockModels = response.body();
                    Result result = stockModels.getChart().getResult().get(0);
                    Meta meta = result.getMeta();
                    updateChart(stockModels);
                    setupRangeButtons(meta);
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<StockModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                modalLoad.setVisibility(View.GONE);
            }
        });
    }

    private void updateChart(StockModel stockModels) {
        if (stockModels != null && stockModels.getChart() != null) {
            List<Result> results = stockModels.getChart().getResult();

            if (results != null && !results.isEmpty()) {
                Result result = results.get(0);

                List<Long> timestamps = result.getTimestamp();
                List<Double> closes = result.getIndicators().getQuote().get(0).getClose();

                ArrayList<Entry> entries = new ArrayList<>();
                for (int i = 0; i < timestamps.size(); i++) {
                    Double closeValue = closes.get(i);
                    if (closeValue != null) {
                        entries.add(new Entry(i, closeValue.floatValue()));
                    } else {

                    }
                }

                int lineColor = ContextCompat.getColor(MainActivity.this, R.color.colorSecondary);

                LineDataSet dataSet = new LineDataSet(entries, "");
                dataSet.setColor(lineColor);
                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);
                dataSet.setLineWidth(3);
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Adicione essa linha para uma curva suave

                LineData lineData = new LineData(dataSet);

                LineChart lineChart = findViewById(R.id.lineChart);
                lineChart.setData(lineData);

                int lastIndex = entries.size() - 1;
                if (lastIndex >= 0) {
                    Entry lastEntry = entries.get(lastIndex);

                    CustomMarker markerView = new CustomMarker(this, R.layout.custom_marker_view);
                    markerView.setChartView(lineChart);
                    lineChart.setMarker(markerView);

                    Highlight highlight = new Highlight(lastEntry.getX(), lastEntry.getY(), 0);
                    lineChart.highlightValue(highlight);
                }

                Legend legend = lineChart.getLegend();
                legend.setEnabled(false);

                Description description = new Description();
                description.setText("");
                lineChart.setDescription(description);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawGridLinesBehindData(false);
                xAxis.setTextSize(10f);
                xAxis.setTextColor(Color.GRAY);

                List<String> xAxisLabels = generateXAxisLabels(timestamps, selectedInterval1);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setAxisMinimum(0f);
                leftAxis.setLabelCount(7, true);
                leftAxis.setGranularity(10f);
                leftAxis.setTextSize(13f);
                leftAxis.setTextColor(Color.GRAY);

                leftAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return "";
                    }
                });

                YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setAxisMinimum(0f);
                rightAxis.setLabelCount(7, true);
                rightAxis.setGranularity(10f);
                rightAxis.setTextSize(13f);
                rightAxis.setTextColor(Color.GRAY);

                rightAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return "R$ " + String.format(Locale.getDefault(), "%.2f", value);  // Adicione o prefixo "R$"
                    }
                });


                lineChart.invalidate();
            }
        }
    }

    private List<String> generateXAxisLabels(List<Long> timestamps, String range) {
        List<String> labels = new ArrayList<>();

        for (Long timestamp : timestamps) {
            labels.add(formatTimestamp(timestamp, range));
        }

        return labels;
    }

    private String formatTimestamp(long timestamp, String range) {
        Date date = new Date(timestamp * 1000);
        SimpleDateFormat sdf;

        switch (range) {
            case "1d":
                sdf = new SimpleDateFormat("HH:mm");
                return sdf.format(date);
            case "5d":
                int days = (int) ((System.currentTimeMillis() - timestamp * 1000) / (24 * 60 * 60 * 1000));
                return days + "d";
            case "1mo":
                sdf = new SimpleDateFormat("d");
                return sdf.format(date);
            case "3mo":
                sdf = new SimpleDateFormat("d MMM");
                return sdf.format(date);
            case "6mo":
            case "1y":
                sdf = new SimpleDateFormat("MMM");
                return formatDateWithYearIfApplicable(date, range);
            case "2y":
            case "5y":
                sdf = new SimpleDateFormat("MMM yyyy");
                return formatDateWithYearIfApplicable(date, range);
            case "10y":
                sdf = new SimpleDateFormat("yyyy");
                return sdf.format(date);
            case "ytd":
                sdf = new SimpleDateFormat("MMM dd");
                return sdf.format(date);
            case "max":
                sdf = new SimpleDateFormat("yyyy");
                return sdf.format(date);
            default:
                return "";
        }
    }

    private String formatDateWithYearIfApplicable(Date date, String range) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);

        if ("1y".equals(range) || "2y".equals(range) || "5y".equals(range) || "max".equals(range)) {
            return new SimpleDateFormat("MMM yyyy").format(date);
        } else {
            return new SimpleDateFormat("MMM").format(date);
        }
    }
}