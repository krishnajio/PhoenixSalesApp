package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Collections;

public class SupplyActivity extends AppCompatActivity {
    String ChicksType;
    AutoCompleteTextView autListCust;
    Spinner spinChkstype,spinHatchries;
    TextView tvUsNameSupp, tvMobSupp;
    String strSavedMem1, strSavedMem2;
    Calendar myCalendar1;
    EditText dtpSuppDate, dtpHadte, edtxtDmNO, edtxtTotalChicks, edtxtMortality, edtxtRate, edtxtRemarks;
    ImageView btnSuppDate, btnHatchDate;
    Button btnSave;
    String VarChicksType, VarHatchries, VarPaymntType, VarCustomer, UserName, AreaCode, AreaName, UserType;
    private ProgressDialog pDialog;
    String DMNo, DMDate, HatchDate, Code, CName, Pay_mode, DD_No, Bank_det, Pay_type, Chick_type, CihckRate, Remarks, TrAmount, Area, MobileNo, TotalChicks, Mortality;
    DatePickerDialog.OnDateSetListener dsupp, dhatch;
    boolean flag=false;
    private String[] ChicksTypeSupp = {"Broiler","Broiler(M+)", "Layer", "Cockerel"};
    JSONObject jobj;
    ArrayList<String> listCustomer,listHatchries;
    int mYear = 0, mMonth = 0, mDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply);

        spinChkstype = (Spinner) findViewById(R.id.spinsChicksType);
        spinHatchries =(Spinner)findViewById(R.id.spinsHatchries);
        tvUsNameSupp = (TextView) findViewById(R.id.tvuser);
        tvMobSupp = (TextView) findViewById(R.id.textSMobno);
        autListCust = (AutoCompleteTextView) findViewById(R.id.autoListSupply);
        btnSuppDate = (ImageView) findViewById(R.id.btnGetSuppDate);
        btnHatchDate = (ImageView) findViewById(R.id.btnGetsHatchDate);
        edtxtDmNO = (EditText) findViewById(R.id.edtxtDmNO);

        edtxtTotalChicks = (EditText) findViewById(R.id.edtxtChicksQty);
        edtxtMortality = (EditText) findViewById(R.id.edtxtMortalit);
        edtxtRate = (EditText) findViewById(R.id.edtxtRate);
        edtxtRemarks = (EditText) findViewById(R.id.textRemarks);
        dtpSuppDate = (EditText) findViewById(R.id.dtpSuppDate);
        dtpHadte = (EditText) findViewById(R.id.dtpshatchDate);
        myCalendar1 = Calendar.getInstance();
        btnSave = (Button) findViewById(R.id.btnNextDM);


        // -----------------------------------------------------------

        //filling Spinnsers
        ArrayAdapter chick_adapter = new ArrayAdapter(SupplyActivity.this, android.R.layout.simple_spinner_item, ChicksTypeSupp);
        chick_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinChkstype.setAdapter(chick_adapter);

        SharedPreferences sharedPreferences = getSharedPreferences(
                "PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");

        tvUsNameSupp.setText("User:- " + UserName + "   Area Name:- " + AreaName);
        Toast.makeText(SupplyActivity.this, AreaCode, Toast.LENGTH_SHORT).show();

        GenNextDMNo genNextDMNo = new GenNextDMNo();
        genNextDMNo.execute(AreaCode);
        CustomerListForSupply customerlist = new CustomerListForSupply();
        customerlist.execute(AreaCode);

        GetHatchreisListForSupply getHatchreisListForSupply = new GetHatchreisListForSupply();
        getHatchreisListForSupply.execute(AreaCode);

        //On Click Auto List
        autListCust.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.d("AutoText", listAuto.getText().toString());
                Toast.makeText(getApplicationContext(), autListCust.getText().toString(), Toast.LENGTH_LONG).show();
                VarCustomer = autListCust.getText().toString();
                Toast.makeText(SupplyActivity.this, VarCustomer, Toast.LENGTH_SHORT).show();
                 flag = true;
                FtechMobileNoSupplu mobno = new FtechMobileNoSupplu();
                mobno.execute(VarCustomer);
                //Log.d("AutoText", flag);
            }
        });
        spinChkstype
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

        spinHatchries
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        VarHatchries = arg0.getItemAtPosition(arg2).toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
    }// end on create



    public void GetSupplyDate(View view) {
        // Toast.makeText(this,"Get date", Toast.LENGTH_LONG).show();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dtpSuppDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }//end button function Supply Date

    public void GetHdateSupply(View view) {
        // Toast.makeText(this,"Get date", Toast.LENGTH_LONG).show();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dtpHadte.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }//end button function Supply Date

    public void SaveDM(View view) {

        if (Validate_Data()==true ) {
            try {

                GenNextDMNo genNextDMNo = new GenNextDMNo();
                genNextDMNo.execute(AreaCode);
                String Customer[] = VarCustomer.split("#");
                //Toast.makeText(getApplicationContext(), Customer[1], Toast.LENGTH_LONG).show();
                SaveDmData saveDmData = new SaveDmData();
                saveDmData.execute(AreaCode, AreaName, edtxtDmNO.getText().toString(), dtpSuppDate.getText().toString(), dtpHadte.getText().toString(),
                        UserName, Customer[1], Customer[0], VarChicksType, edtxtRate.getText().toString(), edtxtTotalChicks.getText().toString(), edtxtRemarks.getText().toString(),
                        edtxtMortality.getText().toString(),VarHatchries);
                //Toast.makeText(getApplicationContext(), VarCustomer, Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public class CustomerListForSupply extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SupplyActivity.this);
            //pDialog.setProgressStyle(ProgressDialog.);
            pDialog.setMessage("Retriving the Customer List. Please wait...");
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
                    listCustomer = new ArrayList<String>();
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("Customer");
                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        listCustomer.add(jobj.getString("customer"));
                    }
                    autListCust.setThreshold(1);
                    ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listCustomer);
                    autListCust.setAdapter(mArrayAdapter);

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
            String login_url = "http://117.240.18.180:91/mynew.asmx/Customer";
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

    private class FtechMobileNoSupplu extends AsyncTask<String, String, String> {
        String resultedData = "";

        @Override
        protected String doInBackground(String... params) {
            String CustCode[] = params[0].split("#"), z;
            //Toast.makeText(TrActivity.this, "Mobile"+ CustCode[1], Toast.LENGTH_SHORT).show();
            Log.i("Code", CustCode[1]);
            if (CustCode[1].trim().trim().equals("")) {
                z = "Please enter User Id and Password";
            } else {
                //String url_string = "http://117.240.18.180:91/mynew.asmx/GetMobileNumber";
                String url_string = "http://117.240.18.180:91/mynew.asmx/GetMobileNumber";
                try {
                    URL url = new URL(url_string);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("Code", "UTF-8") + "=" + URLEncoder.encode(CustCode[1], "UTF-8");
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
            }
            return resultedData;
        }

        protected void onPostExecute(String r) {
            //Toast.makeText(TrActivity.this, "Mobile"+ r, Toast.LENGTH_SHORT).show();
            //Log.i("mobile", r);
            try {
                //////////////
                JSONObject object;
                object = new JSONObject(r);
                JSONArray jarray1 = object.getJSONArray("MobileNo");
                jobj = jarray1.getJSONObject(0);
                // Log.i("JSONdata", jobj.getString("phone"));
                tvMobSupp.setText(jobj.getString("phone"));
                //Toast.makeText(TrActivity.this, "Mobile"+ jobj.getString("phone"), Toast.LENGTH_SHORT).show();
                //////////////

            } catch (Exception ex) {
                //Toast.makeText(TrActivity.this, "Mobile"+ ex.getMessage(), Toast.LENGTH_SHORT).show();
                tvMobSupp.setText(ex.getMessage());
            }
        }

    }

    private class GenNextDMNo extends AsyncTask<String, String, String> {
        String resultedData = "";

        @Override
        protected String doInBackground(String... params) {
            String z = "";
            //String url_string = "http://117.240.18.180:91/mynew.asmx/GetMobileNumber";
            String url_string = "http://117.240.18.180:91/mynew.asmx/GetDMNumber";
            try {
                URL url = new URL(url_string);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
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
            //Toast.makeText(TrActivity.this, "Mobile"+ r, Toast.LENGTH_SHORT).show();
            //Log.i("mobile", r);
            try {
                //////////////
                JSONObject object;
                object = new JSONObject(r);
                JSONArray jarray1 = object.getJSONArray("DmNo");
                jobj = jarray1.getJSONObject(0);
                // Log.i("JSONdata", jobj.getString("phone"));
                edtxtDmNO.setText(jobj.getString("DmNo"));
                //Toast.makeText(TrActivity.this, "Mobile"+ jobj.getString("phone"), Toast.LENGTH_SHORT).show();
                //////////////
            } catch (Exception ex) {
                //Toast.makeText(TrActivity.this, "Mobile"+ ex.getMessage(), Toast.LENGTH_SHORT).show();
                edtxtDmNO.setText(ex.getMessage());
            }
        }
    }

    public class SaveDmData extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SupplyActivity.this);
            pDialog.setMessage("Saving the DM Data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected void onPostExecute(String r) {
            pDialog.dismiss();
           // Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
            //Log.i("str", r);
            int intIndex = r.indexOf("Success");
            if (intIndex != -1) {
                Toast.makeText(getApplicationContext(), "DM  Date Saved Sucessfully,Redirecting to Main Menu...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SupplyActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), r, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), TrActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // (AreaCode, Area, DMNo, DMDate, HatchDate,  uname, ";
                //" Code, CName,  Chick_type,Rate, TotalChicks, Remarks,Mortality) values('" + AreaCode + "',";
                AreaCode = params[0];
                AreaName = params[1];
                DMNo = params[2];
                DMDate = params[3];
                HatchDate = params[4];
                UserName = params[5];
                Code = params[6];
                CName = params[7];
                Chick_type = params[8];
                CihckRate = params[9];
                TotalChicks = params[10];
                Remarks = params[11];
                Mortality = params[12];
                String login_url = "http://117.240.18.180:91/mynew.asmx/InsertDMData";
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Log.d("urlInsertDm", "Insertdm");
                //String AreaCode, String Area, String TRNo, DateTime TrDate, DateTime HatchDate,
                // String uname, String Code, String CName, String Pay_mode, String DD_No, String Bank_det, String Pay_type, String Chick_type,String CihckRate, String Remarks,String TrAmount
                String post_data = URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(AreaCode, "UTF-8") + "&"
                        + URLEncoder.encode("Area", "UTF-8") + "=" + URLEncoder.encode(AreaName, "UTF-8") + "&"
                        + URLEncoder.encode("DMNo", "UTF-8") + "=" + URLEncoder.encode(DMNo, "UTF-8") + "&"
                        + URLEncoder.encode("DMDate", "UTF-8") + "=" + URLEncoder.encode(DMDate, "UTF-8") + "&"
                        + URLEncoder.encode("HatchDate", "UTF-8") + "=" + URLEncoder.encode(HatchDate, "UTF-8") + "&"
                        + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(UserName, "UTF-8") + "&"
                        + URLEncoder.encode("Code", "UTF-8") + "=" + URLEncoder.encode(Code, "UTF-8") + "&"
                        + URLEncoder.encode("CName", "UTF-8") + "=" + URLEncoder.encode(CName, "UTF-8") + "&"
                        + URLEncoder.encode("Chick_type", "UTF-8") + "=" + URLEncoder.encode(Chick_type, "UTF-8") + "&"
                        + URLEncoder.encode("CihckRate", "UTF-8") + "=" + URLEncoder.encode(CihckRate, "UTF-8") + "&"
                        + URLEncoder.encode("TotalChicks", "UTF-8") + "=" + URLEncoder.encode(TotalChicks, "UTF-8") + "&"
                        + URLEncoder.encode("Remarks", "UTF-8") + "=" + URLEncoder.encode(Remarks, "UTF-8") + "&"
                        + URLEncoder.encode("Mortality", "UTF-8") + "=" + URLEncoder.encode(Mortality, "UTF-8") + "&"
                        + URLEncoder.encode("Hatchries", "UTF-8") + "=" + URLEncoder.encode(params[13], "UTF-8");
                Log.d("urlInsertDmPostdata", post_data);
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
                Log.d("SaveError1", result);
                e.printStackTrace();
            } catch (
                    IOException e) {
                result = e.getMessage();
                Log.d("SaveError2", result);
                e.printStackTrace();
            }
            return result;
        }
        }

    public boolean  Validate_Data()
    {
        if (edtxtTotalChicks.getText().toString().isEmpty() == true) {
            Toast.makeText(getApplicationContext(), "Enter Total Chicks Amount ",
                    Toast.LENGTH_SHORT).show();
            edtxtTotalChicks.setError("Plese Entry Total Chicks");
            edtxtTotalChicks.setFocusable(true);

            return false;
        }else if (dtpSuppDate.getText().toString().isEmpty() == true) {
            Toast.makeText(getApplicationContext(), "Select Supply  Date ",
                    Toast.LENGTH_SHORT).show();
            dtpSuppDate.setError("Plese Entry Supply date Date");
            return false;
        }
        if (flag == false) {
            Toast.makeText(getApplicationContext(), "Select Customer Name ",
                    Toast.LENGTH_SHORT).show();
            autListCust.setError("Select Customer ....");
            return false;
        }
        else if (dtpHadte.getText().toString().isEmpty() == true) {
			Toast.makeText(getApplicationContext(), "Select Hatch Date ",
					Toast.LENGTH_SHORT).show();
			return false;
		}

            // return true;
            return true;
    }

    public class GetHatchreisListForSupply extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
    ;
        }

        @Override
        protected void onPostExecute(String r) {

            //  Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
            jobj = new JSONObject();
            if (r != "") {
                //; Toast.makeText(TrActivity.this, "R!==", Toast.LENGTH_SHORT).show();
                try {
                    // Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
                    JSONObject object;
                    listHatchries = new ArrayList<String>();
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("Hatchries");
                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        listHatchries.add(jobj.getString("Hatcheryies"));
                    }

                    ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listHatchries);
                    spinHatchries.setAdapter(mArrayAdapter);

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
            String login_url = "http://117.240.18.180:91/mynew.asmx/GetHatchriesList";
            //String login_url = "http://117.240.18.180:91/mynew.asmx/Customer";
            // if(type.equals("Login")) {
            try {

                URL url = new URL(login_url);
                Log.d("url", "entered show Hatchries");
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
    }//end main class
