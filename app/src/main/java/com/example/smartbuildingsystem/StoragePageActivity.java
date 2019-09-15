package com.example.smartbuildingsystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class StoragePageActivity extends AppCompatActivity {
    private  float x1, x2, y1, y2;
    private LineChart lineChart;
    private final   float [] Temperature = new float[7];
    private final   float [] Humidity = new float[7];
    private final   String [] Time  = new String[7];
    private int Count_values=0;
    private TextView temperature_latest_value,humidity_live_value;

    private String GET_User_Temperature_Humidity_detail_on_basis_of_user_id= "http://10.0.2.2:3000/GET_User_Temperature_Humidity_detail_on_basis_of_user_id";
    private DataBaseHelper SQLite_DATABASE_OBJECT_for_STORAGE;
    private final int DELAY= 5000;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_page);

        SQLite_DATABASE_OBJECT_for_STORAGE = new DataBaseHelper(StoragePageActivity.this);
        temperature_latest_value = (TextView) findViewById(R.id.temperature_latest_value);
        humidity_live_value = (TextView) findViewById(R.id.humidity_live_value);
//        temperature_latest_value.setText((int) Temperature[6]);
////        humidity_live_value.setText((int) Humidity[6]);
//////line chart code

        for (int progress = 0; progress < 3; progress += 1) {
            try {

                Log.d("Storage Thread ", "Fetch_data_from_SQL..........................");

                Graphs_Generation_Data_to_ARRAYS();
                Graph_Generation_Perform();
                add_time();
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

            // for axis expanding
            //axisX.setGranularity(2);
//        axisX.setAxisLineColor(Color.GRAY);
//        axisX.setDrawGridLines(true);
//        axisX.setPosition(XAxis.XAxisPosition.TOP);


//        String[] values = new String[]{"jan ", "jan2 ","jan3 ","jan4 ","jan5 ","jan6 ","jan7 "};
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
//        xAxis.setGranularity(1f);

            Button Home = (Button) findViewById(R.id.home);
            Home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StoragePageActivity.this, HomePageActivity.class);
                    startActivity(intent);
                }
            });
            Button Schedule = (Button) findViewById(R.id.schedule);
            Schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StoragePageActivity.this, SchedulePageActivity.class);
                    startActivity(intent);
                }
            });
            Button Profile = (Button) findViewById(R.id.profile);
            Profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StoragePageActivity.this, ProfilePageActivity.class);
                    startActivity(intent);
                }
            });

    }

//    public class MyXAxisValueFormatter extends IndexAxisValueFormatter {
//
//        private String[] mValues;
//        public MyXAxisValueFormatter( String[] values){
//            this.mValues = values;
//        }
//
//        @Override
//        public String getFormattedValue(float value) {
//            return super.getFormattedValue(value);
//        }
//    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    Intent i = new Intent(StoragePageActivity.this, SchedulePageActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(StoragePageActivity.this, ProfilePageActivity.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }
    private class Fetch_data_from_SQL extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("Storage Activity ", "inside...........................");

            try {
                URL url2 = new URL(GET_User_Temperature_Humidity_detail_on_basis_of_user_id);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url2.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                //Log.d("Storage Activity ", "GET_User_Temperature_Humidity_detail_on_basis_of_user_id...........................");

                // Now to parse data from huge data
                JSONArray JA = new JSONArray(bufferedReader.readLine());
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JO = (JSONObject) JA.get(i);
                    SQLite_DATABASE_OBJECT_for_STORAGE.insert_store_age_detail(JO.getInt("th_id"),Float.valueOf(JO.getString("temperature_value")),Float.valueOf(JO.getString("humidity_value")), JO.getString("data_added_time"));
                    Log.d("Storage Activity ", "Fetched Date ..........................."+JO.getInt("th_id")+"      "+ Float.valueOf(JO.getString("temperature_value"))+"      "+Float.valueOf(JO.getString("humidity_value"))+"      "+ JO.getString("data_added_time"));

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }


    private   void Graphs_Generation_Data_to_ARRAYS (){
        Cursor cursor = SQLite_DATABASE_OBJECT_for_STORAGE.all_data_of_store_age_table();

        if (cursor.getCount() == 0) {
            //Toast.makeText(StoragePageActivity.this , "Empty Nothing in Storage Table ", Toast.LENGTH_SHORT).show();
            Log.d("Storage Activity ", "Empty Nothing is Storage table ...........................");
        } else if (cursor.moveToLast()){

            String  Temperature_value = cursor.getString(1);
            if(Temperature_value.length()!=4){
                Temperature_value = Temperature_value+".0";
            }
            String  Humidity_value = cursor.getString(2);
            if(Humidity_value.length()!=4){
                Humidity_value = Humidity_value+".0";
            }
            temperature_latest_value.setText(Temperature_value);
            humidity_live_value.setText(Humidity_value);

            for (int i=0 ; i<cursor.getCount() && i<7 ;i++){                             //cursor.getCount() to get all elements
                Temperature[6-i] = cursor.getFloat(1);
                Humidity[6-i] = cursor.getFloat(2);
                String Date_time_value = cursor.getString(3);
                String time = Date_time_value.substring(11,16);
//                Log.d("Fetch Storage ", "......................... "+Date_time_value+"................"+time);
                Time[6-i] =time;
                cursor.moveToPrevious();
                Count_values++;
//                Log.d(" sqlite ", " getting data    storage_detail....."+Temperature[i] +i + "         "+Count_values);

            }
        }
    }
    private   void Graph_Generation_Perform(){

        lineChart= (LineChart) findViewById(R.id.linechart);
    //        lineChart.setOnChartGestureListener((OnChartGestureListener) StoragePageActivity.this);
    //        lineChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) StoragePageActivity.this);
        lineChart.setDragEnabled(true);
        lineChart.setEnabled(false);

        ArrayList<Entry> yvalues=new ArrayList<>();

        yvalues.add(new Entry(0, Temperature[0]));
        yvalues.add(new Entry(1, Temperature[1]));
        yvalues.add(new Entry(2, Temperature[2]));
        yvalues.add(new Entry(3, Temperature[3]));
        yvalues.add(new Entry(4, Temperature[4]));
        yvalues.add(new Entry(5, Temperature[5]));
        yvalues.add(new Entry(6, Temperature[6]));
        LineDataSet set1 = new LineDataSet(yvalues , "");
        set1.setFillAlpha(110);
        set1.setColor(Color.parseColor("#ff5aff"));
        set1.setLineWidth(2.5f);
        set1.setDrawCircleHole(false);
        set1.setValueTextColor(Color.parseColor("#212121"));
        set1.setValueTextSize(15);
        set1.setCircleColor(Color.parseColor("#212121"));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data =  new LineData(dataSets);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setData(data);
        lineChart.getXAxis().setEnabled(false);
        YAxis yleftAxis = lineChart.getAxisLeft();
        yleftAxis.removeAllLimitLines();
        yleftAxis.setAxisMaximum(50f);
        yleftAxis.setAxisMinimum(20f);

        XAxis axisX = lineChart.getXAxis();
        axisX.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);




        LineChart lineChart2= (LineChart) findViewById(R.id.linechart2);
    //        lineChart.setOnChartGestureListener((OnChartGestureListener) StoragePageActivity.this);
    //        lineChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) StoragePageActivity.this);
        lineChart2.setDragEnabled(true);
        lineChart2.setEnabled(false);

        ArrayList<Entry> yvalues2=new ArrayList<>();

        yvalues2.add(new Entry(0, Humidity[0]));
        yvalues2.add(new Entry(1, Humidity[1]));
        yvalues2.add(new Entry(2, Humidity[2]));
        yvalues2.add(new Entry(3, Humidity[3]));
        yvalues2.add(new Entry(4, Humidity[4]));
        yvalues2.add(new Entry(5, Humidity[5]));
        yvalues2.add(new Entry(6, Humidity[6]));
        LineDataSet set2 = new LineDataSet(yvalues2 , "");
        set2.setFillAlpha(110);
        set2.setColor(Color.parseColor("#03a9f4"));
        set2.setLineWidth(2.5f);
        set2.setDrawCircleHole(false);
        set2.setValueTextColor(Color.parseColor("#212121"));
        set2.setValueTextSize(15);
        set2.setCircleColor(Color.parseColor("#212121"));
        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
        dataSets2.add(set2);
        LineData data2 =  new LineData(dataSets2);
        lineChart2.getAxisRight().setEnabled(false);
        lineChart2.setData(data2);
        lineChart2.getXAxis().setEnabled(false);
        YAxis yleftAxis2 = lineChart2.getAxisLeft();
        yleftAxis2.removeAllLimitLines();
        yleftAxis2.setAxisMaximum(100f);
        yleftAxis2.setAxisMinimum(40f);

        XAxis axisX2 = lineChart2.getXAxis();
        axisX2.setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    private   void add_time(){
        Log.d("Time Storage ", "Time Function............................................................ ");
        LinearLayout temperature_time = (LinearLayout) findViewById(R.id.temperature_time);

        for(int i=0; i<7;i++){          //looping to create 5 textviews

            final TextView btn = new TextView(this);

            btn.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setId(i);
            btn.setTextSize(12);

            btn.setGravity(Gravity.CENTER);

            btn.setText(Time[i]+"       ");                                  //inflating :)
            temperature_time.addView(btn);
           // Log.d("Time Storage ", "......................... "+Time[i]);
        }

        LinearLayout humidity_time = (LinearLayout) findViewById(R.id.humidity_time);
        for(int i=0; i<7;i++){          //looping to create 5 textviews

            final TextView btn = new TextView(this);

            btn.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setId(i);
            btn.setTextSize(12);

            btn.setGravity(Gravity.CENTER);
            Graphs_Generation_Data_to_ARRAYS();
            btn.setText(Time[i]+"        ");                                    //inflating :)
            humidity_time.addView(btn);
            //Log.d("Time Storage ", "......................... "+Time[i]);
        }
    }
}