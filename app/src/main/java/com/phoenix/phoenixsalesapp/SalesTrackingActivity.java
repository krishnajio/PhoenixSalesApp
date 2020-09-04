package com.phoenix.phoenixsalesapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.*;

public class SalesTrackingActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;


    LocationManager locationManager;
    LocationListener locationListenerGps;
    LocationListener locationListenerNetwork;
    boolean isNetworkEnabled = false;// flag for GPS status
    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    LatLng s,e;
    Location location;
    String type =""; double d=0;
    boolean flag=false;
    int i =0;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String currentDateandTime = sdf.format(new Date());
    String UserName,AreaCode,AreaName,UserType;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
                    }
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences sharedPreferences = getSharedPreferences(
                "PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       mMap.setOnMapLongClickListener(this);
        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);// getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);// getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

       ////////////////////////////
        if(!isNetworkEnabled){


        }else {
        locationListenerNetwork = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("My Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
                Log.i("Location", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };}
        ////////////////


        if(!isGPSEnabled){
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }
        else {
            makeText(this, "GPS", LENGTH_LONG).show();
            locationListenerGps = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                      LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                      mMap.clear();
                      mMap.addMarker(new MarkerOptions().position(userLocation).title("My Current Location"));
                      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
                      Log.i("Location", location.toString());

                     // s = userLocation;
                      //SaveLocation saveLocation = new SaveLocation();
                      //saveLocation.execute(UserName, UserType, AreaCode, String.valueOf(userLocation.longitude), String.valueOf(userLocation.latitude),"S",String.valueOf(d));
                      flag=true;
                 }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                @Override
                public void onProviderEnabled(String provider) {
                }
                @Override
                public void onProviderDisabled(String provider) {
                }
            };
        }
        try {
            if (Build.VERSION.SDK_INT < 23) {
                // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            } else {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                } else {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLatitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("My Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                }
            }
        }catch(Exception ex)
        { makeText(this,ex.getMessage(), LENGTH_LONG).show(); }


    }//end map on ready

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (listAddresses != null && listAddresses.size() > 0) {
                if (listAddresses.get(0).getThoroughfare() != null) {
                    if (listAddresses.get(0).getSubThoroughfare() != null) {
                       address += listAddresses.get(0).getSubThoroughfare() + " ";
                    }
                    address += listAddresses.get(0).getThoroughfare();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address == "") {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address = sdf.format(new Date());
        }

        if(i==0)
        {
            d=0;s= latLng;
            SaveLocation saveLocation = new SaveLocation();
            saveLocation.execute(UserName,UserType,AreaCode,String.valueOf(latLng.longitude),String.valueOf(latLng.latitude) ,"S",String.valueOf(d));
            i=i+1;
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        e= latLng;
        d =  CalculationByDistance(s,e);
        s=e;//making end point as strt point

        SaveLocation saveLocation = new SaveLocation();
        saveLocation.execute(UserName,UserType,AreaCode,String.valueOf(latLng.longitude),String.valueOf(latLng.latitude) ,"E",String.valueOf(d));
        Toast.makeText(this, "Distance in km: "+ String.valueOf(d) , LENGTH_LONG).show();
    }


    public class SaveLocation extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onPostExecute(String r) {
           // Toast.makeText(SalesTrackingActivity.this, r, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            //Code for Webservice
            Log.i("conn", "login");

            String login_url = "http://117.240.18.180:91/mynew.asmx/InsertLocation";
            // String login_url = "http://localhost:50479/mynew.asmx/Login";
            // if(type.equals("Login")) {
            try {
                UserName = params[0];
                UserType = params[1];
                AreaCode = params[2];
                String longi = params[3];
                String lati = params[4];
                String type = params[5];
                String d = params[6];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Log.d("url", "entered show area");
                String post_data = URLEncoder.encode("UserName", "UTF-8") + "=" + URLEncoder.encode(UserName, "UTF-8") + "&"
                        + URLEncoder.encode("UserType", "UTF-8") + "=" + URLEncoder.encode(UserType, "UTF-8") + "&"
                        + URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(AreaCode, "UTF-8")+ "&"
                        + URLEncoder.encode("longi", "UTF-8") + "=" + URLEncoder.encode(longi, "UTF-8")+ "&"
                        + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lati, "UTF-8")+ "&"
                        + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(currentDateandTime, "UTF-8")+ "&"
                        + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(currentDateandTime, "UTF-8")+ "&"
                        + URLEncoder.encode("Type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")+ "&"
                        + URLEncoder.encode("d", "UTF-8") + "=" + URLEncoder.encode(d, "UTF-8");
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

            } catch (MalformedURLException e) { result = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) { result = e.getMessage();
                e.printStackTrace();
            }
            return result;
            //
            // }
        }
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
}// end main class
