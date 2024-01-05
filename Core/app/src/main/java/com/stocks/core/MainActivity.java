package com.stocks.core;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.stocks.core.model.StockModels;
import com.stocks.core.network.ApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private static final String FLUTTER_ENGINE_ID = "module_flutter_engine";
    private ApiClient apiClient;
    private final String selectedInterval1 = "max";
    private final String selectedInterval2 = "1mo";
    private final List<Button> buttons = new ArrayList<>();
    private TextView txtStockPercent;
    private TextView txtStockPrice;
    private Button btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new ApiClient();

        txtStockPercent = findViewById(R.id.txtStockPercent);
        txtStockPrice = findViewById(R.id.txtStockPrice);
        btnHistory = findViewById(R.id.btnHistory);

        fetchAndUpdateStockData();

        FlutterEngine flutterEngine = new FlutterEngine(MainActivity.this);
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );

        FlutterEngineCache.getInstance().put(FLUTTER_ENGINE_ID, flutterEngine);

        btnHistory.setOnClickListener(v -> {
            startActivity(FlutterActivity.withCachedEngine(FLUTTER_ENGINE_ID)
                    .build(this));
        });


        fetchData(selectedInterval1, selectedInterval2);

    }

    private void fetchAndUpdateStockData() {
        apiClient.fetchStockData("PETR4.SA", new Callback<StockModels>() {
            @Override
            public void onResponse(@NonNull Call<StockModels> call, @NonNull retrofit2.Response<StockModels> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StockModels stockModels = response.body();
                    StockModels.Chart.Result result = stockModels.getChart().getResult().get(0);

                    processStockData(result);
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<StockModels> call, Throwable t) {

            }
        });
    }

    private void processStockData(StockModels.Chart.Result result) {
        if (result != null) {
            double regularMarketPrice = result.getMeta().getRegularMarketPrice();
            double previousClose = result.getMeta().getChartPreviousClose();

            double changePercentage = ((regularMarketPrice - previousClose) / previousClose) * 100;

            txtStockPercent.setText(String.format(Locale.getDefault(), "%.2f", changePercentage));
            txtStockPrice.setText(String.format(Locale.getDefault(), "R$ %.2f", regularMarketPrice));

            if (changePercentage < 0) {
                txtStockPercent.setTextColor(Color.RED);
            } else if (changePercentage > 0) {
                txtStockPercent.setTextColor(Color.GREEN);
            } else {
            }
        }
    }


    private void setupRangeButtons(StockModels.Meta meta) {
        List<String> validRanges = meta.getValidRanges();
        int totalButtons = validRanges.size();

        addButton(selectedInterval1, meta.getGranularity(), 100, 15, true);

        for (int i = 0; i < totalButtons; i++) {
            String range = validRanges.get(i);
            String granularity = meta.getGranularity();

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
            fetchData(range, "1mo");
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
        apiClient.fetchChartData("PETR4.SA", range, granularity, new Callback<StockModels>() {
            @Override
            public void onResponse(@NonNull Call<StockModels> call, @NonNull retrofit2.Response<StockModels> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StockModels stockModels = response.body();
                    StockModels.Chart.Result result = stockModels.getChart().getResult().get(0);
                    StockModels.Meta meta = result.getMeta();
                    updateChart(stockModels);
                    setupRangeButtons(meta);
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<StockModels> call, Throwable t) {

            }
        });
    }

    private void updateChart(StockModels stockModels) {
        if (stockModels != null && stockModels.getChart() != null) {
            List<StockModels.Chart.Result> results = stockModels.getChart().getResult();

            if (results != null && !results.isEmpty()) {
                StockModels.Chart.Result result = results.get(0);

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

                LineData lineData = new LineData(dataSet);

                LineChart lineChart = findViewById(R.id.lineChart);
                lineChart.setData(lineData);

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
                        return "R$ " + String.format(Locale.getDefault(), "%.2f", value);
                    }
                });

                YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setEnabled(false);

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
