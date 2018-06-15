package com.example.raghav.smsreceiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class broadfile extends BroadcastReceiver {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("INFO");


    String phoneid, device_id,prefname="abc";
    TelephonyManager tm;



    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    SharedPreferences sp;
    private String Phonenumber="number";


    public void onReceive(Context context, Intent intent) {
        phoneid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        device_id = tm.getDeviceId();


        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if( myKM.inKeyguardRestrictedInputMode()) {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        String format = "HH:mm:ss";
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                        String time = sdf.format(Calendar.getInstance().getTime());


                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        sp = context.getSharedPreferences(prefname,Context.MODE_PRIVATE);


                        String senderNum = phoneNumber;
                        if (senderNum.equals(sp.getString(Phonenumber,""))) {

                            String message = currentMessage.getDisplayMessageBody();

                            Log.i("SmsReceiver", "senderNum: " + time + "; message: " + message);
                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


                            myRef.child(device_id).child("New Messages").child(date).child(time).setValue(message);

                            // Show Alert
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context,
                                    "IMEI-NO: " + device_id + ", message: " + message, duration);
                            toast.show();


                        } // end for loop
                    }
                }

            } else {
                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        String format = "HH:mm:ss";
                        SimpleDateFormat sdf =new SimpleDateFormat(format, Locale.US);
                        String time = sdf.format(Calendar.getInstance().getTime());




                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();



                        String senderNum = phoneNumber;
                        sp = context.getSharedPreferences(prefname,Context.MODE_PRIVATE);

                        if(senderNum.equals(sp.getString(Phonenumber,""))) {


                            String message = currentMessage.getDisplayMessageBody();

                            Log.i("SmsReceiver", "senderNum: " + time + "; message: " + message);
                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());




                            myRef.child(device_id).child("New Messages").child(date).child(time).setValue(message);

                            // Show Alert
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context,
                                    "IMEI-NO: " + device_id + ", message: " + message, duration);
                            toast.show();

                        }

                    } // end for loop
                }
            }

            // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}

