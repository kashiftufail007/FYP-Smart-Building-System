package com.example.smartbuildingsystem;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MobileCodeVerification extends AppCompatActivity {
    private  TextView change_number , otp , resend_code ,resent_code_message;
    private EditText otp_box_1,otp_box_2,otp_box_3,otp_box_4,otp_box_5,otp_box_6;
    private Button verify;
    private String phone_number;
    private int randomnum = 0;
    private RequestQueue requestQueue;
    private String Email_address_got = null;
    private int Resend_code = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_code_verification);
       // initialize all objects
        change_number = (TextView) findViewById(R.id.change_number);
        // initialize all boxes
        otp_box_1 = (EditText) findViewById(R.id.otp_box_1);
        otp_box_2 = (EditText) findViewById(R.id.otp_box_2);
        otp_box_3 = (EditText) findViewById(R.id.otp_box_3);
        otp_box_4 = (EditText) findViewById(R.id.otp_box_4);
        otp_box_5 = (EditText) findViewById(R.id.otp_box_5);
        otp_box_6 = (EditText) findViewById(R.id.otp_box_6);
        verify = (Button) findViewById(R.id.verify);
        otp = (TextView) findViewById(R.id.otp);
        resend_code = (TextView) findViewById(R.id.resend_code);
        resent_code_message = (TextView) findViewById(R.id.resent_code_message);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            phone_number = bundle.getString("Phone_Number");
            Email_address_got = bundle.getString("email_address");
        }
        Log.d(" MobilePhone Activity  ", " upper values ............................................................... "+phone_number+"..."+Email_address_got);
        // HTML Formats
        String input = "<font color=\"#000000\">"
                + "Enter Code send to "
                + "</font>"
                + "<font color=\"#000000\"><bold>"
                + phone_number
                + "</bold></font>";
        String resend_message = "<font color=\"#000000\">"
                + "Wait about "
                + "</font>"
                + "<font color=\"#000000\"><bold>"
                + " \'60 Seconds\'  "
                + "</bold></font>"+"<font color=\"#000000\">"
                + "to RESEND Code"
                + "</font>";
        otp.setText(Html.fromHtml(input));
        resent_code_message.setText(Html.fromHtml(resend_message));
        //Call Sms verification function
        SMS_Verification_Sender();
        otp_box_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable!=null){
                    if(editable.length()==1) {
                        otp_box_1.setTextColor(Color.parseColor("#7535AD"));
                        otp_box_2.requestFocus();
                    }
                }
            }
        });
        otp_box_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null){
                    if(editable.length()==1) {
                        otp_box_2.setTextColor(Color.parseColor("#7535AD"));
                        otp_box_3.requestFocus();
                    }
                }
            }
        });
        otp_box_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null){
                    if(editable.length()==1) {
                        otp_box_3.setTextColor(Color.parseColor("#7535AD"));
                        otp_box_4.requestFocus();
                    }
                }
            }
        });
        otp_box_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null){
                    if(editable.length()==1) {
                        otp_box_4.setTextColor(Color.parseColor("#7535AD"));
                        otp_box_5.requestFocus();
                    }
                }
            }
        });
        otp_box_5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null){
                    if(editable.length()==1) {
                        otp_box_5.setTextColor(Color.parseColor("#7535AD"));
                        otp_box_6.requestFocus();
                    }
                }
            }
        });
        otp_box_6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null){
                    if(editable.length()==1) {
                        otp_box_6.setTextColor(Color.parseColor("#7535AD"));
                        verify.requestFocus();
                    }
                }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // checck if all boxes are empty , then show message
                if ((otp_box_1.getText().toString().isEmpty()) ||
                        (otp_box_2.getText().toString().isEmpty()) ||
                        (otp_box_3.getText().toString().isEmpty()) ||
                        (otp_box_4.getText().toString().isEmpty()) ||
                        (otp_box_5.getText().toString().isEmpty()) ||
                        (otp_box_6.getText().toString().isEmpty())) {
                    Toast.makeText(MobileCodeVerification.this, " Enter Verification Code", Toast.LENGTH_SHORT).show();


                } else {
                    String Code_Entered = otp_box_1.getText().toString() +
                            otp_box_2.getText().toString() +
                            otp_box_3.getText().toString() +
                            otp_box_4.getText().toString() +
                            otp_box_5.getText().toString() +
                            otp_box_6.getText().toString();
                    Log.d(" MobilePhone Activity  ", "add all values of boxes ............................................................... " + Code_Entered + ".." + randomnum);
                    // compare entered values with generated values
                    if (randomnum == Integer.valueOf(Code_Entered.toString())) {
                        Log.d(" Matched Activity  ", "Match values ..................... " + Code_Entered + ".." + randomnum);
                        Toast.makeText(MobileCodeVerification.this, " Verification Successfully", Toast.LENGTH_SHORT).show();
                        // after matched, go to  login activity to fetch more data
                        Intent intent = new Intent(MobileCodeVerification.this, LoginActivity.class);
                        intent.putExtra("email_verification_sent", Email_address_got);
                        startActivity(intent);

                    } else {
                        Toast.makeText(MobileCodeVerification.this, "Wrong Verification Code", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        // om click on change button
        change_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MobileCodeVerification.this,MobileNumberVerification.class);
                startActivity(intent);
            }
        });
        // enable resend code button after onee mint
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resend_code.setTextColor(Color.parseColor("#D81B60"));
                        resend_code.setEnabled(true);
                        Log.d("Button ", ".........................Button Enabled...................................... ");

                    }
                });
            }
        }, 50000);

        resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Resend_code=1;
                SMS_Verification_Sender();
            }
        });
    }
    private   void SMS_Verification_Sender(){
                    Random random = new Random();
                    if (Resend_code==0) {
                        randomnum = random.nextInt(999999);
                        Log.d(" RAndom Num   ", "............................................................... "+randomnum);
                       int breaker=0;
                        for (int i=0; i<5 && breaker==0 ; i++) {
                            // number should be 6 digits
                            if (randomnum >99999 &&randomnum<1000000) {
                                Log.d(" RAndom Num   ", "if............................................................... "+randomnum);

                                breaker=1;
                            }else{
                                randomnum = random.nextInt(999999);
                                Log.d(" RAndom Num   ", "else............................................................... "+randomnum);

                            }
                        }
                    }


        Log.d(" Phone Activity  ", "lower values ............................................................... "+phone_number+".."+randomnum);
                    try {
                        // Construct data
                        // api key from website local text
                        String apiKey = "apikey=" + "fbUqCYiEMnE-pZoxZfoeb5DPPeu6HfU4Xqns8dZeUj";

                        // send message is like this
                        String message = "&message=" + "Smart Building System \n Hey ! Your Verification Code is ..."+ randomnum;
                        String sender = "&sender=" + "Smart Building System ";
                        // phone number on which you want to send
                        String numbers = "&numbers=" + phone_number.toString();
                        Log.d(" Phone Activity  ", "upper values ............................................................... "+phone_number+".."+randomnum);
                        // Send data
                        HttpURLConnection conn = (HttpURLConnection) new URL("https://api.txtlocal.com/send/?").openConnection();
                        String data = apiKey + numbers + message + sender;
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                        conn.getOutputStream().write(data.getBytes("UTF-8"));
                        final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        final StringBuffer stringBuffer = new StringBuffer();
                        String line;
                        while ((line = rd.readLine()) != null) {
                            stringBuffer.append(line);
                        }
                        rd.close();
                        Toast.makeText(MobileCodeVerification.this , " Verification code sent Successfully",Toast.LENGTH_LONG ).show();
                        // return stringBuffer.toString();
                    } catch (Exception e) {
                        System.out.println("Error SMS "+e);
                        Toast.makeText(MobileCodeVerification.this , " Sending Error",Toast.LENGTH_LONG ).show();
                        // return "Error "+e;
                    }

    }
}
