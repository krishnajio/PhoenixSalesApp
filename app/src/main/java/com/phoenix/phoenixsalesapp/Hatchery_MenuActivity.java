package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Hatchery_MenuActivity extends AppCompatActivity {

    Button b3,b4,b5;
    TextView tvhacherymenu;
    String VarChicksType, VarPAyType, VarPaymntType, VarCustomer, UserName, AreaCode, AreaName, UserType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hatchery__menu);

        tvhacherymenu = (TextView)findViewById(R.id.tvUserhatchery);

        SharedPreferences sharedPreferences = getSharedPreferences(
                "PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");

        tvhacherymenu.setText("User:- " + UserName + "   Area Name:- " + AreaName);
        Toast.makeText(getApplicationContext(), AreaCode, Toast.LENGTH_SHORT).show();
    }

    public void Hatchdata(View view)
    {
        Intent intent = new Intent(getApplicationContext(),HatchdataActivity.class);
        startActivity(intent);
    }
    //onShowhatchdata

    public void onShowhatchdata(View view)
    {
        Intent intent = new Intent(getApplicationContext(),Show_hatcherydataActivity.class);
        startActivity(intent);
    }
}
