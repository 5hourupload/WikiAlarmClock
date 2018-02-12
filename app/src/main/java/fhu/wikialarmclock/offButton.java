package fhu.wikialarmclock;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static fhu.wikialarmclock.MainActivity.my_intent;

/**
 * Created by Alan on 2/19/2017.
 */

public class offButton extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        my_intent = new Intent(context, alarmReceiver.class);
        my_intent.putExtra("extra", "off");
        context.sendBroadcast(my_intent);


        int notificationId = intent.getIntExtra("notificationId", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
