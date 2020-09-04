package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
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

public class DisplayTrActivity extends AppCompatActivity {
    EditText edtxtCust, edTedtxtTrNo, edTedtxtTrDate, edtxtTrAmount,
            edtxtChqNo, edtxtBankDet, edtxtHatchDate, edtxtChicksType,
            edtxtPayMode, edtxtMobno,edtxtChickRate,edtxtRemarks,edtxtBankDatetr;
    EditText edtxtpayType;
    Button btnSave,btnPrev;
    TextView tvUsName;
    String UserName,AreaCode,AreaName,UserType;
    private ProgressDialog pDialog;
    String  TRNo,TrDate, HatchDate,Code,CName,Pay_mode,DD_No,Bank_det,Pay_type,Chick_type,CihckRate,Remarks,TrAmount,Area,MobileNo,BankdepDate;
    String strSavedMem1, strSavedMem2;
    SharedPreferences shared_trdetails;
    String SmsMsg;
    JSONObject jobj;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tr);

        btnSave = (Button) findViewById(R.id.btnSave);
        edtxtCust = (EditText) findViewById(R.id.edtxtCustomer);
        edTedtxtTrNo = (EditText) findViewById(R.id.edtxtTrNo);
        edTedtxtTrDate = (EditText) findViewById(R.id.edtxtTrDate);
        edtxtTrAmount = (EditText) findViewById(R.id.edtxtTrAmount);
        edtxtChqNo = (EditText) findViewById(R.id.edtxtChqNo);
        edtxtBankDet = (EditText) findViewById(R.id.edtxtBAnkDet);
        edtxtPayMode = (EditText) findViewById(R.id.edtxtpaymode);
        edtxtChicksType = (EditText) findViewById(R.id.edtxtChicksType);
        edtxtHatchDate = (EditText) findViewById(R.id.edtxtHdate);
        edtxtpayType = (EditText) findViewById(R.id.edtxtPayType);
        edtxtMobno = (EditText) findViewById(R.id.edtxtMobNo);
        tvUsName = (TextView) findViewById(R.id.tv8);
        btnPrev = (Button)findViewById(R.id.btnPrev);
        edtxtChickRate =(EditText)findViewById(R.id.edtxtrate);
        edtxtRemarks= (EditText)findViewById(R.id.edtxRemarks);
        edtxtBankDatetr = (EditText)findViewById(R.id.edtxtBankDatetr);
        shared_trdetails = getSharedPreferences("PhxTrData",0);
        // Toast.makeText(getApplicationContext(), shCustName,
        // Toast.LENGTH_SHORT).show();
        // Retrivee value by DShared Preferences'
        edtxtCust.setText(shared_trdetails.getString("TrCustomer", ""));
        edTedtxtTrNo.setText(shared_trdetails.getString("TrNno", ""));
        edTedtxtTrDate.setText(shared_trdetails.getString("Trdate", ""));
        edtxtTrAmount.setText(shared_trdetails.getString("Tramt", ""));
        edtxtChqNo.setText(shared_trdetails.getString("TrChequeNo", ""));
        edtxtBankDet.setText(shared_trdetails.getString("TrBankDet", ""));
        edtxtPayMode.setText(shared_trdetails.getString("TrpayType", ""));
        edtxtChicksType.setText(shared_trdetails.getString("TrChicksType", ""));
        if ((shared_trdetails.getString("TrHatchDate", "").isEmpty() == true))
            edtxtHatchDate.setText("1-1-2000");
        else
            edtxtHatchDate.setText(shared_trdetails
                    .getString("TrHatchDate", ""));
        edtxtpayType.setText(shared_trdetails.getString("TrPAymentType", ""));
        edtxtMobno.setText(shared_trdetails.getString("MobileNo", ""));

        edtxtChickRate.setText(shared_trdetails.getString("ChickRate", ""));
        edtxtRemarks.setText(shared_trdetails.getString("Remarks", ""));
        edtxtBankDatetr.setText(shared_trdetails.getString("BankDate", ""));
        // java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        SharedPreferences sharedPreferences = getSharedPreferences("PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");
        tvUsName.setText("User:- " + UserName + "   Area Name:- " + AreaName);

        checkForSmsPermission();
    }// end on ctreate

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Permission already granted. Enable the SMS button.
            // enableSmsButton();

        }
    }
    public void Previous(View view)
    {
        Intent intent = new Intent(getApplicationContext(),TrActivity.class);
        startActivity(intent);
    }
    public void SaveTr(View view)
    {

        GenNextTrNo genNextTrNo = new GenNextTrNo();
        genNextTrNo.execute(AreaCode);

        String Customer[] = edtxtCust.getText().toString().split("#");
        SaveTrData saveTrData = new SaveTrData();
        saveTrData.execute(AreaCode,AreaName,edTedtxtTrNo.getText().toString(),edTedtxtTrDate.getText().toString(),edtxtHatchDate.getText().toString(),
        UserName,Customer[1],Customer[0],edtxtPayMode.getText().toString(),edtxtChqNo.getText().toString(),edtxtBankDet.getText().toString(),edtxtpayType.getText().toString(),
        edtxtChicksType.getText().toString(),edtxtChickRate.getText().toString(),edtxtRemarks.getText().toString(),edtxtTrAmount.getText().toString(),edtxtBankDatetr.getText().toString());

        SmsMsg ="Received with thanks "+ edtxtPayMode.getText().toString() + " Rs " + edtxtTrAmount.getText()+ " @ "  + edtxtChickRate.getText().toString() + " From " + edtxtCust.getText() + " For " +  edtxtChicksType.getText() + "Chicks by TR Number:" + edTedtxtTrNo.getText().toString() + "/" + edtxtRemarks.getText().toString() + "For Any Query Contact:9685043413";
        MobileNo = edtxtMobno.getText().toString();
       // Toast.makeText(getApplicationContext(),MobileNo,Toast.LENGTH_LONG).show();
        //Log.i("SMS",MobileNo);
        try {
            if (MobileNo.length() >= 10) {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(MobileNo, null, SmsMsg, null, null);
               // Log.i("SMS", SmsMsg);
                //Toast.makeText(getApplicationContext(), SmsMsg, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Mobile No Not Entered...", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception ex)
            {
                Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            }
            SharedPreferences.Editor e = shared_trdetails.edit();
            e.clear().commit();
        }
    public class SaveTrData extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(DisplayTrActivity.this);
            // pDialog.setProgressStyle(ProgressDialog.);
            pDialog.setMessage("Saving the Tr Data. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected void onPostExecute(String r) {
            pDialog.dismiss();
          //  Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
            int intIndex = r.indexOf("Success");
              if (intIndex!=-1) {
                Toast.makeText(getApplicationContext(),"TR  Date Saved Sucessfully,Redirecting to Main Menu...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SalesMenuActivity.class);
                startActivity(intent);

            } else {

                Toast.makeText(getApplicationContext(),r,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),TrActivity.class);
                startActivity(intent);
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                AreaCode = params[0];Area = params[1]; TRNo = params[2];
                TrDate= params[3];
                HatchDate =  params[4];
                UserName =  params[5];
                Code =  params[6];
                CName =  params[7];
                Pay_mode =  params[8];
                DD_No =  params[9];
                Bank_det =  params[10];
                Pay_type =  params[11];
                Chick_type =  params[12];
                CihckRate =  params[13];
                Remarks =  params[14];
                TrAmount =  params[15];
                BankdepDate= params[16];
                String login_url = "http://117.240.18.180:91/mynew.asmx/InsertTrData";
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
                String post_data = URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(AreaCode, "UTF-8") + "&"
                        + URLEncoder.encode("Area", "UTF-8") + "=" + URLEncoder.encode(Area, "UTF-8") + "&"
                        + URLEncoder.encode("TRNo", "UTF-8") + "=" + URLEncoder.encode(TRNo, "UTF-8")+ "&"
                        + URLEncoder.encode("TrDate", "UTF-8") + "=" + URLEncoder.encode(TrDate, "UTF-8")+ "&"
                        + URLEncoder.encode("HatchDate", "UTF-8") + "=" + URLEncoder.encode(HatchDate, "UTF-8")+ "&"
                        + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(UserName, "UTF-8")+ "&"
                        + URLEncoder.encode("Code", "UTF-8") + "=" + URLEncoder.encode(Code, "UTF-8")+ "&"
                        + URLEncoder.encode("CName", "UTF-8") + "=" + URLEncoder.encode(CName, "UTF-8")+ "&"
                        + URLEncoder.encode("Pay_mode", "UTF-8") + "=" + URLEncoder.encode(Pay_mode, "UTF-8")+ "&"
                        + URLEncoder.encode("DD_No", "UTF-8") + "=" + URLEncoder.encode(DD_No, "UTF-8")+ "&"
                        + URLEncoder.encode("Bank_det", "UTF-8") + "=" + URLEncoder.encode(Bank_det, "UTF-8")+ "&"
                        + URLEncoder.encode("Pay_type", "UTF-8") + "=" + URLEncoder.encode(Pay_type, "UTF-8")+ "&"
                        + URLEncoder.encode("Chick_type", "UTF-8") + "=" + URLEncoder.encode(Chick_type, "UTF-8")+ "&"
                        + URLEncoder.encode("CihckRate", "UTF-8") + "=" + URLEncoder.encode(CihckRate, "UTF-8")+ "&"
                        + URLEncoder.encode("Remarks", "UTF-8") + "=" + URLEncoder.encode(Remarks, "UTF-8")+ "&"
                        + URLEncoder.encode("TrAmount", "UTF-8") + "=" + URLEncoder.encode(TrAmount, "UTF-8")+ "&"
                + URLEncoder.encode("bankdate", "UTF-8") + "=" + URLEncoder.encode(BankdepDate, "UTF-8");
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
            } catch (MalformedURLException e) { result = e.getMessage();
                Log.d("SaveError1", result);
                e.printStackTrace();
            } catch (IOException e) { result = e.getMessage();
                Log.d("SaveError2", result);
                e.printStackTrace();
            }
            return result;
            //
            // }
        }
    }//end class Save Tr data

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
                edTedtxtTrNo.setText( jobj.getString("trno"));
                //Toast.makeText(TrActivity.this, "Mobile"+ jobj.getString("phone"), Toast.LENGTH_SHORT).show();
                //////////////

            }
            catch(Exception ex) {
                //Toast.makeText(TrActivity.this, "Mobile"+ ex.getMessage(), Toast.LENGTH_SHORT).show();
                //tvMobNo.setText( ex.getMessage());
            }
        }
    }


}// end main class
