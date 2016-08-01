package com.weareforge.qms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.weareforge.qms.Objects.EngagementEvidenceData;
import com.weareforge.qms.Objects.OfflineData;
import com.weareforge.qms.Objects.updatedData;

import java.util.ArrayList;

/**
 * Created by prajit on 3/10/16.
 */
public class EngagementEvidenceOfflineContent {

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    public EngagementEvidenceOfflineContent(Context context) {
        dbHelper = new QMSDatabaseHelper(context);
    }

    public void Open() {
        database = dbHelper.getWritableDatabase();
    }

    public void insertPurposeActivityData(OfflineData data) {
        {
            ContentValues values = new ContentValues();
            values.put(QMSDatabaseHelper.COLUMN_PURPOSE_OF_ACTIVITY_ID, data.getId());
            values.put(QMSDatabaseHelper.COLUMN_TITLE, data.getTitle());
            values.put(QMSDatabaseHelper.COLUMN_ISACTIVE, data.getIsActive());
            values.put(QMSDatabaseHelper.COLUMN_ISDELETED, data.getIsDeleted());
            values.put(QMSDatabaseHelper.COLUMN_HELP_TEXT, data.getHelpString());

            try {
                database.insert(QMSDatabaseHelper.TABLE_PURPOSE_OF_ACTIVITY, null, values);
                //  return "successfully saved Data";
            } catch (Exception exx) {
                System.out.print("Exception==" + exx);
                //   return "Error Occured while saving";
            }
        }
    }

    public ArrayList<OfflineData> GetAllPurposeAOfActivity() {
        ArrayList<OfflineData> purposeOfActivitydata = new ArrayList<OfflineData>();

        Cursor cur = database.rawQuery("select * from " + QMSDatabaseHelper.TABLE_PURPOSE_OF_ACTIVITY, null);

        if (cur.moveToFirst()) {
            do {
                OfflineData data = new OfflineData();
                data.setId(cur.getInt(0));
                data.setTitle(cur.getString(1));
                data.setIsActive(String.valueOf(cur.getInt(2)));
                data.setIsDeleted(String.valueOf(cur.getInt(3)));
                data.setHelpString(String.valueOf(cur.getString(4)));

                //Adding Contacts into ArrayList
                purposeOfActivitydata.add(data);
            } while (cur.moveToNext());
        }
        return purposeOfActivitydata;
    }


    public void insertActivityData(OfflineData data) {
        ContentValues values = new ContentValues();
        values.put(QMSDatabaseHelper.COLUMN_ACTIVITY_ID, data.getId());
        values.put(QMSDatabaseHelper.COLUMN_TITLE, data.getTitle());
        values.put(QMSDatabaseHelper.COLUMN_ISACTIVE, data.getIsActive());
        values.put(QMSDatabaseHelper.COLUMN_ISDELETED, data.getIsDeleted());
        values.put(QMSDatabaseHelper.COLUMN_HELP_TEXT, data.getHelpString());

        try {
            database.insert(QMSDatabaseHelper.TABLE_ACTIVITY, null, values);
            //  return "successfully saved Data";
        } catch (Exception exx) {
            System.out.print("Exception==" + exx);
            //   return "Error Occured while saving";
        }
    }

    public ArrayList<OfflineData> GetAllActivity() {
        ArrayList<OfflineData> Activitydata = new ArrayList<OfflineData>();

        Cursor cur = database.rawQuery("select * from " + QMSDatabaseHelper.TABLE_ACTIVITY, null);

        if (cur.moveToFirst()) {
            do {
                OfflineData data = new OfflineData();
                data.setId(cur.getInt(0));
                data.setTitle(cur.getString(1));
                data.setIsActive(String.valueOf(cur.getInt(2)));
                data.setIsDeleted(String.valueOf(cur.getInt(3)));
                data.setHelpString(String.valueOf(cur.getString(4)));

                //Adding Contacts into ArrayList
                Activitydata.add(data);
            } while (cur.moveToNext());
        }
        return Activitydata;
    }


    public void insertFindOutData(OfflineData data) {
        ContentValues values = new ContentValues();
        values.put(QMSDatabaseHelper.COLUMN_FINDOUT_ID, data.getId());
        values.put(QMSDatabaseHelper.COLUMN_TITLE, data.getTitle());
        values.put(QMSDatabaseHelper.COLUMN_ISACTIVE, data.getIsActive());
        values.put(QMSDatabaseHelper.COLUMN_ISDELETED, data.getIsDeleted());

        try {
            database.insert(QMSDatabaseHelper.TABLE_FIND_OUT, null, values);
            //  return "successfully saved Data";
        } catch (Exception exx) {
            System.out.print("Exception==" + exx);
            //   return "Error Occured while saving";
        }
    }

    public ArrayList<OfflineData> GetFindOutData() {
        ArrayList<OfflineData> FindOutdata = new ArrayList<OfflineData>();

        Cursor cur = database.rawQuery("select * from " + QMSDatabaseHelper.TABLE_FIND_OUT, null);

        if (cur.moveToFirst()) {
            do {
                OfflineData data = new OfflineData();
                data.setId(cur.getInt(0));
                data.setTitle(cur.getString(1));
                data.setIsActive(String.valueOf(cur.getInt(2)));
                data.setIsDeleted(String.valueOf(cur.getInt(3)));

                //Adding Contacts into ArrayList
                FindOutdata.add(data);
            } while (cur.moveToNext());
        }
        return FindOutdata;
    }

    public void insertASAQStandard(OfflineData data) {
        ContentValues values = new ContentValues();
        values.put(QMSDatabaseHelper.COLUMN_ASAQ_Standard_Id, data.getId());
        values.put(QMSDatabaseHelper.COLUMN_TITLE, data.getTitle());
        values.put(QMSDatabaseHelper.COLUMN_ISACTIVE, data.getIsActive());
        values.put(QMSDatabaseHelper.COLUMN_ISDELETED, data.getIsDeleted());
        values.put(QMSDatabaseHelper.COLUMN_CATEGORY, data.getCategory());

        try {
            database.insert(QMSDatabaseHelper.TABLE_ASAQ_Standard, null, values);
            //  return "successfully saved Data";
        } catch (Exception exx) {
            System.out.print("Exception==" + exx);
            //   return "Error Occured while saving";
        }
    }

    public ArrayList<OfflineData> GetASAQStandard() {
        ArrayList<OfflineData> Activitydata = new ArrayList<OfflineData>();

        Cursor cur = database.rawQuery("select * from " + QMSDatabaseHelper.TABLE_ASAQ_Standard, null);

        if (cur.moveToFirst()) {
            do {
                OfflineData data = new OfflineData();
                data.setId(cur.getInt(0));
                data.setTitle(cur.getString(1));
                data.setIsActive(String.valueOf(cur.getInt(2)));
                data.setIsDeleted(String.valueOf(cur.getInt(3)));
                data.setCategory(cur.getString(4));
                //Adding Contacts into ArrayList
                Activitydata.add(data);
            } while (cur.moveToNext());
        }
        return Activitydata;
    }

    public void insertDataUpdate(updatedData data) {
        ContentValues values = new ContentValues();

        values.put(QMSDatabaseHelper.COLUMN_ACTIVITY, data.getActivity());
        values.put(QMSDatabaseHelper.COLUMN_FIND_OUT, data.getFindOut());
        values.put(QMSDatabaseHelper.COLUMN_PURPOSE_OF_ACTIVITY, data.getPurposeOfActivity());
        values.put(QMSDatabaseHelper.COLUMN_ASAQ_STANDARD, data.getAsaqStandard());

        try {
            database.insert(QMSDatabaseHelper.TABLE_UPDATE_DATA, null, values);
            //  return "successfully saved Data";
        } catch (Exception exx) {
            System.out.print("Exception==" + exx);
            //   return "Error Occured while saving";
        }
    }

    public updatedData GetUpdateData() {
        updatedData data = new updatedData();
        Cursor cur = database.rawQuery("select * from " + QMSDatabaseHelper.TABLE_UPDATE_DATA, null);
        if (cur.moveToFirst()) {
            do {
                data.setActivity(cur.getInt(0));
                data.setAsaqStandard(cur.getInt(1));
                data.setFindOut(cur.getInt(2));
                data.setPurposeOfActivity(cur.getInt(3));
                //Adding Contacts into ArrayList
            } while (cur.moveToNext());
        }
        return data;
    }

    public void deleteActivity()
    {
        database.delete(QMSDatabaseHelper.TABLE_ACTIVITY, null, null);
    }

    public void deleteFindOut()
    {
        database.delete(QMSDatabaseHelper.TABLE_FIND_OUT, null, null);
    }

    public void deletePurposeActivity()
    {
        database.delete(QMSDatabaseHelper.TABLE_PURPOSE_OF_ACTIVITY, null, null);
    }

    public void deleteAsaqStandard()
    {
        database.delete(QMSDatabaseHelper.TABLE_ASAQ_Standard, null, null);
    }

    public void deleteUpdatedTimeStamp()
    {
        database.delete(QMSDatabaseHelper.TABLE_UPDATE_DATA, null, null);
    }
}
