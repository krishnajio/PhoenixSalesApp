package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class SalesMenuActivity extends AppCompatActivity {
    String UserName,AreaCode,AreaName,UserType;
    TextView tvArea ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_menu);
        tvArea = (TextView)findViewById(R.id.tvAreaCode);

        SharedPreferences sharedPreferences = getSharedPreferences(
                "PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");
        tvArea.setText("User:- " + UserName + "   Area Name:- " + AreaName);

    }
    public void  TrForm(View view)
    {
        Intent intent = new Intent(getApplicationContext(),TrActivity.class);
        startActivity(intent);

    }
    public void  SupplyForm(View view)
    {
        Intent intent = new Intent(getApplicationContext(),SupplyActivity.class);
        startActivity(intent);

    }

    public void  StartTrip(View view){

        Intent intent = new Intent(getApplicationContext(),SalesTrackingActivity.class);
        startActivity(intent);
    }

    public  void  Chicksdemand(View view){
        Intent intent = new Intent(getApplicationContext(),ChicksDemandActivity.class);
        startActivity(intent);
    }
    //DisplayTr
    public  void  DisplayTr(View view){
        Intent intent = new Intent(getApplicationContext(),ListTrActivity.class);
        startActivity(intent);
    }
    //DisplayDM
    public  void  Displaydata(View view){
        Intent intent = new Intent(SalesMenuActivity.this,ListDMActivity.class);
        startActivity(intent);
    }

    public  void  DisplayDemand(View view){
        Intent intent = new Intent(getApplicationContext(),ListDemandActivity.class);
        startActivity(intent);
    }
    //ImageUpload
    public  void  ImageUpload(View view){
        Intent intent = new Intent(getApplicationContext(),ImageUploadActivity.class);
        startActivity(intent);
    }

    //onClickActivityDailyExpenses
    public  void  onClickActivityDailyExpenses(View view){
        Intent intent = new Intent(getApplicationContext(),DailyExpensesActivity.class);
        startActivity(intent);
    }

    public void Bankdeposite(View view)
    {
        Intent intent = new Intent(getApplicationContext(),BankDepositeActivity.class);
        startActivity(intent);
    }


    public void onClickhoebankdepositedata(View view) {
        Intent intent = new Intent(getApplicationContext(),list_depositActivity.class);
        startActivity(intent);

    }
}
