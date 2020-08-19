package com.theking.smssheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;


public class SmsSender extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(params.getExtras().getString("phone"),null,params.getExtras().getString("msg"),null,null);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
