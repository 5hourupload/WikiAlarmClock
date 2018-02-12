package fhu.wikialarmclock;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.content.Context.ALARM_SERVICE;
import static fhu.wikialarmclock.MainActivity.alarm_manager;
import static fhu.wikialarmclock.MainActivity.my_intent;

/**
 * Created by Alan on 2/19/2017.
 */

public class snoozeButton extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        System.out.println("snooze received");
        my_intent = new Intent(context, alarmReceiver.class);
        my_intent.putExtra("extra", "off");
        context.sendBroadcast(my_intent);

        //start actual alarm
        alarm_manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        my_intent.putExtra("extra", "on");
        boolean vibrate = intent.getBooleanExtra("vibrate", false);
        my_intent.putExtra("vibrate", vibrate);

        System.out.println("from snooze class:" + vibrate);

        PendingIntent piNonRepeating = PendingIntent.getBroadcast(context, 1, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //alarm_manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 10), piNonRepeating);
        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 10 * 60), piNonRepeating);

        //make toast
        long millis = System.currentTimeMillis() + (1000 * 10 * 60) - System.currentTimeMillis();
        long hoursFromNow = (millis / 1000) / (60 * 60);
        long minFromNow = ((millis / 1000) - (hoursFromNow * 3600)) / (60);
        long secondsFromNow = (millis / 1000) % 60;

        if (hoursFromNow == 0 && minFromNow == 0)
        {
            Toast.makeText(context, "Alarm set for " + secondsFromNow + " seconds from now.", Toast.LENGTH_LONG).show();
        }
        else if (hoursFromNow == 0)
        {
            Toast.makeText(context, "Alarm set for " + minFromNow + " minutes and " + secondsFromNow + " seconds from now.", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(context, "Alarm set for " + hoursFromNow + "hours, " + minFromNow + " minutes, and " + secondsFromNow + " seconds from now.", Toast.LENGTH_LONG).show();
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(234);


        Intent loadArticle = new Intent(context, loadArticle.class);
        context.startService(loadArticle);


    }
}
