package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

public class ListDemandActivity extends AppCompatActivity {

    Button btnShowdemandData;
    TextView tvdemandName;
    ImageView btnGetdemandHdate;
    EditText edtxtdemandHDate;
    ListView lvDemand;
    int mYear = 0, mMonth = 0, mDay = 0;
    String UserName,AreaCode,AreaName,UserType;
    JSONObject jobj;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>>  items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_demand);

        btnShowdemandData = (Button)findViewById(R.id.btnShowdemandData);
        tvdemandName = (TextView)findViewById(R.id.tvuserlistdemand);
        btnGetdemandHdate = (ImageView)findViewById(R.id.btnGetsHatchDateDemand);
        edtxtdemandHDate = (EditText)findViewById(R.id.dtpDemandHAtchdate);
        lvDemand = (ListView)findViewById(R.id.list_demand);

        SharedPreferences sharedPreferences = getSharedPreferences("PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");
        tvdemandName.setText("User:- " + UserName + "   Area Name:- " + AreaName);
        items= new ArrayList<HashMap<String, String>> ();
    }

    public void GetDHatchDate(View view) {
        // Toast.makeText(this,"Get date", Toast.LENGTH_LONG).show();
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtxtdemandHDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }//end b

    public void ShowDemanddata(View view)
    {
        ShowDemandData showDemandData = new ShowDemandData();
        showDemandData.execute(AreaCode,UserName,edtxtdemandHDate.getText().toString());
    }

    private class ShowDemandData extends AsyncTask<String, String, String> {
        String resultedData="";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ListDemandActivity.this);
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
            String url_string = "http://117.240.18.180:91/mynew.asmx/ShowDemandData";
            try {
                URL url = new URL(url_string);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&"
                        + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&"
                        + URLEncoder.encode("DMDate", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") ;

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
         //  Toast.makeText(ListDemandActivity.this, r, Toast.LENGTH_SHORT).show();
            jobj = new JSONObject();
            if (r != "") {
                //; Toast.makeText(TrActivity.this, "R!==", Toast.LENGTH_SHORT).show();
                try {
                    // Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
                    JSONObject object;
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("DemandData");
                    items.clear();

                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        HashMap<String, String> contact = new HashMap();
                        //  Toast.makeText(ListTrActivity.this, jobj.getString("TRNo")+jobj.getString("TrDate")+jobj.getString("TrAmount"), Toast.LENGTH_SHORT).show();
                        contact.put("Name", "Name: " + jobj.getString("CName") + "#" + jobj.getString("Code"));
                        // contact.put("Code", "Code: " + jobj.getString("Code"));
                        contact.put("TrNo", "DemandNo: " + jobj.getString("DMNo"));
                        contact.put("TrDate", "Hatch Date: " + jobj.getString("HatchDate"));
                        contact.put("TrAmt", "Qty-Chicks: " + jobj.getString("TotalChicks"));
                        //contact.put("Mortality", "Mortality: " + jobj.getString("Mortality"));
                        items.add(contact);
                        //Toast.makeText(ListTrActivity.this, jobj.getString("TrAmount").toString(), Toast.LENGTH_SHORT).show();
                    }

                    ListAdapter adapter = new SimpleAdapter(
                            getApplicationContext(), items,
                            R.layout.list_info, new String[]{"Name", "TrNo",
                            "TrDate", "TrAmt", "-"}, new int[]{R.id.Custname,
                            R.id.Custcode, R.id.CustTrno, R.id.CustTrAmt, R.id.textView1});
                    lvDemand.setAdapter(adapter);

                }catch (Exception ex) {
                    //Toast.makeText(ListTrActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    //Log.i("err",ex.getMessage().toLowerCase());
                }
            } else {

            }
        }
    }
}
