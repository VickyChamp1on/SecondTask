package com.example.android.deltatask2;



import android.Manifest;
import android.content.ContentValues;
import android.database.Cursor;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import android.view.View;

import android.provider.MediaStore;

import android.content.pm.PackageManager;
import android.widget.EditText;

import android.widget.ListView;

import java.util.ArrayList;


import android.net.Uri;
import android.widget.Toast;

import static android.R.attr.x;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 10;
    private static final int MY_PERMISSION_REQUEST_READ_STORAGE = 20 ;
    ArrayList<PhotoList> photoList;
    ListView myList;
    private CustomAdapter adapter;
    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    EditText enterItem,removeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button myButton = (Button) findViewById(R.id.myButton);
        Button secondButton = (Button)findViewById(R.id.secondButton);
        Button removeButton = (Button)findViewById(R.id.removeButton);
        enterItem = (EditText) findViewById(R.id.enterItem);
        removeText = (EditText) findViewById(R.id.remove_item);
        myList = (ListView) findViewById(R.id.myList);
        photoList = new ArrayList<>();

        adapter = new CustomAdapter(this, photoList);
        myList.setAdapter(adapter);

        myButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(enterItem.getText().toString().equals("")) {
                            final Toast toast = Toast.makeText(MainActivity.this, "No Item entered", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        else {
                            manageBtnClick();
                            //launchcameracheck

                            launchCamera();
                        }
                    }
                }
        );

        secondButton.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(enterItem.getText().toString().equals("")) {
                            final Toast toast = Toast.makeText(MainActivity.this, "No Item entered", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        else {
                            //opengallerycheck
                            manageGallery();
                            openGallery();
                        }
                    }
                }
        );

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(removeText.getText().toString().length()==0)
                {
                    Toast.makeText(MainActivity.this,"Enter a position",Toast.LENGTH_SHORT).show();
                }

                else{
                    int x = Integer.parseInt(removeText.getText().toString());
                    if (x >= 1 && x <= photoList.size()) {
                        photoList.remove(x - 1);
                        adapter.notifyDataSetChanged();
                    } else
                        Toast.makeText(MainActivity.this, "No item in this position to remove", Toast.LENGTH_SHORT).show();
                }
                removeText.setText(null);
            }

        });

    }

    private void manageGallery() {
        //gallery check
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_READ_STORAGE);
        }
        else
            openCamera();
    }

    private void manageBtnClick() {
        //camera check
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA},MY_PERMISSION_REQUEST_CAMERA);
        }
        else
            openCamera();
    }

    private void openCamera() {
        Toast.makeText(this , "Permission Granted",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CAMERA:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){
                        new AlertDialog.Builder(this).setTitle("Open Camera Permission").setMessage("You need to give camera permission to use Camera" +
                                "Option. Retry and grant it !").show();

                    }
                    else{
                        new AlertDialog.Builder(this).setTitle(" Camera Permission denied").setMessage("You denied camera permission. So the feature will be disabled.To enable" +
                                "Go to settings and grant permission for the app").show();

                    }
                }

            case MY_PERMISSION_REQUEST_READ_STORAGE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        new AlertDialog.Builder(this).setTitle("Open Gallery Permission").setMessage("You need to give gallery permission to use gallery" +
                                "Option. Retry and grant it !").show();

                    }
                    else{
                        new AlertDialog.Builder(this).setTitle(" Gallery Permission denied").setMessage("You denied Gallery permission. So the feature will be disabled.To enable" +
                                "Go to settings and grant permission for the app").show();

                    }
                }

        }
    }

    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            String fileName = "temp.jpg";
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
        adapter.notifyDataSetChanged();

    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE: {
                if (requestCode == RESULT_LOAD_IMAGE &&
                        resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver()
                            .query(selectedImage, null, null, null,
                                    null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    PhotoList image = new PhotoList();

                    image.setCaptions(enterItem.getText().toString());
                    image.setPath(picturePath);
                    photoList.add(image);
                    enterItem.setText("");
                }
            }


            case REQUEST_IMAGE_CAPTURE: {
                if (requestCode == REQUEST_IMAGE_CAPTURE &&
                        resultCode == RESULT_OK) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(mCapturedImageURI, projection, null,
                            null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(
                            MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String picturePath = cursor.getString(column_index_data);
                    PhotoList image = new PhotoList();

                    image.setCaptions(enterItem.getText().toString());
                    image.setPath(picturePath);
                    photoList.add(image);
                    enterItem.setText("");
                }
            }

        }
    }
}


