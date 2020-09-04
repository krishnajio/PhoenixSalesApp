package com.phoenix.phoenixsalesapp;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class TrActivity extends AppCompatActivity {

    Button b1, btnSaveTr;
    AutoCompleteTextView listAuto;
    AutoCompleteTextView autotxt1;
    EditText edtxtTrNo, edtxtTrAmount, edtxtChqNo, edtxtBankDet,edtxtRate,edtxtRemarks,edtxtBankDate;
    Calendar myCalendar1;
    Spinner spinChtype, spinPayType, spinPaymentType;
    EditText dtpTrDate, dtpHadte;
    TextView tvUsName,tvMobNo;
    ImageView btnDate, btnHDate;
    private ProgressDialog pDialog;
    DatePickerDialog.OnDateSetListener d, dhatch;
    SharedPreferences shared_trdetails;
    JSONObject jobj;
    ArrayList<String> listCustomer;

    private String[] ChicksType = {"Broiler","Broiler(M+)", "Layer", "Cockerel"};
    private String[] PayType = { "CHEQUE", "CASH", "TRANSFER", "RTGS", "NEFT" };
    private String[] PaymentType = { "Old-Payment", "Current-Payment","Advance-Payment" };
    String VarChicksType, VarPAyType, VarPaymntType,VarCustomer,UserName,AreaCode,AreaName,UserType;
    String flag = "false";
    int mYear=0,mMonth=0,mDay=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tr);

        listAuto = (AutoCompleteTextView) findViewById(R.id.autoList);
        edtxtTrNo = (EditText) findViewById(R.id.edtxtTRno);
        dtpTrDate = (EditText) findViewById(R.id.dtpTrDate);
        edtxtTrAmount = (EditText) findViewById(R.id.edtxtTrAmt);
        spinPayType = (Spinner) findViewById(R.id.spinPaytype);
        edtxtChqNo = (EditText) findViewById(R.id.edtxtNo);
        edtxtBankDet = (EditText) findViewById(R.id.edtxtBankDetails);
        edtxtBankDate= (EditText) findViewById(R.id.edtxtBankDate);
        dtpHadte = (EditText) findViewById(R.id.dtpHatchdate);
        spinChtype = (Spinner) findViewById(R.id.spinChicksType);
        spinPaymentType = (Spinner) findViewById(R.id.spinPaymenttype);
       // myCalendar1 = Calendar.getInstance();
        btnDate = (ImageView) findViewById(R.id.btnGetDate);
        btnHDate = (ImageView) findViewById(R.id.btnGetHatchDate);
        dtpHadte = (EditText) findViewById(R.id.dtpHatchdate);
        tvUsName = (TextView) findViewById(R.id.tvAreaCode);
        btnSaveTr = (Button) findViewById(R.id.btnSave);
        tvMobNo =(TextView)findViewById(R.id.textMobno);
        edtxtRate =(EditText)findViewById(R.id.editRate);
        edtxtRemarks = (EditText)findViewById(R.id.editRemarks);

        shared_trdetails = getSharedPreferences("PhxTrData",0);
        ///loading prev data
        listAuto.setText(shared_trdetails.getString("TrCustomer", ""));
        edtxtTrNo.setText(shared_trdetails.getString("TrNno", ""));
        dtpTrDate.setText(shared_trdetails.getString("Trdate", ""));
        edtxtTrAmount.setText(shared_trdetails.getString("Tramt", ""));
        edtxtChqNo.setText(shared_trdetails.getString("TrChequeNo", ""));
        edtxtBankDet.setText(shared_trdetails.getString("TrBankDet", ""));
        //spinPayType.t   .setText(shared_trdetails.getString("TrpayType", ""));
        //edtxtChicksType.setText(shared_trdetails.getString("TrChicksType", ""));
        dtpHadte.setText(shared_trdetails.getString("TrHatchDate", ""));
        //spinPaymentType.setText(shared_trdetails.getString("TrPAymentType", ""));
        tvMobNo.setText(shared_trdetails.getString("MobileNo", ""));
        edtxtRate.setText(shared_trdetails.getString("ChickRate", ""));
        edtxtRemarks.setText(shared_trdetails.getString("Remarks", ""));
       //------------

       //filling Spinnsers
        ArrayAdapter chick_adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, ChicksType);
        chick_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinChtype.setAdapter(chick_adapter);

        ArrayAdapter paytype_adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, PayType);
        paytype_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPayType.setAdapter(paytype_adapter);

        ArrayAdapter paymenttype_adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, PaymentType);
        paymenttype_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPaymentType.setAdapter(paymenttype_adapter);
       ////////////////////////////////////////////

        SharedPreferences sharedPreferences = getSharedPreferences(
                "PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");

        tvUsName.setText("User:- " + UserName + "   Area Name:- " + AreaName);
        Toast.makeText(TrActivity.this, AreaCode, Toast.LENGTH_SHORT).show();
       //////////////Getting Customer*******************************
        CustomerList customerlist = new CustomerList();
        customerlist.execute(AreaCode);

        GenNextTrNo generatetrno = new GenNextTrNo();
        generatetrno.execute(AreaCode);

        //On Clock Auto List
        listAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.d("AutoText", listAuto.getText().toString());
                Toast.makeText(getApplicationContext(),listAuto.getText().toString(), Toast.LENGTH_SHORT).show();
                VarCustomer= listAuto.getText().toString();
                Toast.makeText(TrActivity.this, VarCustomer, Toast.LENGTH_SHORT).show();
                flag = "true";
               FtechMobileNo mobno = new FtechMobileNo();
                mobno.execute(VarCustomer);
                //Log.d("AutoText", flag);
            }
        });
        spinChtype
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

        spinPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                VarPaymntType = arg0.getItemAtPosition(arg2).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }//end on Create

    public void GetTrDate(View view)
    {
       // Toast.makeText(this,"Get date", Toast.LENGTH_LONG).show();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
         DatePickerDialog datePickerDialog = new DatePickerDialog(this,  new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                        dtpTrDate.setText( (monthOfYear + 1)  + "-" + dayOfMonth + "-" + year);
                    }
                } ,mYear, mMonth, mDay);
        datePickerDialog.show();
    }//end button function GetTr Date

    public void GetHatchDate(View view)
    {   // Toast.makeText(this,"Get date", Toast.LENGTH_LONG).show();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                       // dtpHadte.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        dtpHadte.setText( (monthOfYear + 1)  + "-" + dayOfMonth + "-" + year);
                   }
                } ,mYear, mMonth, mDay);
        datePickerDialog.show();
    }// //end button function  GetHatchDate

    public void GetBankDate(View view)
    {   // Toast.makeText(this,"Get date", Toast.LENGTH_LONG).show();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                        // dtpHadte.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        edtxtBankDate.setText( (monthOfYear + 1)  + "-" + dayOfMonth + "-" + year);
                    }
                } ,mYear, mMonth, mDay);
        datePickerDialog.show();
    }// //end button function  GetHatchDate


    public void btnNext(View view)
    {
        boolean result;
        result = Validate_Data();
        if (result == true) {
            SavePreferences();
            Toast.makeText(getApplicationContext(),"You have Clicked ", Toast.LENGTH_SHORT).show();
            Intent nextPage = new Intent(getApplicationContext(),DisplayTrActivity.class);
            startActivity(nextPage);
        }
    }//end button function  btnNext
    public boolean  Validate_Data()
    {
        if (edtxtTrAmount.getText().toString().isEmpty() == true) {
            Toast.makeText(getApplicationContext(), "Enter TR Amount ",
                    Toast.LENGTH_SHORT).show();
            edtxtTrAmount.setError("Plese Entry Tr Amount");
            edtxtTrAmount.setFocusable(true);

            return false;
        }else if (dtpTrDate.getText().toString().isEmpty() == true) {
            Toast.makeText(getApplicationContext(), "Select TR Date ",
                    Toast.LENGTH_SHORT).show();
            edtxtTrAmount.setError("Plese Entry Tr Date");
            return false;
        }
        if (flag == "false") {
            Toast.makeText(getApplicationContext(), "Select Customer Name ",
                    Toast.LENGTH_SHORT).show();
            listAuto.setError("Select Customer ....");
            return false;
        }
        else
		/*if (dtpHadte.getText().toString().isEmpty() == true) {
			Toast.makeText(getApplicationContext(), "Select Hatch Date ",
					Toast.LENGTH_SHORT).show();
			return false;
		}*/

            // return true;
            return true;
    }
    //Getting Customer List
    public class CustomerList extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(TrActivity.this);
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
                    listAuto.setThreshold(1);
                    ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listCustomer);
                    listAuto.setAdapter(mArrayAdapter);

                } catch (Exception ex) {  }
            }else      {
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

    //Fetchign Mobile Number
    private class FtechMobileNo extends AsyncTask<String, String, String> {
        String resultedData="";
        @Override
        protected String doInBackground(String... params) {
            String CustCode[] = params[0].split("#"),z;
            //Toast.makeText(TrActivity.this, "Mobile"+ CustCode[1], Toast.LENGTH_SHORT).show();
            Log.i("Code",CustCode[1]);
             if (CustCode[1].trim().trim().equals(""))  {
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
                    String post_data = URLEncoder.encode("Code", "UTF-8") + "=" + URLEncoder.encode(CustCode[1], "UTF-8") ;
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
               tvMobNo.setText( jobj.getString("phone"));
               //Toast.makeText(TrActivity.this, "Mobile"+ jobj.getString("phone"), Toast.LENGTH_SHORT).show();
               //////////////
           }
           catch(Exception ex) {
               //Toast.makeText(TrActivity.this, "Mobile"+ ex.getMessage(), Toast.LENGTH_SHORT).show();
               tvMobNo.setText( ex.getMessage());
           }
        }

    }
    //GenNextTrNo
    private class GenNextTrNo extends AsyncTask<String, String, String> {
        String resultedData="";
        @Override
        protected String doInBackground(String... params) {
            String z = "";
            //String url_string = "http://117.240.18.180:91/mynew.asmx/GetMobileNumber";
            String url_string = "http://117.240.18.180:91/mynew.asmx/GetTRNumber";
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
                JSONArray jarray1 = object.getJSONArray("TrNo");
                jobj = jarray1.getJSONObject(0);
                // Log.i("JSONdata", jobj.getString("phone"));
                edtxtTrNo.setText( jobj.getString("trno"));
                //Toast.makeText(TrActivity.this, "Mobile"+ jobj.getString("phone"), Toast.LENGTH_SHORT).show();
                //////////////

            }
            catch(Exception ex) {
                //Toast.makeText(TrActivity.this, "Mobile"+ ex.getMessage(), Toast.LENGTH_SHORT).show();
                tvMobNo.setText( ex.getMessage());
            }
        }
    }

    private void SavePreferences() {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = shared_trdetails.edit();
        editor.clear();
        // if(shared_trdetails.contains("Customer")==true)
        // {
        Toast.makeText(getApplicationContext(), "Before Shared ",Toast.LENGTH_SHORT).show();
        editor.putString("TrCustomer", listAuto.getText().toString());
        editor.putString("TrNno", edtxtTrNo.getText().toString());
        editor.putString("Trdate", dtpTrDate.getText().toString());
        editor.putString("Tramt", edtxtTrAmount.getText().toString());
        editor.putString("TrHatchDate", dtpHadte.getText().toString());
        editor.putString("TrChicksType", VarChicksType.toString());
        editor.putString("TrpayType", VarPAyType.toString());
        editor.putString("TrChequeNo", edtxtChqNo.getText().toString());
        editor.putString("TrBankDet", edtxtBankDet.getText().toString());
        editor.putString("TrPAymentType", VarPaymntType.toString());
        editor.putString("MobileNo", tvMobNo.getText().toString());
        editor.putString("ChickRate", edtxtRate.getText().toString());
        editor.putString("Remarks", edtxtRemarks.getText().toString());
        editor.putString("BankDate", edtxtBankDate.getText().toString());
        editor.commit();
        Toast.makeText(getApplicationContext(),
                "After Shared " + edtxtTrNo.getText().toString(), Toast.LENGTH_SHORT).show();
        // }
    }//end function SavePreferences
}//end main class
