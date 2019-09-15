package com.example.smartbuildingsystem;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(Context context) {

        super(context, "SQLite_Store_User_Detail", null, 1, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table user_detail(serial_number INTEGER  PRIMARY KEY AUTOINCREMENT, user_id INTEGER   , user_type INTEGER , user_name text ,  email_address text , user_password text , user_contact text, user_status INTEGER , location_name text )");
        Log.d("Database Helper ", "Table  Created ........");



        db.execSQL("Create table storeage_detail(data_id INTEGER  PRIMARY KEY AUTOINCREMENT   , temperature REAL , humidity real ,data_time  TEXT  )");
        Log.d("Database Helper ", "Storage Table  Created  for Temperature and Humidity........");


        db.execSQL("Create table device_detail(device_id INTEGER  PRIMARY KEY AUTOINCREMENT   , device_name text , device_status integer , device_on_off_status Integer , device_on_command text,device_off_command text )" );
        Log.d("Database Helper ", "Device Table  Created  for Device Management...................................");

        db.execSQL("Create table schedule_detail(serial INTEGER  PRIMARY KEY AUTOINCREMENT, schedule_id INTEGER    , device_id integer , time_before text , time_after text , schedule_action integer,schedule_status integer, time_before_matched integer, time_after_matched integer)");
        Log.d("Database Helper ", "schedule_detail Table  Created  for Schedule Management...................................");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop  table if exists user_detail");
        db.execSQL("drop  table if exists storeage_detail");
    }
    //    public void delete(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete("user_detail", null, null);
//    }
    //insertion in database
    public  void insert (Integer user_id , Integer user_type ,String user_name , String email_address , String user_password , String user_contact , Integer user_status , String location_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues  = new ContentValues();
        contentValues.put("user_id", user_id);
        contentValues.put("user_type", user_type);
        contentValues.put("user_name", user_name);
        contentValues.put("email_address", email_address);
        contentValues.put("user_password", user_password);
        contentValues.put("user_contact", user_contact);
        contentValues.put("user_status", user_status);
        contentValues.put("location_name",location_name);

        long ins = db.insert("user_detail" , null , contentValues);
        Log.d("Database Helper ", " Data Inserted ........"+ user_id + "     "  +  user_type+ "     " + user_name + "     "+
                email_address+ "     " + user_password + "     "+ user_contact + "     "+ user_status);
        if (ins == -1)
            Log.d(" sqlite ", " contain nothing................................................ ");
        else

            Log.d(" sqlite ", " have data .........................................");
    }
    // check it either it already exist or not
    public  Boolean check_email(String email){
        SQLiteDatabase  db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from user_detail where email_address = ?",new String[]{email});
        Log.d("Database Helper ", "Check Email  ........");
        if (cursor.getCount()>0) return false;
        else return true;

    }
    public Cursor alldata(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from user_detail ", null);
        Log.d("Database Helper ", "All Data ........");
        return cursor;
    }
    public void delete_user_detail_data(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from user_detail");
        Log.d(" DataBaseHelper ", " SQLite data have been deleted ............................................................... ");
    }

    public boolean update_password(Integer id,String new_password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d(" DataBaseHelper ", " Update Password Called  ................................................ "+id +"   "+new_password);
        contentValues.put("user_id",id);
        contentValues.put("user_password",new_password);
        db.update("user_detail", contentValues, "user_id=?", new String[]{id.toString()});
        Log.d(" DataBaseHelper ", " Update Password  ............................................................... ");

        return true;
    }

    public boolean update_ph_name_profile_user_detail(Integer id,String user_name, String user_contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d(" DataBaseHelper ", " Update Password Called  ................................................ "+id +"   "+user_name+ "    "+user_contact);
        contentValues.put("user_id",id);
        contentValues.put("user_name",user_name);
        contentValues.put("user_contact",user_contact);
        db.update("user_detail", contentValues, "user_id=?", new String[]{id.toString()});
        Log.d(" DataBaseHelper ", " Update Password  ............................................................... ");

        return true;
    }


    // For Temperature and Humidity Storage Management
    public  void insert_store_age_detail (int  data_id    , float temperature  ,float  humidity ,String data_time   ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues  = new ContentValues();
        contentValues.put("data_id", data_id);
        contentValues.put("temperature", temperature);
        contentValues.put("humidity", humidity);
        contentValues.put("data_time", data_time);
        long ins = db.insert("storeage_detail" , null , contentValues);
        Log.d("Database Helper ", " Data Inserted ........"+ data_id + "     "  +  temperature+ "     " + humidity + "     "+ data_time);
        if (ins == -1)
            Log.d(" sqlite ", " contain nothing    storeage_detail............................................... ");
        else

            Log.d(" sqlite ", " have data storeage_detail ........................................."+data_id+".."+temperature+".."+humidity+".."+data_time);
    }
    public Cursor all_data_of_store_age_table(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from storeage_detail ", null);
        //Log.d("Database Helper ", "All Data from Storage Table ........");
        return cursor;
    }


    public void delete_storage_detail_data(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from storeage_detail");
        Log.d(" DataBaseHelper ", " SQLite data have been deleted ............................................................... ");
    }


    ////  Device Management , Home page
    public  void insert_device_detail (int  device_id    , String device_name  ,int device_status  ,int device_on_off_status, String device_on_command,String device_off_command     ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues  = new ContentValues();
        contentValues.put("device_id", device_id);
        contentValues.put("device_name", device_name);
        contentValues.put("device_on_command", device_on_command);
        contentValues.put("device_off_command", device_off_command);
        contentValues.put("device_on_off_status", device_on_off_status);
        contentValues.put("device_status", device_status);
        long ins = db.insert("device_detail" , null , contentValues);
        Log.d("Database Helper ", " Data Inserted ........"+ device_id + "     "  +  device_name+ "     " + device_status+ "     " + device_on_off_status+ "     " + device_on_command + "     "+ device_off_command);
        if (ins == -1)
            Log.d(" sqlite ", " contain nothing    device_detail............................................... ");
        else

            Log.d(" sqlite ", " have data device_detail .........................................");
    }
    public Cursor all_data_of_device_detail_table(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from device_detail ", null);
        Log.d("Database Helper ", "All Data from device Table ........");
        return cursor;
    }

    public void update_device_on_off_status_detail_table(Integer device_id,Integer device_on_off_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d(" DataBaseHelper ", " Update Password Called  ................................................ "+device_id +"   "+device_on_off_status);
        contentValues.put("device_id",device_id);
        contentValues.put("device_on_off_status",device_on_off_status);
        db.update("device_detail", contentValues, "device_id=?", new String[]{device_id.toString()});
        Log.d(" DataBaseHelper ", " Update Device Status   ............................................................... "+device_id+"......"+device_on_off_status );
    }

    public void update_device_status_detail_table(Integer device_id,Integer device_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("device_id",device_id);
        contentValues.put("device_status",device_status);
        db.update("device_detail", contentValues, "device_id=?", new String[]{device_id.toString()});
        Log.d(" DataBaseHelper ", " Update Device Status   ............................................................... "+device_id+"......"+device_status );
    }

    public void delete_device_detail_data(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from device_detail");
        Log.d(" DataBaseHelper ", " SQLite data for device detail  have been deleted ............................................................... ");
    }

//

    public Cursor fetch_device_on_status_detail_table(Integer device_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Database Helper ", "device on command ........" +device_id);

        Cursor cursor = db.rawQuery("select device_id, device_on_command from device_detail where device_id = ? ", new String[]{device_id.toString()});
        //Log.d("Database Helper ", "device on command ........" +cursor.getString(1));
        return cursor;
    }

////// Schedule Management
public  void insert_schedule_detail (Integer schedule_id  ,Integer device_id  ,String time_before  ,String time_after  ,Integer schedule_action ,Integer schedule_status ){
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues  = new ContentValues();
    contentValues.put("schedule_id", schedule_id);
    contentValues.put("device_id", device_id);
    contentValues.put("time_before", time_before);
    contentValues.put("time_after", time_after);
    contentValues.put("schedule_action", schedule_action);
    contentValues.put("schedule_status", schedule_status);
    contentValues.put("time_before_matched", 0);
    contentValues.put("time_after_matched", 0);

    long ins = db.insert("schedule_detail" , null , contentValues);
    Log.d("Database Helper ", " Data Inserted ........"+ schedule_id + "     "  +  device_id+ "     " + time_before + "     "+
            time_after+ "     " + schedule_action + "     "+ schedule_status+ "     " + 0+ "     "+
            0);
    if (ins == -1)
        Log.d(" sqlite ", " schedule_detail contain nothing................................................ ");
    else

        Log.d(" sqlite ", " schedule_detail  have data .........................................");
}




    public Cursor all_data_of_schedule_detail_table(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from schedule_detail ", null);
        Log.d("Database Helper ", "All Data from schedule_detail Table ........");
        return cursor;
    }

    public void delete_all_schedule_detail_data(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from schedule_detail");
        Log.d(" DataBaseHelper ", " SQLite data for schedule_detail  have been deleted ............................................................... ");
    }
    public void Update_Schedule_device_action_status(Integer device_id,Integer schedule_action) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("device_id",device_id);
        contentValues.put("schedule_action",schedule_action);
        db.update("schedule_detail", contentValues, "device_id=?", new String[]{device_id.toString()});
        Log.d(" Schedule_Update ", " Update Schedule Device Status   ............................................................... "+device_id+"......"+schedule_action );
    }
    public void delete_schedule_data_DeviceID(String device_id ){
        SQLiteDatabase db = this.getWritableDatabase();
        long ins = db.delete("schedule_detail", "device_id=?", new String[]{device_id});
        //Log.d(" DataBaseHelper ", " just one schedule deleted ............................................................... ");
        if (ins == -1)
            Log.d(" sqlite ", " schedule_detail  not deleted................................................ ");
        else

            Log.d(" sqlite ", " schedule_detail  data deleted on basis of User id and Deviceid .........................................");
    }
    // if time before matched then set it to 1
    public void Update_Schedule_device_time_before_match_status(Integer device_id,Integer time_before_matched) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("device_id",device_id);
        contentValues.put("time_before_matched",time_before_matched);
        db.update("schedule_detail", contentValues, "device_id=?", new String[]{device_id.toString()});
        Log.d(" Schedule_Update ", " Update Schedule Device Status   time before  ............................................................... "+device_id+"......"+time_before_matched );
    }
    public void Update_Schedule_device_time_after_match_status(Integer device_id,Integer time_after_matched) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("device_id",device_id);
        contentValues.put("time_after_matched",time_after_matched);
        db.update("schedule_detail", contentValues, "device_id=?", new String[]{device_id.toString()});
        Log.d(" Schedule_Update ", " Update Schedule Device Status  Time after ............................................................... "+device_id+"......"+time_after_matched );
    }
}
