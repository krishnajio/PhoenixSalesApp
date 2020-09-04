package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ListView;
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

public class BankDepositeActivity extends AppCompatActivity {

    TextView tvBankdepoUserName;
    Button btnSavebankDeposite;
    EditText edtxtHatchBankDepo,edtxtBAnkDepoEntryDate,edtxtBankDeppositeDate,edtxtDepAmount,edtxtDepoVouNo,edtxtBankdocNo,edtxtBankdepoRemarks;
    AutoCompleteTextView autoListBank;
    Spinner spinPayType;
    ListView lvChickRate;
    String AreaNameChickRate;
    int mYear = 0, mMonth = 0, mDay = 0;
    private String[] ChicksTypeSupp = {"Broiler","Broiler(M+)", "Layer", "Cockerel"};
    private String[] PayType = { "CHEQUE", "CASH", "TRANSFER", "RTGS", "NEFT" };
    String VarChicksType, VarPAyType, VarPaymntType, VarArea, UserName, AreaCode, AreaName, UserType;
    private ProgressDialog pDialog;
    ArrayList<String> listArea;
    JSONObject jobj;
    String  Hatch_Date,  State, ChickType, Rate, uname, EntryDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String currentDateandTime = sdf.format(new Date());
    String VarBanklist;
    ArrayList<HashMap<String, String>>  items_rate;
    ArrayList<String> listBank;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_deposite);
        tvBankdepoUserName = (TextView) findViewById(R.id.tvBakDepUsername);
        edtxtBAnkDepoEntryDate = (EditText)findViewById(R.id.edtxtBankdepoEntryDate);
        edtxtHatchBankDepo =  (EditText)findViewById(R.id.edtxtBankdepoHatchDate);
        edtxtBankDeppositeDate =  (EditText)findViewById(R.id.edtxtBankdepositehDate);
        edtxtDepAmount =   (EditText)findViewById(R.id.edtxtBankdepoAmount);
        edtxtDepoVouNo =(EditText)findViewById(R.id.edtxtDepoVouNo);
        edtxtBankdocNo = (EditText) findViewById(R.id.edtxtBankdocNo);
        edtxtBankdepoRemarks = (EditText) findViewById(R.id.edtxtBankdepoRemarks);
        spinPayType =(Spinner)findViewById(R.id.spnPaymode);
        autoListBank =(AutoCompleteTextView)findViewById(R.id.autoLstBankList);

        SharedPreferences sharedPreferences = getSharedPreferences("PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");
        tvBankdepoUserName.setText("User:- " + UserName + "   Area Name:- " + AreaName);

        ArrayAdapter paytype_adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, PayType);
        paytype_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPayType.setAdapter(paytype_adapter);

        spinPayType
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        VarPAyType = arg0.getItemAtPosition(arg2).toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
        BankList bankList = new BankList();
        bankList.execute(AreaCode);

        autoListBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VarBanklist = autoListBank.getText().toString();
                flag = true;
            }
        });

        GenNextVouNo genNextVouNo = new GenNextVouNo();
        genNextVouNo.execute(AreaCode);
    }// end on Create
    public void GetBankVoudate(View view)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtxtBAnkDepoEntryDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void GetBankDepoHatchdate(View view)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtxtHatchBankDepo.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
            }
        }, mYear, mMonth, mDay);
       // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void GetBankDepoDate(View view)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtxtBankDeppositeDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
            }
        }, mYear, mMonth, mDay);
       // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void SaveDepositeDetials(View view)
    {
        if (Validate_Data()==true) {
            GenNextVouNo genNextVouNo = new GenNextVouNo();
            genNextVouNo.execute(AreaCode);

            String Bank[] = VarBanklist.split("#");
         //(String AreaCode, String Area, String VouNo, DateTime VouDate, String Code, String CName,
           //         DateTime HatchDate, String DepositeAmount, String Deposite_Mode, String DD_No, String Remarks, String uname, DateTime entrydate, DateTime BankDate)
            Toast.makeText(getApplicationContext(),VarPAyType + edtxtBankdocNo.getText().toString()+edtxtBankdepoRemarks.getText().toString(),Toast.LENGTH_LONG).show();
            SaveBankDepoeData saveBankDepoeData = new SaveBankDepoeData();
            saveBankDepoeData.execute(AreaCode,AreaName,edtxtDepoVouNo.getText().toString(),edtxtBAnkDepoEntryDate.getText().toString(),
            Bank[1],Bank[0],edtxtHatchBankDepo.getText().toString(),edtxtDepAmount.getText().toString(),VarPAyType,edtxtBankdocNo.getText().toString(),
            edtxtBankdepoRemarks.getText().toString(),UserName,currentDateandTime, edtxtBankDeppositeDate.getText().toString());
        }
    }

    public class BankList extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(BankDepositeActivity.this);
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
                    listBank = new ArrayList<String>();
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("BankList");
                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        listBank.add(jobj.getString("Bank"));
                    }
                    autoListBank.setThreshold(1);
                    ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listBank);
                    autoListBank.setAdapter(mArrayAdapter);

                } catch (Exception ex) {  }
            }else      {
            }
        }
        @Override
        protected String doInBackground(String... params) {
            //Code for Webservice
            Log.i("conn", "login");
            String area_code = params[0];
            String login_url = "http://117.240.18.180:91/mynew.asmx/GetBankList";
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
                String post_data = URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(area_code, "UTF-8") ;
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
            Log.i("result",result);
            return result;
        }
    }

    private class GenNextVouNo extends AsyncTask<String, String, String> {
        String resultedData="";
        @Override
        protected String doInBackground(String... params) {
            String z = "";
            //String url_string = "http://117.240.18.180:91/mynew.asmx/GetMobileNumber";
            String url_string = "http://117.240.18.180:91/mynew.asmx/GetDepositeVouNumber";
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
                JSONArray jarray1 = object.getJSONArray("DepositeVouNumber");
                jobj = jarray1.getJSONObject(0);
                // Log.i("JSONdata", jobj.getString("phone"));
                edtxtDepoVouNo.setText( jobj.getString("JCNo"));
                //Toast.makeText(TrActivity.this, "Mobile"+ jobj.getString("phone"), Toast.LENGTH_SHORT).show();
                //////////////

            }
            catch(Exception ex) {
                //Toast.makeText(TrActivity.this, "Mobile"+ ex.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    public boolean  Validate_Data()
    {
        if (edtxtDepAmount.getText().toString().isEmpty() == true) {
            Toast.makeText(getApplicationContext(), "Enter Deposite Amount ",
                    Toast.LENGTH_SHORT).show();
            edtxtDepAmount.setError("Plese Entry Deposite Chicks");
            edtxtDepAmount.setFocusable(true);

            return false;
        }else if (edtxtBAnkDepoEntryDate.getText().toString().isEmpty() == true) {
            Toast.makeText(getApplicationContext(), "Select Date ",
                    Toast.LENGTH_SHORT).show();
            edtxtBAnkDepoEntryDate.setError("Plese Entry  Date");
            return false;
        }
        if (flag == false) {
            Toast.makeText(getApplicationContext(), "Select Bank Name ",
                    Toast.LENGTH_SHORT).show();
            autoListBank.setError("Select Bank ....");
            return false;
        }
        else if (edtxtHatchBankDepo.getText().toString().isEmpty() == true) {
            Toast.makeText(getApplicationContext(), "Select Hatch Date ",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // return true;
        return true;
    }


    public class SaveBankDepoeData extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(BankDepositeActivity.this);
            pDialog.setMessage("Saving the DM Data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected void onPostExecute(String r) {
            pDialog.dismiss();
             Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
            //Log.i("str", r);
            int intIndex = r.indexOf("Success");
            if (intIndex != -1) {
                Toast.makeText(getApplicationContext(), "Deposite Data Saved Sucessfully,Redirecting to Main Menu...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SalesMenuActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), r, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SalesMenuActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {
         Log.i("SaveBankDepo","Into do loop" + params[12].toString() );


           try {

            //(String AreaCode, String Area, String VouNo, DateTime VouDate, String Code, String CName,
              //          DateTime HatchDate, String DepositeAmount, String Deposite_Mode, String DD_No, String Remarks, String uname, DateTime entrydate)
                String login_url = "http://117.240.18.180:91/mynew.asmx/InsertDepositeData";
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
                String post_data = URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&"
                        + URLEncoder.encode("Area", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&"
                        + URLEncoder.encode("VouNo", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&"
                        + URLEncoder.encode("VouDate", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&"
                        + URLEncoder.encode("Code", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&"
                        + URLEncoder.encode("CName", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&"
                        + URLEncoder.encode("HatchDate", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8") + "&"
                        + URLEncoder.encode("DepositeAmount", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8") + "&"
                        + URLEncoder.encode("Deposite_Mode", "UTF-8") + "=" + URLEncoder.encode(params[8], "UTF-8") + "&"
                        + URLEncoder.encode("DD_No", "UTF-8") + "=" + URLEncoder.encode(params[9], "UTF-8") + "&"
                        + URLEncoder.encode("Remarks", "UTF-8") + "=" + URLEncoder.encode(params[10], "UTF-8") + "&"
                        + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(params[11], "UTF-8") + "&"
                        + URLEncoder.encode("entrydate", "UTF-8") + "=" + URLEncoder.encode(params[12], "UTF-8")+ "&"
                        + URLEncoder.encode("BankDate", "UTF-8") + "=" + URLEncoder.encode(params[13], "UTF-8");
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
            return result="";
        }
    }
}// emd main
