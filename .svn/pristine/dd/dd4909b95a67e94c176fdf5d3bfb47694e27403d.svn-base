package com.weareforge.qms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.weareforge.qms.Objects.EngagementEvidenceData;
import com.weareforge.qms.Objects.IndustryContactDetails;

import java.util.ArrayList;

import eu.inmite.android.lib.validations.form.Utils;

/**
 * Created by prajit on 3/10/16.
 */
public class EngagementEvidenceDbHandler {

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    public EngagementEvidenceDbHandler(Context context)
    {
        dbHelper = new QMSDatabaseHelper(context);
    }

    public void Open()
    {
        database = dbHelper.getWritableDatabase();
    }

    public String InsertEngagementEvidence(EngagementEvidenceData engagementEvidenceData)
    {
        ContentValues values = new ContentValues();
        values.put(QMSDatabaseHelper.COLUMN_IND_ENG_ID, engagementEvidenceData.getIndustyEngagementId());
        values.put(QMSDatabaseHelper.COLUMN_ENG_EV_ID, engagementEvidenceData.getEngagementEvidenceId());
        values.put(QMSDatabaseHelper.COLUMN_REFERENCE, engagementEvidenceData.getReference());
        values.put(QMSDatabaseHelper.COLUMN_DATE_TIME, engagementEvidenceData.getDateTime());
        values.put(QMSDatabaseHelper.COLUMN_HOURS, engagementEvidenceData.getHours());
        values.put(QMSDatabaseHelper.COLUMN_VENUE, engagementEvidenceData.getVenue());
        values.put(QMSDatabaseHelper.COLUMN_FINDOUT_ID, engagementEvidenceData.getFindOutId());

        try {
            database.insert(QMSDatabaseHelper.TABLE_INDUSTRY_ENGAGEMENT,null,values);
            return "successfully saved Data";
        }
        catch (Exception exx)
        {
            System.out.print("Exception==" + exx);
            return "Error Occured while saving";
        }
    }

    public String InsertEngagementEvidenceData(EngagementEvidenceData engagementEvidenceData)
    {
        ContentValues values = new ContentValues();
        values.put(QMSDatabaseHelper.COLUMN_ENG_EV_ID, engagementEvidenceData.getEngagementEvidenceId());
        values.put(QMSDatabaseHelper.COLUMN_TITLE, engagementEvidenceData.getTitle());
        values.put(QMSDatabaseHelper.COLUMN_STATUS, engagementEvidenceData.getStatus());
        values.put(QMSDatabaseHelper.COLUMN_USER_ID, engagementEvidenceData.getUserid());
        values.put(QMSDatabaseHelper.COLUMN_CURRENT_STEP, engagementEvidenceData.getCurrentStep());

        try {
            database.insert(QMSDatabaseHelper.TABLE_ENGAGEMENT_EVIDENCE,null,values);
            return "successfully saved Data";
        }
        catch (Exception exx)
        {
            System.out.print("Exception==" + exx);
            return "Error Occured while saving";
        }
    }

    public ArrayList<EngagementEvidenceData> GetAllEngagementEvidence()
    {
        ArrayList<EngagementEvidenceData> engagementEvidenceArrayList = new ArrayList<EngagementEvidenceData>();

        String rawQuery = "SELECT * FROM " + QMSDatabaseHelper.TABLE_INDUSTRY_ENGAGEMENT + " JOIN " + QMSDatabaseHelper.TABLE_ENGAGEMENT_EVIDENCE
                + " ON " + QMSDatabaseHelper.TABLE_INDUSTRY_ENGAGEMENT+"."+QMSDatabaseHelper.COLUMN_ENG_EV_ID + " = " + QMSDatabaseHelper.TABLE_ENGAGEMENT_EVIDENCE + "." +QMSDatabaseHelper.COLUMN_ENG_EV_ID;
        System.out.println("Query==" + rawQuery);
        Cursor cur = database.rawQuery(
                rawQuery,
                null
        );

        if (cur.moveToFirst()) {
            do {
                EngagementEvidenceData evidenceData = new EngagementEvidenceData();
                evidenceData.setIndustryEngagementId(cur.getInt(0));
                evidenceData.setEngagementEvidenceId(cur.getInt(1));
                evidenceData.setReference(cur.getString(2));
                evidenceData.setDatetime(cur.getString(3));
                evidenceData.setHours(String.valueOf(cur.getFloat(4)));
                evidenceData.setVenue(cur.getString(5));
                evidenceData.setFindOutId(cur.getInt(6));
                evidenceData.setTitle(cur.getString(8));
                evidenceData.setStatus(String.valueOf(cur.getInt(9)));
                evidenceData.setUserid(cur.getInt(10));
                evidenceData.setCurrentStep(String.valueOf(cur.getInt(11)));
                //Adding Contacts into ArrayList
                engagementEvidenceArrayList.add(evidenceData);
            } while (cur.moveToNext());
        }
        return engagementEvidenceArrayList;
    }

    public void deleteEngagementEvidence(int deletedId) {
        database.delete(QMSDatabaseHelper.TABLE_ENGAGEMENT_EVIDENCE, QMSDatabaseHelper.COLUMN_ENG_EV_ID + "=" + deletedId, null);
    }

    public String UpdateEngagementEvidence(EngagementEvidenceData engagementEvidenceData)
    {
        ContentValues values = new ContentValues();
        values.put(QMSDatabaseHelper.COLUMN_REFERENCE, engagementEvidenceData.getReference());
        values.put(QMSDatabaseHelper.COLUMN_DATE_TIME, engagementEvidenceData.getDateTime());
        values.put(QMSDatabaseHelper.COLUMN_HOURS, engagementEvidenceData.getHours());
        values.put(QMSDatabaseHelper.COLUMN_VENUE, engagementEvidenceData.getVenue());
        values.put(QMSDatabaseHelper.COLUMN_FINDOUT_ID, engagementEvidenceData.getFindOutId());

        try {
             database.update(QMSDatabaseHelper.TABLE_INDUSTRY_ENGAGEMENT, values, QMSDatabaseHelper.COLUMN_IND_ENG_ID + " = ?",
                    new String[] { String.valueOf(engagementEvidenceData.getIndustyEngagementId()) });
            return "successfully saved Data";
        }
        catch (Exception exx)
        {
            System.out.print("Exception==" + exx);
            return "Error Occured while saving";
        }
    }

    public String UpdateEngagementEvidenceData(EngagementEvidenceData engagementEvidenceData)
    {
        ContentValues values = new ContentValues();
        values.put(QMSDatabaseHelper.COLUMN_TITLE, engagementEvidenceData.getTitle());
        values.put(QMSDatabaseHelper.COLUMN_STATUS, engagementEvidenceData.getStatus());
        values.put(QMSDatabaseHelper.COLUMN_USER_ID, engagementEvidenceData.getUserid());
        values.put(QMSDatabaseHelper.COLUMN_CURRENT_STEP, engagementEvidenceData.getCurrentStep());

        try {
            database.update(QMSDatabaseHelper.TABLE_ENGAGEMENT_EVIDENCE, values, QMSDatabaseHelper.COLUMN_ENG_EV_ID + " = ?",
                    new String[]{String.valueOf(engagementEvidenceData.getEngagementEvidenceId())});
            return "successfully saved Data";
        }
        catch (Exception exx)
        {
            System.out.print("Exception==" + exx);
            return "Error Occured while saving";
        }
    }


    public void InsertEngagementHours(int i, int totalHours, int totalHoursThisYear) {
        ContentValues values = new ContentValues();
        values.put(QMSDatabaseHelper.COLUMN_TOTAL_HOURS, totalHours);
        values.put(QMSDatabaseHelper.COLUMN_THIS_YEAR, totalHoursThisYear);

        try {
            database.insert(QMSDatabaseHelper.TABLE_ENGAGEMENT_HOURS, null, values);
        }
        catch (Exception exx)
        {
            System.out.print("Exception==" + exx);
        }
    }

    public void AddEngagementHours(int addHours)
    {
        String rawQuery = "UPDATE " + QMSDatabaseHelper.TABLE_ENGAGEMENT_HOURS +
                          " SET " + QMSDatabaseHelper.COLUMN_TOTAL_HOURS + "=" + QMSDatabaseHelper.COLUMN_TOTAL_HOURS+ "+"+ addHours + "," +QMSDatabaseHelper.COLUMN_THIS_YEAR + "=" + QMSDatabaseHelper.COLUMN_THIS_YEAR+ "+" + addHours +
                                  " WHERE " +  QMSDatabaseHelper.COLUMN_ID + " = 1";
//
//        ContentValues values = new ContentValues();
//        values.put(QMSDatabaseHelper.COLUMN_TOTAL_HOURS,QMSDatabaseHelper.COLUMN_TOTAL_HOURS + " + " + addHours );
//        values.put(QMSDatabaseHelper.COLUMN_THIS_YEAR, QMSDatabaseHelper.COLUMN_THIS_YEAR + " +" + addHours);
        System.out.println("RawQuery=="+ rawQuery);


        database.beginTransaction();
        try {
            database.execSQL(rawQuery);
            database.setTransactionSuccessful();
        }
        catch (SQLiteException exx)
        {
            System.out.print("Exception==" + exx);
        }
        finally {
            database.endTransaction();
        }
    }

    public void SubEngagementHours(int subHours)
    {
        String rawQuery = "UPDATE " + QMSDatabaseHelper.TABLE_ENGAGEMENT_HOURS +
                " SET " + QMSDatabaseHelper.COLUMN_TOTAL_HOURS + "=" + QMSDatabaseHelper.COLUMN_TOTAL_HOURS+ "-"+ subHours + "," +QMSDatabaseHelper.COLUMN_THIS_YEAR + "=" + QMSDatabaseHelper.COLUMN_THIS_YEAR+ "-" + subHours +
                " WHERE " +  QMSDatabaseHelper.COLUMN_ID + " = 1";
//
//        ContentValues values = new ContentValues();
//        values.put(QMSDatabaseHelper.COLUMN_TOTAL_HOURS,QMSDatabaseHelper.COLUMN_TOTAL_HOURS + " + " + addHours );
//        values.put(QMSDatabaseHelper.COLUMN_THIS_YEAR, QMSDatabaseHelper.COLUMN_THIS_YEAR + " +" + addHours);
        System.out.println("RawQuery==" + rawQuery);


        database.beginTransaction();
        try {
            database.execSQL(rawQuery);
            database.setTransactionSuccessful();
        }
        catch (SQLiteException exx)
        {
            System.out.print("Exception==" + exx);
        }
        finally {
            database.endTransaction();
        }
    }

    public String[] getHours() {
        String[] hours = new String[2];
        try {
            Cursor c=database.rawQuery("Select "+  QMSDatabaseHelper.COLUMN_TOTAL_HOURS+ " from " + QMSDatabaseHelper.TABLE_ENGAGEMENT_HOURS + " Where " + QMSDatabaseHelper.COLUMN_ID + "=1",null);
            while(c.moveToNext()){
                hours[0]=c.getString(0);
                hours[1] = c.getString(0);
                //  hours[1] = c.getString(c.getColumnIndex(QMSDatabaseHelper.COLUMN_THIS_YEAR));
            }
            c.close();
        }
        catch (Exception exx)
        {
            System.out.print("Exception==" + exx);
        }

        Log.d("HOURS", "garbage date===" + hours);
        return hours;
    }
}
