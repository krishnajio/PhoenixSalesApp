package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChicksrateArea extends AppCompatActivity {

    TextView tvAC_ChickRate;
    ImageView imgHatchDateChickRateArea;
    Button btnSaveChickRateArea,btnShowChickRateArea1;
    EditText edtxtHatchDateChickRateArea,edtxtRate;
    AutoCompleteTextView autoListArea;
    Spinner sChicksTypeAreaRate;
    ListView lvChickRate;
    String AreaNameChickRate;
    int mYear = 0, mMonth = 0, mDay = 0;
    private String[] ChicksTypeSupp = {"Broiler","Broiler(M+)", "Layer", "Cockerel"};
    String VarChicksType, VarPAyType, VarPaymntType, VarArea, UserName, AreaCode, AreaName, UserType;
    private ProgressDialog pDialog;
    ArrayList<String> listArea;
    JSONObject jobj;
    String  Hatch_Date,  State, ChickType, Rate, uname, EntryDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    String currentDateandTime = sdf.format(new Date());
    ArrayList<HashMap<String, String>>  items_rate;
    boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chicksrate_area);

        imgHatchDateChickRateArea= (ImageView)findViewById(R.id.imgGetsHatchDateArea);
        edtxtHatchDateChickRateArea  =(EditText)findViewById(R.id.dtpChickdRateHatchDateArea);
        edtxtRate  =(EditText)findViewById(R.id.edtxtChickdRateArea);
        btnSaveChickRateArea =(Button)findViewById(R.id.btnSaveChickRAteArea);
        autoListArea = ( AutoCompleteTextView)findViewById(R.id.autoListAreaChickRate);
        sChicksTypeAreaRate = (Spinner)findViewById(R.id.sChicksTypeAreaRate);
        tvAC_ChickRate = (TextView)findViewById(R.id.tvAC_ChickRate);
        lvChickRate = (ListView)findViewById(R.id.lvChickRatreArea);

        items_rate= new ArrayList<HashMap<String, String>> ();


        SharedPreferences sharedPreferences = getSharedPreferences(
                "PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");

        tvAC_ChickRate.setText("User:- " + UserName + "   Area Name:- " + AreaName);

        //filling Spinnsers
        ArrayAdapter chick_adapter = new ArrayAdapter(ChicksrateArea.this, android.R.layout.simple_spinner_item, ChicksTypeSupp);
        chick_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sChicksTypeAreaRate.setAdapter(chick_adapter);

        GetAreaList getAreaList = new GetAreaList();
        getAreaList.execute(AreaCode);

        sChicksTypeAreaRate
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        VarChicksType = arg0.getItemAtPosition(arg2).toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });

        //On Click Auto List
        autoListArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.d("AutoText", listAuto.getText().toString());
                flag=true;
                Toast.makeText(getApplicationContext(), autoListArea.getText().toString(), Toast.LENGTH_SHORT).show();

                VarArea = autoListArea.getText().toString();
            }
        });

    }//end on Create

    public  void SaveChickRateArea(View view)
    {
        try {

           if (Validate_Data()) {
               String AreaCodeName[] = VarArea.split("#");
              // Log.i("save",AreaCodeName[1]+ AreaCodeName[0]+ edtxtHatchDateChickRateArea.getText().toString()+VarChicksType+UserName);
               SaveChicksRate saveChicksRate = new SaveChicksRate();
               saveChicksRate.execute(AreaCodeName[1], AreaCodeName[0], edtxtHatchDateChickRateArea.getText().toString(),
                     VarChicksType, edtxtRate.getText().toString(), UserName);
           }
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),Toast.LENGTH_LONG);
        }
    }
    public void GetHDAreaRate(View view)
    {
       final Calendar c = Calendar.getInstance();
       mYear = c.get(Calendar.YEAR);
       mMonth = c.get(Calendar.MONTH);
       mDay = c.get(Calendar.DAY_OF_MONTH);
       DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
       @Override
       public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
               edtxtHatchDateChickRateArea.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
           }
       }, mYear, mMonth, mDay);
       datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
       datePickerDialog.show();



   }//end GetHDAreaRate

    public void click1(View view)
    {
       Toast.makeText(getApplicationContext(),"Button Click"+ edtxtHatchDateChickRateArea.getText().toString(),Toast.LENGTH_LONG).show();
        ShowChickRateArea showChickRateArea = new ShowChickRateArea();
       showChickRateArea.execute(edtxtHatchDateChickRateArea.getText().toString(),UserName);
    }

    public class GetAreaList extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ChicksrateArea.this);
            //pDialog.setProgressStyle(ProgressDialog.);
            pDialog.setMessage("Retriving the Area List. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            pDialog.dismiss();
            //  Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
            jobj = new JSONObject();
            if (r != "") {
                //; Toast.makeText(TrActivity.this, "R!==", Toast.LENGTH_SHORT).show();
                try {
                    // Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
                    JSONObject object;
                    listArea = new ArrayList<String>();
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("AreaName");
                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        listArea.add(jobj.getString("AreaName"));
                    }
                    autoListArea.setThreshold(1);
                    ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listArea);

                    autoListArea.setAdapter(mArrayAdapter);

                } catch (Exception ex) {
                }
            } else {

            }
        }

        @Override
        protected String doInBackground(String... params) {
            //Code for Webservice
            Log.i("conn", "login");
            String area_code = params[0];
            String login_url = "http://117.240.18.180:91/mynew.asmx/GetArea";
            //String login_url = "http://117.240.18.180:91/mynew.asmx/Customer";
            // if(type.equals("Login")) {
            try {

                URL url = new URL(login_url);
                Log.d("url", "entered show Customer");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Log.d("url", "entered show area");
                String post_data = URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(area_code, "UTF-8");
                Log.d("url", post_data);
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
            Log.i("result", result);
            return result;
        }
    }


    public class SaveChicksRate extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ChicksrateArea.this);
            pDialog.setMessage("Saving the chicks rate data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected void onPostExecute(String r) {
            pDialog.dismiss();
            // Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
            Log.i("str", r);
            int intIndex = r.indexOf("Success");
            if (intIndex != -1) {
                Toast.makeText(getApplicationContext(), "Chicks Rate Saved Sucessfully,Redirecting to Main Menu...", Toast.LENGTH_LONG).show();
                //edtxtHatchDateChickRateArea.setText("");
                edtxtRate.setText("");

            } else {
                Toast.makeText(getApplicationContext(), r, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SalesAdminMenuctivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                AreaCode = params[0];
                AreaName = params[1];
                Hatch_Date = params[2];
                ChickType = params[3];
               // State = params[4];
                Rate = params[4];
                uname = params[5];

                Log.i("urlInsertTr",AreaCode+ AreaName+Hatch_Date+Rate+uname+ChickType);
                String login_url = "http://117.240.18.180:91/mynew.asmx/InsertChicksReateArea";
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Log.i("url", login_url);

                String post_data = URLEncoder.encode("Hatch_Date", "UTF-8") + "=" + URLEncoder.encode(Hatch_Date, "UTF-8") + "&"
                        + URLEncoder.encode("Area", "UTF-8") + "=" + URLEncoder.encode(AreaName, "UTF-8") + "&"
                        + URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(AreaCode, "UTF-8") + "&"
                        + URLEncoder.encode("State", "UTF-8") + "=" + URLEncoder.encode("S", "UTF-8") + "&"
                        + URLEncoder.encode("ChicksType", "UTF-8") + "=" + URLEncoder.encode(ChickType, "UTF-8") + "&"
                        + URLEncoder.encode("Rate", "UTF-8") + "=" + URLEncoder.encode(Rate, "UTF-8") + "&"
                        + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(uname, "UTF-8") + "&"
                        + URLEncoder.encode("EntryDate", "UTF-8") + "=" + URLEncoder.encode(currentDateandTime, "UTF-8") + "&"
                        + URLEncoder.encode("State_code", "UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")  + "&"
                        + URLEncoder.encode("Remark", "UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")+ "&"
                        + URLEncoder.encode("Remark1", "UTF-8") + "=" + URLEncoder.encode( "-", "UTF-8");

                Log.d("urlInsertTrPostdata", post_data);
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
            } catch (
                    MalformedURLException e) {
                result = e.getMessage();
                Log.i("SaveError1", result);
                e.printStackTrace();
            } catch (
                    IOException e) {
                result = e.getMessage();
                Log.i("SaveError2", result);
                e.printStackTrace();
            }
            return result;
        }
    }

    private class ShowChickRateArea extends AsyncTask<String, String, String> {
        String resultedData="";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ChicksrateArea.this);
            //pDialog.setProgressStyle(ProgressDialog.);
            pDialog.setMessage("Retriving the Demand Data List. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String z = "";
            //String url_string = "http://117.240.18.180:91/mynew.asmx/GetMobileNumber";
            String url_string = "http://117.240.18.180:91/mynew.asmx/ShowChickRateArea";
            try {
                URL url = new URL(url_string);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Hatch_Date", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&"
                        + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") ;
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    resultedData += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return resultedData;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultedData;
        }
        protected void onPostExecute(String r) {
            pDialog.dismiss();
          //  Toast.makeText(ChicksrateArea.this, r, Toast.LENGTH_SHORT).show();
           Log.i("rate",r);
            jobj = new JSONObject();
            if (r != "") {
                //; Toast.makeText(TrActivity.this, "R!==", Toast.LENGTH_SHORT).show();
                try {
                    // Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
                    JSONObject object;
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("Chicks_Rate_Area");
                    items_rate.clear();

                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        HashMap<String, String> contact = new HashMap();
                        contact.put("Name", "Hatch_Date: " + jobj.getString("Hatch_Date"));
                        contact.put("TrNo", "Area: " + jobj.getString("Area"));
                        contact.put("TrDate", "ChickType: " + jobj.getString("ChickType"));
                        contact.put("TrAmt", "Rate: " + jobj.getString("Rate"));
                        items_rate.add(contact);
                        Log.i("rate1",  items_rate.get(i) + jobj.getString("Rate"));
                    }

                    ListAdapter adapter1 = new SimpleAdapter(
                            getApplicationContext(),  items_rate,
                            R.layout.list_chickrate, new String[]{"Name","TrNo","TrDate","TrAmt"}, new int[]{R.id.Custcode1,R.id.Custname1,R.id.CustTrno1,R.id.CustTrAmt1});
                 //   ArrayAdapter adapter = new ArrayAdapter(ChicksrateArea.this, android.R.layout.simple_spinner_item,  items_rate);
                    lvChickRate.setAdapter(adapter1);

                }catch (Exception ex) {
                    Toast.makeText(ChicksrateArea.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Log.i("err",ex.getMessage().toLowerCase());
                }
            } else {

            }
        }
    }

    public boolean  Validate_Data()
    {
        if (edtxtHatchDateChickRateArea.getText().toString().isEmpty() == true) {
             edtxtHatchDateChickRateArea.setError("Please Entry Hatch Date ..");
            edtxtHatchDateChickRateArea.setFocusable(true);

            return false;
        }else if (edtxtRate.getText().toString().isEmpty() == true) {
          edtxtRate.setError("Plese Entry Rate ");
            return false;
        }
        if (flag==false) {
            Toast.makeText(getApplicationContext(), "Select Area Name ",
                    Toast.LENGTH_SHORT).show();
            autoListArea.setError("Select Area Name ....");
            return false;
        }
        else
		 return true;
    }

}// end classs
