package com.phoenix.phoenixsalesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import java.util.Date;

public class ImageUploadActivity extends AppCompatActivity {
    Button btnImageClick,btnUpload;
    private ImageView imageView;
    EditText edtxtImageName;
    Uri file;
    byte[] byteArray;
    String encodedImage;
    ProgressDialog  pDialog;
    Uri outPutfileUri;
    Bitmap bitmap = null;
    Bitmap mBitmap;
    String UserName,AreaCode,AreaName,UserType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        btnImageClick = (Button)findViewById(R.id.btnchooseimage);
        btnUpload = (Button)findViewById(R.id.btnupload);
        imageView = (ImageView)findViewById(R.id.imageview);
        edtxtImageName = (EditText) findViewById(R.id.edtname);
        SharedPreferences sharedPreferences = getSharedPreferences(
                "PhxGlobalData", 0);
        UserName = sharedPreferences.getString("UserName", "");
        AreaCode = sharedPreferences.getString("AreaCode", "");
        AreaName = sharedPreferences.getString("AreaName", "");
        UserType = sharedPreferences.getString("UserType", "");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnImageClick.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

    }// end on create

    //GetImage
    public void GetImage(View view)
    {
       openCamera();
    }// GetImage end

    public void onClickUpload(View view)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        if (edtxtImageName.getText().toString()== "") {
           Toast.makeText(getApplicationContext(),"Plese Enter Image Name",Toast.LENGTH_SHORT).show();
        }
        else
        { SaveImage saveImage = new SaveImage();
            saveImage.execute(AreaCode, AreaName, UserName, edtxtImageName.getText().toString(), encodedImage);}

    }
    private void openCamera() {
        //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, 0);

        // Define the file-name to save photo taken by Camera activity
        String fileName = "Camera_Example.jpg";
        // Create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
        // imageUri is the current activity attribute, define and save it for later usage
        outPutfileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        /**** EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume. ****/
        // Standard Intent action that can be sent to have the camera
        // application capture an image and return it.
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult( intent, 2);
        /*************************** Camera Intent End ************************/
    }

    public static String convertImageUriToFile ( Uri imageUri, Activity activity )  {

        Cursor cursor = null;
        int imageID = 0;
        try {
            /*********** Which columns values want to get *******/
            String [] proj={
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };
            cursor = activity.managedQuery(
                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)
            );

            //  Get Query Data

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            //int orientation_ColumnIndex = cursor.
            //    getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
            int size = cursor.getCount();
            /*******  If size is 0, there are no images on the SD Card. *****/
            if (size == 0) {
               // imageDetails.setText("No Image");
            }
            else
            {
                int thumbID = 0;
                if (cursor.moveToFirst()) {
                    /**************** Captured image details ************/
                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID     = cursor.getInt(columnIndex);
                    thumbID     = cursor.getInt(columnIndexThumb);
                    String Path = cursor.getString(file_ColumnIndex);
                    //String orientation =  cursor.getString(orientation_ColumnIndex);
                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            +" ImageID :"+imageID+"\n"
                            +" ThumbID :"+thumbID+"\n"
                            +" Path :"+Path+"\n";
                    // Show Captured Image detail on activity
                  //  imageDetails.setText( CapturedImageDetails );
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )
        return ""+imageID;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0 ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnImageClick.setEnabled(true);
            }
        }
    }//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        //if(resultCode == 2 ) {
          /* Bitmap bp = (Bitmap) data.getExtras().get("data");
           imageView.setImageBitmap(bp);

           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
           bp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
           byte[] byteArray = byteArrayOutputStream.toByteArray();
           encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);*/

                /*********** Load Captured Image And Data Start ****************/
                String imageId = convertImageUriToFile( outPutfileUri,this);
                //  Create and excecute AsyncTask to load capture image
                new LoadImagesFromSDCard().execute(""+imageId);
                /*********** Load Captured Image And Data End ****************/

       //}
    }


    public class LoadImagesFromSDCard  extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(ImageUploadActivity.this);

        protected void onPreExecute() {
            /****** NOTE: You can call UI Element here. *****/
            // Progress Dialog
            Dialog.setMessage(" Loading image from Sdcard..");
            Dialog.show();
        }
       // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;
            try {
                      uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);
                /**************  Decode an input stream into a bitmap. *********/
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                if (bitmap != null) {
                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
                   newBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true);
                   bitmap.recycle();
                    if (newBitmap != null) {
                      mBitmap = newBitmap;
                    }
                }
            } catch (Exception e) {
                // Error fetching image, try to recover
                /********* Cancel execution of this task. **********/
               cancel(true);
            }
            return null;
        }
        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.
            // Close progress dialog
            Dialog.dismiss();
            if(mBitmap != null)
            {

                imageView.setImageBitmap(mBitmap);

            }

        }

    }

    public class SaveImage extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        String result = "";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ImageUploadActivity.this);
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

                Toast.makeText(getApplicationContext(), "Image Uploades Sucessfully,Redirecting to Main Menu...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SalesMenuActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getApplicationContext(), r, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SalesMenuActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String login_url = "http://117.240.18.180:91/mynew.asmx/InsertImage";
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Log.d("urlInsertTr", "Inserttr");
               // AreaCode, AreaName, uname, Img_Name, Img, entrydate
                  String post_data = URLEncoder.encode("AreaCode", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&"
                        + URLEncoder.encode("AreaName", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&"
                        + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&"
                        + URLEncoder.encode("Img_Name", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&"
                        + URLEncoder.encode("Img", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8");// + "&"
                      //  + URLEncoder.encode("entrydate", "UTF-8") + "=" + URLEncoder.encode("2-25-2020", "UTF-8") ;

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

}//end main class
