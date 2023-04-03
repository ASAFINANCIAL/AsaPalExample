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
    }
}
