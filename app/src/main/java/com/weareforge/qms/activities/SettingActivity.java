package com.weareforge.qms.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.weareforge.qms.R;
import com.weareforge.qms.db.QMSDatabaseHelper;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.Opener;
import com.weareforge.qms.utils.SharedPreference;

/**
 * Created by prajit on 3/3/16.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView txtBack;
    private ImageButton btn_back;
    private TextView txtSyncOverWifi;
    private TextView txtDisconnect;
    private TextView txtAboutApp;
    private TextView txtTerms;
    private TextView txtSettings;

    private Switch toggleSync;
    private Switch toggleDisconnect;

    private QMSDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private Opener opener;
    private FontHelper fontHelper;
    private SharedPreference sharedPreference;

    private static Boolean isSync = false;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.txtSettings = (TextView) findViewById(R.id.txtSetting);
        this.btn_back = (ImageButton) findViewById(R.id.back_img);
        this.txtBack = (TextView) findViewById(R.id.txt_back);
        this.txtSyncOverWifi = (TextView) findViewById(R.id.txtSyncOverWifi);
        this.txtDisconnect = (TextView) findViewById(R.id.txtDisconnectAccount);
        this.txtAboutApp = (TextView) findViewById(R.id.txtAboutApp);
        this.txtTerms = (TextView) findViewById(R.id.txtTermsConditions);
        fontHelper = new FontHelper(this);

        this.txtBack.setVisibility(View.VISIBLE);
        this.btn_back.setVisibility(View.VISIBLE);

        dbHelper = new QMSDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        //ClickListners
        this.txtAboutApp.setOnClickListener(this);
        this.txtTerms.setOnClickListener(this);

        //Treatment Fonts
        this.txtSettings.setTypeface(fontHelper.getDefaultFont());
        this.txtBack.setTypeface(fontHelper.getDefaultFont("bold"));
        this.txtSyncOverWifi.setTypeface(fontHelper.getDefaultFont("regular"));
        this.txtDisconnect.setTypeface(fontHelper.getDefaultFont("regular"));
        this.txtAboutApp.setTypeface(fontHelper.getDefaultFont("regular"));
        this.txtTerms.setTypeface(fontHelper.getDefaultFont("regular"));

        this.toggleDisconnect = (Switch) findViewById(R.id.toggleDisconnect);
        this.toggleSync = (Switch) findViewById(R.id.toggleSync);

        this.opener = new Opener(this);
        this.sharedPreference = new SharedPreference(this);
        isSync = sharedPreference.getBoolValues("isSync");

        if(isSync)
        {
            this.toggleSync.setChecked(true);
        }
        else
        {
            this.toggleSync.setChecked(false);
        }

        this.toggleDisconnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    cancelAllAlarmNotifications();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
                    builder1.setMessage("Are you sure you want to logout?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sharedPreference.setKeyValues("Flag", 0);
                                    dbHelper.dropAllTable(db);
                                    cancelAllAlarmNotifications();
                                    sharedPreference.setKeyValues("isSync", false);
                                    opener.Login();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    toggleDisconnect.setChecked(false);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
//                    cancelAllAlarmNotifications();
                }
            }
        });

        this.toggleSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sharedPreference.setKeyValues("isSync", true);
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                    if (mWifi.isConnected()) {
//                        Toast.makeText(SettingActivity.this,"Wifi Connected",Toast.LENGTH_LONG).show();
                       startAlarm();
                    }
                    else
                    {
                        cancelAllAlarmNotifications();

//                        Toast.makeText(SettingActivity.this,"Mobile Network",Toast.LENGTH_LONG).show();
                    }

                } else {
                    isSync = false;
                    sharedPreference.setKeyValues("isSync", false);
//                    Toast.makeText(SettingActivity.this,"Sync over any Network",Toast.LENGTH_LONG).show();
//                    Intent intent1 = new Intent(SettingActivity.this, AlarmReceiver.class);
//
//                    PendingIntent sender = PendingIntent.getBroadcast(SettingActivity.this, 1995, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
//                    // Get the AlarmManager service
//                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//                    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, sender);

                }
            }
        });

        this.btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        this.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txtTermsConditions:
                v.startAnimation(buttonClick);
                opener.TermsAndConditions();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.txtAboutApp:
                v.startAnimation(buttonClick);
                opener.AboutApp();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void startAlarm()
    {
        Intent intent1 = new Intent(SettingActivity.this, AlarmReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(SettingActivity.this, 1995, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        // Get the AlarmManager service
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 60000, sender);

       /* ComponentName receiver = new ComponentName(SettingActivity.this, SampleBootReceiver.class);
        PackageManager pm = SettingActivity.this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/

    }

    public void cancelAllAlarmNotifications() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent intent1 = new Intent(SettingActivity.this, AlarmReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(SettingActivity.this, 1995, intent1, 0);

        try {
            alarmManager.cancel(sender);

        } catch (Exception e) {
            Log.e("LOGTAG", "AlarmManager update was not canceled. " + e.toString());
        }
    }
}
