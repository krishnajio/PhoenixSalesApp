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

public class HatchdataActivity extends AppCompatActivity {

    TextView tvhatcheryUserName;
    Button btnSavehatchDetials;
    EditText edtxtHatchDate,edQty,edNofBox;
    AutoCompleteTextView autoListarea,autoListvehicle,autoListParty,autoListDriver;
    Spinner spinchickType;
    ListView lvChickRate;
    String AreaNameChickRate;
    int mYear = 0, mMonth = 0, mDay = 0;
    private String[] ChicksTypeSupp = {"Broiler","Broiler(M+)", "Layer", "Cockerel"};
    private String[] PayType = { "CHEQUE", "CASH", "TRANSFER", "RTGS", "NEFT" };
    String VarChicksType, VarPAyType, VarPaymntType, VarArea, UserName, AreaCode, AreaName, UserType;
    private ProgressDialog pDialog;
    ArrayList<String> listArea,listVehicle,listParty,listDriver;
    JSONObject jobj;
    String  Hatch_Date,  State, ChickType, Rate, uname, EntryDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String currentDateandTime = sdf.format(new Date());
    String VarBanklist;
    ArrayList<HashMap<String, String>>  items_rate;
    boolean flag;
    String VarDriver , VarParty ,VarVehicle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hatchdata);

        tvhatcheryUserName =(TextView)findViewById(R.id.tvHatcheryusername);
        spinchickType = (Spinner)findViewById(R.id.spinChicksTypehatchery);
        edtxtHatchDate = (EditText)findViewById(R.id.edtxthatchert_hatchdate);

        edQty =(EditText) findViewById(R.id.edtxthatcherychicksqty);
        edNofBox =(EditText) findViewById(R.id.edtxthatcheryboxqty);

        autoListarea = (AutoCompleteTextView)findViewById(R.id.autoLsthatcheryarealist);
        autoListvehicle= (AutoCompleteTextView)findViewById(R.id.autoLstvehicle);
        autoListParty=  (AutoCompleteTextView)findViewById(R.id.autoLsthatcherypartyname);
        autoListDriver = (AutoCompleteTextView)findViewById(R.id.autoLstdriver);
        listArea = new ArrayList<String>();
        listVehicle = new ArrayList<String>();
        listParty = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences("PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");
        tvhatcheryUserName.setText("User:- " + UserName + "   Area Name:- " + AreaName);

        ArrayAdapter chicktype_adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, ChicksTypeSupp);
        chicktype_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinchickType.setAdapter(chicktype_adapter);

        GetVehicleList getVehicleList = new GetVehicleList();
        getVehicleList.execute(AreaCode);

        GetAreaList getAreaList = new GetAreaList();
        getAreaList.execute(AreaCode);

        GetDriverList getDriverList = new GetDriverList();
        getDriverList.execute(AreaCode);

        GetPartyList getPartyList = new GetPartyList();
        getPartyList.execute(AreaCode);

       spinchickType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               VarChicksType = parent.getItemAtPosition(position).toString();
               Toast.makeText(HatchdataActivity.this, VarChicksType, Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

        autoListarea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.d("AutoText", listAuto.getText().toString());
                flag=true;
                Toast.makeText(getApplicationContext(), autoListarea.getText().toString(), Toast.LENGTH_SHORT).show();

                VarArea = autoListarea.getText().toString();
            }
        });

        autoListDriver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.d("AutoText", listAuto.getText().toString());
                flag=true;
                Toast.makeText(getApplicationContext(), autoListDriver.getText().toString(), Toast.LENGTH_SHORT).show();

                VarDriver = autoListDriver.getText().toString();
            }
        });

        autoListParty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.d("AutoText", listAuto.getText().toString());
                flag=true;
                Toast.makeText(getApplicationContext(), autoListParty.getText().toString(), Toast.LENGTH_SHORT).show();
                VarParty = autoListParty.getText().toString();
            }
        });

        autoListvehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.d("AutoText", listAuto.getText().toString());
                flag=true;
                Toast.makeText(getApplicationContext(), autoListvehicle.getText().toString(), Toast.LENGTH_SHORT).show();
                VarVehicle = autoListvehicle.getText().toString();
            }
        });




    }//on Creates


    public boolean  Validate_Data()
    {
        if (edtxtHatchDate.getText().toString().isEmpty() == true) {
            edtxtHatchDate.setError("Plese Entry Hatch Date ..");
            edtxtHatchDate.setFocusable(true);

            return false;
        }else if (edQty.getText().toString().isEmpty() == true) {
            edQty.setError("Plese Entry Chicks Quantity ");
            return false;
        }
    else if (edNofBox.getText().toString().isEmpty() == true) {
        edQty.setError("Plese Entry Box Quantity ");
        return false;
    }

       else if (flag==false) {
            Toast.makeText(getApplicationContext(), "Select Area Name ",
                    Toast.LENGTH_SHORT).show();
            autoListarea.setError("Select Area Name ....");
            return false;
        }
        else if (VarParty.equals("")) {
            Toast.makeText(getApplicationContext(), "Select Area Name ",
                    Toast.LENGTH_SHORT).show();
            autoListarea.setError("Select party Name ....");
            return false;
        }
        else
            return true;
    }

    public void Gethatchdatehatchery(View view)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edtxtHatchDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
            }
        }, mYear, mMonth, mDay);
      //  datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void SaveHatchData(View view)
    {
        //  String AreaCode, String Area, String uname, DateTime HatchDate, String Chick_type, String qty, String NoofBox,
        //        String VehicleNo, String Delevery_Area_Code, String Delevery_Area_Name, String Code, String CName)

        if(Validate_Data()==true) {
            String darea[] = VarArea.split("#");
            String party[] = VarParty.split("#");
            SaveHatcheryData saveHatcheryData = new SaveHatcheryData();
            saveHatcheryData.execute(AreaCode, AreaName, UserName, edtxtHatchDate.getText().toString(), VarChicksType,
                    edQty.getText().toString(), edNofBox.getText().toString(), VarVehicle, darea[1], darea[0], party[1], party[0]);

            Toast.makeText(getApplicationContext(), VarVehicle, Toast.LENGTH_SHORT).show();
        }

    }

    public class GetAreaList extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(HatchdataActivity.this);
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
                    autoListarea.setThreshold(1);
                    ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listArea);

                    autoListarea.setAdapter(mArrayAdapter);

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

    public class GetVehicleList extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
           // pDialog = new ProgressDialog(HatchdataActivity.this);
            //pDialog.setProgressStyle(ProgressDialog.);
            //pDialog.setMessage("Retriving the Vehicle List. Please wait...");
            //pDialog.setIndeterminate(false);
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
           // pDialog.dismiss();
            //  Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
            jobj = new JSONObject();
            if (r != "") {
                //; Toast.makeText(TrActivity.this, "R!==", Toast.LENGTH_SHORT).show();
                try {
                    // Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
                    JSONObject object;
                    listVehicle = new ArrayList<String>();
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("VehicleList");
                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        listVehicle.add(jobj.getString("REGNO"));
                    }
                    autoListvehicle.setThreshold(1);
                    ArrayAdapter<String> mArrayAdapterveh = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listVehicle);

                    autoListvehicle.setAdapter(mArrayAdapterveh);

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
            String login_url = "http://117.240.18.180:91/mynew.asmx/GetVehicleList";
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

    public class GetDriverList extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
            // pDialog = new ProgressDialog(HatchdataActivity.this);
            //pDialog.setProgressStyle(ProgressDialog.);
            //pDialog.setMessage("Retriving the Vehicle List. Please wait...");
            //pDialog.setIndeterminate(false);
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            // pDialog.dismiss();
            //  Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
            jobj = new JSONObject();
            if (r != "") {
                //; Toast.makeText(TrActivity.this, "R!==", Toast.LENGTH_SHORT).show();
                try {
                    // Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
                    JSONObject object;
                    listDriver = new ArrayList<String>();
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("DriverList");
                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        listDriver.add(jobj.getString("Driver"));
                    }
                    autoListParty.setThreshold(1);
                    ArrayAdapter<String> mArrayAdapterdriver = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listDriver);

                    autoListDriver.setAdapter(mArrayAdapterdriver);

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
            String login_url = "http://117.240.18.180:91/mynew.asmx/GetDriverList";
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

    public class GetPartyList extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";

        @Override
        protected void onPreExecute() {
            // pDialog = new ProgressDialog(HatchdataActivity.this);
            //pDialog.setProgressStyle(ProgressDialog.);
            //pDialog.setMessage("Retriving the Vehicle List. Please wait...");
            //pDialog.setIndeterminate(false);
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            // pDialog.dismiss();
            //  Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
            jobj = new JSONObject();
            if (r != "") {
                //; Toast.makeText(TrActivity.this, "R!==", Toast.LENGTH_SHORT).show();
                try {
                    // Toast.makeText(TrActivity.this, r, Toast.LENGTH_SHORT).show();
                    JSONObject object;
                    listParty = new ArrayList<String>();
                    object = new JSONObject(r);
                    JSONArray jarray1 = object.getJSONArray("PartyList");
                    for (int i = 0; i < jarray1.length(); i++) {
                        jobj = jarray1.getJSONObject(i);
                        listParty.add(jobj.getString("Party"));
                    }
                    autoListParty.setThreshold(1);
                    ArrayAdapter<String> mArrayAdapterdriver = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, listParty);

                    autoListParty.setAdapter(mArrayAdapterdriver);

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
            String login_url = "http://117.240.18.180:91/mynew.asmx/GetPartyList";
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


    public class SaveHatcheryData extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";
        @Override
        protected void onPreExecute() {
           // pDialog = new ProgressDialog(HatchdataActivity.this);
           // pDialog.setMessage("Saving the Hatchery Data. Please wait...");
            //pDialog.setIndeterminate(false);
            //pDialog.setCancelable(false);
            //pDialog.show();
        }
        @Override
        protected void onPostExecute(String r) {
            //pDialog.dismiss();
          //   Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
            Log.i("str", r);
           edNofBox.setText("");
           edQty.setText("");
           edtxtHatchDate.setText("");
            int intIndex = r.indexOf("Success");
            if (intIndex != -1) {
                Toast.makeText(getApplicationContext(), "Hatch data Saved Sucessfully,Redirecting to Main Menu...", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), Hatchery_MenuActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), r, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Hatchery_MenuActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {
          //  result ="result:-" + params[2];
           try {

                //  String AreaCode, String Area, String uname, DateTime HatchDate, String Chick_type, String qty, String NoofBox,
                //        String VehicleNo, String Delevery_Area_Code, String Delevery_Area_Name, String Code, String CName)

                String login_url = "http://117.240.18.180:91/mynew.asmx/InsertHatcheryData";
                Log.d("urlInsertTr", "Inserttr");
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Log.d("urlInsertTr", "Inserttr");
                String post_data = URLEncoder.encode("Area", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&"
                        + URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&"
                        + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&"
                        + URLEncoder.encode("HatchDate", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&"
                        + URLEncoder.encode("Chick_type", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8") + "&"
                        + URLEncoder.encode("qty", "UTF-8") + "=" + URLEncoder.encode(params[5], "UTF-8") + "&"
                        + URLEncoder.encode("NoofBox", "UTF-8") + "=" + URLEncoder.encode(params[6], "UTF-8") + "&"
                        + URLEncoder.encode("VehicleNo", "UTF-8") + "=" + URLEncoder.encode(params[7], "UTF-8") + "&"
                        + URLEncoder.encode("Delevery_Area_Code", "UTF-8") + "=" + URLEncoder.encode(params[8], "UTF-8") + "&"
                        + URLEncoder.encode("Delevery_Area_Name", "UTF-8") + "=" + URLEncoder.encode(params[9], "UTF-8") + "&"
                        + URLEncoder.encode("Code", "UTF-8") + "=" + URLEncoder.encode(params[10], "UTF-8") + "&"
                        + URLEncoder.encode("CName", "UTF-8") + "=" + URLEncoder.encode(params[11], "UTF-8") ;
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
          //  }
             return result;
        }
    }



}// end mains
