package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class Image_uploadnewActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    protected LocationManager locationManager;
    protected LocationListener locationListener;

    protected boolean gps_enabled,network_enabled;
    protected String latlong;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    String  longi,lati;
    String imagedatetime;
    public String imagename;
    String img_str;
    public String  img_path;
    private TextView txtMessage;
    private Button btnSubmit,btnOpenCamera;
    EditText edImageName ;
    TelephonyManager telephonyManager;
    String   imeistring = "123";
    String mobileno;
    String result=null;
    String message;
    String file;
    String filename;
    String url = null;
    String status="No";
    int serverResponseCode = 0;
    protected Context context;
    int TAKE_PHOTO_CODE = 0;
    public static int count=0;
    public Uri outputFileUri;
    private ImageView imageView;
    String upLoadServerUri = null;
    String picturePath;
    private Bitmap bitmap;
    String dir;

    public Image_uploadnewActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_uploadnew);

        edImageName = (EditText)findViewById(R.id.edtxtImageName);
        btnOpenCamera = (Button)findViewById(R.id.btn_Capture);
        imageView = (ImageView)findViewById(R.id.imageview);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnOpenCamera.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

    }//end on Create

    public void  onClickOpenCamera(View view)
    {
        count++;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
        imagedatetime = sdf.format(cal.getTime());
        // String file = dir+latlong+".jpg";
        file = dir + imagedatetime+".jpg";
        imagename=imagedatetime+".jpg";
        img_path=file;
        File newfile = new File(file);
        Toast.makeText(getApplicationContext(),
                " File Created With Name:  " + imagename , Toast.LENGTH_SHORT)
                .show();
        txtMessage.setText(" Image File Created With Name:  " +  imagename );
        try {
             newfile.createNewFile();
                } catch (IOException e) {}


        outputFileUri = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

    }
}//end Class
