package com.example.smartbuildingsystem;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
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
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class LoginActivity extends AppCompatActivity {
    private EditText edt_email, edt_password;
    private Button btn_login;
    private RequestQueue requestQueue;
    private String Login_POST_API = "http://10.0.2.2:3000/login";
    private String Login_GET_User_All_Data_API = "http://10.0.2.2:3000/login_response_data";
    private String Login_GET_Email_Form_DB = "http://10.0.2.2:3000/For_login_check_Email_Local_DB_API";
    private String Connection_With_DB_GET_API = "http://10.0.2.2:3000/Connection_Checker_API";
    private String POST_Email_Sign_In_to_fetch_user_data_API = "http://10.0.2.2:3000/POST_Email_Sign_In_to_fetch_user_data_API";
    private String GET_Device_detail_from_SQL_on_basis_of_User_id_for_SQLite = "http://10.0.2.2:3000/GET_User_Device_Control_detail_on_basis_of_user_id";
    private String Get_Schedules_According_UserID = "http://10.0.2.2:3000/Fetched_schedules_According_to_User_ID_API";
    public static int Total_devices = 0;
    private int get_status_value = 3;
    private int Email_Founded_local_DB = 0;
    private int Email_validation_check = 1;
    private int password_match = 0;
    private int email_match = 0;
    private static int user_type = 1;
    private SharedPreferences prf_for_log_in;
    private int RC_SIGN_IN = 264;
    private String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    // Google signin data will be stored in these variables
    public static String personName;
    public String personGivenName;
    public String personFamilyName;
    public static String personEmail;
    private String personId;
    public static String personPhoto;
    private Dialog epic_dialog;
    private static  String Message_of_DB_Connected;
    // for SQLite Variables to store data from API
    private static Integer SQLite_USER_ID;
    private static Integer SQLite_USER_TYPE;
    private static String SQLite_USER_NAME;
    private static String SQLite_USER_EMAIL_ADDRESS;
    private static String SQLite_USER_PASSWORD;
    private static String SQLite_USER_CONTACT_NUMBER;
    private static Integer SQLite_USER_STATUS;
    private static String SQLite_LOCATION_NAME;

    private static DataBaseHelper SQLite_DATABASE_OBJECT;
    private int Google_login_status = 0;

    private TextInputLayout layoutTextInput_email,layoutTextInput_password;
    private String Email_Address_After_verification= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize all objects
        SQLite_DATABASE_OBJECT = new DataBaseHelper(LoginActivity.this);
        edt_email = (EditText) findViewById(R.id.email);
        edt_password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.login_button);
        layoutTextInput_email = (TextInputLayout) findViewById(R.id.layoutTextInput_email);
        layoutTextInput_password = (TextInputLayout)findViewById(R.id.layoutTextInput_password);


        requestQueue = Volley.newRequestQueue(getApplicationContext());
        /// After verification , get values from intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        // fetch value from intent , like email, then fetch data according to it
        if(bundle!=null){
            Email_Address_After_verification = bundle.getString("email_verification_sent");
            if (!Email_Address_After_verification.toString().isEmpty()){
                Log.d(" Bundle Matched  ", ".............................................................. " + Email_Address_After_verification);
                // call POST function ,to fetch data, and then get data , GET function called
                After_Verification_POST_User_Email(Email_Address_After_verification);
                After_Verification_fetch_user_detail process = new After_Verification_fetch_user_detail();
                process.execute();

            }
        }

        // Popup work , popup to check internet connection
        epic_dialog = new Dialog(this);

        // To check Connection with Database

        CheckConnectionWithDatabase process_to_check_connection = new CheckConnectionWithDatabase();
        process_to_check_connection.execute();
        fun_for_checking_connection();

        // To open phone number verification activity
        TextView txt_forget_password = (TextView) findViewById(R.id.txt_forget_password);
        txt_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this , MobileNumberVerification.class);
                startActivity(intent);
            }
        });

        //Snack bar to show message of connecction with BD in bottom
//        Snackbar snackbar = (Snackbar) Snackbar.make(findViewById(R.id.login_page_activity), "Connecting ", Snackbar.LENGTH_INDEFINITE);
//        snackbar.show();
//        CheckConnectionWithDatabase process_to_check_email = new CheckConnectionWithDatabase();
//        process_to_check_email.execute();


//        SQLite_DATABASE_OBJECT.insert_store_age_detail(1,30.5f,70.0f, "05:00");
//        SQLite_DATABASE_OBJECT.insert_store_age_detail(2,33.5f,75.5f, "15:00");
//        SQLite_DATABASE_OBJECT.insert_store_age_detail(3,44.5f,95.5f, "12:00");
//        SQLite_DATABASE_OBJECT.insert_store_age_detail(4,41.5f,65.5f, "16:00");
//        SQLite_DATABASE_OBJECT.insert_store_age_detail(5,39.5f,75.5f, "06:00");
//        SQLite_DATABASE_OBJECT.insert_store_age_detail(6,42.5f,75.5f, "17:00");
//        SQLite_DATABASE_OBJECT.insert_store_age_detail(7,40.5f,70.5f, "17:00");
//        SQLite_DATABASE_OBJECT.insert_device_detail(1,"FAN", "fanon", "fanoff",1,1);
//        SQLite_DATABASE_OBJECT.insert_device_detail(2,"EXHAUST FAN", "efanon", "efanoff",1,1);
//        SQLite_DATABASE_OBJECT.insert_device_detail(3,"ENERGY SEVER", "eson", "esoff",1,1);
//        SQLite_DATABASE_OBJECT.insert_device_detail(4,"MOTOR", "motoron", "motoroff",1,1);
//        SQLite_DATABASE_OBJECT.insert_device_detail(5,"TUBE LIGHT", "tlon", "tloff",1,1);
//        SQLite_DATABASE_OBJECT.insert_device_detail(6,"AC", "acon", "acoff",1,1);
//        SQLite_DATABASE_OBJECT.insert_device_detail(7,"BULB", "bulbon", "bulboff",1,1);


        //Check Email in Local Database
        //Shared Preferences to skip activity after login next time
        prf_for_log_in = getSharedPreferences("user_details", MODE_PRIVATE);
        Intent intent1 = new Intent(LoginActivity.this, HomePageActivity.class);
        if (prf_for_log_in.contains("user_email") && prf_for_log_in.contains("user_password") && prf_for_log_in.contains("user_status")) {
            startActivity(intent1);
        }
        // Post data send start from here
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //email validation checked here
                if (!edt_email.getText().toString().isEmpty()) {
                    if (!checkEmail(edt_email.getText().toString())) {
                        layoutTextInput_email.setErrorEnabled(true);
                        edt_email.getBackground().clearColorFilter();
                        layoutTextInput_email.setError("Email is not in  Valid Format");
                        layoutTextInput_email.setErrorTextAppearance(R.style.Widget_AppCompat_ProgressBar_Horizontal);
//                        Toast.makeText(getApplicationContext(), "Email is not in  Valid Format", Toast.LENGTH_SHORT).show();
                        Email_validation_check = 0;
                    } else {
                        layoutTextInput_email.setError(null);
                        Email_validation_check = 1;
                    }
                    if ( edt_password.getText().toString() != " "){
                        layoutTextInput_password.setError(null);
                    }
                    if ( edt_password.getText().toString().isEmpty() || edt_password.getText().toString() == " "){
                        layoutTextInput_password.setErrorEnabled(true);
                        edt_password.getBackground().clearColorFilter();
                        layoutTextInput_password.setError("Fill Password first");
                        layoutTextInput_password.setErrorTextAppearance(R.style.Widget_AppCompat_ProgressBar_Horizontal);
                    }
                }
                if (Email_validation_check == 1 && edt_email.getText().toString().isEmpty() && edt_password.getText().toString().isEmpty() || edt_email.getText().toString() == " " || edt_password.getText().toString() == " ") {
                    layoutTextInput_email.setErrorEnabled(true);
                    edt_email.getBackground().clearColorFilter();
                    layoutTextInput_email.setError("Fill Email first");
                    layoutTextInput_email.setErrorTextAppearance(R.style.Widget_AppCompat_ProgressBar_Horizontal);
                    layoutTextInput_password.setErrorEnabled(true);
                    edt_password.getBackground().clearColorFilter();
                    layoutTextInput_password.setError("Fill Password first");
                    layoutTextInput_password.setErrorTextAppearance(R.style.Widget_AppCompat_ProgressBar_Horizontal);

                    //Toast.makeText(getApplicationContext(), "Fill Email and Password ", Toast.LENGTH_SHORT).show();
                } else if (edt_email.getText().toString().isEmpty() || edt_email.getText().toString() == "") {
                   // Toast.makeText(getApplicationContext(), "Fill Email ", Toast.LENGTH_SHORT).show();
                    layoutTextInput_email.setErrorEnabled(true);
                    edt_email.getBackground().clearColorFilter();
                    layoutTextInput_email.setError("Fill Email first");
                    layoutTextInput_email.setErrorTextAppearance(R.style.Widget_AppCompat_ProgressBar_Horizontal);
                    layoutTextInput_password.setError(null);
                } else if (Email_validation_check == 1 && edt_password.getText().toString().isEmpty() || edt_password.getText().toString() == "") {
                   // Toast.makeText(getApplicationContext(), "Fill Password ", Toast.LENGTH_SHORT).show();
                    layoutTextInput_password.setErrorEnabled(true);
                    edt_password.getBackground().clearColorFilter();
                    layoutTextInput_password.setError("Fill Password first");
                    layoutTextInput_password.setErrorTextAppearance(R.style.Widget_AppCompat_ProgressBar_Horizontal);

                } else {

                    StringRequest request = new StringRequest(Request.Method.POST, Login_POST_API, new Response.Listener<String>() {
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
                            parameters.put("email_address", edt_email.getText().toString());
                            parameters.put("user_password", edt_password.getText().toString());

                            return parameters;
                        }
                    };
                    requestQueue.add(request);

                    // call two functions in background , tto fetch data according to it
                    fetchDataofEmailToCheckinLocalDB process_to_check_email = new fetchDataofEmailToCheckinLocalDB();
                    process_to_check_email.execute();

                    fetchDatatoCheckStatusofLogin process_for_status = new fetchDatatoCheckStatusofLogin();
                    process_for_status.execute();

                }
            }


        });

        // google login
        Button btn_google_sing_in = (Button) findViewById(R.id.btn_google_sing_in);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        // show gmail loading gif
        btn_google_sing_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epic_dialog.setContentView(R.layout.google_loading_screen);
                epic_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                epic_dialog.show();
                signIn();
            }
        });
    }

    private void fun_for_checking_connection() {


        if (Message_of_DB_Connected != null) {

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.login_page_activity), Message_of_DB_Connected.toString(), Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.login_page_activity), "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            fun_for_checking_connection();
                        }
                    });
            // Changing message text color
            snackbar.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    // to check email is in valid format or not
    private static boolean checkEmail(String email) {

        Pattern EMAIL_ADDRESS_PATTERN = Pattern
                .compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private  String DB_Connection_data;

    private class CheckConnectionWithDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url2 = new URL(Connection_With_DB_GET_API);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url2.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                // Now to parse data from huge data

                JSONObject JO = new JSONObject(bufferedReader.readLine());
                DB_Connection_data = (JO.getString("error"));

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Message_of_DB_Connected = DB_Connection_data;
        }
    }

    // here in it your email will be compared with emails in local DB and then if exist or not show messsages according to it

    private class fetchDataofEmailToCheckinLocalDB extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url2 = new URL(Login_GET_Email_Form_DB);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url2.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                // Now to parse data from huge data
                JSONArray JA = new JSONArray(bufferedReader.readLine());
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JO = (JSONObject) JA.get(i);
                    if (edt_email.getText().toString().equals(JO.getString("email_address"))) {
                        Email_Founded_local_DB = 1;
                    }
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (Email_validation_check == 1 && Email_Founded_local_DB == 1) {
//
//                Here Google Signin will be added in order to visible with google login after matching email
//                forget_password.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Email Matched  ", Toast.LENGTH_SHORT).show();
                email_match = 1;

            } else if (Email_validation_check == 1 && Email_Founded_local_DB == 0) {
                Toast.makeText(getApplicationContext(), "Email not Matched  ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class Fetch_device_detail_from_sql_on_basis_of_user_id extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url2 = new URL(GET_Device_detail_from_SQL_on_basis_of_User_id_for_SQLite);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url2.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                // Now to parse data from huge data
                JSONArray JA = new JSONArray(bufferedReader.readLine());
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JO = (JSONObject) JA.get(i);
                    Total_devices++;
                    SQLite_DATABASE_OBJECT.insert_device_detail(JO.getInt("device_id"), JO.getString("device_name"), JO.getInt("device_status"), JO.getInt("device_on_off_status"), JO.getString("device_on_command"), JO.getString("device_off_command"));


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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(getApplicationContext(), "Device Data Fetched ", Toast.LENGTH_SHORT).show();

        }
    }


    // Google Sign in Functions
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(acc);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            TextView signining = (TextView) epic_dialog.findViewById(R.id.signining);
            signining.setText("Signing Up . . .");
        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, " Sign in Error ", Toast.LENGTH_LONG).show();
            epic_dialog.dismiss();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, " Error ", Toast.LENGTH_LONG).show();
                            epic_dialog.dismiss();
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }


                });
    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acc != null) {

            personName = acc.getDisplayName();
            personGivenName = acc.getGivenName();
            personFamilyName = acc.getFamilyName();
            personEmail = acc.getEmail();
            //personId = acc.getId();
            personPhoto = acc.getPhotoUrl().toString();
            personId = acc.getGrantedScopes().toString();
            Toast.makeText(LoginActivity.this, personName + "  " + personEmail + "  " + personId, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);


            // to send mail to fetch data accordingly when login with Google Sign in
            StringRequest request = new StringRequest(Request.Method.POST, POST_Email_Sign_In_to_fetch_user_data_API, new Response.Listener<String>() {
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
                    parameters.put("email_address", personEmail);
                    Log.d(" Google Sign In  ", "upper Parameters putted ............................................................... " + personEmail);
                    return parameters;
                }
            };
            requestQueue.add(request);
            Google_login_status = 1;

//            After_Verification_POST_User_Email(personEmail);
//            Log.d(" Login Activity  ", " Google Signin f1.............................................................. ");
//
//            After_Verification_fetch_user_detail process = new After_Verification_fetch_user_detail();
//            process.execute();
//            Log.d(" Login Activity  ", " Google Signin f2.............................................................. ");
//
            fetchDatatoCheckStatusofLogin process_new  = new fetchDatatoCheckStatusofLogin();
            process_new.execute();
            Log.d(" Login Activity  ", " Google Signin f3.............................................................. ");



        }

    }


    private class fetchDatatoCheckStatusofLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Log.d(" Login   ", " fetchDatatoCheckStatusofLogin............................................................ ");

                URL url = new URL(Login_GET_User_All_Data_API);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                // Now to parse data from huge data
                JSONArray JA = new JSONArray(bufferedReader.readLine());
                JSONObject JO = (JSONObject) JA.get(0);

                if (!edt_password.getText().toString().equals(JO.getString("user_password"))) {
                    password_match = 1;
                }
                if ((JO.getInt("user_status") == 1) && (edt_email.getText().toString().equals(JO.getString("email_address"))) && (edt_password.getText().toString().equals(JO.getString("user_password"))))
                    get_status_value = 1;
                else {
                    get_status_value = 3;
                }
                user_type = JO.getInt("user_type");

                //fetched data from data base stored into these variables
                SQLite_USER_ID = JO.getInt("user_id");
                SQLite_USER_TYPE = JO.getInt("user_type");
                SQLite_USER_NAME = JO.getString("user_name");
                SQLite_USER_EMAIL_ADDRESS = JO.getString("email_address");
                SQLite_USER_PASSWORD = JO.getString("user_password");
                SQLite_USER_CONTACT_NUMBER = JO.getString("user_contact");
                SQLite_USER_STATUS = JO.getInt("user_status");
                SQLite_LOCATION_NAME = JO.getString("location_name");
                Log.d("Data................", "Data values ............................" + SQLite_USER_ID + "    " +
                        SQLite_USER_TYPE + "     " + "   " + SQLite_USER_NAME + "   " + SQLite_USER_EMAIL_ADDRESS + "     " +
                        SQLite_USER_PASSWORD + "      " + SQLite_USER_CONTACT_NUMBER + "     " + SQLite_USER_STATUS + "     " + SQLite_LOCATION_NAME);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (get_status_value == 1 && Email_validation_check == 1 && user_type == 1) {
                Toast.makeText(getApplicationContext(), "Welcome ", Toast.LENGTH_SHORT).show();

                SQLite_DATABASE_OBJECT.insert(Integer.parseInt(SQLite_USER_ID.toString()),
                        Integer.parseInt(SQLite_USER_TYPE.toString()), SQLite_USER_NAME, SQLite_USER_EMAIL_ADDRESS,
                        SQLite_USER_PASSWORD, SQLite_USER_CONTACT_NUMBER, Integer.parseInt(SQLite_USER_STATUS.toString()), SQLite_LOCATION_NAME);
                Log.d("Data................", "Data values .............." + SQLite_USER_ID + "    " +
                        SQLite_USER_TYPE + "     " + "   " + SQLite_USER_NAME + "   " + SQLite_USER_EMAIL_ADDRESS + "     " +
                        SQLite_USER_PASSWORD + "      " + SQLite_USER_CONTACT_NUMBER + "     " + SQLite_USER_STATUS + "     " + SQLite_LOCATION_NAME);


                Fetch_device_detail_from_sql_on_basis_of_user_id process_to_get_device_detail = new Fetch_device_detail_from_sql_on_basis_of_user_id();
                process_to_get_device_detail.execute();

                fetchDataofSchedulesFromDataBase process_schedule_fetch = new fetchDataofSchedulesFromDataBase();
                process_schedule_fetch.execute();


                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                Log.d(" Login Activity  ", " welcome after 2nd activity............................................................... ");

                SharedPreferences.Editor editor = prf_for_log_in.edit();
                editor.putString("user_email", String.valueOf(Email_validation_check));
                editor.putString("user_status", String.valueOf(get_status_value));
                editor.putString("user_password", String.valueOf(password_match));
                editor.commit();
                startActivity(intent);
                finish();

            } else if (get_status_value == 1 && Email_validation_check == 1 && user_type == 2) {
                Toast.makeText(getApplicationContext(), "Welcome Admin ", Toast.LENGTH_SHORT).show();

                SQLite_DATABASE_OBJECT.insert(Integer.parseInt(SQLite_USER_ID.toString()),
                        Integer.parseInt(SQLite_USER_TYPE.toString()), SQLite_USER_NAME, SQLite_USER_EMAIL_ADDRESS,
                        SQLite_USER_PASSWORD, SQLite_USER_CONTACT_NUMBER, Integer.parseInt(SQLite_USER_STATUS.toString()), SQLite_LOCATION_NAME);
                Log.d("Data................", "Data values .............." + SQLite_USER_ID + "    " +
                        SQLite_USER_TYPE + "     " + "   " + SQLite_USER_NAME + "   " + SQLite_USER_EMAIL_ADDRESS + "     " +
                        SQLite_USER_PASSWORD + "      " + SQLite_USER_CONTACT_NUMBER + "     " + SQLite_USER_STATUS + SQLite_LOCATION_NAME);
                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                Log.d(" Login Activity  ", " after 2nd activity............................................................... ");


                Fetch_device_detail_from_sql_on_basis_of_user_id process_to_get_device_detail = new Fetch_device_detail_from_sql_on_basis_of_user_id();
                process_to_get_device_detail.execute();

                fetchDataofSchedulesFromDataBase process_schedule_fetch = new fetchDataofSchedulesFromDataBase();
                process_schedule_fetch.execute();


                SharedPreferences.Editor editor = prf_for_log_in.edit();
                editor.putString("user_email", String.valueOf(Email_validation_check));
                editor.putString("user_status", String.valueOf(get_status_value));
                editor.putString("user_password", String.valueOf(password_match));
                editor.commit();
                startActivity(intent);
                finish();


            } else if (get_status_value == 0 && Email_validation_check == 1) {
                Toast.makeText(getApplicationContext(), "Blocked User ", Toast.LENGTH_SHORT).show();
            }
            if (password_match == 1 && Email_validation_check == 1 && email_match == 1) {
                Toast.makeText(getApplicationContext(), "Wrong Password ", Toast.LENGTH_SHORT).show();
            }
            if (Google_login_status == 1) {
                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                intent.putExtra("google_login", "true");

                SQLite_DATABASE_OBJECT.insert(Integer.parseInt(SQLite_USER_ID.toString()),
                        Integer.parseInt(SQLite_USER_TYPE.toString()), SQLite_USER_NAME, SQLite_USER_EMAIL_ADDRESS,
                        SQLite_USER_PASSWORD, SQLite_USER_CONTACT_NUMBER, Integer.parseInt(SQLite_USER_STATUS.toString()), SQLite_LOCATION_NAME);
                Log.d("Data................", " Google_login_status ==1 .............." + SQLite_USER_ID + "    " +
                        SQLite_USER_TYPE + "     " + "   " + SQLite_USER_NAME + "   " + SQLite_USER_EMAIL_ADDRESS + "     " +
                        SQLite_USER_PASSWORD + "      " + SQLite_USER_CONTACT_NUMBER + "     " + SQLite_USER_STATUS);
                Fetch_device_detail_from_sql_on_basis_of_user_id process_to_get_device_detail = new Fetch_device_detail_from_sql_on_basis_of_user_id();
                process_to_get_device_detail.execute();

                fetchDataofSchedulesFromDataBase process_schedule_fetch = new fetchDataofSchedulesFromDataBase();
                process_schedule_fetch.execute();

                SharedPreferences.Editor editor = prf_for_log_in.edit();
                editor.putString("user_email", String.valueOf(Email_validation_check));
                editor.putString("user_status", String.valueOf(get_status_value));
                editor.putString("user_password", String.valueOf(password_match));
                editor.commit();

                startActivity(intent);
            }
        }
    }


    private class fetchDataofSchedulesFromDataBase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url2 = new URL(Get_Schedules_According_UserID);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url2.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                Log.d("Login Activity ", "fetch_Data_of_LAST_Schedules_ADDED_FromDataBase............ called...................................");

                // Now to parse data from huge data
                JSONArray JA = new JSONArray(bufferedReader.readLine());
                int schedule_number = 1;
                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JO = (JSONObject) JA.get(i);

                    // Linear_Time_Display.setText(selectedItemTextDropDown + "\n"+starting_Hours+":"+starting_Minuts +ampm+"\n");

                    // insert Schedules data to SQLite
                    String Time_before = JO.getString("time_before").substring(0,5);
                    String Time_after = JO.getString("time_after").substring(0,5);
                    Log.d("Login Activity ", "time_before............ called..................................."+Time_before +"......."+Time_after);

                    SQLite_DATABASE_OBJECT.insert_schedule_detail(JO.getInt("schedule_id"), JO.getInt("device_id"),Time_before   ,Time_after , JO.getInt("schedule_action"), JO.getInt("schedule_status"));


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

    private class After_Verification_fetch_user_detail extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url = new URL(Login_GET_User_All_Data_API);
                HttpURLConnection httpURLConnection;
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                // Now to parse data from huge data
                JSONArray JA = new JSONArray(bufferedReader.readLine());
                JSONObject JO = (JSONObject) JA.get(0);
                SQLite_DATABASE_OBJECT.insert(JO.getInt("user_id"),JO.getInt("user_type"),JO.getString("user_name"),JO.getString("email_address"), JO.getString("user_password"),
                                                         JO.getString("user_contact"),JO.getInt("user_status"),JO.getString("location_name"));


                Log.d(" Login   ", "DAta inserting............................................................... "+
                                JO.getInt("user_id")+""+JO.getInt("user_type")+JO.getString("user_name")+JO.getString("email_address")+ JO.getString("user_password")+
                        JO.getString("user_contact")+JO.getInt("user_status")+JO.getString("location_name"));

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

                Toast.makeText(getApplicationContext(), "Welcome ", Toast.LENGTH_SHORT).show();

                Fetch_device_detail_from_sql_on_basis_of_user_id process_to_get_device_detail = new Fetch_device_detail_from_sql_on_basis_of_user_id();
                process_to_get_device_detail.execute();

                fetchDataofSchedulesFromDataBase process_schedule_fetch = new fetchDataofSchedulesFromDataBase();
                process_schedule_fetch.execute();

                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                SharedPreferences.Editor editor = prf_for_log_in.edit();
                editor.putString("user_email", String.valueOf(Email_validation_check));
                editor.putString("user_status", String.valueOf(get_status_value));
                editor.putString("user_password", String.valueOf(password_match));
                editor.commit();
                startActivity(intent);
                finish();


        }
    }


    // after verification from verification activity , then send email to fetch data from database
    private void After_Verification_POST_User_Email(final String Email_Address_After_verification){
        Log.d("Email POST FN  ", " .............................................................. "+Email_Address_After_verification);

        StringRequest request = new StringRequest(Request.Method.POST, POST_Email_Sign_In_to_fetch_user_data_API, new Response.Listener<String>() {
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
                parameters.put("email_address",  Email_Address_After_verification);


                Log.d("  POST Verify", "......... ................." +Email_Address_After_verification);
                Log.d("  POST Verify", "parameter value......... ................." +parameters.get("email_address"));

                return parameters;
            }
        };
        requestQueue.add(request);
    }
}
