package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * Created by PallaviNishanth on 4/20/17.
 */

public class StockChartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String EXTRA_SYMBOL = "e_symbol";
    private static final String LOG_TAG = StockChartActivity.class.getSimpleName();
    String Stock_symbol;
    private static Uri sUri;
    private static final int STOCK_LOADER_CHART = 1;

    @BindView(R.id.chart)
    LineChart chart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_layout);

        Log.d(LOG_TAG, "StockChartActivity Created");
        ButterKnife.bind(this);
        Stock_symbol = getIntent().getStringExtra(EXTRA_SYMBOL);
        setTitle(Stock_symbol);
        sUri = Quote.makeUriForStock(Stock_symbol);
        Timber.d("StockChartActivity" + sUri);
        getSupportLoaderManager().initLoader(STOCK_LOADER_CHART, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, sUri, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()){

            String history = data.getString(data.getColumnIndex(Quote.COLUMN_HISTORY));

            //Timber.d("StockChartActivity History" + history);
            ArrayList<String> historyDate = new ArrayList<>(); // Array data of historical dates
            ArrayList<Float> historyPrice = new ArrayList<>();

            if (null != history) {
                String[] str = history.split("\\r?\\n|,");
                String pattern = "MM/yyyy";
                for (int i = 0; i < str.length - 1; i++) {
                    if (i % 2 == 0) {
                        long dateInMilliseconds = Long.parseLong(str[i]);
                        historyDate.add((ChangeDateFormat(dateInMilliseconds, pattern)));

                    } else {
                        historyPrice.add(Float.valueOf(str[i]));


                    }
                }
            }
            plotGraph(historyDate, historyPrice);
        }

    }

    void plotGraph(ArrayList<String> dates, ArrayList<Float> prices) {

        List<Entry> entries = new ArrayList<Entry>();

        final String[] datesArray = dates.toArray(new String[dates.size()]);

        for (int i = 0; i < prices.size(); i++) {
            // turn your data into Entry objects
            entries.add(new Entry(i, prices.get(i)));
        }
        Collections.sort(entries, new EntryXComparator());
        LineDataSet stockPrices = new LineDataSet(entries, Stock_symbol); // add entries to dataset
        stockPrices.setColor(R.color.white);
        stockPrices.setLineWidth(3f);
        stockPrices.setDrawFilled(true);
        XAxis xAxis = chart.getXAxis();
        YAxis left = chart.getAxisLeft();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return datesArray[(int) value];
            }
        });

        //stockPrices.setValueTextColor(R.color.colorAccent);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(stockPrices);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.getAxisRight().setEnabled(false); // no right axis
        chart.setBackgroundColor(Color.WHITE);
        chart.setBorderColor(Color.BLACK);
        chart.setDescription(null);
        chart.invalidate();

    }

    public static String ChangeDateFormat(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(milliSeconds));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
