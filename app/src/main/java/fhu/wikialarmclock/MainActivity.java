package fhu.wikialarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity
{
    final static ArrayList<alarmItem> alarmsList = new ArrayList<alarmItem>();
    final static ArrayList<String> titles = new ArrayList<String>();
    final static ArrayList<String> urls = new ArrayList<String>();
    static ListView buckysListView;
    static SharedPreferences mPrefs;
    static ListAdapter buckysAdapter;
    static AlarmManager alarm_manager;
    Context context;
    static Intent my_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        //yourTextView.setTextColor(Color.BLACK);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/HoeflerText.ttf");
        yourTextView.setTypeface(custom_font);
*/

        SpannableString s = new SpannableString("Wiki Alarm Clock");
        fhu.wikialarmclock.TypefaceSpan tfs = new fhu.wikialarmclock.TypefaceSpan(this, "HoeflerText.ttf");
        s.setSpan(tfs, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ActionBar actionBar = getActionBar();
        //actionBar.setTitle(s);
        getSupportActionBar().setTitle(s);



        context = this;


        loadFromStorage();

        buckysAdapter = new CustomAdapter(this, alarmsList, this);
        buckysListView = (ListView) findViewById(R.id.alarm_list);
        buckysListView.setAdapter(buckysAdapter);

        my_intent = new Intent(context, alarmReceiver.class);

        mPrefs = MainActivity.this.getSharedPreferences("some_name", 0);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /**
                Intent myIntent = new Intent(view.getContext(), alarmSettings.class);
                startActivityForResult(myIntent, 0);

                my_intent.putExtra("extra", "off");
                sendBroadcast(my_intent);
                */

                int id = (int) System.currentTimeMillis();
                Date dt = new Date();;     // gets the current month
                int hours = dt.getHours();
                int minutes = dt.getMinutes();
                boolean[] day = {false, false, false, false, false, false, false};
                alarmsList.add(new alarmItem(hours, minutes, false, false, day, 0, true, "", id));
                buckysListView.setAdapter(buckysAdapter);
                SharedPreferences.Editor editor = mPrefs.edit();
                /*
                Set<String> set= new HashSet<String>();
                for (int i = 0; i < alarmsList.size(); i++)
                {
                    set.add(alarmsList.get(i).getJSONObject().toString());
                }
                editor.putStringSet("alarms", set);
                editor.apply();
                */
                JSONArray jArray = new JSONArray();
                for (int i = 0; i < alarmsList.size(); i++)
                {
                    jArray.put(alarmsList.get(i).getJSONObject());
                }
                editor.putString("alarms", jArray.toString());
                editor.apply();
            }
        });
        fab.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.w, 100, 100));
        //fab.setImageResource(R.drawable.w);

        Log.e("Main Activity", "was called");

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null)
            {
                //Cry about not being clicked on
            }
            else if (extras.getBoolean("NotiClick"))
            {
                Log.e("Main Activity", "saved instance");
                //Intent myIntent = new Intent(this.context, alarmSettings.class);
                //startActivityForResult(myIntent, 0);

                my_intent.putExtra("extra", "off");
                sendBroadcast(my_intent);
            }

        }

        Intent loadArticle = new Intent(this, loadArticle.class);
        startService(loadArticle);
        Set<String> test = mPrefs.getStringSet("wikipedia title", new HashSet<String>());
        if (test.toString().equals("[]"))
        {
            Snackbar.make(this.findViewById(android.R.id.content), "Unable to connect to connect to internet.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        //imageView.setImageDrawable(getDrawable(R.drawable.wikilogo));
        imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.wikilogo, 100, 100));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        CustomAdapter buckysAdapter = new CustomAdapter(this, alarmsList,this);
        buckysListView = (ListView) findViewById(R.id.alarm_list);
        buckysListView.setAdapter(buckysAdapter);
        buckysListView.invalidateViews();

    }

    public static void deleteEntry(int id)
    {
        System.out.println("0000000000000000000000000000000000 " + id);
        for (int i = 0; i < alarmsList.size(); i++)
        {
            if (alarmsList.get(i).getId() == id)
            {
                alarmsList.remove(i);
                System.out.println("removed");
            }
        }
        buckysListView.invalidateViews();
        SharedPreferences.Editor editor = mPrefs.edit();

        Set<String> set= new HashSet<String>();
        for (int i = 0; i < alarmsList.size(); i++)
        {
            set.add(alarmsList.get(i).getJSONObject().toString());
        }
        editor.putStringSet("some_name", set);
        editor.apply();
    }


    public void loadFromStorage()
    {
        SharedPreferences mPrefs = MainActivity.this.getSharedPreferences("some_name", 0);
        /*
        Set<String> set = mPrefs.getStringSet("some_name", null);

        if (set != null)
        {
            alarmsList.clear();
            for (String s : set) {
                try {

                    JSONObject jsonObject = new JSONObject(s);

                    int hour = jsonObject.getInt("hour");
                    int minutes = jsonObject.getInt("minutes");
                    boolean active = jsonObject.getBoolean("active");
                    System.out.println(active);
                    boolean repeat = jsonObject.getBoolean("repeat");
                    JSONArray daysJ = jsonObject.getJSONArray("days");
                    boolean[] days = new boolean[7];
                    for (int i = 0; i < daysJ.length(); ++i)
                    {
                        days[i] = daysJ.getBoolean(i);
                    }
                    int wikiMode = jsonObject.getInt("wikiMode");
                    boolean vibrate = jsonObject.getBoolean("vibrate");
                    String label = jsonObject.getString("label");
                    int id = jsonObject.getInt("id");
                    alarmItem myclass = new alarmItem(hour, minutes, active, repeat, days, wikiMode, vibrate, label, id);
                    alarmsList.add(myclass);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        */

        try
        {
            JSONArray jsonArray = new JSONArray(mPrefs.getString(
                    "alarms", null));
            // jsonArray contains the data, use jsonArray.getString(index) to
            // retreive the elements
            alarmsList.clear();
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                System.out.println(jsonObject);
                int hour = jsonObject.getInt("hour");
                int minutes = jsonObject.getInt("minutes");
                boolean active = jsonObject.getBoolean("active");
                System.out.println(active);
                boolean repeat = jsonObject.getBoolean("repeat");
                JSONArray daysJ = jsonObject.getJSONArray("days");
                boolean[] days = new boolean[7];
                for (int j = 0; j < daysJ.length(); ++j)
                {
                    days[j] = daysJ.getBoolean(j);
                }
                int wikiMode = jsonObject.getInt("wikiMode");
                boolean vibrate = jsonObject.getBoolean("vibrate");
                String label = jsonObject.getString("label");
                int id = jsonObject.getInt("id");
                alarmItem myclass = new alarmItem(hour, minutes, active, repeat, days, wikiMode, vibrate, label, id);
                alarmsList.add(myclass);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        /*

        Set<String> set2 = mPrefs.getStringSet("historyTitles", null);

        if (set2 != null)
        {
            titles.clear();
            for (String s : set2)
            {
                titles.add(s);
            }
        }
        Set<String> set3 = mPrefs.getStringSet("urls", null);

        if (set3 != null)
        {
            urls.clear();
            for (String s : set3)
            {
                urls.add(s);
            }
        }
        */
        try
        {
            JSONArray jsonArray = new JSONArray(mPrefs.getString(
                    "history_json_titles", null));
            // jsonArray contains the data, use jsonArray.getString(index) to
            // retreive the elements
            titles.clear();
            for(int i = 0; i < jsonArray.length(); i++)
            {
                titles.add(jsonArray.getString(i));
            }
            JSONArray jsonArray2 = new JSONArray(mPrefs.getString(
                    "history_json_urls", null));
            // jsonArray contains the data, use jsonArray.getString(index) to
            // retreive the elements
            urls.clear();
            for(int i = 0; i < jsonArray2.length(); i++)
            {
                urls.add(jsonArray2.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }






    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        */

        if (id == R.id.history)
        {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
