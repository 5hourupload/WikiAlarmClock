package fhu.wikialarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Alan on 2/5/2017.
 */

public class alarmReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.e("we in", "iiiii");

        String get_your_string = intent.getExtras().getString("extra");
        Boolean vibrate = intent.getExtras().getBoolean("vibrate");
        int position = intent.getExtras().getInt("position", -1);
        boolean repeating = intent.getExtras().getBoolean("repeating", false);
        int id = intent.getExtras().getInt("id", -1);
        int spinner = intent.getExtras().getInt("spinner", 0);

        Log.e("key:", get_your_string);

        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        service_intent.putExtra("extra", get_your_string);
        service_intent.putExtra("vibrate", vibrate);
        service_intent.putExtra("position", position);
        service_intent.putExtra("repeating", repeating);
        service_intent.putExtra("spinner", spinner);
        service_intent.putExtra("id", id);

        context.startService(service_intent);
    }
}
