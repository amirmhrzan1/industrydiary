package com.weareforge.qms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.weareforge.qms.Objects.IndustryContactDetails;

import java.util.ArrayList;

/**
 * Created by Admin on 12/31/2015.
 */
public class IndustryContactsDb {

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    public IndustryContactsDb(Context context)
    {
        dbHelper = new QMSDatabaseHelper(context);
    }

    public void Open()
    {
        database = dbHelper.getWritableDatabase();
    }

    public String InsertIndustryContact(IndustryContactDetails industryContactDetails)
    {
        ContentValues values = new ContentValues();
        values.put(QMSDatabaseHelper.COLUMN_INDUSTRY_CONTACT_ID, industryContactDetails.getIndustry_Contact_Id());
        values.put(QMSDatabaseHelper.COLUMN_TITLE, industryContactDetails.getTitle());
        values.put(QMSDatabaseHelper.COLUMN_ORGANIZATION, industryContactDetails.getOrganization());
        values.put(QMSDatabaseHelper.COLUMN_STREET_ADDRESS, industryContactDetails.getStreet_Address());
        values.put(QMSDatabaseHelper.COLUMN_SUBURB, industryContactDetails.getSuburb());
        values.put(QMSDatabaseHelper.COLUMN_POSTAL_CODE, industryContactDetails.getPostal_Code());
        values.put(QMSDatabaseHelper.COLUMN_PHONE, industryContactDetails.getPhone());
        values.put(QMSDatabaseHelper.COLUMN_EMAIL, industryContactDetails.getEmail());
        values.put(QMSDatabaseHelper.COLUMN_QUALIFICATION, industryContactDetails.getQualification());
        values.put(QMSDatabaseHelper.COLUMN_COMMENT, industryContactDetails.getComment());
        values.put(QMSDatabaseHelper.COLUMN_OPPORTUNITIES, industryContactDetails.getOpportunities());
        values.put(QMSDatabaseHelper.COLUMN_ACTION_REQUIRED, industryContactDetails.getAction_Required());
        values.put(QMSDatabaseHelper.COLUMN_ACTIVITY_RECOMMENDATION, industryContactDetails.getActivity_Recommendation());
        values.put(QMSDatabaseHelper.COLUMN_USER_ID, industryContactDetails.getUser_Id());

        try {
            database.insert(QMSDatabaseHelper.TABLE_INDUSTRY_CONTACT,null,values);
            return "successfully saved Data";
        }
        catch (Exception exx)
        {
            return "Error Occured while saving";
        }
    }

    public ArrayList<IndustryContactDetails> GetAllIndustryContact()
    {
        ArrayList<IndustryContactDetails> industryContactDetailsArrayList = new ArrayList<IndustryContactDetails>();
        Cursor cur = database.query(QMSDatabaseHelper.TABLE_INDUSTRY_CONTACT,null ,null,null, null, null, null);
        if (cur.moveToFirst()) {
            do {
               IndustryContactDetails contacts = new IndustryContactDetails();
                contacts.setIndustry_Contact_Id(cur.getInt(0));
                contacts.setTitle(cur.getString(1));
                contacts.setOrganization(cur.getString(2));
                contacts.setStreet_Address(cur.getString(3));
                contacts.setSuburb(cur.getString(4));
                contacts.setPostal_Code(cur.getString(5));
                contacts.setPhone(cur.getString(6));
                contacts.setEmail(cur.getString(7));
                contacts.setQualification(cur.getString(8));
                contacts.setComment(cur.getString(9));
                contacts.setOpportunities(cur.getString(10));
                contacts.setAction_Required(cur.getString(11));
                contacts.setActivity_Recommendation(cur.getString(12));
                contacts.setUser_Id(cur.getString(13));
                //Adding Contacts into ArrayList
                industryContactDetailsArrayList.add(contacts);
            } while (cur.moveToNext());
        }
        return industryContactDetailsArrayList;
    }

    public void DeleteContact(int IndustryContactId)
    {
        database.delete(QMSDatabaseHelper.TABLE_INDUSTRY_CONTACT, QMSDatabaseHelper.COLUMN_INDUSTRY_CONTACT_ID + "=" + IndustryContactId, null);
    }

    public String UpdateContact(IndustryContactDetails industryContactDetails) {
        ContentValues values = new ContentValues();
        //  values.put(QMSDatabaseHelper.COLUMN_INDUSTRY_CONTACT_ID, industryContactDetails.getIndustry_Contact_Id());
        values.put(QMSDatabaseHelper.COLUMN_TITLE, industryContactDetails.getTitle());
        values.put(QMSDatabaseHelper.COLUMN_ORGANIZATION, industryContactDetails.getOrganization());
        values.put(QMSDatabaseHelper.COLUMN_STREET_ADDRESS, industryContactDetails.getStreet_Address());
        values.put(QMSDatabaseHelper.COLUMN_SUBURB, industryContactDetails.getSuburb());
        values.put(QMSDatabaseHelper.COLUMN_POSTAL_CODE, industryContactDetails.getPostal_Code());
        values.put(QMSDatabaseHelper.COLUMN_PHONE, industryContactDetails.getPhone());
        values.put(QMSDatabaseHelper.COLUMN_EMAIL, industryContactDetails.getEmail());
        values.put(QMSDatabaseHelper.COLUMN_QUALIFICATION, industryContactDetails.getQualification());
        values.put(QMSDatabaseHelper.COLUMN_COMMENT, industryContactDetails.getComment());
        values.put(QMSDatabaseHelper.COLUMN_OPPORTUNITIES, industryContactDetails.getOpportunities());
        values.put(QMSDatabaseHelper.COLUMN_ACTION_REQUIRED, industryContactDetails.getAction_Required());
        values.put(QMSDatabaseHelper.COLUMN_ACTIVITY_RECOMMENDATION, industryContactDetails.getActivity_Recommendation());
        values.put(QMSDatabaseHelper.COLUMN_USER_ID, industryContactDetails.getUser_Id());
        try {
            database.update(QMSDatabaseHelper.TABLE_INDUSTRY_CONTACT, values, QMSDatabaseHelper.COLUMN_INDUSTRY_CONTACT_ID + " = ?", new String[] { String.valueOf(industryContactDetails.getIndustry_Contact_Id()) });
            return "Succcessfully Updated";
        }
        catch (Exception exx)
        {
            Log.e("MYAPPDB", "exception", exx);
            return "Exception Occured";
        }
    }

}
