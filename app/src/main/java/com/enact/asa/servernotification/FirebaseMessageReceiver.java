package com.enact.asa.servernotification;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService {

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.


        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.e("NOTIFICATION PRINT", object.toString());

        String notificationType = "";
        try {
            notificationType = remoteMessage.getData().get("NotificationType");
        } catch (Exception e) {
            e.printStackTrace();
            notificationType = "";
        }
        if (remoteMessage.getData().size() > 0) {

            try {
                if (notificationType.isEmpty()) {
                    notificationType = remoteMessage.getData().get("NotificationType");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (notificationType.equalsIgnoreCase("ConsumerAuthorize")
                    || notificationType.equalsIgnoreCase("UpdateConsumer")) {
                Intent intent = new Intent();
                intent.setAction("PushReciver");
                sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("notificationType", "" + notificationType);

        Gson gson = new Gson();
        String json = gson.toJson(remoteMessage.getData());
        Log.e("notificationData", "" + json);

//        String finalNotificationType = notificationType;
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//              //  Toast.makeText(getApplicationContext(),""+ finalNotificationType,Toast.LENGTH_LONG).show();
//
//            }
//        });

        // showNotificationUser(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    // Method to display the notifications
//    public void showNotificationUser(String title, String message) {
//        // Pass the intent to switch to the MainActivity
//        Intent intent
//                = new Intent(this, Splash.class);
//        // Assign channel ID
//        String channel_id = "notification_channel";
//        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
//        // the activities present in the activity stack,
//        // on the top of the Activity that is to be launched
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Pass the intent to PendingIntent to start the
//        // next Activity
//        PendingIntent pendingIntent
//                = PendingIntent.getActivity(
//                this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        // Create a Builder object using NotificationCompat
//        // class. This will allow control over all the flags
//        NotificationCompat.Builder builder
//                = new NotificationCompat
//                .Builder(getApplicationContext(),
//                channel_id)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setAutoCancel(true)
//                .setVibrate(new long[]{1000, 1000, 1000,
//                        1000, 1000})
//                .setOnlyAlertOnce(true)
//                .setContentIntent(pendingIntent);
//
//        // A customized design for the notification can be
//        // set only for Android versions 4.1 and above. Thus
//        // condition for the same is checked here.
//        builder = builder.setContentTitle(title)
//                .setContentText(message)
//                .setSmallIcon(R.mipmap.ic_launcher);
//        // Create an object of NotificationManager class to
//        // notify the
//        // user of events that happen in the background.
//        NotificationManager notificationManager
//                = (NotificationManager) getSystemService(
//                Context.NOTIFICATION_SERVICE);
//        // Check if the Android Version is greater than Oreo
//        if (Build.VERSION.SDK_INT
//                >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel
//                    = new NotificationChannel(
//                    channel_id, "web_app",
//                    NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(
//                    notificationChannel);
//        }
//
//
//        int id= (int) System.currentTimeMillis();
//        notificationManager.notify(id, builder.build());
//    }
}
