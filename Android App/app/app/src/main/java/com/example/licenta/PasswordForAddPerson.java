package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PasswordForAddPerson extends AppCompatActivity {

    private Button homeBtn, procedeBtn;
    private TextInputLayout textInputLayout;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_for_add_person);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        homeBtn = findViewById(R.id.home_btn);
        procedeBtn = findViewById(R.id.procedeBtn);
        textInputLayout = findViewById(R.id.passwordText);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PasswordForAddPerson.this, Locker.class));
            }
        });

        procedeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = textInputLayout.getEditText().getText().toString();
                if (password.length() == 0){
                    openDialog("No password inserted");
                }else{
                    EncryptToMD5 hashMaker = new EncryptToMD5(password);
                    String hash = hashMaker.generateMd5Hash();
                    System.out.println(hash);

                    String response = postCheckPasswordAddPerson(hash);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        response =  doPostPasswordUnlock(hash);
//                    }
//                }).start();
                    try {
                        //Thread.sleep(4000);
                        JSONObject obj = new JSONObject(response);
                        String textRecived =(String) obj.get("text");
                        System.out.println("Response :"+textRecived);
                        if(textRecived.equals("wrong password")){
                            openDialog("Wrong Password");
                        }else{
                            startActivity(new Intent(PasswordForAddPerson.this, AddPerson.class));
                        }
                    } catch (JSONException e) {
                        openDialog("Connection problems");
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public String postCheckPasswordAddPerson(String md5){
        Log.d("OKHTTP3","Post function for password lock");
        String url = Route.link+"/addPersonPassword";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        JSONObject actualData = new JSONObject();


        try {
            actualData.put("execute","lock");
            actualData.put("md5",md5);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("OKHTTP3","JSON excetion");
        }

        RequestBody body = RequestBody.create(JSON,actualData.toString());
        Log.d("OKHTTP3","create body");
        Request newReq = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Log.d("OKHTTP3","build body");

        try {
            Response response = client.newCall(newReq).execute();
            Log.d("OKHTTP3", "Request Done, got the response.");
            assert response.body() != null;
            return response.body().string();
        }catch (IOException e)
        {
            Log.d("OKHTTP3", "Exception while doing request.");
            openDialog("Connection problems");
            e.printStackTrace();
            return "Exception";
        }
    }

    public void openDialog(String msg){
        MessageDialog dialog = new MessageDialog(msg);
        dialog.show(getSupportFragmentManager(), msg);
    }
}