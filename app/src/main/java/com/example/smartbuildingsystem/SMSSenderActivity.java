package com.example.smartbuildingsystem;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SMSSenderActivity extends Activity {

    int num =000;
    String sms = "dd";
    private final static int SEND_SMS_PERMISSION_REQUEST_CODE=111;
    private Button sendMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
       // sendMessage = (Button) findViewById(R.id.send_message);
        //final EditText phone=(EditText) findViewById(R.id.phone_no);

//        phone.setText("+92");
//        final EditText message=(EditText) findViewById(R.id.message);
//        sendMessage.setEnabled(false);
        if (checkPermission(Manifest.permission.SEND_SMS)){
            sendMessage.setEnabled(true);


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);

        }

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String msg = message.getText().toString();
//                String phoneNumber = phone.getText().toString();
                if (!TextUtils.isEmpty(sms) && !TextUtils.isEmpty(String.valueOf(num))) {
                    if (checkPermission(Manifest.permission.SEND_SMS)) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(String.valueOf(num), null, sms, null, null);
                        Toast.makeText(SMSSenderActivity.this, "send", Toast.LENGTH_SHORT).show();
//                        phone.setText("+92               ");
//////                        message.setText("                   ");

                    } else {
                        Toast.makeText(SMSSenderActivity.this, "No Permission Granted!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SMSSenderActivity.this, "Enter message and phone number", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
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
                    sendMessage.setEnabled(true);

                }

                break;
        }
    }
}
