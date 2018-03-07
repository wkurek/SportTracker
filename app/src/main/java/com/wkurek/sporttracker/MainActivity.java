package com.wkurek.sporttracker;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "tracker service channel";
    static final String CHANNEL_GROUP_ID = "tracker channel group";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            if(manager != null) {
                CharSequence channelGroupName = getString(R.string.channel_group_name);
                manager.createNotificationChannelGroup(new NotificationChannelGroup(CHANNEL_GROUP_ID,
                        channelGroupName));

                CharSequence channelName = getString(R.string.channel_name);
                String channelDescription = getString(R.string.channel_description);
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setGroup(CHANNEL_GROUP_ID);
                channel.setDescription(channelDescription);
                channel.enableLights(true);
                channel.setLightColor(Color.MAGENTA);

                manager.createNotificationChannel(channel);
            }
        }
    }
}
