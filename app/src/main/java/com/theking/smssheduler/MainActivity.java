package com.theking.smssheduler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    long timeS=0;

    ComponentName componentName;
    Calendar calendar;
    Button send;
    TextInputLayout time ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
            Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        send= findViewById(R.id.send);
        componentName = new ComponentName(this,SmsSender.class);
        time = findViewById(R.id.time);

        final JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    calendar = Calendar.getInstance();
                    String givenDateString="00:00";
                    try{
                        givenDateString = time.getEditText().getText().toString();

                    }catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Enter phone",Toast.LENGTH_SHORT).show();
                    }
                    TextInputLayout phone = findViewById(R.id.phone);
                    TextInputLayout msg = findViewById(R.id.msg);
                    //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    long sendingTime;
                    timeS=Long.parseLong(givenDateString.split(":")[0])*60*60*1000+Long.parseLong(givenDateString.split(":")[1])*60*1000;
                    long timeN=calendar.getTime().getHours()*60*60*1000+calendar.getTime().getMinutes()*60*1000+calendar.getTime().getSeconds()*1000;
                    sendingTime = timeS-timeN>=0?timeS-timeN:24*60*60*1000-(timeN-timeS);


                    PersistableBundle pb = new PersistableBundle();
                    try{

                        pb.putString("phone",phone.getEditText().getText().toString());
                    }catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Enter time",Toast.LENGTH_SHORT).show();
                    }
                    try{
                        pb.putString("msg",msg.getEditText().getText().toString());
                    }catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Enter msg",Toast.LENGTH_SHORT).show();
                    }
                    final JobInfo jobInfoObj;


                    jobInfoObj = new JobInfo.Builder(1, componentName)
                            .setMinimumLatency(sendingTime)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setPersisted(true)
                            .setExtras(pb).build();


                    jobScheduler.schedule(jobInfoObj);
                    Toast.makeText(getApplicationContext(),"SMS WILL BE SENT IN "+String.valueOf(sendingTime/1000)+"sec",Toast.LENGTH_SHORT).show();
                }catch(Exception e) {}
            }
        });
    }
}
