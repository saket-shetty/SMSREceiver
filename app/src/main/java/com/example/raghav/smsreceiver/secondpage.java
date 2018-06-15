package com.example.raghav.smsreceiver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class secondpage extends AppCompatActivity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondpage);

        btn = (Button) findViewById(R.id.button2);




    }

    public void update(View view){

        startActivity(new Intent(secondpage.this,MainActivity.class));
    }
}
