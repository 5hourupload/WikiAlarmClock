package fhu.wikialarmclock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alan on 1/18/2017.
 */

public class alarmItem
{

    int hour;
    int minutes;
    boolean active;
    boolean repeat;
    boolean[] days = new boolean[7];
    int wikiMode;
    boolean vibrate;
    String label;
    int id;


    public alarmItem(int h, int m, boolean a, boolean r, boolean[] d, int s, boolean v, String l, int i)
    {
        hour = h;
        minutes = m;
        active = a;
        repeat = r;
        days = d;
        wikiMode = s;
        vibrate = v;
        label = l;
        id = i;
    }

    public int getHour()
    {
        return hour;
    }
    public int getMinutes()
    {
        return minutes;
    }
    public boolean getActive()
    {
        return active;
    }
    public boolean getRepeat()
    {
        return repeat;
    }
    public boolean[] getDays()
    {
        return days;
    }
    public int getWikiMode()
    {
        return wikiMode;
    }
    public boolean getVibrate()
    {
        return vibrate;
    }
    public String getLabel()
    {
        return label;
    }
    public int getId()
    {
        return id;
    }

    public void setHour(int h)
    {
        hour = h;
    }
    public void setMinutes(int m)
    {
        minutes = m;
    }
    public void setActive(boolean a)
    {
        active = a;
    }
    public void setRepeat(boolean a)
    {
        repeat = a;
    }
    public void setDays(boolean[] d) {
        days = d;
    }
    public void setWikiMode(int w)
    {
        wikiMode = w;
    }
    public void setVibrate(boolean v)
    {
        vibrate = v;
    }
    public void setLabel(String l)
    {
        label = l;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public JSONObject getJSONObject()
    {
        JSONArray daysJ = new JSONArray();
        for (int i = 0; i < 7; i++)
        {
            daysJ.put(days[i]);
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("hour", this.hour);
            obj.put("minutes", this.minutes);
            obj.put("active", this.active);
            obj.put("repeat", this.repeat);
            obj.put("days", daysJ);
            obj.put("wikiMode", this.wikiMode);
            obj.put("vibrate", this.vibrate);
            obj.put("label", this.label);
            obj.put("id", this.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return obj;
    }
}
