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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.HashMap;

public class DailyExpensesActivity extends AppCompatActivity {

    Button btn_SaveExpenses,btn_ShowExpenses;
    EditText edtxt_ExpVouno,edtxt_ExpAmount,edtxt_ExpRemark,edtxt_ExpDate;
    AutoCompleteTextView aut_ExpType;
    ListView lst_ExpData;
    ImageView img_CalenderExp;
    TextView tvUserNameDailyExpEntry;
    int mYear = 0, mMonth = 0, mDay = 0;
    JSONObject jobj;
    ArrayList<String> listExpType;boolean flag=false;
    String VarChicksType, VarPAyType, VarPaymntType, VarExpType, UserName, AreaCode, AreaName, UserType;
    ArrayList<HashMap<String, String>>  list_exp;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_expenses);

        //imgDailyExpDateEntry
        img_CalenderExp =(ImageView)findViewById(R.id.imgDailyExpDateEntry);
        //btnSaveExpData btnShowExpData
        btn_SaveExpenses= (Button)findViewById(R.id.btnSaveExpData);
        btn_ShowExpenses= (Button)findViewById(R.id.btnShowExpData);

        //dtpDailyExpDateEntry edtxtDailyExpVouNo edtxtExpensesAmount edtxtExpensesRemark
        edtxt_ExpVouno = (EditText)findViewById(R.id.edtxtDailyExpVouNo);
        edtxt_ExpAmount = (EditText)findViewById(R.id.edtxtExpensesAmount);
        edtxt_ExpRemark = (EditText)findViewById(R.id.edtxtExpensesRemark);
        edtxt_ExpDate = (EditText)findViewById(R.id.dtpDailyExpDateEntry);
        //autoListExpensesType
        aut_ExpType = (AutoCompleteTextView)findViewById(R.id.autoListExpensesType);
        tvUserNameDailyExpEntry =(TextView) findViewById(R.id.txtUserNameDailyExpEntry);
        //lvExpensesData
        lst_ExpData = (ListView)findViewById(R.id.lvExpensesData);
        list_exp = new  ArrayList<HashMap<String, String>>();
        SharedPreferences sharedPreferences = getSharedPreferences(
                "PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");

        tvUserNameDailyExpEntry.setText("User:- " + UserName + "   Area Name:- " + AreaName);


        aut_ExpType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.d("AutoText", listAuto.getText().toString());
                Toast.makeText(getApplicationContext(), aut_ExpType.getText().toString(), Toast.LENGTH_LONG).show();
                VarExpType = aut_ExpType.getText().toString();
                flag = true;

            }
        });

        GenExpNo genExpNo = new GenExpNo();
        genExpNo.execute(AreaCode);

        ExpTypeList expTypeList = new ExpTypeList();
        expTypeList.execute(AreaCode);


    }//end on create

    //onClickGetExpDate
    public void onClickGetExpDate(View view)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtxt_ExpDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void onClickSaveExpenses(View view)
    {
        if(Validate_Data()==true) {
            //String AreaCode, String Area, String uname, String ExpNo,
            // DateTime ExpDate, String ExpenseType,  String ExpenseAmount, String Remarks
            GenExpNo genExpNo = new GenExpNo();
            genExpNo.execute(AreaCode);
            SaveExpData saveExpData = new SaveExpData();
            saveExpData.execute(AreaCode, AreaName, UserName, edtxt_ExpVouno.getText().toString(), edtxt_ExpDate.getText().toString(),
                    VarExpType, edtxt_ExpAmount.getText().toString(), edtxt_ExpRemark.getText().toString());
        }
    }
    public  void onClickShowExpData(View view)
    {
        ShowExpData showExpData = new ShowExpData();
        showExpData.execute(edtxt_ExpDate.getText().toString(),UserName);
    }

    private class GenExpNo extends AsyncTask<String, String, String> {
        String resultedData = "";

        @Override
        protected String doInBackground(String... params) {
            String z = "";
            //String url_string = "http://117.240.18.180:91/mynew.asmx/GetMobileNumber";
            String url_string = "http://117.240.18.180:91/mynew.asmx/GetExpNo";
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
                JSONArray jarray1 = object.getJSONArray("ExpenseNo");
                jobj = jarray1.getJSONObject(0);
                // Log.i("JSONdata", jobj.getString("phone"));
                edtxt_ExpVouno.setText(jobj.getString("EXPNo"));
                //Toast.makeText(TrActivity.this, "Mobile"+ jobj.getString("phone"), Toast.LENGTH_SHORT).show();
                //////////////
            } catch (Exception ex) {
                //Toast.makeText(TrActivity.this, "Mobile"+ ex.getMessage(), Toast.LENGTH_SHORT).show();
                edtxt_ExpVouno.setText(ex.getMessage());
            }
        }
    }//end exp no
    public class ExpTypeList extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(DailyExpensesActivity.this);
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
                    listExpType = new ArrayList<String>();
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("Area_Expenses_Master");
                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        listExpType.add(jobj.getString("Expense_head"));
                    }
                    aut_ExpType.setThreshold(1);
                    ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listExpType);
                    aut_ExpType.setAdapter(mArrayAdapter);

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
            String login_url = "http://117.240.18.180:91/mynew.asmx/GetExpsType";
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

    public class SaveExpData extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(DailyExpensesActivity.this);
            pDialog.setMessage("Saving the Expenses Data. Please wait...");
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
               edtxt_ExpDate.setText("");
               edtxt_ExpAmount.setText("");
               edtxt_ExpVouno.setText("");
                Toast.makeText(getApplicationContext(), "Expenses  Data Saved Sucessfully,Redirecting to Main Menu...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), DailyExpensesActivity.class);
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

                //String AreaCode, String Area, String uname, String ExpNo,
                // DateTime ExpDate, String ExpenseType,  String ExpenseAmount, String Remarks

                String login_url = "http://117.240.18.180:91/mynew.asmx/InsertExpsData";
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Log.d("urlInsertTr", "Inserttr");
                //String AreaCode, String Area, String TRNo, DateTime TrDate, DateTime HatchDate,
                // String uname, String Code, String CName, String Pay_mode, String DD_No, String Bank_det, String Pay_type, String Chick_type,String CihckRate, String Remarks,String TrAmount
                String post_data = URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&"
                        + URLEncoder.encode("Area", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&"
                       + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&"
                        + URLEncoder.encode("ExpNo", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&"
                        + URLEncoder.encode("ExpDate", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&"
                        + URLEncoder.encode("ExpenseType", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&"
                        + URLEncoder.encode("ExpenseAmount", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8") + "&"
                        + URLEncoder.encode("Remarks", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8");

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

    private class ShowExpData extends AsyncTask<String, String, String> {
        String resultedData="";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(DailyExpensesActivity.this);
            //pDialog.setProgressStyle(ProgressDialog.);
            pDialog.setMessage("Retriving the  Data List. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String z = "";
            //String url_string = "http://117.240.18.180:91/mynew.asmx/GetMobileNumber";
            String url_string = "http://117.240.18.180:91/mynew.asmx/ShowAreaExpsData";
            try {
                URL url = new URL(url_string);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Exp_Date", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&"
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
                    JSONArray jarray1 = object.getJSONArray("AreaExpenseData");
                    list_exp.clear();

                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        HashMap<String, String> contact = new HashMap();
                        contact.put("Name", "Vou_no: " + jobj.getString("ExpNo"));
                        contact.put("TrNo", "Exp_Date: " + jobj.getString("ExpDate"));
                        contact.put("TrDate", "Exp_Type: " + jobj.getString("ExpenseType"));
                        contact.put("TrAmt", "Exp_Amount: " + jobj.getString("ExpenseAmount"));
                        list_exp.add(contact);

                    }

                    ListAdapter adapter1 = new SimpleAdapter(
                            getApplicationContext(),  list_exp,
                            R.layout.list_chickrate, new String[]{"Name","TrNo","TrDate","TrAmt"}, new int[]{R.id.Custcode1,R.id.Custname1,R.id.CustTrno1,R.id.CustTrAmt1});
                    //   ArrayAdapter adapter = new ArrayAdapter(ChicksrateArea.this, android.R.layout.simple_spinner_item,  items_rate);
                    lst_ExpData.setAdapter(adapter1);

                }catch (Exception ex) {
                    Toast.makeText(DailyExpensesActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Log.i("err",ex.getMessage().toLowerCase());
                }
            } else {

            }
        }
    }

    public boolean  Validate_Data()
    {
        if (edtxt_ExpDate.getText().toString().isEmpty() == true) {
            Toast.makeText(getApplicationContext(), "Enter date  ",
                    Toast.LENGTH_SHORT).show();
            edtxt_ExpDate.setError("Enter date");
            edtxt_ExpDate.setFocusable(true);

            return false;
        }else if (edtxt_ExpAmount.getText().toString().isEmpty() == true) {
            Toast.makeText(getApplicationContext(), "Please Enter Expenses Amount ",
                    Toast.LENGTH_SHORT).show();
            edtxt_ExpAmount.setError("Please Enter Expenses Amount");
            return false;
        }
        if (flag == false) {
            Toast.makeText(getApplicationContext(), "Select  Expenses Type ",
                    Toast.LENGTH_SHORT).show();
            aut_ExpType.setError("Select Expenses Type ....");
            return false;
        }



        // return true;
        return true;
    }
}// end main class
