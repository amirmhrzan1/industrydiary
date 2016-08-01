package com.weareforge.qms.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Prajeet on 12/25/2015.
 */
public class QMSDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOGTAG = "QMSInfo";

    private static final String DATABASE_NAME = "QMS.db";
    private static final int DATABASE_VERSION = 1;

    //Table Engagement Evidence
    public static final String TABLE_ENGAGEMENT_EVIDENCE = "Engagement_evidence";

    public static final String COLUMN_ENG_EV_ID = "Engagement_Evidence_Id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_STATUS = "Status";
    public static final String COLUMN_USER_ID = "User_Id";
    public static final String COLUMN_CURRENT_STEP = "Current_step";

    private static final String CREATE_TABLE_ENGAGEMENT_EVIDENCE = "CREATE TABLE "
            + TABLE_ENGAGEMENT_EVIDENCE + "(" + COLUMN_ENG_EV_ID + " BIGINT PRIMARY KEY, "
            + COLUMN_TITLE + " VARCHAR(50), "
            + COLUMN_STATUS + " BIT, "
            + COLUMN_USER_ID + " BIGINT, "
            + COLUMN_CURRENT_STEP + " TINYINT" + ");";

    //Table Industry_Engagement
    public static final String TABLE_INDUSTRY_ENGAGEMENT = "Industry_Engagement";

    public static final String COLUMN_IND_ENG_ID = "Industry_Engagement_Id";
    public static final String COLUMN_REFERENCE = "Reference";
    public static final String COLUMN_DATE_TIME = "Datetime";
    public static final String COLUMN_HOURS = "Hours";
    public static final String COLUMN_VENUE = "Venue";
    public static final String COLUMN_FINDOUT_ID = "Findout_id";

    private static final String CREATE_TABLE_INDUSTRY_ENGAGEMENT = "CREATE TABLE "
            + TABLE_INDUSTRY_ENGAGEMENT + "( " + COLUMN_IND_ENG_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_ENG_EV_ID + " INTEGER, "
            + COLUMN_REFERENCE + " VARCHAR(50), "
            + COLUMN_DATE_TIME + " datetime, "
            + COLUMN_HOURS + " float, "
            + COLUMN_VENUE + " VARCHAR(50), "
            + COLUMN_FINDOUT_ID + " INT, " +
            " FOREIGN KEY (" + COLUMN_ENG_EV_ID + ") REFERENCES " + TABLE_INDUSTRY_ENGAGEMENT + " ( " + COLUMN_ENG_EV_ID + ")" +" ON DELETE CASCADE);";

    //TABLE HOURS
    public static final String TABLE_ENGAGEMENT_HOURS = "Engagement_Hours";
    public static final String COLUMN_TOTAL_HOURS = "Total_Hours";
    public static final String COLUMN_THIS_YEAR = "This_Year";
    public static final String COLUMN_ID = "ID";

    private static final String CREATE_TABLE_HOURS = "CREATE TABLE "
            + TABLE_ENGAGEMENT_HOURS + "( " + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TOTAL_HOURS + " FLOAT, "
            + COLUMN_THIS_YEAR + " FLOAT );";


    //TABLE FIND OUT
    public static final String TABLE_FIND_OUT = "Find_Out";

    public static final String COLUMN_ISACTIVE = "Is_Active";
    public static final String COLUMN_ISDELETED = "Is_Deleted";

    private static final String CREATE_TABLE_FIND_OUT = "CREATE TABLE "
            + TABLE_FIND_OUT + "(" + COLUMN_FINDOUT_ID + " BIGINT , "
            + COLUMN_TITLE + " VARCHAR(50), "
            + COLUMN_ISACTIVE + " TINYINT, "
            + COLUMN_ISDELETED + " TINYINT "
            + ");";


    //TABLE ACTIVITY
    public static final String TABLE_ACTIVITY = "Activity";
    public static final String COLUMN_ACTIVITY_ID = "Activity_Id";
    public static final String COLUMN_HELP_TEXT = "Help_text";

    private static final String CREATE_TABLE_ACTIVITY = "CREATE TABLE "
            + TABLE_ACTIVITY + "(" + COLUMN_ACTIVITY_ID + " BIGINT , "
            + COLUMN_TITLE + " VARCHAR(50), "
            + COLUMN_ISACTIVE + " TINYINT, "
            + COLUMN_ISDELETED + " TINYINT, "
            + COLUMN_HELP_TEXT + " TEXT "
            + ");";

    // TABLE PURPOSE OF ACTIVITY

    public static final String TABLE_PURPOSE_OF_ACTIVITY = "Purpose_Of_Activity";
    public static final String COLUMN_PURPOSE_OF_ACTIVITY_ID = "Purpose_Activity_Id";

    private static final String CREATE_TABLE_PURPOSE_OF_ACTIVITY = "CREATE TABLE "
            + TABLE_PURPOSE_OF_ACTIVITY + "(" + COLUMN_PURPOSE_OF_ACTIVITY_ID + " BIGINT, "
            + COLUMN_TITLE + " VARCHAR(50), "
            + COLUMN_ISACTIVE + " TINYINT, "
            + COLUMN_ISDELETED + " TINYINT, "
            + COLUMN_HELP_TEXT + " TEXT "
            + ");";


    //TABLE ASAQ Standard
    public static final String TABLE_ASAQ_Standard = "ASAQ_Standard";
    public static final String COLUMN_ASAQ_Standard_Id = "ASAQ_Standard_Id";
    public static final String COLUMN_CATEGORY = "Category";

    private static final String CREATE_TABLE_ASAQ_STANDARD = "CREATE TABLE "
            + TABLE_ASAQ_Standard + "(" + COLUMN_ASAQ_Standard_Id + " BIGINT, "
            + COLUMN_TITLE + " VARCHAR(50), "
            + COLUMN_ISACTIVE + " TINYINT, "
            + COLUMN_ISDELETED + " TINYINT, "
            + COLUMN_CATEGORY + " VARCHAR(50) "
            + ");";


    //TABLE UPDATED DATA
    public static final String TABLE_UPDATE_DATA = "Data_Update";
    public static final String COLUMN_ACTIVITY = "Activity";
    public static final String COLUMN_FIND_OUT = "Find_Out";
    public static final String COLUMN_PURPOSE_OF_ACTIVITY = "Purpose_Of_Activity";
    public static final String COLUMN_ASAQ_STANDARD = "Asaq_Standard";

    private static final String CREATE_TABLE_DATA_UPDATE = "CREATE TABLE "
            + TABLE_UPDATE_DATA + "("
            + COLUMN_ACTIVITY + " BIGINT, "
            + COLUMN_FIND_OUT + " BIGINT, "
            + COLUMN_PURPOSE_OF_ACTIVITY + " BIGINT, "
            + COLUMN_ASAQ_STANDARD + " BIGINT "
            + ");";

    //TABLE INDUSTRY CONTACT
    public static final String TABLE_INDUSTRY_CONTACT = "Industry_Contact";
    public static final String COLUMN_INDUSTRY_CONTACT_ID = "Industry_Contact_Id";
    public static final String COLUMN_ORGANIZATION = "Organization";
    public static final String COLUMN_STREET_ADDRESS ="Street_Address";
    public static final String COLUMN_SUBURB="Suburb";
    public static final String COLUMN_POSTAL_CODE="Postal_Code";
    public static final String COLUMN_PHONE = "Phone";
    public static final String COLUMN_EMAIL="Email";
    public static final String COLUMN_QUALIFICATION="Qualification";
    public static final String COLUMN_COMMENT="Comment";
    public static final String COLUMN_OPPORTUNITIES = "Opportunities";
    public static final String COLUMN_ACTION_REQUIRED="Action_Required";
    public static final String COLUMN_ACTIVITY_RECOMMENDATION = "Activity_Recommendation";

    private static final String CREATE_TABLE_INDUSTRY_CONTACT= "CREATE TABLE "
            + TABLE_INDUSTRY_CONTACT + "(" + COLUMN_INDUSTRY_CONTACT_ID + " BIGINT, "
            + COLUMN_TITLE + " VARCHAR(50), "
            + COLUMN_ORGANIZATION + " VARCHAR(50), "
            + COLUMN_STREET_ADDRESS + " VARCHAR(50), "
            + COLUMN_SUBURB + " VARCHAR(50), "
            + COLUMN_POSTAL_CODE + " INT, "
            + COLUMN_PHONE + " VARCHAR(15), "
            + COLUMN_EMAIL + " VARCHAR(50), "
            + COLUMN_QUALIFICATION + " TEXT, "
            + COLUMN_COMMENT + " TEXT, "
            + COLUMN_OPPORTUNITIES + " TEXT, "
            + COLUMN_ACTION_REQUIRED + " TEXT, "
            + COLUMN_ACTIVITY_RECOMMENDATION + " TEXT, "
            + COLUMN_USER_ID + " BIGINT " + ");";


    public QMSDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_ENGAGEMENT_EVIDENCE);
        db.execSQL(CREATE_TABLE_INDUSTRY_ENGAGEMENT);
        db.execSQL(CREATE_TABLE_FIND_OUT);
        db.execSQL(CREATE_TABLE_INDUSTRY_CONTACT);
        db.execSQL(CREATE_TABLE_HOURS);
        db.execSQL(CREATE_TABLE_ACTIVITY);
        db.execSQL(CREATE_TABLE_PURPOSE_OF_ACTIVITY);
        db.execSQL(CREATE_TABLE_ASAQ_STANDARD);
        db.execSQL(CREATE_TABLE_DATA_UPDATE);
        System.out.println("query==" + CREATE_TABLE_HOURS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENGAGEMENT_EVIDENCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INDUSTRY_ENGAGEMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIND_OUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INDUSTRY_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENGAGEMENT_HOURS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PURPOSE_OF_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASAQ_Standard);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPDATE_DATA);

        //Create tables again
        onCreate(db);
    }

    public void dropAllTable(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENGAGEMENT_EVIDENCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INDUSTRY_ENGAGEMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIND_OUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INDUSTRY_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENGAGEMENT_HOURS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PURPOSE_OF_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PURPOSE_OF_ACTIVITY);
       db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASAQ_Standard);
       db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPDATE_DATA);

        onCreate(db);
    }
}
