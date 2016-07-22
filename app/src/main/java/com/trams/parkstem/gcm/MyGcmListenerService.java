package com.trams.parkstem.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.trams.parkstem.R;
import com.trams.parkstem.activity.ParkStatusActivity;

/**
 * Created by Noverish on 2016-07-18.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    public static final String PUSH_RECEIVE = "push_receive";

    /**
     *
     * @param from SenderID 값을 받아온다.
     * @param data Set형태로 GCM으로 받은 데이터 payload이다.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String message = data.getString("message");

        Log.e("local_id",data.getString("local_id") + "");
        Log.e("time",data.getString("time"));
        Log.e("car_in_out",data.getString("car_in_out"));

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Message: " + message);

        // GCM으로 받은 메세지를 디바이스에 알려주는 sendNotification()을 호출한다.
        sendNotification(title, message);
    }


    /**
     * 실제 디바에스에 GCM으로부터 받은 메세지를 알려주는 함수이다. 디바이스 Notification Center에 나타난다.
     * @param title
     * @param message
     */
    private void sendNotification(String title, String message) {
        PendingIntent intent = PendingIntent.getActivity(this, 0,
                new Intent(getApplicationContext(), ParkStatusActivity.class),
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification noti = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("파크스템")
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .build();

        manager.notify(1, noti);

        Log.e("Send","Broadcast");

        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(PUSH_RECEIVE));

    }
}