package com.weareforge.qms.activities;

/**
 * Created by prajit on 4/5/16.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.weareforge.qms.Objects.updatedData;
import com.weareforge.qms.db.EngagementEvidenceOfflineContent;
import com.weareforge.qms.utils.ConnectionMngr;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "WasteInfoVal";
    private Context context;
    private ConnectionMngr connectionMngr;
    private EngagementEvidenceOfflineContent offlineContent;
    int activity;
    int findOut;
    int purposeActivity;
    int asaqStandard;

    @Override
    public void onReceive(Context context, Intent intent) {


        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent intent1 = new Intent(context, AlarmReceiver.class);

            PendingIntent sender = PendingIntent.getService(context, 1995, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            // Get the AlarmManager service
            AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60000, sender);
        }

        try{
//            Toast.makeText(context, "test adfadf ads fsd fs dfs df sdf!", Toast.LENGTH_LONG).show();
            Log.d(LOGTAG,"Inside AlarmReceiver");
            SharedPreferences settings=context.getSharedPreferences("PREF_TAG_TIMESTAMP", 0);
            String ts=settings.getString("currentTimestamp","");

            this.context=  context;
            offlineContent = new EngagementEvidenceOfflineContent(this.context);
            offlineContent.Open();
            connectionMngr = new ConnectionMngr(this.context);
            updatedData data = offlineContent.GetUpdateData();
            activity = data.getActivity();
            findOut = data.getFindOut();
            purposeActivity = data.getPurposeOfActivity();
            asaqStandard = data.getAsaqStandard();
            if(connectionMngr.hasConnection())
            {
                //Toast.makeText(context, "Pages to be loaded", Toast.LENGTH_SHORT).show();
                UpdateDB runner=new UpdateDB(this.context);
                runner.execute(activity,findOut,purposeActivity,asaqStandard);
            }else{
//				Toast toast = Toast.makeText(this.context, "No internet connection!", Toast.LENGTH_SHORT);
//			    toast.show();
            }
        }catch(Exception e){
//            Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    void scheduleAlarms(Context context)
   {
       Intent intent1 = new Intent(context, AlarmReceiver.class);

       PendingIntent sender = PendingIntent.getBroadcast(context, 1995, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
       // Get the AlarmManager service
       AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
       am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000, sender);
   }
}