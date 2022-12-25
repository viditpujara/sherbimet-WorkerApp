package com.sherbimet.worker.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sherbimet.worker.Activity.WorkerRequestsServiceActivity;
import com.sherbimet.worker.R;
import com.sherbimet.worker.Utils.WorkerSessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import androidx.core.app.NotificationCompat;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "SBMWFirebaseMsgService";
    public static MyFirebaseMessagingService myFirebaseMessagingService;
    private NotificationManager mNotificationManager;
    WorkerSessionManager workerSessionManager;

    String NotificationTitle, NotificationDescription;

    String SessionLoginType;
    boolean isLogin = false;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //session=new Session(this);
        myFirebaseMessagingService = this;
        workerSessionManager = new WorkerSessionManager(this);

        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
       /* Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());*/

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //handleNotification(remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendNotification(String.valueOf(json));
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void sendNotification(String messageBody) {
        try {
            JSONObject jsonObject = new JSONObject(messageBody);
            JSONObject data = jsonObject.getJSONObject("data");
            String title = data.getString("notification_title");
            String message = data.getString("notification_description");
            String DateTime = data.getString("date_time");
            String notificationImageURL = data.getString("is_image");
            String screenType = data.getString("screen_type");
            //String isBooking = data.getString("is_booking");
            String screenTitle = data.getString("screen_title");
            String statusID = data.getString("status_id");


            boolean isBackground = true;
            String imageUrl = "";
            String timestamp = "11:22";

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = null;
            if (isLogin) {
                if (screenType != null && !screenType.isEmpty() && !screenType.equals("")) {
                    /*if (screenType.equalsIgnoreCase("bookings")) {
                        intent = new Intent(this, WorkerBookingsServiceActivity.class);
                        intent.putExtra("Title",screenTitle);
                        intent.putExtra("StatusID",StatusID);
                    }else*/
                    if (screenType.equalsIgnoreCase("requests")) {
                        intent = new Intent(this, WorkerRequestsServiceActivity.class);
                        intent.putExtra("Title",screenTitle);
                        intent.putExtra("StatusID",statusID);
                    }
                } else {
                    Log.d("ScreenType", "ST $ScreenType");
                }

            } else {
                Log.d("inside else main", "Yes");
                Log.d("isLogin", String.valueOf(isLogin));
                Log.d("ScreenType", "ST"+"ScreenType");
            }

            RemoteViews downloadViews = null;
            downloadViews = new RemoteViews(getPackageName(), R.layout.notification_view);
            downloadViews.setTextViewText(R.id.tv_notification_title, title);
            downloadViews.setTextViewText(R.id.tv_notification_message, message);

            if (notificationImageURL != null && !notificationImageURL.isEmpty() && !notificationImageURL.equals("")) {
                //*Glide.with(this).load(NotificationImageURL).diskCacheStrategy(DiskCacheStrategy.NONE).asBitmap().into();*//*
                Bitmap bitmap = getBitmapFromURL(notificationImageURL);
                if (bitmap != null) {
                    downloadViews.setImageViewBitmap(R.id.iVNotificationImage, bitmap);
                    downloadViews.setViewVisibility(R.id.iVNotificationImage, View.VISIBLE);
                } else {
                    downloadViews.setViewVisibility(R.id.iVNotificationImage, View.GONE);
                }
            } else {
                downloadViews.setViewVisibility(R.id.iVNotificationImage, View.GONE);
            }

            if (DateTime != null && !DateTime.isEmpty() && !DateTime.equals("")) {
                String[] createdDateTime = DateTime.split(" ");
                String createdDate = createdDateTime[0];
                String createdTime = createdDateTime[1];
                String[] finalTime = createdTime.split(":");
                String timeHour = finalTime[0];
                String timeMinutes = finalTime[1];
                String timeSeconds = finalTime[2];
                downloadViews.setViewVisibility(R.id.tv_notification_time, View.VISIBLE);
                downloadViews.setTextViewText(R.id.tv_notification_time, timeHour+":"+timeMinutes);
            } else {
                downloadViews.setViewVisibility(R.id.tv_notification_time, View.GONE);
            }

            Random notification_id = new Random();
            String channelId = "channel-0290202021";
            String channelName = "Sherbimet Worker Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                    .setContent(downloadViews)
                    //.setSound()
                    .setAutoCancel(true)
                    .setGroupSummary(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            final Notification notification = mBuilder.build();
            notification.bigContentView = downloadViews;
            notification.icon = R.drawable.app_icon_transparent;

            if (intent != null) {
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(contentIntent);
                notification.contentIntent = contentIntent;
            }

            mNotificationManager.notify(notification_id.nextInt(100), notification);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void launchHomeScreen() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN, null);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(homeIntent);
    }

    public long milliseconds(String date) {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static Bitmap getBitmapFromURL(String urL) {
        try {
            URL url = new URL(urL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmapFrmUrl = BitmapFactory.decodeStream(input);
            return bitmapFrmUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
