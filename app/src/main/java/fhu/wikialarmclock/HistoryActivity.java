package fhu.wikialarmclock;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import static fhu.wikialarmclock.MainActivity.titles;
import static fhu.wikialarmclock.MainActivity.urls;


public class HistoryActivity extends AppCompatActivity
{


    static ListAdapter historyAdapter;
    static ListView historyListView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        loadFromStorage();

        historyAdapter = new HistoryAdapter(this, titles);
        historyListView = (ListView) findViewById(R.id.alarm_list);
        historyListView.setAdapter(historyAdapter);


        SpannableString s = new SpannableString("History");
        fhu.wikialarmclock.TypefaceSpan tfs = new fhu.wikialarmclock.TypefaceSpan(this, "HoeflerText.ttf");
        s.setSpan(tfs, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ActionBar actionBar = getActionBar();
        //actionBar.setTitle(s);
        getSupportActionBar().setTitle(s);

    }
    public void loadFromStorage()
    {
        SharedPreferences mPrefs = HistoryActivity.this.getSharedPreferences("some_name", 0);

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

            System.out.println("--------------------"+jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            e.printStackTrace();
        }


    }
}
