package com.example.licenta;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPerson extends AppCompatActivity implements View.OnClickListener {

    ImageView IV1,IV2,IV3,IV4,IV5,IV6,IV7,IV8,IV9,IV10;
    private TextInputEditText name ;
    public static final int CMAERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public int imagesGetDone = 0;
    public Bitmap[] images = new Bitmap[10];
    private Boolean readyToTrain = false;
    private Button uploadBtn, trainBtn, homeBtn;
    private String currentPhotoPath;
    private ProgressBar progressBar;
    public int prgs = 0;
    int count = 0;
    Timer timer;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        openDialog("In this section you will need to touch all the face figures for taking a selfie" +
                " for each of them and the name input will need to be filled .\n" +
                "After that the UPLOAD & TRAIN button will need to be pressed " +
                "for completing the addition of the new person");

        progressBar = findViewById(R.id.progressBar);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }


        IV1 = (ImageView) findViewById(R.id.IV1);
        IV2 = (ImageView) findViewById(R.id.IV2);
        IV3 = (ImageView) findViewById(R.id.IV3);
        IV4 = (ImageView) findViewById(R.id.IV4);
        IV5 = (ImageView) findViewById(R.id.IV5);
        IV6 = (ImageView) findViewById(R.id.IV6);
        IV7 = (ImageView) findViewById(R.id.IV7);
        IV8 = (ImageView) findViewById(R.id.IV8);
        IV9 = (ImageView) findViewById(R.id.IV9);
        IV10 = (ImageView) findViewById(R.id.IV10);
        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        trainBtn = (Button) findViewById(R.id.trainBtn);
        homeBtn = (Button) findViewById(R.id.home_btn);
        name = findViewById(R.id.nameOfThePerson);

        IV1.setOnClickListener((View.OnClickListener) this);
        IV2.setOnClickListener((View.OnClickListener) this);
        IV3.setOnClickListener((View.OnClickListener) this);
        IV4.setOnClickListener((View.OnClickListener) this);
        IV5.setOnClickListener((View.OnClickListener) this);
        IV6.setOnClickListener((View.OnClickListener) this);
        IV7.setOnClickListener((View.OnClickListener) this);
        IV8.setOnClickListener((View.OnClickListener) this);
        IV9.setOnClickListener((View.OnClickListener) this);
        IV10.setOnClickListener((View.OnClickListener) this);
        uploadBtn.setOnClickListener((View.OnClickListener) this);
        trainBtn.setOnClickListener((View.OnClickListener) this);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPerson.this, MainActivity.class));
            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.IV1:
                System.out.println("Click on image IV1 images get done :"+imagesGetDone);
                askCameraPermissions();
                break;
            case R.id.IV2:
                System.out.println("Click on image IV2");
                askCameraPermissions();
                break;
            case R.id.IV3:
                System.out.println("Click on image IV3");
                askCameraPermissions();
                break;
            case R.id.IV4:
                System.out.println("Click on image IV4");
                askCameraPermissions();
                break;
            case R.id.IV5:
                System.out.println("Click on image IV5");
                askCameraPermissions();
                break;
            case R.id.IV6:
                System.out.println("Click on image IV6");
                askCameraPermissions();
                break;
            case R.id.IV7:
                System.out.println("Click on image IV7");
                askCameraPermissions();
                break;
            case R.id.IV8:
                System.out.println("Click on image IV8");
                askCameraPermissions();
                break;
            case R.id.IV9:
                System.out.println("Click on image IV9");
                askCameraPermissions();
                break;
            case R.id.IV10:
                System.out.println("Click on image IV10");
                askCameraPermissions();
                break;
            case R.id.uploadBtn:
                String nameToSend = name.getEditableText().toString();
                if(imagesGetDone != 10){
                    System.out.println("Not ready " + nameToSend +" "+imagesGetDone);
                    openDialog("Complete all the images");
                }else if(nameToSend.length() < 4){
                    System.out.println("The name should be more than 4 characters");
                    openDialog("The name should contain more than 4 characters");
                }else {
                    System.out.println(" ready , send the photos with " + nameToSend);
                    Handler han = new Handler();
                    han.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }, 1000);
                    uploadImages();
                    han.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }, 1000);
                }
                break;
            case R.id.trainBtn:
                if(readyToTrain == false){
                    System.out.println("Not ready");
                    openDialog("You need to upload the images first");
                }else{
                    trainImages();
                    System.out.println("Ready for train");
                }
                break;
            default:
                break;
        }
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA}, CMAERA_PERM_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera(){
        String fileName = "photo";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try{
            File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory);
            currentPhotoPath = imageFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", imageFile);
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(camera, CAMERA_REQUEST_CODE);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE && imagesGetDone < 10){
            images[imagesGetDone] = BitmapFactory.decodeFile(currentPhotoPath);
            images[imagesGetDone] = rotateImage(images[imagesGetDone], 270);
            images[imagesGetDone] = Bitmap.createScaledBitmap(images[imagesGetDone], 550, 550, true);

            switch (imagesGetDone){
                case 0:
                    IV1.setImageBitmap(null);
                    IV1.setImageBitmap(images[imagesGetDone]);
                    break;
                case 1:
                    IV2.setImageBitmap(null);
                    IV2.setImageBitmap(images[imagesGetDone]);
                    break;
                case 2:
                    IV3.setImageBitmap(null);
                    IV3.setImageBitmap(images[imagesGetDone]);
                    break;
                case 3:
                    IV4.setImageBitmap(null);
                    IV4.setImageBitmap(images[imagesGetDone]);
                    break;
                case 4:
                    IV5.setImageBitmap(null);
                    IV5.setImageBitmap(images[imagesGetDone]);
                    break;
                case 5:
                    IV6.setImageBitmap(null);
                    IV6.setImageBitmap(images[imagesGetDone]);
                    break;
                case 6:
                    IV7.setImageBitmap(null);
                    IV7.setImageBitmap(images[imagesGetDone]);
                    break;
                case 7:
                    IV8.setImageBitmap(null);
                    IV8.setImageBitmap(images[imagesGetDone]);
                    break;
                case 8:
                    IV9.setImageBitmap(null);
                    IV9.setImageBitmap(images[imagesGetDone]);
                    break;
                case 9:
                    IV10.setImageBitmap(null);
                    IV10.setImageBitmap(images[imagesGetDone]);
                    break;
                default:
                    break;
            }
            imagesGetDone++;
        }
    }

    public void uploadImages(){
        int exeception = 0;
        Log.d("OKHTTP3","Post function for images called");
        String url = Route.link+"/createFolderName";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();
        try {
            actualData.put("name",name.getEditableText().toString());
            Log.d("OKHTTP3","add the name");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("OKHTTP3","JSON excetion");
            exeception = 1;
        }

        RequestBody body = RequestBody.create(JSON,actualData.toString());
        Request newReq = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(newReq).execute();
            Log.d("OKHTTP3", "Request Done, sent the name for the folder , got the response.");
            assert response.body() != null;
            Log.d("OKHTTP3","the response "+response.body().string());

        }catch (IOException e)
        {
            Log.d("OKHTTP3", "Exception while doing request.");
            e.printStackTrace();
            exeception = 1;
        }

        for(int i=0;i<10;i++){
            {
                Log.d("OKHTTP3","Post function called");
                url = Route.link+"/addImages";
                JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject imageData = new JSONObject();

                ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                images[i].compress(Bitmap.CompressFormat.PNG,100, baos);
                byte [] b=baos.toByteArray();
                String temp= Base64.encodeToString(b, Base64.DEFAULT);

                try {
                    imageData.put("img",temp);
                    imageData.put("directory",name.getEditableText().toString());
                    imageData.put("nr",i);

                    Log.d("OKHTTP3","add the data for image");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("OKHTTP3","JSON excetion");
                    exeception = 1;
                }

                body = RequestBody.create(JSON,imageData.toString());
                newReq = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                try {
                    Response response = client.newCall(newReq).execute();
                    Log.d("OKHTTP3", "Request Done, added the image.");
                    Log.d("OKHTTP3","the response "+response.body().string());
                    if (i == 9){
                        readyToTrain = true;
                    }
                }catch (IOException e)
                {
                    Log.d("OKHTTP3", "Exception while doing request.");
                    e.printStackTrace();
                    exeception = 1;
                }

            }
        }
        if(exeception == 0)
            openDialog("The images have been uploaded");
        else
            openDialog("Connection error");

    }

    public void trainImages(){
            int exeception = 0;
            Log.d("OKHTTP3","Post function for train called");
            String url = Route.link+"/trainModel";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            JSONObject actualData = new JSONObject();
            try {
                actualData.put("name","train the model pls");
                Log.d("OKHTTP3","add the name");
            } catch (JSONException e) {
                exeception = 1;
                e.printStackTrace();
                Log.d("OKHTTP3","JSON excetion");
            }

            RequestBody body = RequestBody.create(JSON,actualData.toString());
            Request newReq = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(newReq).execute();
                Log.d("OKHTTP3", "Request Done, sent the name for the folder , got the response.");
                assert response.body() != null;
                Log.d("OKHTTP3","the response "+response.body().string());

            }catch (IOException e)
            {
                exeception = 1;
                Log.d("OKHTTP3", "Exception while doing request.");
                e.printStackTrace();
            }

            if(exeception == 0)
                openDialog("The person have been added");
            else
                openDialog("Connection error");

    }

    public void openDialog(String msg){
        MessageDialog dialog = new MessageDialog(msg);
        dialog.show(getSupportFragmentManager(), msg);
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}