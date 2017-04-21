package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        String reply = QuoteSyncJob.getQuotes(getApplicationContext());

        Log.d("sender", "Broadcasting message");
        Intent mIntent = new Intent("custom-event-name");
        mIntent.putExtra("message", reply);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);
    }
}
