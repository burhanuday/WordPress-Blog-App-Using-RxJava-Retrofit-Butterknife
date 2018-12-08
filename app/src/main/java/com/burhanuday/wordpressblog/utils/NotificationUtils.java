package com.burhanuday.wordpressblog.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import com.burhanuday.wordpressblog.R;
import com.burhanuday.wordpressblog.network.model.Post;
import com.burhanuday.wordpressblog.view.Home;

/**
 * Created by burhanuday on 08-12-2018.
 */
public class NotificationUtils {

    private static String NOTIFICATION_CHANNEL_ID = "new_post_id";
    private static int NOTIFICATION_ID = 1234;

    public static void showNotification(Context context, Post post){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "New posts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        notificationBuilder.setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setContentText(Html.fromHtml(post.getExcerpt().getRendered(), Html.FROM_HTML_MODE_COMPACT));
            notificationBuilder.setContentTitle(Html.fromHtml(post.getTitle().getRendered(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            notificationBuilder.setContentText(Html.fromHtml(post.getExcerpt().getRendered()));
            notificationBuilder.setContentTitle(Html.fromHtml(post.getTitle().getRendered()));
        }
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setLargeIcon(largeIcon(context));
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                Html.fromHtml(post.getExcerpt().getRendered())
        ));
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationBuilder.setContentIntent(contentIntent(context));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent = new Intent(context, Home.class);

        return PendingIntent.getActivity(context,
                NOTIFICATION_ID, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        Resources resources = context.getResources();
        return BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
    }
}
