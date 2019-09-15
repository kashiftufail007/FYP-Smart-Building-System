package com.example.smartbuildingsystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity {

    private LinearLayout linear_page_Button_generation_layout , linear_page_check_box_generation;
    private float x1, x2, y1, y2;
    private int button_generated=0;
    private DataBaseHelper SQLite_DATABASE_OBJECT;
    private static  DataBaseHelper SQLite_DATABASE_OBJECT_FOR_SCHEDULES,SQLite_DATABASE_OBJECT_for_STORAGE;
    private boolean time_matched = false ;
    private RequestQueue requestQueue;
    private String GET_User_Temperature_Humidity_detail_on_basis_of_user_id= "http://10.0.2.2:3000/GET_User_Temperature_Humidity_detail_on_basis_of_user_id";
    private String POST_Delete_Schedule_on_Basis_UserID_DeviceID= "http://10.0.2.2:3000/Delete_schedule_using_device_user_id";
    private Calendar calender, calender2;
    private int Total_buttons_generated=0;
    private SimpleDateFormat simpledateformat,simpledateformat2;
    private final static int SEND_SMS_PERMISSION_REQUEST_CODE=111;
    private String device_name = null, Status = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        SQLite_DATABASE_OBJECT = new DataBaseHelper(HomePageActivity.this);
        SQLite_DATABASE_OBJECT_FOR_SCHEDULES = new DataBaseHelper(HomePageActivity.this);
        SQLite_DATABASE_OBJECT_for_STORAGE = new DataBaseHelper(HomePageActivity.this);

        linear_page_Button_generation_layout = (LinearLayout) findViewById(R.id.linear_page_Button_generation_layout);
        linear_page_check_box_generation = (LinearLayout) findViewById(R.id.linear_page_check_box_generation);
        Cursor cursor = SQLite_DATABASE_OBJECT.all_data_of_device_detail_table();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        /// To get system time

//        for (int progress = 0; progress < 3; progress += 1) {
//            try {
//                Thread.sleep(100);
//
              Generating_Buttons_fun();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

           if(button_generated!= LoginActivity.Total_devices){

               linear_page_Button_generation_layout.removeAllViews();
               linear_page_check_box_generation.removeAllViews();
                Generating_Buttons_fun();
        }

           // to move to other activities
        Button Schedule = (Button)findViewById(R.id.schedule);
        Schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, SchedulePageActivity.class);
                startActivity(intent);
            }
        });

        Button Storage = (Button)findViewById(R.id.chart);
        Storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, StoragePageActivity.class);
                startActivity(intent);
            }
        });
        Button Profile = (Button)findViewById(R.id.profile);
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, ProfilePageActivity.class);
                startActivity(intent);
            }
        });

        TimeManagement_Notifications process = new TimeManagement_Notifications();
        process.execute();

    }

    // to swap left right
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 > x2) {
                    Intent i = new Intent(HomePageActivity.this, SchedulePageActivity.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }

    // function to generate buttons
    @SuppressLint("ResourceType")
    private void Generating_Buttons_fun(){

        Cursor cursor = SQLite_DATABASE_OBJECT.all_data_of_device_detail_table();
        if (cursor.getCount() == 0) {
            Toast.makeText(HomePageActivity.this, "Unable to Fetch Data ", Toast.LENGTH_SHORT).show();

        } else if (cursor.moveToFirst()) {

            // global after the declaration of your class
            for (int i = 0; i < cursor.getCount(); i++) {
                int profile_send_extra = 0;
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    profile_send_extra = bundle.getInt("Device_management");
                }
                if (cursor.getInt(2) == 1 && profile_send_extra==0) {
                    Button_Generation_fun(profile_send_extra , cursor);
                    cursor.moveToNext();

                }else if (profile_send_extra==1 ) {
                    Button_Generation_fun(profile_send_extra, cursor);
                    cursor.moveToNext();
                }else if (cursor.getInt(2) == 0){
                  cursor.moveToNext();
                }

            }
        }

    }

    private void Button_Generation_fun(int profile_send_extra , final Cursor cursor){

        final CheckBox checkBox = new CheckBox(this);
        final Button btn = new Button(this);
        checkBox.setId(cursor.getInt(0));
        checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4caf50")));
        LinearLayout.LayoutParams rel_button2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rel_button2.setMargins(100, 0, 0, 215);
        checkBox.setLayoutParams(rel_button2);
       // Log.d("Checkbox ", " ......................................." + cursor.getInt(2));


        if (profile_send_extra == 1) {
            checkBox.setVisibility(View.VISIBLE);
            if (cursor.getInt(2) == 1) {
                Log.d("Checkbox ", " ...................................." + cursor.getInt(2));
                checkBox.setChecked(true);
            }

        } else if (profile_send_extra == 0) {
            checkBox.setVisibility(View.INVISIBLE);
        }

        linear_page_check_box_generation.addView(checkBox);

        button_generated++;
        Total_buttons_generated++;
        btn.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn.setId(cursor.getInt(0));
        btn.setTextSize(20);
        LinearLayout.LayoutParams rel_button1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300);
        rel_button1.setMargins(60, 20, 0, 10);
        btn.setLayoutParams(rel_button1);
        btn.setGravity(Gravity.CENTER);
        btn.setText(cursor.getString(1));

        // for button color on basis of its status
        Log.d("Home Color ", " ......................................." +cursor.getString(1)+"....."+ cursor.getInt(3));
        if ( cursor.getInt(3) == 1 ){
            btn.setBackgroundResource(R.drawable.home_background_stroke);
            btn.setTextColor(Color.parseColor("#f44336"));

        }else{
            btn.setBackgroundResource(R.drawable.storage_background_stroke);
            btn.setTextColor(Color.parseColor("#4caf50"));
        }



        // Give Button Colors on basis of Device Status
        if (cursor.getInt(2) == 1 && profile_send_extra == 1) {
            // button enabled
            btn.setTextColor(Color.parseColor("#4caf50"));
            btn.setBackgroundResource(R.drawable.storage_background_stroke);
        }else if (cursor.getInt(2) == 0 && profile_send_extra == 1){
            // button after disabled
            btn.setBackgroundResource(R.drawable.disable_button_background_stroke);
            btn.setTextColor(Color.parseColor("#e0e0e0"));
        }
        btn.setMarqueeRepeatLimit(11);
        btn.setOnClickListener(new View.OnClickListener() {
                Boolean isClickedDummy = true;

            public void onClick(View v) {

                if (isClickedDummy) {
                    // button color will be changed
                    v.setBackgroundResource(R.drawable.storage_background_stroke);
                    btn.setTextColor(Color.parseColor("#4caf50"));
                     isClickedDummy = false;
                    Cursor cursor_on = SQLite_DATABASE_OBJECT.all_data_of_device_detail_table();
                    String msg= "";
                    String device_name = "";
                    cursor_on.moveToFirst();
                    int device_off_status = 0;
                    for (int i=0 ; i< cursor_on.getCount() ; i++) {
                        //Log.d("Home Command ", " ......................................." +cursor_on.getString(4));

                        if (cursor_on.getInt(0) == btn.getId()) {
                            msg = cursor_on.getString(5);
                            Log.d("Home Command ", " ......................................." +cursor_on.getString(5));
                            device_name=cursor_on.getString(1);
                            if (cursor_on.getInt(3)==1){
                                device_off_status=1;
                            }
                        }
                        cursor_on.moveToNext();
                    }
                    if (device_off_status  == 1) {
                        String phoneNumber = "+923024789833";
                        if (!TextUtils.isEmpty(msg) && !TextUtils.isEmpty(phoneNumber)) {
                            if (checkPermission(Manifest.permission.SEND_SMS)) {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
                                SQLite_DATABASE_OBJECT.update_device_on_off_status_detail_table(btn.getId(), 0);

                                Toast.makeText(HomePageActivity.this, device_name + " is OFF now", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(HomePageActivity.this, "No Permission Granted!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    v.setBackgroundResource(R.drawable.home_background_stroke);
                    btn.setTextColor(Color.parseColor("#f44336"));
                    isClickedDummy = true;
                    Cursor cursor_on = SQLite_DATABASE_OBJECT.all_data_of_device_detail_table();
                    String msg= "";
                    String device_name = "";
                    cursor_on.moveToFirst();

                    for (int i=0 ; i< cursor_on.getCount() ; i++) {
                        //Log.d("Home Command ", " ......................................." +cursor_on.getString(4));

                        if (cursor_on.getInt(0) == btn.getId()) {
                            msg = cursor_on.getString(4);
                            device_name = cursor_on.getString(1);
                            Log.d("Home Command ", " ......................................." +cursor_on.getString(4));
                        }
                        cursor_on.moveToNext();
                    }
                    String phoneNumber ="+923024789833";
                    if (!TextUtils.isEmpty(msg) && !TextUtils.isEmpty(phoneNumber)) {
                        if (checkPermission(Manifest.permission.SEND_SMS)) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
                            SQLite_DATABASE_OBJECT.update_device_on_off_status_detail_table(btn.getId(), 1);

                            Toast.makeText(HomePageActivity.this,device_name+" is ON now", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(HomePageActivity.this, "No Permission Granted!", Toast.LENGTH_SHORT).show();
                        }
                    }
//                    Intent intent = new Intent(HomePageActivity.this , HomePageActivity.class);
//                    startActivity(intent);

                }
            }
        });

        linear_page_Button_generation_layout.addView(btn);
        // check box , when device will be disabled
        checkBox.setOnClickListener(new View.OnClickListener() {

        // checkbox Button color change , update in SQLite and Refresh Activity
            public void onClick(View v) {

                if (!checkBox.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
                    builder.setTitle("Disable Device !");
                    builder.setMessage("\n If you Disable this device, Schedule on this  will be Delete !");
                    builder.setIcon(R.drawable.ic_sad);
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    btn.setBackgroundResource(R.drawable.disable_button_background_stroke);
                                    btn.setTextColor(Color.parseColor("#e0e0e0"));
                                    Cursor cursor_user_data = SQLite_DATABASE_OBJECT.alldata();
                                    cursor_user_data.moveToFirst();
                                    Delete_Schedule_POST_Fun(cursor_user_data.getInt(1),checkBox.getId());
                                   // SQLite_DATABASE_OBJECT_FOR_SCHEDULES.delete_schedule_data_DeviceID(String.valueOf(checkBox.getId()));
                                    SQLite_DATABASE_OBJECT.update_device_status_detail_table(checkBox.getId(), 0);
                                    Intent intent = new Intent(HomePageActivity.this, HomePageActivity.class);
                                    startActivity(intent);

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // User clicked the No button
                                    Toast.makeText(getApplicationContext(),
                                            "You clicked Cancel button.", Toast.LENGTH_SHORT).show();
                                    checkBox.setChecked(true);
                                    break;

//                                case DialogInterface.BUTTON_NEUTRAL:
//                                    // Neutral/Cancel button clicked
//                                    Toast.makeText(getApplicationContext(),
//                                            "You clicked Cancel button.", Toast.LENGTH_SHORT).show();
//                                    break;
                            }
                        }
                    };
                    builder.setPositiveButton("OK",dialogClickListener );
                    builder.setNegativeButton("CANCEL", dialogClickListener);
                    builder.show();


                } else {

                    btn.setBackgroundResource(R.drawable.storage_background_stroke);
                    btn.setTextColor(Color.parseColor("#4caf50"));
                    SQLite_DATABASE_OBJECT.update_device_status_detail_table(checkBox.getId(), 1);
                    Intent intent = new Intent(HomePageActivity.this, HomePageActivity.class);
                    startActivity(intent);

                }
            }
        });

    }



    private final void Notify_on_Schedule_activated(String device_name , String Status){

        Log.d("NOTIFICATION ", "Notification IS...................................................................");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "Schedule_Notify";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample To Display Schedules Notification when Schedule Execute ");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_timer_black_24dp)
                .setTicker("Schedule")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("SCHEDULE")
                .setContentText("   Device  "+device_name+"  is   "+Status+"  now ")
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setColor(ContextCompat.getColor(this, R.color.blue_inactive));
            // .setContentInfo("Information");
            notificationManager.notify(1, notificationBuilder.build());

    }


    private void Fun_Compare_System_time_SQLite_Time(String System_time){
        Cursor cursor_time = SQLite_DATABASE_OBJECT_FOR_SCHEDULES.all_data_of_schedule_detail_table();
        String Command="" ;
        if(cursor_time.getCount()!=0){
            cursor_time.moveToFirst();
            for (int i=0 ; i<cursor_time.getCount() ; i++){
                Log.d(" System Time  ", " ............................................................... " + System_time.toString()+"........."+cursor_time.getString(3));

                if ( time_matched==false  &&  System_time.toString().equals( cursor_time.getString(3))  || System_time.toString().equals( cursor_time.getString(4) )){

                    //Log.d(" System Time  ", " Matching Time............................................................... " + System_time.toString()+"........."+cursor_time.getString(3));

                    time_matched=true;
                    Cursor cursor_device = SQLite_DATABASE_OBJECT_FOR_SCHEDULES.all_data_of_device_detail_table();

                    if (cursor_time.getInt(5) == 1 ) {

                        SQLite_DATABASE_OBJECT_FOR_SCHEDULES.update_device_on_off_status_detail_table(cursor_time.getInt(2), 1);

                        Status = "ON";

                        cursor_device.moveToFirst();
                        for (int j= 0 ; j <cursor_device.getCount() ; j++){

                            if (cursor_time.getInt(2) == cursor_device.getInt(0)){
                                device_name =cursor_device.getString(1);
                                Command = cursor_device.getString(4);
                            }
                            cursor_device.moveToNext();
                        }
                        SQLite_DATABASE_OBJECT_FOR_SCHEDULES.Update_Schedule_device_action_status(cursor_time.getInt(2), 0);
                    }else {
                        SQLite_DATABASE_OBJECT_FOR_SCHEDULES.update_device_on_off_status_detail_table(cursor_time.getInt(2), 0);
                        SQLite_DATABASE_OBJECT_FOR_SCHEDULES.Update_Schedule_device_action_status(cursor_time.getInt(2), 1);
                        Status = "OFF";

                        cursor_device.moveToFirst();
                        for (int j= 0 ; j <cursor_device.getCount() ; j++){

                            if (cursor_time.getInt(2) == cursor_device.getInt(0)){
                                device_name =cursor_device.getString(1);
                                Command = cursor_device.getString(5);
                            }
                            cursor_device.moveToNext();
                        }

                    }

                    Notify_on_Schedule_activated(device_name , Status);
                    SendMessagetoNumberonBasisofStatus(Command);
                    Log.d(" Command  ", " ............................................................... " + Command);


                    if (System_time.toString().equals( cursor_time.getString(4) )){
                        /// to delete schedule
                       // SQLite_DATABASE_OBJECT_FOR_SCHEDULES.delete_schedule_data_UserID_DeviceID(cursor_time.getInt(1));

                        Log.d(" Delete Schedule  ", " Schedule deleted ............................................................... ");

                    }
                }

                if (System_time.toString().equals( cursor_time.getString(3))){
                    SQLite_DATABASE_OBJECT.Update_Schedule_device_time_before_match_status(cursor_time.getInt(2),1);
                }else if (cursor_time.getInt(7)==1 &&  System_time.toString().equals( cursor_time.getString(4) )){
                    SQLite_DATABASE_OBJECT_FOR_SCHEDULES.Update_Schedule_device_time_after_match_status(cursor_time.getInt(2),1);
                }
                cursor_time.moveToNext();
            }

        }
    }


    // run time compare in background , start only when home activity open
    private   class TimeManagement_Notifications extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            for (int progress = 0; progress < 30000; progress += 1) {
                try {
                    Thread.sleep(10000);
                    String System_time = "";

                    calender2 = Calendar.getInstance();
                    simpledateformat2 = new SimpleDateFormat("HH:mm");
                    System_time = simpledateformat2.format(calender2.getTime());

                    ///   function defined at 443
                    Fun_Compare_System_time_SQLite_Time(System_time);
                    Log.d(" System Time  ", " ............................................................... " + System_time.toString());
                    if (time_matched == true) {
                        Log.d(" Thread ", " Thread Sleep Long............................................................... ");
                        Thread.sleep(59000);
                    }
                    Fetch_data_from_SQL();
                    time_matched = false;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
        @Override
        protected void onPostExecute (Void aVoid){
            super.onPostExecute(aVoid);

        }
    }

    private void Fetch_data_from_SQL() {

        try {
            URL url2 = new URL(GET_User_Temperature_Humidity_detail_on_basis_of_user_id);
            HttpURLConnection httpURLConnection;
            httpURLConnection = (HttpURLConnection) url2.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //Log.d("Storage Activity ", "GET_User_Temperature_Humidity_detail_on_basis_of_user_id...........................");
            Log.d("Storage Activity ", "inside Storage Function...........................");
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
    }

    private void Delete_Schedule_POST_Fun(final int user_id , final int device_id){

        StringRequest request = new StringRequest(Request.Method.POST, POST_Delete_Schedule_on_Basis_UserID_DeviceID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("user_id",  String.valueOf(user_id));

                parameters.put("device_id", String.valueOf(device_id));
                Log.d("  Delete_ScheduleFn", "......... ................." +user_id+"......."+ device_id);

                return parameters;
            }
        };
        requestQueue.add(request);
        SQLite_DATABASE_OBJECT_FOR_SCHEDULES.delete_schedule_data_DeviceID(String.valueOf(device_id));
        Toast.makeText(getApplicationContext(), "Schedule have been Deleted", Toast.LENGTH_SHORT).show();
    }



    // sms send Manifest permissions
    private boolean checkPermission(String permission){
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        switch (requestCode)
        {
            case SEND_SMS_PERMISSION_REQUEST_CODE:
                if (grantResults.length>0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    linear_page_Button_generation_layout.setEnabled(true);

                }

                break;
        }
    }


    public void SendMessagetoNumberonBasisofStatus(String Command ){


        String msg= "";
        Log.d("HomePageActivity", "......... ................." +Command);


            msg = Command;
            String phoneNumber = "+923024789833";
            if (!TextUtils.isEmpty(msg) && !TextUtils.isEmpty(phoneNumber)) {
                if (checkPermission(Manifest.permission.SEND_SMS)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
                    //SQLite_DATABASE_OBJECT.update_device_on_off_status_detail_table(btn.getId(), 0);
                    Log.d("HomePageActivity", "......... .SEND................" +Command);


                } else {
                    Toast.makeText(HomePageActivity.this, "No Permission Granted!", Toast.LENGTH_SHORT).show();
                }
            }

    }
}
