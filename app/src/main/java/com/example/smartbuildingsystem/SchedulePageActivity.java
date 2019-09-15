package com.example.smartbuildingsystem;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulePageActivity extends AppCompatActivity {
    private float x1, x2, y1, y2;
    private Spinner spinner;
    private TextView Start_Time= null;
    private TextView End_Time = null;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private int currentHour=0;
    private int currentMint= 0 ;
    private String ampm=" ";
    private LinearLayout linear_page_for_schedule_add;
    private int selectedItemTextDropDown;
    private int starting_Hours=0,starting_Minuts=0;
    private int ending_Hours=0,ending_Minuts=0;
    private String starting_AmPm="";
    private String ending_AmPm="";
    private int  array_of_devices[]= new int[100];
    private int array_index=0;
    private RequestQueue requestQueue;
    private String  Add_Schedule_POST_API="http://10.0.2.2:3000/Add_Schedule_to_DB_API";
    private String POST_Delete_Schedule_on_Basis_UserID_DeviceID= "http://10.0.2.2:3000/Delete_schedule_using_device_user_id";
    private int schedule_status;
//   change these variable later according to login page/ user
    private int user_id = 0;
   // int device_id=3;
    private Calendar calender, calender2;
    private SimpleDateFormat simpledateformat,simpledateformat2;
    private String system_hour_time;
    private String system_mint_time;
    private String system_time;
    private static int Schedule_id=500;
    private static  DataBaseHelper SQLite_DATABASE_OBJECT_FOR_SCHEDULES,SQLite_DATABASE_OBJECT_for_STORAGE;
    private LinearLayout linear_page_for_schedule_delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_page);

        // initialize all objects
        linear_page_for_schedule_add = (LinearLayout) findViewById(R.id.linear_page_for_schedule_add);
        linear_page_for_schedule_delete = (LinearLayout) findViewById(R.id.linear_page_for_schedule_delete);
        calender2 = Calendar.getInstance();
        simpledateformat2 = new SimpleDateFormat("HH:mm");

                    SQLite_DATABASE_OBJECT_FOR_SCHEDULES = new DataBaseHelper(SchedulePageActivity.this);
                    SQLite_DATABASE_OBJECT_for_STORAGE = new DataBaseHelper(SchedulePageActivity.this);
                    final Cursor cursor = SQLite_DATABASE_OBJECT_FOR_SCHEDULES.alldata();

                    all_schedules_from_sqlite();
                    if (cursor.getCount() == 0) {
                        Toast.makeText(getApplicationContext(), "Unable to Add Schedules on basis of User", Toast.LENGTH_SHORT).show();
                    } else {
                        cursor.moveToFirst();
                        user_id = cursor.getInt(1);
                        Log.d("Schedule Activity ", "User ID Fetched ........" + user_id + "    " + cursor.getInt(1));

                    }


                    Start_Time = (TextView) findViewById(R.id.start_time_select_view);
                    End_Time = (TextView) findViewById(R.id.end_time_select_view);
                    Button Add = (Button) findViewById(R.id.add);
                    final Button device_status = (Button) findViewById(R.id.device_status);

                    calender = Calendar.getInstance();
                    simpledateformat = new SimpleDateFormat("HH");
                    system_hour_time = simpledateformat.format(calender.getTime());
                    simpledateformat = new SimpleDateFormat("mm");
                    system_mint_time = simpledateformat.format(calender.getTime());
                    system_time = system_hour_time + ":" + system_mint_time;


            //        fetch_schedules_from_DB();
                    // Display all data inside linear text display box like this
                    device_status.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (device_status.getText().toString().equals("ON")) {
                                schedule_status = 1;
                                device_status.setText("OFF");
                            } else {
                                schedule_status = 0;
                                device_status.setText("ON");
                            }
                        }
                    });

            //        Notify_on_Schedule_activated();
                    //this will send user id and then according to user id POST user id and GET Schedules related to it

                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    Add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //email validation checked here

                            if (starting_Hours == 0 && starting_Minuts == 0 || ending_Hours == 0 && ending_Minuts == 0) {
                                Toast.makeText(getApplicationContext(), "Select Time First", Toast.LENGTH_SHORT).show();

                            } else {
                                if (selectedItemTextDropDown == 0) {
                                    Toast.makeText(getApplicationContext(), "Select a Device First", Toast.LENGTH_SHORT).show();
                                } else {
                                    boolean flag = false;
                                    for (int i = 0; i < array_of_devices.length; i++) {
                                        if (selectedItemTextDropDown == array_of_devices[i]) {
                                            Toast.makeText(getApplicationContext(), " This Device Already Under Action, Change to other one", Toast.LENGTH_SHORT).show();
                                            flag = true;
                                        }
                                    }
                                    if (flag == false) {
                                        StringRequest request = new StringRequest(Request.Method.POST, Add_Schedule_POST_API, new Response.Listener<String>() {
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
                                                parameters.put("user_id", String.valueOf(user_id));
                                                parameters.put("device_id", String.valueOf(Integer.valueOf(selectedItemTextDropDown)));

                                                parameters.put("time_before",  + starting_Hours + ":" + starting_Minuts);

                                                parameters.put("time_after", ending_Hours + ":" + ending_Minuts);
                                                parameters.put("schedule_action", String.valueOf(schedule_status));
                                                String Starting_Hours_SQLite="0" , Starting_Mints_SQLite="0";
                                                    Starting_Hours_SQLite =String.valueOf(   starting_Hours);
                                                    Starting_Mints_SQLite = String.valueOf(starting_Minuts);
                                                    if (starting_Hours<10){
                                                        Starting_Hours_SQLite="0"+Starting_Hours_SQLite;
                                                    }
                                                    if (starting_Minuts<10){
                                                        Starting_Mints_SQLite = "0"+Starting_Mints_SQLite;
                                                    }
                                                String Ending_Hours_SQLite="0" , Ending_Mints_SQLite="0";


                                                    Ending_Hours_SQLite = String.valueOf( ending_Hours);
                                                    Ending_Mints_SQLite = String.valueOf(ending_Minuts);
                                                    if (ending_Hours<10){
                                                        Ending_Hours_SQLite = "0"+Ending_Hours_SQLite;
                                                    }
                                                    if(ending_Minuts <10){
                                                        Ending_Mints_SQLite="0"+Ending_Mints_SQLite;
                                                    }

                                                String Time_before = Starting_Hours_SQLite + ":" + Starting_Mints_SQLite;
                                                String Time_after = Ending_Hours_SQLite + ":" + Ending_Mints_SQLite;

                                                SQLite_DATABASE_OBJECT_FOR_SCHEDULES.insert_schedule_detail(Schedule_id, Integer.valueOf(selectedItemTextDropDown), Time_before, Time_after, schedule_status, 1);

                                                return parameters;
                                            }
                                        };
                                        requestQueue.add(request);
                                        array_of_devices[array_index] = selectedItemTextDropDown;
                                        array_index++;
                                        Schedule_id++;
            //                            fetch_Data_of_LAST_Schedules_ADDED_FromDataBase process_schedule_fetch = new fetch_Data_of_LAST_Schedules_ADDED_FromDataBase();
            //                            process_schedule_fetch.execute();
                                        Intent intent = new Intent(SchedulePageActivity.this, SchedulePageActivity.class);
                                        startActivity(intent);
                                        addItemsOnSpinner();
                                        Toast.makeText(getApplicationContext(), "Schedule have been Added", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });

                    //Get Spinner device and store it inside selecytedItemTex
                    spinner = (Spinner) findViewById(R.id.spinner);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            //   this to get id , on basis of device name to device id
                            Cursor cursor = SQLite_DATABASE_OBJECT_FOR_SCHEDULES.all_data_of_device_detail_table();
                            int device_id_drop_down = 0;
                            if (cursor.getCount() == 0) {
                                Toast.makeText(getApplicationContext(), "Unable to get device detail ", Toast.LENGTH_SHORT).show();
                            } else if (cursor.moveToFirst()) {
                                for (int i = 1; i <= cursor.getCount(); i++) {
                                    if (parent.getItemAtPosition(position).equals(cursor.getString(1).toString())) {
                                        Log.d("Schedule Add ", "..................................." + parent.getItemAtPosition(position) + "     " + cursor.getString(1).toString());

                                        device_id_drop_down = cursor.getInt(0);
                                    }
                                    cursor.moveToNext();
                                }
                            }
                            selectedItemTextDropDown = Integer.valueOf(device_id_drop_down);
                            //Start_Time.setText(selectedItemTextDropDown);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });


                    Start_Time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calendar = Calendar.getInstance();
                            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                            currentMint = calendar.get(Calendar.MINUTE);
                            timePickerDialog = new TimePickerDialog(SchedulePageActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    starting_Hours = hourOfDay;
                                   starting_Minuts = minute;

                                    starting_AmPm = ampm;
                                    Start_Time.setText(String.format("%02d:%02d", hourOfDay, minute) + ampm);
                                    // Start_Time.setText(system_hour_time+":"+system_mint_time);
                                }
                            }, currentHour, currentMint, false);
                            timePickerDialog.show();
                        }
                    });

                    End_Time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calendar = Calendar.getInstance();
                            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                            currentMint = calendar.get(Calendar.MINUTE);
                            timePickerDialog = new TimePickerDialog(SchedulePageActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            //                            if (hourOfDay>12 && hourOfDay<0){
            //                                ampm= "PM";
            //                            }else if(hourOfDay<12 && hourOfDay>0){
            //                                ampm= "AM";
            //                            }else if(hourOfDay==12){
            //                                ampm= "PM";
            //                                hourOfDay=12;
            //                            }else {
            //                                ampm= "AM";
            //                                hourOfDay=12;
            //                            }
                                    ending_Hours = hourOfDay;
                                    ending_Minuts = minute;
                                    if (starting_Hours >= ending_Hours && starting_Minuts >= ending_Minuts) {
                                        Toast.makeText(getApplicationContext(), "Ending Time should be different ", Toast.LENGTH_LONG).show();

                                    } else {
                                        ending_AmPm = ampm;
                                        End_Time.setText(String.format("%02d:%02d", hourOfDay, minute) + ampm);
                                        // End_Time.setText(hourOfDay+":"+minute);
                                    }
                                }
                            }, currentHour, currentMint, false);
                            timePickerDialog.show();
                        }
                    });

                    // On click on button a new text box will be added inside the linear layout
                    linear_page_for_schedule_add = (LinearLayout) findViewById(R.id.linear_page_for_schedule_add);

                    Button Home = (Button) findViewById(R.id.home);
                    Home.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            array_index=0;
                            Intent intent = new Intent(SchedulePageActivity.this, HomePageActivity.class);
                            startActivity(intent);
                        }
                    });
                    Button Storage = (Button) findViewById(R.id.chart);
                    Storage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SchedulePageActivity.this, StoragePageActivity.class);
                            array_index=0;
                            startActivity(intent);
                        }
                    });
                    Button Profile = (Button) findViewById(R.id.profile);
                    Profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SchedulePageActivity.this, ProfilePageActivity.class);
                            array_index=0;
                            startActivity(intent);
                        }
                    });


                    addItemsOnSpinner();
//        for (int progress = 0; progress < 30000; progress += 1) {
//            try {
//                   Thread.sleep(1000000);
//
//        System_time = simpledateformat2.format(calender2.getTime());
//
//        Log.d(" System Time  ", " ............................................................... " + System_time.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//       }

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
                if (x1 < x2) {
                    Intent i = new Intent(SchedulePageActivity.this, HomePageActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(SchedulePageActivity.this, StoragePageActivity.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }

//here we select devices
    private void addItemsOnSpinner() {


        List<String> list = new ArrayList<String>();
        list.add("Select Device");
        Cursor cursor = SQLite_DATABASE_OBJECT_FOR_SCHEDULES.all_data_of_device_detail_table();
        if (cursor.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Unable to Add Devices ", Toast.LENGTH_SHORT).show();
        }else if(cursor.moveToFirst()){
            for(int i=1 ; i<=cursor.getCount() ; i++ )
            {
                if (cursor.getInt(2)==1) {
                    boolean flag = false;
                    for (int j = 0; j < array_of_devices.length && j < array_index; j++) {
                        Log.d("Schedule Activity ", "Comparing already added devices ........     " + cursor.getString(0) + "...." + array_of_devices[j]);

                        if (cursor.getInt(0) == array_of_devices[j]) {
                            flag = true;

                        }
                    }
                    if (flag == false)
                        list.add(cursor.getString(1).toString());
                    Log.d("Schedule Activity ", "Devices added over here in Drop Down ........     " + cursor.getString(1));
                    cursor.moveToNext();
                }else{
                    cursor.moveToNext();
                }
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
//this is notification function

        private   void  all_schedules_from_sqlite(){
            Log.d("Schedule Activity ", "all_schedules_from_sqlite..........................");
            final Cursor cursor_for_schedule_detail = SQLite_DATABASE_OBJECT_FOR_SCHEDULES.all_data_of_schedule_detail_table();
            cursor_for_schedule_detail.moveToFirst();
            final Cursor cursor_for_device_detail = SQLite_DATABASE_OBJECT_FOR_SCHEDULES.all_data_of_device_detail_table();

            int i=0;
            int total_devices=0;

            for ( i=0 ; i<cursor_for_schedule_detail.getCount() ; i++ ) {



                final ImageView Img_view = new ImageView(this);
                Img_view.setId(cursor_for_schedule_detail.getInt(1));
                Img_view.setImageResource(R.drawable.ic_delete_black_24dp);
                LinearLayout.LayoutParams rel_button2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                rel_button2.setMargins(50, 50, 50, 370);
                Img_view.setLayoutParams(rel_button2);

                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                int  profile_send_extra = 0;
                if (bundle != null){
                    profile_send_extra = bundle.getInt("Schedule_management");
                }
                if (profile_send_extra ==1){
                    Img_view.setVisibility(View.VISIBLE);
                }else if (profile_send_extra==0){
                    Img_view.setVisibility(View.INVISIBLE);
                }
                linear_page_for_schedule_delete.addView(Img_view);



                //Log.d("Schedule Activity ", "all_schedules_from_sqlite.........loop called................."+i+"     "+cursor_for_schedule_detail.getInt(0)+"    "+cursor_for_schedule_detail.getInt(1)+"     "+cursor_for_schedule_detail.getString(2)+"     "+cursor_for_schedule_detail.getString(3));
                final TextView textview = new TextView(this);

                textview.setLayoutParams(new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textview.setId(cursor_for_schedule_detail.getInt(1));

                LinearLayout.LayoutParams rel_button1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                rel_button1.setMargins(50, 50, 50, 10);
                textview.setLayoutParams(rel_button1);

//               btn.setGravity(Gravity.CENTER);
                String Device_name = "0";
                cursor_for_device_detail.moveToFirst();
                for (int j = 0; j < cursor_for_device_detail.getCount(); j++) {
                    if (cursor_for_schedule_detail.getInt(2) == cursor_for_device_detail.getInt(0)) {
                        Device_name = cursor_for_device_detail.getString(1);
                        break;
                    }
                    cursor_for_device_detail.moveToNext();
                    // Log.d("Schedule Activity ", "Device Loop To get device name -------------------------");

                }
                // Log.d("Schedule Activity ", "Schedule Loop To get device name -------------------------");
                String device_on_off_type = "";
                if (cursor_for_schedule_detail.getInt(5) == 1) {
                    device_on_off_type = "ON";
                } else {
                    device_on_off_type = "OFF";
                }
                int Schedule_Number= i+1;
                Log.d(" Schedule Data", " got schedule data............................. .."+cursor_for_schedule_detail.getInt(0)+".."+cursor_for_schedule_detail.getInt(1)
                        +cursor_for_schedule_detail.getInt(2)+".."+cursor_for_schedule_detail.getString(3)
                        +cursor_for_schedule_detail.getString(4)+".."+cursor_for_schedule_detail.getInt(5)
                        +cursor_for_schedule_detail.getInt(6)+".."+cursor_for_schedule_detail.getInt(7)+".."+cursor_for_schedule_detail.getInt(8));

                String start_time =  cursor_for_schedule_detail.getString(3);
                if (cursor_for_schedule_detail.getInt(7)==1 &&cursor_for_schedule_detail.getInt(8)==0){

                    textview.setText("\n                   Schedule#  " + Schedule_Number + "\n   Device: " + Device_name + "   (" + device_on_off_type + ")" + "\n                       Start Time: " + start_time + " \n                       End Time:   " + cursor_for_schedule_detail.getString(4) + "        \n");
                    Log.d("  else if", "  time before matched , time after matched ............................. .."+cursor_for_schedule_detail.getInt(7)+".."+cursor_for_schedule_detail.getInt(8));

                    textview.setTextColor(Color.parseColor("#4caf50"));

                    textview.setBackgroundResource(R.drawable.schedule_disable_gray_stroke);
                    Log.d("   if", " time before matched , time after not ............................. .."+cursor_for_schedule_detail.getInt(7) +"...."+cursor_for_schedule_detail.getInt(8));

                }else if (cursor_for_schedule_detail.getInt(7)==1 && cursor_for_schedule_detail.getInt(8)==1){
                    textview.setText("\n                   Schedule#  " + Schedule_Number + "\n   Device: " + Device_name + "   (" + device_on_off_type + ")" + "\n                       Start Time: " + start_time + " \n                       End Time:   " + cursor_for_schedule_detail.getString(4) + "        \n");
                    Log.d("  else if", "  time before matched , time after matched ............................. .."+cursor_for_schedule_detail.getInt(7)+".."+cursor_for_schedule_detail.getInt(8));

                    textview.setTextColor(Color.parseColor("#9e9e9e"));
                    textview.setBackgroundResource(R.drawable.schedule_disable_gray_stroke);
                    SQLite_DATABASE_OBJECT_FOR_SCHEDULES.delete_schedule_data_DeviceID(String.valueOf(cursor_for_schedule_detail.getInt(2)));
                }else{
                    textview.setText("\n                   Schedule#  " + Schedule_Number + "\n   Device: " + Device_name + "   (" + device_on_off_type + ")" + "\n                       Start Time: " + start_time + " \n                       End Time:   " + cursor_for_schedule_detail.getString(4) + "        \n");
                    Log.d("  else ", " nothing matched  ............................. ..");

                    textview.setTextColor(Color.parseColor("#4caf50"));
                    textview.setBackgroundResource(R.drawable.storage_background_stroke);
                }
                textview.setTextSize(20);
                Schedule_id = cursor_for_schedule_detail.getInt(0);
                array_of_devices[total_devices] = cursor_for_schedule_detail.getInt(2);

                linear_page_for_schedule_add.addView(textview);
                for (int j = 0; j < array_of_devices.length && j < total_devices; j++) {
                    Log.d("  array_of_devices", "......... ................." + array_of_devices[j] + "....." + array_of_devices[array_index]);
                }




                Img_view.setOnClickListener(new View.OnClickListener() {
                    Boolean isClickedDummy = true;

                    public void onClick(View v) {

                        if (isClickedDummy) {
                            Img_view.setImageResource(R.drawable.ic_delete_red_24dp);
                            textview.setTextColor(Color.parseColor("#f44336"));
                            textview.setBackgroundResource(R.drawable.home_background_stroke);
                            String  device_id = null , user_id = null;
                            cursor_for_schedule_detail.moveToFirst();
                            final Cursor cursor_to_get_user_id = SQLite_DATABASE_OBJECT_FOR_SCHEDULES.alldata();
                            cursor_to_get_user_id.moveToFirst();
                            cursor_for_schedule_detail.moveToFirst();
                            for (int k =0 ; k < cursor_for_schedule_detail.getCount() ; k++){

                                if (cursor_for_schedule_detail.getInt(1)==Img_view.getId() ){
                                    Log.d("  Delete_Schedule", "matched......... ................." +cursor_for_schedule_detail.getString(2)+"......."+ cursor_to_get_user_id.getString(1));
                                        final int user_id_delete_schedule=Integer.valueOf(cursor_to_get_user_id.getString(1));
                                                final int device_id_delete_schedule =Integer.valueOf(cursor_for_schedule_detail.getString(2));
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SchedulePageActivity.this);
                                    builder.setTitle("Disable Device Schedule !");
                                    builder.setMessage("\n Are you sure to delete this Schedule !");
                                    builder.setIcon(R.drawable.ic_sad);
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    textview.setTextColor(Color.parseColor("#f44336"));
                                                    textview.setBackgroundResource(R.drawable.home_background_stroke);
                                                    Log.d("  Delete_Schedule", "matched......... ................." +user_id_delete_schedule+"......."+device_id_delete_schedule);

                                                    Delete_Schedule_POST_Fun(user_id_delete_schedule,device_id_delete_schedule);


                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // User clicked the No button
                                                    Toast.makeText(getApplicationContext(),
                                                            "You clicked Cancel button.", Toast.LENGTH_SHORT).show();

                                                    Img_view.setImageResource(R.drawable.ic_delete_black_24dp);
                                                    textview.setTextColor(Color.parseColor("#4caf50"));
                                                    textview.setBackgroundResource(R.drawable.storage_background_stroke);
                                                    break;
//                                case DialogInterface.BUTTON_NEUTRAL:
//                                    // Neutral/Cancel button clicked
//                                    Toast.makeText(getApplicationContext(),
//                                            "You clicked Cancel button.", Toast.LENGTH_SHORT).show();
//                                    break;
                                            }
                                        }
                                    };
                                    builder.setPositiveButton("YES",dialogClickListener );
                                    builder.setNegativeButton("CANCEL", dialogClickListener);
                                    builder.show();
                                }
                                cursor_for_schedule_detail.moveToNext();
                            }
                            isClickedDummy = false;
                        }
                    }
                });
                cursor_for_schedule_detail.moveToNext();
                total_devices++;
            }
            array_index = total_devices;
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

                        //                           fetch_Data_of_LAST_Schedules_ADDED_FromDataBase process_schedule_fetch = new fetch_Data_of_LAST_Schedules_ADDED_FromDataBase();
                                            //        process_schedule_fetch.execute();
                       Intent intent = new Intent(SchedulePageActivity.this, SchedulePageActivity.class);
                        startActivity(intent);
//                        addItemsOnSpinner();

                        SQLite_DATABASE_OBJECT_FOR_SCHEDULES.delete_schedule_data_DeviceID(String.valueOf(device_id));
                        Toast.makeText(getApplicationContext(), "Schedule have been Deleted", Toast.LENGTH_SHORT).show();
    }
}
