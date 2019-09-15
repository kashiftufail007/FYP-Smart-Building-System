package com.example.smartbuildingsystem;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

import static com.example.smartbuildingsystem.LoginActivity.personPhoto;


public class ProfilePageActivity extends AppCompatActivity {
    private  Dialog epic_dialog;
    private  Button profile_setting_dots, btn_save_profile;
    private  Button btn_set_phone_call,btn_message_opener,btn_to_send_email,btn_verification_old_password;
    private  TextView txt_name_for_profile,txt_profile_email,txt_phone_number,txt_location_added,txt_total_devices_control;
    private  ImageView img_edit_name,img_edit_phone_number_profile,img_edit_password_profile;
    private  LinearLayout image_linear_layout,contact_linear_layout,layout_for_old_password,layout_for_new_password;
    private  ScrollView profile_data_scroll_layout;
    private  GifImageView arrow_profile_upward, arrow_profile_downward;
    private  ImageView imageView_for_profile_picture;
    private  SharedPreferences prf_for_sign_out;
    // Google Sign Out Code
    private  FirebaseAuth mAuth;
    private  Button signout;
    private  GoogleSignInClient mGoogleSignInClient;
    private  DataBaseHelper SQLite_DATABASE_OBJECT;
    private  RequestQueue requestQueue;
    private  String POST_Update_new_Password_API = "http://10.0.2.2:3000/POST_Update_new_Password_API";
    private  String POST_Update_new_Ph_Name_Profile_API = "http://10.0.2.2:3000/POST_Update_new_Ph_Name_Profile_API";

    private  EditText edt_txt_phone_number,edt_txt_name_for_profile;
    public int total_devices_under_control_count=0;
    private  TextView txt_Schedule_management, txt_device_management_homeActivity;
    private  ImageView img_Schedule_management,img_device_management_homeActivity;
    String kashif_pic="https://lh3.googleusercontent.com/a-/AAuE7mChCRy_sjbk8OaZwJXj1KruGpVEv7gXRITwLjXZNQ=s96-c";
    TextInputLayout layoutTextInput_old_password , layoutTextInput_new_password1 , layoutTextInput_new_password2;
    private  static String GoogleSigninPhoto=null;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        // initialize all objects
        profile_setting_dots = (Button) findViewById(R.id.profile_setting);
        btn_save_profile = (Button) findViewById(R.id.btn_save_profile);
        img_edit_name = (ImageView)findViewById(R.id.img_edit_name);
        img_edit_phone_number_profile = (ImageView)findViewById(R.id.img_edit_phone_number_profile);
//        img_image_uploader = (ImageView) findViewById(R.id.img_image_uploader);
        img_edit_password_profile = (ImageView)findViewById(R.id.img_edit_password_profile);
        image_linear_layout = (LinearLayout) findViewById(R.id.image_linear_layout);
        contact_linear_layout= (LinearLayout) findViewById(R.id.contact_linear_layout);
        profile_data_scroll_layout = (ScrollView)findViewById(R.id.profile_data_scroll_layout);
        arrow_profile_upward = (GifImageView)findViewById(R.id.arrow_profile_upword);
        arrow_profile_downward= (GifImageView) findViewById(R.id.arrow_profile_downward);
        txt_total_devices_control = (TextView)findViewById(R.id.txt_total_devices_control);
        //to set text change
        txt_name_for_profile = (TextView) findViewById(R.id.txt_name_for_profile);
        txt_profile_email = (TextView) findViewById(R.id.txt_profile_email);
        txt_phone_number = (TextView) findViewById(R.id.txt_phone_number);
        txt_location_added = (TextView) findViewById(R.id.txt_location_added);
        SQLite_DATABASE_OBJECT = new DataBaseHelper(ProfilePageActivity.this);
        Button profile_keyboard_backspace = (Button) findViewById(R.id.profile_keyboard_backspace);
        //imageView_for_profile_picture = (ImageView ) findViewById(R.id.imageView_for_profile_picture);
        txt_Schedule_management  =  (TextView) findViewById(R.id.txt_Schedule_management);
        img_Schedule_management = (ImageView) findViewById(R.id.img_Schedule_management);
        txt_device_management_homeActivity = (TextView) findViewById(R.id.txt_device_management_homeActivity);
        img_device_management_homeActivity = (ImageView ) findViewById(R.id.img_device_management_homeActivity);
        epic_dialog= new Dialog(this);
        btn_message_opener = (Button) findViewById(R.id.btn_message_opener);
        // initialize all layouts
        layoutTextInput_old_password =(TextInputLayout) findViewById(R.id.layoutTextInput_old_password);
        layoutTextInput_new_password1 = (TextInputLayout) findViewById(R.id.layoutTextInput_new_password1);
        layoutTextInput_new_password2 = (TextInputLayout) findViewById(R.id.layoutTextInput_new_password2);

        btn_set_phone_call = (Button ) findViewById(R.id.btn_set_phone_call);
        btn_set_phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:03087564568")); //Replace with valid phone number. Remember to add the
                        startActivity(intent);
            }
        });

        txt_Schedule_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent( ProfilePageActivity.this , SchedulePageActivity.class);
                intent.putExtra("Schedule_management" , 1);
                startActivity(intent);
            }
        });
        txt_device_management_homeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent( ProfilePageActivity.this , HomePageActivity.class);
                intent.putExtra("Device_management" , 1);
                startActivity(intent);
            }
        });

        // open email app , then auto write messages
        btn_to_send_email = (Button ) findViewById(R.id.btn_to_send_email);
        btn_to_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Compile a Uri with the 'mailto' schema
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","kashiftufail007@gmail.com", null));
                // Subject
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Smart Building Issue");
                // Body of email
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi! I am sending you a test email.");
                // File attachment
                       // emailIntent.putExtra(Intent.EXTRA_STREAM, attachedFileUri);
                // Check if the device has an email client
                        if (emailIntent.resolveActivity(getPackageManager()) != null) {
                // Prompt the user to select a mail app
                            startActivity(Intent.createChooser(emailIntent,"Choose your mail application"));
                        } else {
                // Inform the user that no email clients are installed or provide an alternative
                        }
            }
        });

        LinearLayout contact_linear_layout = (LinearLayout) findViewById(R.id.contact_linear_layout);
//        contact_linear_layout.setBackgroundColor(Col);

        // back space to go back to home page
        profile_keyboard_backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent( ProfilePageActivity.this , HomePageActivity.class);
              startActivity(intent);
            }
        });
        // to change name of profile person
        img_edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_name_for_profile.setVisibility(View.INVISIBLE);
                img_edit_name.setVisibility(View.INVISIBLE);
                edt_txt_name_for_profile=(EditText)findViewById(R.id.edt_txt_name_for_profile);
                edt_txt_name_for_profile.setVisibility(View.VISIBLE);
                edt_txt_name_for_profile.setText(txt_name_for_profile.getText());
            }
        });

        img_edit_phone_number_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_phone_number.setVisibility(View.INVISIBLE);
                edt_txt_phone_number = (EditText) findViewById(R.id.edt_txt_phone_number);
                edt_txt_phone_number.setVisibility(View.VISIBLE);
                img_edit_phone_number_profile.setVisibility(View.INVISIBLE);
                edt_txt_phone_number.setText(txt_phone_number.getText());

            }
        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // To fetch data from SQLite Database
        final Cursor cursor = SQLite_DATABASE_OBJECT.alldata();
        if (cursor.getCount()<0)
        {
            Log.d(" Profile Activity  ", " Failed to Update Profile............................................................... ");
        }else {
            if (cursor.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "Failed to Fetch Profile Data  ", Toast.LENGTH_SHORT).show();
                Log.d(" Profile Activity  ", " Failed to Update Profile............................................................... ");

            } else if (cursor.moveToFirst()) {
                Log.d(" Profile Activity  ", " Updating Profile  ............................................................... ");












                imageView_for_profile_picture = (ImageView) findViewById(R.id.imageView_for_profile_picture);
                txt_name_for_profile.setText(cursor.getString(3).toString());
                txt_profile_email.setText(cursor.getString(4).toString());
                if (cursor.getString(4).toString().equals("kashiftufail007@gmail.com")){
                    Glide.with(getApplicationContext()).load(kashif_pic)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView_for_profile_picture);
                }
                txt_phone_number.setText(cursor.getString(6));
                txt_location_added.setText(cursor.getString(8));
                Log.d(" Profile Activity  ", "Data is  ............... " + cursor.getString(3).toString() + cursor.getString(4).toString()+cursor.getString(8));

            }
        }

        // Message APP to send message
        btn_message_opener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n = new Intent(Intent.ACTION_VIEW);
                n.setType("vnd.android-dir/mms-sms");
                n.putExtra("address", "+923087564568");
                n.putExtra("sms_body","User:   "+cursor.getString(3).toString()+"\nLocation:   "+cursor.getString(8)+"\n Issue:\n    ");
                startActivity(n);
            }
        });
   // Count Total devices under user Control
        final Cursor cursor_device_count = SQLite_DATABASE_OBJECT.all_data_of_device_detail_table();
        if (cursor_device_count.getCount()<0)
        {
            Log.d(" Profile Activity  ", " Failed to count Devices............................................................... ");
        }else if (cursor_device_count.moveToFirst()) {

            // global after the declaration of your class
            for (int i = 0; i < cursor_device_count.getCount(); i++) {
                total_devices_under_control_count++;
                cursor_device_count.moveToNext();

            }
            txt_total_devices_control.setText(Integer.toString(total_devices_under_control_count));
        }

        // for password updation following things will be happen
        layout_for_old_password= (LinearLayout) findViewById(R.id.layout_for_old_password);
        layout_for_new_password = (LinearLayout) findViewById(R.id.layout_for_new_password);
        btn_verification_old_password = (Button) findViewById(R.id.btn_verification_old_password);
        final TextView txt_old_password = (TextView) findViewById(R.id.txt_old_password);
        final TextView txt_new_password = (TextView) findViewById(R.id.txt_new_password);
        final TextView txt_new_verify_password = (TextView) findViewById(R.id.txt_new_verify_password);
        final Button btn_save_new_passwords = (Button) findViewById(R.id.btn_save_new_passwords);
        btn_verification_old_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_old_password.getText().toString().isEmpty()){
                   
                    layoutTextInput_old_password.setError("Enter Old Password First");
                }else if (cursor.getCount()==0){
                    Toast.makeText(ProfilePageActivity.this, "        Error \n Server Offline", Toast.LENGTH_SHORT).show();

                }else if (txt_old_password.getText().toString().equals(cursor.getString(5).toString())) {

                    layout_for_old_password.setVisibility(View.GONE);
                    layout_for_new_password.setVisibility(View.VISIBLE);
                } else{
                        Toast.makeText(ProfilePageActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }

            }
        });
        btn_save_new_passwords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(" Profile Activity  ", "Password ............................................................... "+txt_new_password.getText().toString()+txt_new_verify_password.getText().toString());
                if(txt_new_password.getText().toString().isEmpty() ){

                    layoutTextInput_new_password1.setError("Enter New Password First!");
                    layoutTextInput_new_password2.setError(null);

                }else  if(txt_new_verify_password.getText().toString().isEmpty() ){
                    layoutTextInput_new_password1.setError(null);
                    layoutTextInput_new_password2.setError("Enter New Password First!");

                }else  if(!txt_new_password.getText().toString().equals(txt_new_verify_password.getText().toString())){

                        layoutTextInput_new_password1.setError("Password Does'nt Match!");
                        layoutTextInput_new_password2.setError("Password Does'nt Match!");


                }else if(txt_new_password.getText().toString().equals(txt_new_verify_password.getText().toString()))
                {
                     StringRequest request = new StringRequest(Request.Method.POST, POST_Update_new_Password_API, new Response.Listener<String>() {
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
                            parameters.put("user_id",cursor.getString(1));
                            parameters.put("user_password", txt_new_password.getText().toString());
                            Log.d(" Profile Activity  ", "Perameters putted ............................................................... ");
                            SQLite_DATABASE_OBJECT.update_password(Integer.valueOf(cursor.getString(1)),txt_new_password.getText().toString());

                            return parameters;
                        }
                    };
                    requestQueue.add(request);

                    Toast.makeText(ProfilePageActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                    layout_for_new_password.setVisibility(View.GONE);
                }else{
                    txt_new_password.setText("");
                    txt_new_verify_password.setText("");
                    Toast.makeText(ProfilePageActivity.this, "Passwords does't match ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // POST Data of phone number and name onn click save button to database
        btn_save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(" Profile Activity  ", "Password ............................................................... "+txt_new_password.getText().toString()+txt_new_verify_password.getText().toString());

                    StringRequest request = new StringRequest(Request.Method.POST, POST_Update_new_Ph_Name_Profile_API, new Response.Listener<String>() {
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
                            parameters.put("user_id",cursor.getString(1));
                            parameters.put("user_name", edt_txt_name_for_profile.getText().toString());
                            parameters.put("user_contact", edt_txt_phone_number.getText().toString());
                            Log.d(" Profile Activity  ", "Parameters putted ............................................................... ");
                           SQLite_DATABASE_OBJECT.update_ph_name_profile_user_detail(Integer.valueOf(cursor.getString(1)),edt_txt_name_for_profile.getText().toString(), edt_txt_phone_number.getText().toString() );

                            return parameters;
                        }
                    };
                    requestQueue.add(request);
                txt_name_for_profile.setVisibility(View.VISIBLE);
                edt_txt_name_for_profile.setVisibility(View.INVISIBLE);
                txt_name_for_profile.setText(edt_txt_name_for_profile.getText());
                txt_phone_number.setVisibility(View.VISIBLE);
                edt_txt_phone_number.setVisibility(View.INVISIBLE);
                txt_phone_number.setText(edt_txt_phone_number.getText());
            }
        });

       imageView_for_profile_picture = (ImageView) findViewById(R.id.imageView_for_profile_picture);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String google_login=null;
        Log.d(" Google SignIn  ", "lower values ............................................................... "  +"..."+personPhoto);
        GoogleSigninPhoto = personPhoto;
        if (GoogleSigninPhoto != null){

                Glide.with(getApplicationContext()).load(kashif_pic)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView_for_profile_picture);

                txt_profile_email.setText(LoginActivity.personEmail);
                txt_name_for_profile.setText(LoginActivity.personName);

        }

        //imageView_for_profile_picture.setImageResource(Integer.parseInt(LoginActivity.personPhoto));

        // for Google Sign out
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this , gso );
        mAuth = FirebaseAuth.getInstance();


        prf_for_sign_out = getSharedPreferences("user_details",MODE_PRIVATE);

        img_edit_password_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_for_old_password.setVisibility(View.VISIBLE);
            }
        });


        profile_setting_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenuExample();
            }
        });

        arrow_profile_upward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrow_profile_upward.setVisibility(View.INVISIBLE);
                arrow_profile_downward.setVisibility(View.INVISIBLE);
                image_linear_layout.setAlpha((float) 1.0);
            }
        });
        arrow_profile_downward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_linear_layout.setAlpha((float)1.0);
                arrow_profile_upward.setVisibility(View.INVISIBLE);
                arrow_profile_downward.setVisibility(View.INVISIBLE);
            }
        });
        // Profile picture image view
        imageView_for_profile_picture = (ImageView) findViewById(R.id.imageView_for_profile_picture);
        imageView_for_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePageActivity.this);
                builder.setTitle("Profile Picture !");
                builder.setMessage("Only Google Login profile will display here.");
                builder.setIcon(R.drawable.ic_happy);
                builder.setPositiveButton("OK", null);
               // builder.setNegativeButton("CANCEL", null);
                builder.show();
            }
        });
    }

    private void popupMenuExample() {
        PopupMenu profile_popup= new PopupMenu(ProfilePageActivity.this, profile_setting_dots);
        profile_popup.getMenuInflater().inflate(R.menu.setting_profile_menu, profile_popup .getMenu());

        profile_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.setting:
                        TextView text_view_verison = (TextView) findViewById(R.id.text_view_verison);
                        text_view_verison.setVisibility(View.INVISIBLE);
                        btn_save_profile.setVisibility(View.VISIBLE);
//                        img_image_uploader.setVisibility(View.VISIBLE);
                        img_edit_name.setVisibility(View.VISIBLE);
                        img_edit_phone_number_profile.setVisibility(View.VISIBLE);
                        img_edit_password_profile.setVisibility(View.VISIBLE);

                        txt_device_management_homeActivity.setEnabled(true);
                         txt_Schedule_management.setEnabled(true);
                         img_Schedule_management.setImageResource(R.drawable.ic_schedule_settings);
                         img_device_management_homeActivity.setImageResource(R.drawable.ic_bulb_settings);
                        Toast.makeText(ProfilePageActivity.this, "Setting", Toast.LENGTH_SHORT).show();

                        return true;
                    case R.id.contact:
                        image_linear_layout.setAlpha((float) 0.6);
                        //profile_data_scroll_layout.setAlpha((float)0.9);
                        arrow_profile_upward.setVisibility(View.VISIBLE);
                        arrow_profile_downward.setVisibility(View.VISIBLE);
                        Button profile_keyboard_backspace = (Button)findViewById(R.id.profile_keyboard_backspace);
                        profile_keyboard_backspace.setVisibility(View.VISIBLE);
                        profile_keyboard_backspace.setAlpha(1);

                        btn_save_profile.setVisibility(View.INVISIBLE);
//                        img_image_uploader.setVisibility(View.INVISIBLE);
                        img_edit_name.setVisibility(View.INVISIBLE);
                        img_edit_phone_number_profile.setVisibility(View.INVISIBLE);
                        img_edit_password_profile.setVisibility(View.INVISIBLE);

                        Toast.makeText(ProfilePageActivity.this, "Contact", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.help:
                        ShowPopupForDeviceManagement();
                        Toast.makeText(ProfilePageActivity.this, "Help Manual", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.sign_out:
                        Intent intent = new Intent(ProfilePageActivity.this, LoginActivity.class);
                        mAuth.signOut();
                        // Below lines are for after sign out and again login it will ask for email conformation
                        mGoogleSignInClient.signOut().addOnCompleteListener(ProfilePageActivity.this,
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(ProfilePageActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });

                        //this is due to sign_out and delete data from shared reference
                        SharedPreferences.Editor editor = prf_for_sign_out.edit();
                        editor.clear();
                        editor.commit();
                        SQLite_DATABASE_OBJECT.delete_user_detail_data();
                        SQLite_DATABASE_OBJECT.delete_device_detail_data();
                        SQLite_DATABASE_OBJECT.delete_storage_detail_data();
                        SQLite_DATABASE_OBJECT.delete_all_schedule_detail_data();
                        total_devices_under_control_count=0;
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(ProfilePageActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        Toast.makeText(ProfilePageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
        profile_popup.show();
    }
    public void ShowPopupForDeviceManagement(){

        epic_dialog.setContentView(R.layout.help_manual_layout_profile);

        epic_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epic_dialog.show();

    }
}