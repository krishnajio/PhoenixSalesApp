package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity1 extends AppCompatActivity {
    Button btn1;
    TextView tv1;
    EditText edtxtUsrName, edtxtPwd;
    private ProgressDialog pDialog;
    public static String UserType = "",AreaCode= "";
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;
    JSONObject jobj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        btn1 = (Button) findViewById(R.id.btnB1);
        tv1 = (TextView) findViewById(R.id.tv1);
        edtxtUsrName = (EditText) findViewById(R.id.edUserName);
        edtxtPwd = (EditText) findViewById(R.id.edPwd);

        if (isOnline() == false) {
            Toast.makeText(getApplicationContext(),
                    "Please Check Your InterNet Connection...",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "You Are Connected to InterNet...",
                    Toast.LENGTH_SHORT).show();
        }

    }
    public void Login(View view) {
        String User = edtxtUsrName.getText().toString();
        String Password = edtxtPwd.getText().toString();
        if (isOnline() == true) {
            if (Validate_Data() == true) {
                Toast.makeText(MainActivity1.this, "validating", Toast.LENGTH_SHORT).show();
                DoLogin dologin = new DoLogin();
                dologin.execute("", User, Password);
                Toast.makeText(MainActivity1.this, AreaCode.toLowerCase(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please Enter User Name and Password...",
                        Toast.LENGTH_SHORT).show();
            }
        }
    else {
            Toast.makeText(getApplicationContext(),
                    "Please Check Your InterNet Connection...",
                    Toast.LENGTH_SHORT).show();
    }
    }// end function login
    public class DoLogin extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        String userid = edtxtUsrName.getText().toString();
        String password = edtxtPwd.getText().toString();
        String result = "";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity1.this);
            //pDialog.setProgressStyle(ProgressDialog.);
            pDialog.setMessage("Retriving the Data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected void onPostExecute(String r) {
            pDialog.dismiss();
           // Toast.makeText(MainActivity1.this, r, Toast.LENGTH_SHORT).show();
            jobj = new JSONObject();
            if (r != "") {
              //  Toast.makeText(MainActivity1.this, "R!==", Toast.LENGTH_SHORT).show();
                try {
                JSONObject object;
                object = new JSONObject(r);
                JSONArray jarray1 = object.getJSONArray("Login");
                 jobj = jarray1.getJSONObject(0);
                 Log.i("JSONdata", jobj.getString("UserName"));
                    SharedPreferences sharedPreferences = getSharedPreferences(
                            "PhxGlobalData", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    //UserName, Password, Role, UserType, AreaCode, AreaName, id
                    editor.putString("UserName",  jobj.getString("UserName").toString());
                    editor.putString("AreaCode", jobj.getString("AreaCode").toString());
                    editor.putString("AreaName", jobj.getString("AreaName").toString());
                    editor.putString("UserType", jobj.getString("UserType").toString());
                    UserType = jobj.getString("UserType").toString();
                    AreaCode=  jobj.getString("AreaCode").toString();
                    editor.commit();
                    edtxtUsrName.setText("");
                    edtxtPwd.setText("");
                    if (UserType.equals("Sales")) {
                        Toast.makeText(MainActivity1.this, "in sales", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SalesMenuActivity.class);
                        startActivity(intent);
                    }else if (UserType.equals("SalesGM"))
                    {
                        Toast.makeText(MainActivity1.this, jobj.getString("UserType").toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SalesAdminMenuctivity.class);
                        startActivity(intent);
                    }
                    else if (UserType.equals("Hatchery"))
                    {
                        Toast.makeText(MainActivity1.this, jobj.getString("UserType").toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Hatchery_MenuActivity.class);
                        startActivity(intent);
                    }
                    //

            } catch (Exception ex) {
                    Toast.makeText(MainActivity1.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
        }else {  }
        }
        @Override
        protected String doInBackground(String... params) {
            if (userid.trim().equals("") || password.trim().equals("")) {
                z = "Please enter User Id and Password";
            } else {
                //Code for Webservice
                Log.i("conn", "login");
                String type = params[0];
               // String login_url = "http://192.168.0.130:91/mynew.asmx/Login";
               String login_url1 = "http://117.240.18.180:91/mynew.asmx/Login";
                // if(type.equals("Login")) {
                try {
                    String User = params[1];
                    String Password = params[2];
                    URL url = new URL(login_url1);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    Log.d("url", "entered show area");
                    String post_data = URLEncoder.encode("User", "UTF-8") + "=" + URLEncoder.encode(User, "UTF-8") + "&"
                            + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(Password, "UTF-8");
                    Log.d("url", login_url1);
                    // alertDialog.setTitle(post_data);
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //
               // }
            }
            return z;
        }
    }
    public boolean Validate_Data()
    {
        if (edtxtUsrName.getText().toString().isEmpty() == true) {
            edtxtUsrName.setError("Plese Enter User Name!");
            edtxtUsrName.setFocusable(true);
            return false;
        }else if(edtxtPwd.getText().toString().isEmpty() == true) {
            edtxtPwd.setError("Plese Enter Passsword!");
            edtxtPwd.setFocusable(true);
            return false;
        }else
            return true;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable()
                    && networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
            System.out
                    .println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

}//end mina class
