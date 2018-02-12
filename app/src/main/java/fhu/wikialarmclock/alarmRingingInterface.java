package fhu.wikialarmclock;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by Alan on 2/19/2017.
 */

public class alarmRingingInterface extends MainActivity
{

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_ringing_interface);


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (!isScreenOn)
        {
            //PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
            //wakeLock.acquire();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        }


        TextView textview = (TextView) findViewById(R.id.article_title);
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("some_name", 0);
        Set<String> set = mPrefs.getStringSet("current wikipedia title",  null);
        String title = set.toString().substring(1, set.toString().length() - 1);
        textview.setText(title);
        textview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                my_intent.putExtra("extra", "off");
                sendBroadcast(my_intent);
                SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("some_name", 0);
                Set<String> set = mPrefs.getStringSet("current wikipedia url", null);

                String url = set.toString().substring(1, set.toString().length() - 1);

                Uri uriUrl = Uri.parse(url);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(234);
            }
        });




        final boolean vibrate = getIntent().getBooleanExtra("vibrate", false);
        System.out.println("from alaarm ring interface" + vibrate);
        Button off = (Button) findViewById(R.id.off);
        off.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                my_intent.putExtra("extra", "off");
                sendBroadcast(my_intent);
                finish();

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(234);
            }
        });
        Button snooze = (Button) findViewById(R.id.snooze);
        snooze.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent =  new Intent(getApplicationContext(), snoozeButton.class);
                intent.putExtra("vibrate", vibrate);
                sendBroadcast(intent);
                finish();

            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.ringingBackground);
        //imageView.setImageDrawable(getDrawable(R.drawable.wikilogo));
        imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.wikilogo, 100, 100));

        /*
        Button dismiss = (Button) findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                my_intent.putExtra("extra", "off");
                sendBroadcast(my_intent);
                finish();

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(234);
            }
        });
        Button viewArticle = (Button) findViewById(R.id.viewarticle);
        viewArticle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                my_intent.putExtra("extra", "off");
                sendBroadcast(my_intent);
                SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("some_name", 0);
                Set<String> set = mPrefs.getStringSet("current wikipedia url", null);

                String url = set.toString().substring(1, set.toString().length() - 1);

                Uri uriUrl = Uri.parse(url);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(234);
            }
        });
        */


        // custom_font = Typeface.createFromAsset(getAssets(),  "fonts/HoeflerText.ttf");
        //off.setTypeface(custom_font);

    }
}
