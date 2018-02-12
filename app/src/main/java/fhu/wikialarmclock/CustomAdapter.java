package fhu.wikialarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static fhu.wikialarmclock.MainActivity.alarm_manager;
import static fhu.wikialarmclock.MainActivity.alarmsList;
import static fhu.wikialarmclock.MainActivity.deleteEntry;
import static fhu.wikialarmclock.MainActivity.mPrefs;
import static fhu.wikialarmclock.MainActivity.my_intent;

/**
 * Created by Alan on 1/18/2017.
 */


public class CustomAdapter extends ArrayAdapter<alarmItem>
{

    private Context context;
    Toast currentToast;
    boolean threadActive = false;
    long lastUse = 0;
    private static MainActivity parent;


    public CustomAdapter(Context context, ArrayList<alarmItem> foods, MainActivity activity)
    {
        super(context, R.layout.custom_row, foods);
        this.context = context;
        parent = activity;
    }

    @NonNull
    @Override
    public View getView(int position, final View convertView, ViewGroup parent)
    {
        LayoutInflater buckysInflator = LayoutInflater.from(getContext());
        //final View customView = buckysInflator.inflate(R.layout.custom_row, parent, false);
        final View customView = buckysInflator.inflate(R.layout.custom_row, parent, false);


        final alarmItem alarmItem = getItem(position);
        //alarmPosition = position;
        final int id = alarmItem.getId();

        updateText(customView, id);

        final int p = position;
        Switch activeSwitch = (Switch) customView.findViewById(R.id.activeSwitch);
        activeSwitch.setChecked(alarmItem.getActive());
        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //if (activeSwitch.isChecked())'
                if (isChecked)
                {

                    save(customView, id);
                    setAlarm(customView, id);
                    System.out.println(p);
                }
                else
                {

                    for (int i = 0; i < 7; i++)
                    {
                        alarm_manager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                        PendingIntent pi = PendingIntent.getBroadcast(context, alarmItem.getId() + i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarm_manager.cancel(pi);

                    }

                    save(customView, id);
                }

                /*
                SharedPreferences.Editor editor = mPrefs.edit();

                Set<String> set= new HashSet<String>();
                for (int i = 0; i < alarmsList.size(); i++)
                {
                    set.add(alarmsList.get(i).getJSONObject().toString());
                }
                editor.putStringSet("some_name", set);
                editor.apply()*/


            }
        });
        final Button delete = (Button) customView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //deleteEntry(alarmItem.getId());
                deleteEntry(id);
                for (int i = 0; i < 7; i++)
                {
                    alarm_manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    PendingIntent pi = PendingIntent.getBroadcast(context, alarmItem.getId() + i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.cancel(pi);
                }

            }
        });
        final LinearLayout editLayout = (LinearLayout) customView.findViewById(R.id.editLayout);
        editLayout.setVisibility(View.GONE);
        final RelativeLayout halfRow = (RelativeLayout) customView.findViewById(R.id.halfRow);
        final RelativeLayout editRow = (RelativeLayout) customView.findViewById(R.id.editRow);

        final Button edit = (Button) customView.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                /**
                 alarmID = alarmItem.getId();
                 Intent myIntent = new Intent(view.getContext(), alarmSettings.class);
                 //context.startActivityForResult(myIntent, 0);
                 if (context instanceof MainActivity)
                 {
                 ((Activity) context).startActivityForResult(myIntent, 0);
                 }
                 else
                 {
                 }
                 */
                if (editLayout.getVisibility() == View.GONE)
                {
                    editLayout.setVisibility(View.VISIBLE);
                    halfRow.setVisibility(View.GONE);
                    editRow.setVisibility(View.GONE);
                    //editLayout.animate().translationY(view.getHeight());
                }
            }

        });


        final Button done = (Button) customView.findViewById(R.id.doneEdit);
        done.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                editLayout.setVisibility(View.GONE);
                halfRow.setVisibility(View.VISIBLE);
                editRow.setVisibility(View.VISIBLE);
            }

        });

        TimePicker timepicker = (TimePicker) customView.findViewById(R.id.timepicker);
        final Switch repeat = (Switch) customView.findViewById(R.id.repeat);
        final LinearLayout daysLayout = (LinearLayout) customView.findViewById(R.id.daysLayout);
        final ToggleButton[] days = new ToggleButton[7];
        days[0] = (ToggleButton) customView.findViewById(R.id.sun);
        days[1] = (ToggleButton) customView.findViewById(R.id.mon);
        days[2] = (ToggleButton) customView.findViewById(R.id.tue);
        days[3] = (ToggleButton) customView.findViewById(R.id.wed);
        days[4] = (ToggleButton) customView.findViewById(R.id.thu);
        days[5] = (ToggleButton) customView.findViewById(R.id.fri);
        days[6] = (ToggleButton) customView.findViewById(R.id.sat);
        Spinner spinner = (Spinner) customView.findViewById(R.id.spinner);
        final ArrayAdapter<String> adapter;
        List<String> list;
        list = new ArrayList<String>();
        list.add("Random Article");
        list.add("Featured Article");
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Switch vibrate = (Switch) customView.findViewById(R.id.vibrate);
        final EditText labelEdit = (EditText) customView.findViewById(R.id.label);

        if (Build.VERSION.SDK_INT >= 23)
        {
            timepicker.setHour(alarmItem.getHour());
            timepicker.setMinute(alarmItem.getMinutes());
        }
        else
        {
            timepicker.setCurrentHour(alarmItem.getHour());
            timepicker.setCurrentMinute(alarmItem.getMinutes());
        }
        repeat.setChecked(alarmItem.getRepeat());
        boolean[] daysValues = alarmItem.getDays();
        for (int j = 0; j < 7; j++)
        {
            days[j].setChecked(daysValues[j]);
        }
        spinner.setSelection(alarmItem.getWikiMode());
        vibrate.setChecked(alarmItem.getVibrate());
        labelEdit.setText(alarmItem.getLabel());


        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener()
        {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
            {
                save(customView, id);
                setAlarm(customView, id);
                updateText(customView, id);
            }
        });

        if (repeat.isChecked())
        {
            daysLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            daysLayout.setVisibility(View.GONE);
        }
        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    daysLayout.setVisibility(View.VISIBLE);
                    boolean anyTrue = false;
                    for (int i = 0; i < 7; i++)
                    {
                        if (days[i].isChecked())
                        {
                            anyTrue = true;
                        }
                    }
                    if (!anyTrue)
                    {
                        for (int i = 0; i < 7; i++)
                        {
                            days[i].setChecked(true);
                        }
                    }
                }
                else
                {
                    daysLayout.setVisibility(View.GONE);
                }
                save(customView, id);
                setAlarm(customView, id);
            }
        });
        for (int i = 0; i < 7; i++)
        {
            days[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    boolean anyTrue = false;
                    for (int i = 0; i < 7; i++)
                    {
                        if (days[i].isChecked())
                        {
                            anyTrue = true;
                        }
                    }
                    if (!anyTrue)
                    {
                        daysLayout.setVisibility(View.GONE);
                        repeat.setChecked(false);
                    }
                    save(customView, id);
                    setAlarm(customView, id);
                    updateText(customView, id);
                }
            });
        }
        labelEdit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                save(customView, id);
                updateText(customView, id);
            }
        });
        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                save(customView, id);
                setAlarm(customView, id);
            }
        });
        final int alarm_id = id;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                save(customView, alarm_id);
                setAlarm(customView, alarm_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        return customView;
    }

    private void updateText(View customView, int id)
    {
        int hour = 0;
        int minutes = 0;
        String AP = "AM";
        String label = "";
        boolean[] days = new boolean[7];
        boolean rep = false;
        for (alarmItem a : alarmsList)
        {
            if (a.getId() == id)
            {
                hour = a.getHour();
                minutes = a.getMinutes();
                label = a.getLabel();
                days = a.getDays();
                rep = a.getRepeat();
            }
        }
        final TextView textRow1 = (TextView) customView.findViewById(R.id.textRow1);
        final TextView textRow2 = (TextView) customView.findViewById(R.id.textRow2);
        final TextView textRow3 = (TextView) customView.findViewById(R.id.textRow3);
        if (hour > 12)
        {
            hour = hour - 12;
            AP = "PM";
        }

        if (!label.equals(""))
        {
            textRow1.setVisibility(View.VISIBLE);
            textRow1.setText(label);
        }
        else
        {
            textRow1.setVisibility(View.GONE);
        }
        textRow2.setText(hour + ":" + String.format("%02d", minutes) + " " + AP);
        if (!rep)
        {
            textRow3.setVisibility(View.GONE);
        }
        else
        {
            textRow3.setVisibility(View.VISIBLE);
            textRow3.setText("");
            if (days[0])
            {
                textRow3.setText(textRow3.getText() + "Sun ");
            }
            if (days[1])
            {
                textRow3.setText(textRow3.getText() + "Mon ");
            }
            if (days[2])
            {
                textRow3.setText(textRow3.getText() + "Tue ");
            }
            if (days[3])
            {
                textRow3.setText(textRow3.getText() + "Wed ");
            }
            if (days[4])
            {
                textRow3.setText(textRow3.getText() + "Thu ");
            }
            if (days[5])
            {
                textRow3.setText(textRow3.getText() + "Fri ");
            }
            if (days[6])
            {
                textRow3.setText(textRow3.getText() + "Sat ");
            }


        }

    }

    private void save(View customView, int id)
    {

        TimePicker timepicker = (TimePicker) customView.findViewById(R.id.timepicker);
        final Switch repeat = (Switch) customView.findViewById(R.id.repeat);
        final ToggleButton[] days = new ToggleButton[7];
        days[0] = (ToggleButton) customView.findViewById(R.id.sun);
        days[1] = (ToggleButton) customView.findViewById(R.id.mon);
        days[2] = (ToggleButton) customView.findViewById(R.id.tue);
        days[3] = (ToggleButton) customView.findViewById(R.id.wed);
        days[4] = (ToggleButton) customView.findViewById(R.id.thu);
        days[5] = (ToggleButton) customView.findViewById(R.id.fri);
        days[6] = (ToggleButton) customView.findViewById(R.id.sat);
        Spinner spinner = (Spinner) customView.findViewById(R.id.spinner);
        Switch vibrate = (Switch) customView.findViewById(R.id.vibrate);
        final EditText labelEdit = (EditText) customView.findViewById(R.id.label);
        Switch activeSwitch = (Switch) customView.findViewById(R.id.activeSwitch);

        int hou;
        int min;
        if (Build.VERSION.SDK_INT >= 23)
        {
            hou = timepicker.getHour();
            min = timepicker.getMinute();
        }
        else
        {
            hou = timepicker.getCurrentHour();
            min = timepicker.getCurrentMinute();
        }
        boolean rep = repeat.isChecked();
        boolean[] day = new boolean[7];
        for (int i = 0; i < 7; i++)
        {
            day[i] = days[i].isChecked();
        }
        int spi = spinner.getSelectedItemPosition();
        boolean vib = vibrate.isChecked();
        String lab = labelEdit.getText().toString();
        lab.trim();
        boolean act = activeSwitch.isChecked();

        //if alarm already exists, update info, retreive id
        for (alarmItem a : alarmsList)
        {
            if (a.getId() == id)
            {
                a.setHour(hou);
                a.setMinutes(min);
                a.setRepeat(rep);
                a.setDays(day);
                a.setWikiMode(spi);
                a.setVibrate(vib);
                a.setLabel(lab);
                a.setActive(act);
            }
        }
        /*
        alarmsList.get(alarmPosition).setHour(hou);
        alarmsList.get(alarmPosition).setMinutes(min);
        alarmsList.get(alarmPosition).setRepeat(rep);
        alarmsList.get(alarmPosition).setDays(day);
        alarmsList.get(alarmPosition).setWikiMode(spi);
        alarmsList.get(alarmPosition).setVibrate(vib);
        alarmsList.get(alarmPosition).setLabel(lab);
        alarmsList.get(alarmPosition).setActive(act);
        */

        //store new alarm info
        SharedPreferences.Editor editor = mPrefs.edit();

        /*
        Set<String> set= new HashSet<String>();
        for (int i = 0; i < alarmsList.size(); i++)
        {
            set.add(alarmsList.get(i).getJSONObject().toString());
        }
        editor.putStringSet("some_name", set);
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


    private void setAlarm(View customView, int id)
    {
        TimePicker timepicker = (TimePicker) customView.findViewById(R.id.timepicker);
        final Switch repeat = (Switch) customView.findViewById(R.id.repeat);
        final ToggleButton[] days = new ToggleButton[7];
        days[0] = (ToggleButton) customView.findViewById(R.id.sun);
        days[1] = (ToggleButton) customView.findViewById(R.id.mon);
        days[2] = (ToggleButton) customView.findViewById(R.id.tue);
        days[3] = (ToggleButton) customView.findViewById(R.id.wed);
        days[4] = (ToggleButton) customView.findViewById(R.id.thu);
        days[5] = (ToggleButton) customView.findViewById(R.id.fri);
        days[6] = (ToggleButton) customView.findViewById(R.id.sat);
        Spinner spinner = (Spinner) customView.findViewById(R.id.spinner);
        Switch vibrate = (Switch) customView.findViewById(R.id.vibrate);
        final EditText labelEdit = (EditText) customView.findViewById(R.id.label);
        Switch activeSwitch = (Switch) customView.findViewById(R.id.activeSwitch);


        /*
        if (!activeSwitch.isChecked())
        {
            return;
        */
        activeSwitch.setChecked(true);
        int hou;
        int min;
        if (Build.VERSION.SDK_INT >= 23)
        {
            hou = timepicker.getHour();
            min = timepicker.getMinute();
        }
        else
        {
            hou = timepicker.getCurrentHour();
            min = timepicker.getCurrentMinute();
        }
        boolean rep = repeat.isChecked();
        boolean vib = vibrate.isChecked();
        boolean[] day = new boolean[7];
        for (int i = 0; i < 7; i++)
        {
            day[i] = days[i].isChecked();
        }
        int spi = spinner.getSelectedItemPosition();

        //start actual alarm
        alarm_manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        my_intent.putExtra("extra", "on");
        my_intent.putExtra("vibrate", vib);
        my_intent.putExtra("id", id);
        my_intent.putExtra("spinner", spi);

        //Bundle extras = new Bundle();
        //extras.putString("extra","on");
        //extras.putBoolean("vibrate", vib);
        //my_intent.putExtras(extras);

        //for none repeating alarms
        if (!rep)
        {
            my_intent.putExtra("repeating", false);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hou);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            if (calendar.getTimeInMillis() < System.currentTimeMillis())
            {
                calendar.add(Calendar.HOUR_OF_DAY, 24);
            }

            PendingIntent piNonRepeating = PendingIntent.getBroadcast(context, id, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), piNonRepeating);
            for (int i = 1; i < 7; i++)
            {
                PendingIntent pi = PendingIntent.getBroadcast(context, id + i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarm_manager.cancel(pi);
            }

            //make toast
            long millis = calendar.getTimeInMillis() - System.currentTimeMillis();
            long hoursFromNow = (millis / 1000) / (60 * 60);
            long minFromNow = ((millis / 1000) - (hoursFromNow * 3600)) / (60);
            long secondsFromNow = (millis / 1000) % 60;

            if (hoursFromNow == 0 && minFromNow == 0)
            {
                tryShowToast("Alarm set for " + secondsFromNow + " seconds from now.");
            }
            else if (hoursFromNow == 0)
            {
                tryShowToast("Alarm set for " + minFromNow + " minutes and " + secondsFromNow + " seconds from now.");
            }
            else
            {
                tryShowToast("Alarm set for " + hoursFromNow + "hours, " + minFromNow + " minutes, and " + secondsFromNow + " seconds from now.");
            }
            lastUse = System.currentTimeMillis();

        }
        if (rep)
        {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hou);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            long millis = 0;
            for (int i = 0; i < 7; i++)
            {
                if (day[i])
                {
                    calendar.set(Calendar.DAY_OF_WEEK, i + 1);
                    my_intent.putExtra("repeating", true);
                    my_intent.putExtra("id", id + i);
                    PendingIntent piRepeating = PendingIntent.getBroadcast(context, id + i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (calendar.getTimeInMillis() < System.currentTimeMillis())
                    {
                        //alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (1000 * 60 * 60 * 24 * 7), AlarmManager.INTERVAL_DAY * 7, piRepeating);
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (1000 * 60 * 60 * 24 * 7), piRepeating);
                        if (millis == 0 || (calendar.getTimeInMillis() + (1000 * 60 * 60 * 24 * 7)) < millis)
                        {
                            millis = calendar.getTimeInMillis() + (1000 * 60 * 60 * 24 * 7);
                        }
                    }
                    else
                    {
                        //alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, piRepeating);
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), piRepeating);
                        if (millis == 0 || calendar.getTimeInMillis() < millis)
                        {
                            millis = calendar.getTimeInMillis();
                        }
                    }
                }
                else
                {
                    PendingIntent piRepeating = PendingIntent.getBroadcast(context, id + i, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.cancel(piRepeating);
                }

            }

            //make toast
            millis = millis - System.currentTimeMillis();
            long daysFromNow = ((millis / 1000) / (60 * 60 * 24));
            long hoursFromNow = (millis / 1000 - (daysFromNow * 3600 * 24)) / (60 * 60);
            long minFromNow = ((millis / 1000) - ((hoursFromNow * 3600) + (daysFromNow * 3600 * 24))) / (60);
            long secondsFromNow = (millis / 1000) % 60;

            if (daysFromNow == 0 && hoursFromNow == 0 && minFromNow == 0)
            {
                tryShowToast("Alarm set for " + secondsFromNow + " seconds from now.");
            }
            else if (daysFromNow == 0 && hoursFromNow == 0)
            {
                tryShowToast("Alarm set for " + minFromNow + " minutes and " + secondsFromNow + " seconds from now.");
            }
            else if (daysFromNow == 0)
            {
                tryShowToast("Alarm set for " + hoursFromNow + " hours, " + minFromNow + " minutes and " + secondsFromNow + " seconds from now.");
            }
            else
            {
                tryShowToast("Alarm set for " + daysFromNow + " days, " + hoursFromNow + " hours, " + minFromNow + " minutes, and " + secondsFromNow + " seconds from now.");
            }
            lastUse = System.currentTimeMillis();

        }
    }

    private void tryShowToast(final String text)
    {
        if (threadActive)
        {
            return;
        }
        Thread t = new Thread()
        {
            public void run()
            {
                threadActive = true;
                while (System.currentTimeMillis() - lastUse < 500)
                {

                }
                threadActive = false;
                showToast(text);
                //MainActivity.newImageRequired = true;
//                try
//                {
//                    setNewImage();
//                } catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
            }
        };
        t.start();


    }

    private void showToast(final String text)
    {
        if (currentToast != null)
        {
            currentToast.cancel();
        }
        parent.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                currentToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                currentToast.show();
            }
        });
    }

}
