package com.example.licenta;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Locker extends AppCompatActivity {
    ImageView selectedImage;
    TextView txtIdentity;
    Button cameraBtn, sendReqBtn ,chooseBtn, homeBtn, passwordBtn, addAPersonBtn;
    Bitmap image;
    public static String identity;
    public static final int CMAERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int IMAGE_PICK_CODE = 1000;
    public static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker);

        selectedImage = findViewById(R.id.iv_image);
        cameraBtn = findViewById(R.id.cameraBtn);
        sendReqBtn = findViewById(R.id.send_Req_Btn);
        chooseBtn = findViewById(R.id.chooseBtn);
        txtIdentity = findViewById(R.id.textViewIdentity);
        homeBtn = findViewById(R.id.home_btn);
        passwordBtn = findViewById(R.id.passwordBtn);
        addAPersonBtn = findViewById(R.id.addFaceBtn);

        sendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] ide = new String[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        identity = doPostRequest();
                    }
                }).start();
                try {
                    Thread.sleep(4000);
                    JSONObject obj = new JSONObject(identity);
                    String name =(String) obj.get("ret_val");
                    txtIdentity.setText(name);
                    System.out.println("NAME :"+name);
                } catch (InterruptedException | JSONException e) {
                    openDialog("Connection problems");
                    e.printStackTrace();
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
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
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
            image = (Bitmap) data.getExtras().get("data");
            selectedImage.setImageBitmap(image);
        }

        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            System.out.println("BEFORE BITMAP");
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
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
        String url = Route.link;
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

}
