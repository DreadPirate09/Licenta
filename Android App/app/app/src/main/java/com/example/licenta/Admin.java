package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Admin extends AppCompatActivity {

    private boolean disable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if(disable == true){
            
        }
    }
}