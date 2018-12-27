package com.example.administrator.reminderapp.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.administrator.reminderapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tv_toolbar, txt_reminder;
    Button btn_reminder;
    EditText input_what, input_where, input_whom, input_date, input_st;
    DatePickerDialog mDatePicker;
    TimePickerDialog mTimePicker;
    Toolbar tb_toolbar;
    Typeface typeface;
    LinearLayout ll_main;
    Calendar yCalendar;
    Context context;
    public static String TAG = "MainActivity";
    public static String font = "fonts/AvenirLTStd-Light.otf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        context = this;
        typeface = Typeface.createFromAsset(context.getAssets(), font);
        yCalendar = Calendar.getInstance();

        final SharedPreferences prefs = context.getSharedPreferences(LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String str_user_name = prefs.getString("user_name", "");

        tb_toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        tv_toolbar = (TextView) tb_toolbar.findViewById(R.id.tv_toolbar);
        tv_toolbar.setTypeface(typeface);
        tv_toolbar.setText(str_user_name);

        setSupportActionBar(tb_toolbar);

        setUIAndTypeface();

        setListeners();


    }

    public void setUIAndTypeface() {
        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        txt_reminder = (TextView) findViewById(R.id.txt_reminder);
        btn_reminder = (Button) findViewById(R.id.btn_reminder);
        input_what = (EditText) findViewById(R.id.input_what);
        input_where = (EditText) findViewById(R.id.input_where);
        input_whom = (EditText) findViewById(R.id.input_whom);
        input_date = (EditText) findViewById(R.id.input_date);
        input_st = (EditText) findViewById(R.id.input_st);

        txt_reminder.setTypeface(typeface);
        btn_reminder.setTypeface(typeface);
        input_what.setTypeface(typeface);
        input_where.setTypeface(typeface);
        input_whom.setTypeface(typeface);
        input_date.setTypeface(typeface);
        input_st.setTypeface(typeface);
    }

    public void setListeners() {
        input_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    final Calendar myCalendar = Calendar.getInstance();
                    int year = myCalendar.get(Calendar.YEAR);
                    int month = myCalendar.get(Calendar.MONTH);
                    int dayOfMonth = myCalendar.get(Calendar.DAY_OF_MONTH);

                    mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker datePicker, int y, int m, int day) {
                            yCalendar.set(Calendar.YEAR, y);
                            yCalendar.set(Calendar.MONTH, m);
                            yCalendar.set(Calendar.DAY_OF_MONTH, day);


                            SimpleDateFormat sd_format = new SimpleDateFormat("dd MMM,yy", Locale.US);
                            String myDate = sd_format.format(yCalendar.getTime());
                            myDate = myDate.replace(",", "'");
                            input_date.setText(myDate);
                        }
                    }, year, month, dayOfMonth);

                    mDatePicker.setTitle("Date");
                    mDatePicker.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        input_st.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    final Calendar myCalendar = Calendar.getInstance();
                    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                    int minute = myCalendar.get(Calendar.MINUTE);

                    String str_date = String.valueOf(input_date.getText());

                    if (!(TextUtils.isEmpty(str_date))) {

                        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                yCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                                yCalendar.set(Calendar.MINUTE, selectedMinute);

                                String myFormat = "HH:mm";
                                SimpleDateFormat sd_format = new SimpleDateFormat(myFormat, Locale.US);

                                String start_time = sd_format.format(yCalendar.getTime());
                                input_st.setText(start_time);

                            }
                        }, hour, minute, true);


                        mTimePicker.setTitle("Time");
                        mTimePicker.show();
                    } else {
                        Toast.makeText(context, "Please enter Date", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btn_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            if (validation()) {
                                String str_what = input_what.getText().toString();
                                String str_where = input_where.getText().toString();
                                String str_email = input_whom.getText().toString();

                                inviteByIntent(str_what, str_where, str_email, yCalendar);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });
    }

    public boolean validation() {
        boolean valid = false;

        String str_email = input_whom.getText().toString();
        if (!(TextUtils.isEmpty(str_email)) && str_email.contains("@") && str_email.contains(".")) {
            valid = true;
        } else {
            valid = false;
        }

        String str_where = input_where.getText().toString();
        if (!(TextUtils.isEmpty(str_where))) {
            valid = true;
        } else {
            valid = false;

        }

        String str_what = input_what.getText().toString();
        if (!(TextUtils.isEmpty(str_what))) {
            valid = true;
        } else {
            valid = false;

        }

        if (!valid) {
            String str_alert = "all fields are mandatory";
            Snackbar.make(ll_main, str_alert, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }


        return valid;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem menuItem = menu.findItem(R.id.log_out);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.log_out:
                showLogOutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    public void inviteByIntent(String str_what, String str_where, String str_email, Calendar yCalendar) {
        try {

            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            yCalendar.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            yCalendar.getTimeInMillis())
                    //.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                    .putExtra(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Calcutta")
                    .putExtra(CalendarContract.Events.TITLE, str_what)
                    .putExtra(CalendarContract.Events.DESCRIPTION, "Reminder")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, str_where)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    .putExtra(Intent.EXTRA_EMAIL, str_email)
                    .putExtra(CalendarContract.Events.ALLOWED_REMINDERS, true)
                    .putExtra(CalendarContract.Events.ALLOWED_ATTENDEE_TYPES, true);

            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showExitDialog() {
        final Dialog exitDialog;
        exitDialog = new Dialog(MainActivity.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setCanceledOnTouchOutside(false);
        exitDialog.setContentView(R.layout.exit_dialog);

        TextView text = (TextView) exitDialog.findViewById(R.id.textDialog);
        text.setTypeface(typeface);

        TextView text_2 = (TextView) exitDialog.findViewById(R.id.textDialog_2);
        text_2.setTypeface(typeface);

        Button declineButton = (Button) exitDialog.findViewById(R.id.declineButton);
        declineButton.setTypeface(typeface);

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exitDialog.isShowing()) {
                    exitDialog.dismiss();
                }
            }
        });

        Button acceptButton = (Button) exitDialog.findViewById(R.id.acceptButton);
        acceptButton.setTypeface(typeface);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (exitDialog.isShowing()) {
                    exitDialog.dismiss();
                }
                finish();
            }
        });

        if (!(exitDialog.isShowing())) {
            exitDialog.show();
        }
    }

    public void showLogOutDialog() {
        final Dialog exitDialog;
        exitDialog = new Dialog(MainActivity.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setCanceledOnTouchOutside(false);
        exitDialog.setContentView(R.layout.logout_dialog);

        TextView text = (TextView) exitDialog.findViewById(R.id.textDialog);
        text.setTypeface(typeface);

        TextView text_2 = (TextView) exitDialog.findViewById(R.id.textDialog_2);
        text_2.setTypeface(typeface);

        Button declineButton = (Button) exitDialog.findViewById(R.id.declineButton);
        declineButton.setTypeface(typeface);

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exitDialog.isShowing()) {
                    exitDialog.dismiss();
                }
            }
        });

        Button acceptButton = (Button) exitDialog.findViewById(R.id.acceptButton);
        acceptButton.setTypeface(typeface);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (exitDialog.isShowing()) {
                    exitDialog.dismiss();
                }

                final SharedPreferences prefs = context.getSharedPreferences(LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString("registered", "");
                editor.commit();

                Snackbar.make(ll_main, "successfully logged out", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        finish();

                    }
                }, 1500);

            }
        });

        if (!(exitDialog.isShowing())) {
            exitDialog.show();
        }
    }


}
