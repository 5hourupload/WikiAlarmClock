package fhu.wikialarmclock;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import static fhu.wikialarmclock.MainActivity.alarm_manager;
import static fhu.wikialarmclock.MainActivity.alarmsList;
import static fhu.wikialarmclock.MainActivity.buckysAdapter;
import static fhu.wikialarmclock.MainActivity.buckysListView;
import static fhu.wikialarmclock.MainActivity.my_intent;
import static fhu.wikialarmclock.MainActivity.titles;
import static fhu.wikialarmclock.MainActivity.urls;


/**
 * Created by Alan on 2/5/2017.
 */

public class RingtonePlayingService extends Service
{
    int startId;
    boolean isRunning;
    TextToSpeech tts;

    private static final int uniqueID = 234;
    private Intent window;


    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        String state = intent.getExtras().getString("extra");
        Log.e("ringtone extra state:", state);
        Boolean vibrate = intent.getExtras().getBoolean("vibrate");
        Boolean repeating = intent.getExtras().getBoolean("repeating");
        int id = intent.getExtras().getInt("id");
        final int spinner = intent.getExtras().getInt("spinner");

        assert state != null;
        switch (state)
        {
            case "on":
                startId = 1;
                break;
            case "off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (!this.isRunning && startId == 1)
        {
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener()
            {
                @Override
                public void onInit(int status)
                {
                    String title = "";
                    String body = "";
                    String text = "";
                    Set<String> URL = null;
                    Set<String> article_title = null;
                    SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("some_name", 0);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    if (spinner == 0)
                    {
                        Set<String> set = mPrefs.getStringSet("wikipedia title", null);
                        title = set.toString().substring(1, set.toString().length() - 1);
                        Set<String> set3 = mPrefs.getStringSet("wikipedia body", null);
                        body = set3.toString().substring(1, set3.toString().length() - 1);
                        text = title + " " + body;

                        URL = mPrefs.getStringSet("wikipedia url", null);
                        article_title = mPrefs.getStringSet("wikipedia title", null);
                        editor.putStringSet("current wikipedia url", URL);
                        editor.putStringSet("current wikipedia title", article_title);
                        editor.apply();
                    }
                    else if (spinner == 1)
                    {
                        Set<String> set = mPrefs.getStringSet("featured title", null);
                        title = set.toString().substring(1, set.toString().length() - 1);
                        Set<String> set3 = mPrefs.getStringSet("featured body", null);
                        body = set3.toString().substring(1, set3.toString().length() - 1);
                        text = title + " " + body;

                        URL = mPrefs.getStringSet("featured url", null);
                        article_title = mPrefs.getStringSet("featured title", null);
                        editor.putStringSet("current wikipedia url", URL);
                        editor.putStringSet("current wikipedia title", article_title);
                        editor.apply();
                    }

                    System.out.println("text" + text);
                    if (text.equals(" "))
                    {
                        text = "Unable to connect to the internet to load an article. Therefore, you are hearing this instead.";
                    }
                    tts.setLanguage(Locale.US);
                    if (status == TextToSpeech.SUCCESS)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            String utteranceId = this.hashCode() + "";
                            Bundle params = new Bundle();
                            params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_ALARM);
                            //params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM)
                            //tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId);
                        } else
                        {
                            HashMap<String, String> map = new HashMap<>();
                            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
                            map.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
                        }
                        System.out.println("SUCCESS");

                    }

                    Collections.reverse(titles);
                    titles.add(title);
                    Collections.reverse(titles);
                    String url = URL.toString();
                    Collections.reverse(urls);
                    urls.add(url);
                    Collections.reverse(urls);

                    editor = mPrefs.edit();
                    editor.putString("history_json_titles", new JSONArray(titles).toString());
                    editor.putString("history_json_urls", new JSONArray(urls).toString());
                    editor.apply();

//                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                    boolean isScreenOn = pm.isInteractive();
//                    if (!isScreenOn)
//                    {
//                        //PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//                        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
//                        //wakeLock.acquire();
//                        Intent i = new Intent(getApplicationContext(), alarmRingingInterface.class);
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(i);
//                    }
                    Intent i = new Intent(getApplicationContext(), alarmRingingInterface.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            });

            this.isRunning = true;
            this.startId = 0;

            String title = "";
            SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("some_name", 0);
            if (spinner == 0)
            {
                Set<String> set = mPrefs.getStringSet("wikipedia title", null);
                title = set.toString().substring(1, set.toString().length() - 1);
            } else if (spinner == 1)
            {
                Set<String> set = mPrefs.getStringSet("featured title", null);
                title = set.toString().substring(1, set.toString().length() - 1);
            }


            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);

            Intent intentRingingInterface = new Intent(this.getApplicationContext(), alarmRingingInterface.class);
            intentRingingInterface.putExtra("vibrate", vibrate);
            PendingIntent pendingIntentRingingInterface = PendingIntent.getActivity(this, 0, intentRingingInterface, PendingIntent.FLAG_UPDATE_CURRENT);

//            Intent offIntent = new Intent(this, offButton.class);
//            offIntent.putExtra("notificationId", uniqueID);
//            PendingIntent pendingOffIntent = PendingIntent.getBroadcast(this, 0, offIntent, 0);
//            remoteViews.setOnClickPendingIntent(R.id.off, pendingOffIntent);
//
//            Intent snoozeIntent = new Intent(this, snoozeButton.class);
//            snoozeIntent.putExtra("vibrate", vibrate);
//            PendingIntent pendingSnoozeIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.snooze, pendingSnoozeIntent);

            remoteViews.setTextViewText(R.id.notifTitle, "Article: " + title);

            Notification notification_popup = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("An Alarm is going off ")
                    .setContentText("Click me")
                    .setContentIntent(pendingIntentRingingInterface)
                    //.setAutoCancel(true)
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .build();

            NotificationManager notify_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notify_manager.notify(uniqueID, notification_popup);

            if (vibrate)
            {
                long[] pattern = {0, 400, 1000};
                vibrator.vibrate(pattern, 0);
            }
            if (repeating)
            {
                alarm_manager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                my_intent = new Intent(getBaseContext(), alarmReceiver.class);
                my_intent.putExtra("extra", "on");
                my_intent.putExtra("vibrate", vibrate);
                my_intent.putExtra("repeating", repeating);
                my_intent.putExtra("id", id);
                PendingIntent piNonRepeating = PendingIntent.getBroadcast(getApplicationContext(), id, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7), piNonRepeating);

            } else
            {
                try
                {
                    JSONArray jsonArray = new JSONArray(mPrefs.getString("alarms", null));
                    // jsonArray contains the data, use jsonArray.getString(index) to
                    // retreive the elements
                    alarmsList.clear();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                        int hour = jsonObject.getInt("hour");
                        int minutes = jsonObject.getInt("minutes");
                        boolean active = jsonObject.getBoolean("active");
                        boolean repeat = jsonObject.getBoolean("repeat");
                        JSONArray daysJ = jsonObject.getJSONArray("days");
                        boolean[] days = new boolean[7];
                        for (int j = 0; j < daysJ.length(); ++j)
                        {
                            days[j] = daysJ.getBoolean(j);
                        }
                        int wikiMode = jsonObject.getInt("wikiMode");
                        boolean v = jsonObject.getBoolean("vibrate");
                        String label = jsonObject.getString("label");
                        int id1 = jsonObject.getInt("id");
                        alarmItem myclass = new alarmItem(hour, minutes, active, repeat, days, wikiMode, v, label, id1);
                        alarmsList.add(myclass);
                    }

                    for (alarmItem a : alarmsList)
                    {
                        if (a.getId() == id)
                        {
                            a.setActive(false);
                        }
                    }
                    SharedPreferences.Editor editor = mPrefs.edit();

                    JSONArray jArray = new JSONArray();
                    for (int i = 0; i < alarmsList.size(); i++)
                    {
                        jArray.put(alarmsList.get(i).getJSONObject());
                    }
                    editor.putString("alarms", jArray.toString());
                    editor.apply();

                    buckysListView.setAdapter(buckysAdapter);

                } catch (NullPointerException n)
                {
                    n.printStackTrace();
                } catch (IndexOutOfBoundsException i)
                {
                    i.printStackTrace();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }



            Intent loadArticle = new Intent(this, loadArticle.class);
            startService(loadArticle);

        } else if (this.isRunning && startId == 0)
        {
            //media_song.stop();
            //media_song.reset();
            tts.stop();
            vibrator.cancel();
            this.isRunning = false;
            this.startId = 0;
            //PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyWakelockTag");
            //wakeLock.release();


        } else if (!this.isRunning && startId == 0)
        {
            this.isRunning = false;
            this.startId = 0;

        } else if (this.isRunning && startId == 1)
        {
            this.isRunning = true;
            this.startId = 1;
        }


        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy()
    {

        // Tell the user we stopped.
        Toast.makeText(this, "On destory called", Toast.LENGTH_SHORT).show();
    }

/*
    public Intent getWindow()
    {
        return window;
    }
*/

}
