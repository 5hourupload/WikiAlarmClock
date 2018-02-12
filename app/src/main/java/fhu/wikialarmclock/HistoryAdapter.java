package fhu.wikialarmclock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static fhu.wikialarmclock.MainActivity.titles;
import static fhu.wikialarmclock.MainActivity.urls;

/**
 * Created by Alan on 1/18/2017.
 */


public class HistoryAdapter extends ArrayAdapter<String>
{

    private Context context;


    public HistoryAdapter(Context context, ArrayList<String> foods)
    {
        super(context, R.layout.history_row, foods);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, final View convertView, ViewGroup parent)
    {
        LayoutInflater historyInflator = LayoutInflater.from(getContext());
        //final View customView = buckysInflator.inflate(R.layout.custom_row, parent, false);
        final View customView = historyInflator.inflate(R.layout.history_row, parent, false);


        final String historyItem = getItem(position);
        final TextView label = (TextView) customView.findViewById(R.id.history_text);
        label.setText(historyItem);

        SharedPreferences mPrefs = getContext().getSharedPreferences("some_name", 0);
        label.setText(historyItem);



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
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        final String url = urls.get(position).substring(1, urls.get(position).length() -1);


        /*

        Set<String> set = mPrefs.getStringSet("urls", null);
        ArrayList<String> array = new ArrayList<String>();
        for (String s : set)
        {
            array.add(s);
            System.out.println(s);
        }
        final String url = array.get(position).substring(1, array.get(position).length() - 1);

    *   */



        LinearLayout row = (LinearLayout) customView.findViewById(R.id.history_row_link);
        row.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (null != intent.resolveActivity(context.getPackageManager()))
                {
                    context.startActivity(intent);
                }
            }
        });

        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        row.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                v.startAnimation(buttonClick);

                return false;
            }
        });


        return customView;
    }


}
