package com.example.licenta;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Locker extends AppCompatActivity {
    ImageView selectedImage;
    TextView txtIdentity;
    TextView resultTxt;
    Button cameraBtn, sendReqBtn ,chooseBtn, homeBtn, passwordBtn, addAPersonBtn;
    Bitmap image;
    public static String identity;
    public static final int CMAERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int IMAGE_PICK_CODE = 1000;
    public static final int PERMISSION_CODE = 1001;
    private int canDoRequest = 0;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker);

        selectedImage = findViewById(R.id.iv_image);
        cameraBtn = findViewById(R.id.cameraBtn);
        sendReqBtn = findViewById(R.id.send_Req_Btn);
        chooseBtn = findViewById(R.id.chooseBtn);
        txtIdentity = findViewById(R.id.textViewIdentity);
        resultTxt = findViewById(R.id.resultTxt);
        homeBtn = findViewById(R.id.home_btn);
        passwordBtn = findViewById(R.id.passwordBtn);
        addAPersonBtn = findViewById(R.id.addFaceBtn);


        sendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] ide = new String[1];
                if(canDoRequest == 1){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            identity = doPostRequest();
                        }
                    }).start();
                    try {
                        Thread.sleep(4000);
                        JSONObject obj = new JSONObject(identity);
                        String name =(String) obj.get("name");
                        String result = (String) obj.get("result");
                        txtIdentity.setText(name);
                        resultTxt.setText(result);
                        System.out.println("NAME :"+name);
                    } catch (InterruptedException | JSONException e) {
                        openDialog("Connection problems");
                        e.printStackTrace();
                    }
                }else{
                    openDialog("You need to take a selfie first");
                }
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Locker.this, MainActivity.class));
            }
        });

        addAPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Locker.this, PasswordForAddPerson.class));
            }
        });

        passwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Locker.this, PasswordActivity.class));
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA}, CMAERA_PERM_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CMAERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Permission denied !", Toast.LENGTH_SHORT).show();
            }
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

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE ){
            image = BitmapFactory.decodeFile(currentPhotoPath);
            image = rotateImage(image, 270);
            image = Bitmap.createScaledBitmap(image, 550, 550, true);
            canDoRequest = 1;
            selectedImage.setImageBitmap(image);
        }

        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            System.out.println("BEFORE BITMAP");
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                image = rotateImage(image, 270);
                image = Bitmap.createScaledBitmap(image, 550, 550, true);
                System.out.println("Bitmap the image from the gallery");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("AFTER BITMAP");
            selectedImage.setImageURI(data.getData());
        }
    }

    private String doPostRequest() {
        Log.d("OKHTTP3","Post function called");
        String url = Route.link+"/faceRecognition";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);

        try {
            actualData.put("img",temp);
            Log.d("OKHTTP3","add the image");
        } catch (JSONException e) {
            openDialog("No image selected");
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
            Log.d("OKHTTP3", "Request Done, got the response.");
            assert response.body() != null;
            return response.body().string();

        }catch (IOException e)
        {
            openDialog("Connection problems");
            Log.d("OKHTTP3", "Exception while doing request.");
            e.printStackTrace();
            return "ASD";
        }
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
