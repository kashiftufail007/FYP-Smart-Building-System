package com.example.smartbuildingsystem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;

public class MobileNumberVerification extends AppCompatActivity {

    private  Button  btn_send_verification_code;
    private  EditText txt_mobile_number;
    private  TextView txt_message_otp_display , change_number;
    private  String phone_number = null;
    private  int Number_matched=0;
    private  String Email_address_user_phone_matched=null;
    private  String GET_For_user_Authentication_check_phone_Local_DB_API = "http://10.0.2.2:3000/For_user_Authentication_check_phone_Local_DB_API";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number_verification);
        // initialized all objects
        btn_send_verification_code  = (Button) findViewById(R.id.btn_send_verification_code);
        txt_mobile_number = (EditText) findViewById(R.id.txt_mobile_number);
        txt_message_otp_display = (TextView) findViewById(R.id.txt_message_otp_display);
        String otp_message = "<font color=\"#000000\">"
                + "We will send you an "
                + "</font>"
                + "<font color=\"#000000\"><bold>"
                + " \'One Time Password\' "
                + "</bold></font>"+"<font color=\"#000000\">"
                + " on this mobile number"
                + "</font>";
        txt_message_otp_display.setText(Html.fromHtml(otp_message));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btn_send_verification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // number check is not empty
                if(txt_mobile_number.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(),"Please Enter the Mobile Number",Toast.LENGTH_SHORT).show();
                else if(txt_mobile_number.getText().length()!=11)
                    Toast.makeText(getApplicationContext(),"Please Enter Correct Mobile Number",Toast.LENGTH_SHORT).show();
                else{
                    String Mobile_num = txt_mobile_number.getText().toString();
                    Mobile_num = Mobile_num.substring(1,11);
                    phone_number = "0092"+Mobile_num;
                    Log.d(" Phone Activity  ", "upper values ............................................................... "+phone_number+".."+Mobile_num);
                    For_user_Authentication_check_phone_Local_DB_API ();
                    Log.d(" Phone Activity  ", " Number_matched values ............................................................... "+Number_matched);

                    if (Number_matched==1){
                        Toast.makeText(MobileNumberVerification.this , "Verification Code Sent",Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent(MobileNumberVerification.this,MobileCodeVerification.class);
                        intent.putExtra("Phone_Number" , phone_number);
                        intent.putExtra("email_address", Email_address_user_phone_matched);
                        Log.d(" Phone Activity  ", " lower values ............................................................... "+phone_number+"..");
                        Number_matched=0;
                        startActivity(intent);
                    }else{
                        Toast.makeText(MobileNumberVerification.this , " Choose Another Number",Toast.LENGTH_LONG ).show();
                    }

                }
            }
        });
    }

    private void For_user_Authentication_check_phone_Local_DB_API() {

            try {
                URL url2 = new URL(GET_For_user_Authentication_check_phone_Local_DB_API);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url2.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                Log.d("Login Activity ", "fetch_Data_of_LAST_Schedules_ADDED_FromDataBase............ called...................................");

                // Now to parse data from huge data
                JSONArray JA = new JSONArray(bufferedReader.readLine());
                int schedule_number = 1;
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JO = (JSONObject) JA.get(i);
                    Log.d("Num Matching ", "..............................................."+txt_mobile_number.getText().toString() +"....."+ JO.getString("user_contact"));

                  if(txt_mobile_number.getText().toString().equals( JO.getString("user_contact"))){
                      Email_address_user_phone_matched=JO.getString("email_address");
                      Log.d("Num matched ", "..............matched...................."+txt_mobile_number.getText().toString() +"....."+ JO.getString("user_contact")+"....."+Email_address_user_phone_matched);

                      Number_matched=1;
                  }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
}
