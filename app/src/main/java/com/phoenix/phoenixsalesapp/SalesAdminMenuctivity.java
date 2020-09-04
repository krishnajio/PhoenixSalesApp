package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SalesAdminMenuctivity extends AppCompatActivity {
    Button b1,b2,b3;
    TextView tvSalesAdminMenu;
    String VarChicksType, VarPAyType, VarPaymntType, VarCustomer, UserName, AreaCode, AreaName, UserType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_admin_menuctivity);

        b1 = (Button)findViewById(R.id.btnChickRateArea);
        //btnChickRateState
        b2 = (Button)findViewById(R.id.btnChickRateState);
        b3 = (Button)findViewById(R.id.btnStartTrpSalesAdmin);

        tvSalesAdminMenu = (TextView)findViewById(R.id.tvAreaCodeSalesAdmin);

        SharedPreferences sharedPreferences = getSharedPreferences(
                "PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");

        tvSalesAdminMenu.setText("User:- " + UserName + "   Area Name:- " + AreaName);
        Toast.makeText(SalesAdminMenuctivity.this, AreaCode, Toast.LENGTH_SHORT).show();


    }


    //ChickRateAreaEntry
    public void ChickRateAreaEntry(View view)
    {
       Intent intent = new Intent(getApplicationContext(),ChicksrateArea.class);
       startActivity(intent);

    }
    //ChickRateAreaEntry
    public void onClickState(View view)
    {
       Intent intent = new Intent(getApplicationContext(),ChickrateState.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(),"clic",Toast.LENGTH_SHORT).show();
    }

    public void onClickStartTripSaleasAdmin(View view)
    {
        Intent intent = new Intent(getApplicationContext(),SalesTrackingActivity.class);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(),"clic",Toast.LENGTH_SHORT).show();
    }


    public void onClickDailyExpenseSalesAdmin(View view)
    {
        Intent intent = new Intent(getApplicationContext(),DailyExpensesActivity.class);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(),"clic",Toast.LENGTH_SHORT).show();
    }
}
