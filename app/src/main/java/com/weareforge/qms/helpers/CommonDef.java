package com.weareforge.qms.helpers;

/**
 * Created by Robz Stha on 12/26/2014.
 */
public final class CommonDef {
    public static final int REQUEST_LOGIN = 1;
    public static final int REQUEST_INDUSTRY_CONTACTS=2;
    public static final int REQUEST_INDUSTRY_CONTACT_LIST = 3 ;
    public static final int REQUEST_INDUSTRY_CONTACT_EDIT = 4;
    public static final int REQUEST_INDUSTRY_CONTACT_ADD = 5;
    public static final int REQUEST_INDUSTRY_CONTACT_DELETE =6 ;
    public static final int REQUEST_MY_DETAILS = 7;
    public static final int PICTURE_REQUEST_CODE = 10;
    public static final int REQUEST_INDUSTRY_ENGAGEMENT_ADD = 11;

    public static final String COGNITO_POOL_ID = "us-east-1:9038aac5-e65d-4c2f-95f6-d63f43795506";

    public static final String BUCKET_NAME = "diary.qmsveis.info";
    public static final int REQUEST_SAVE_PROGRESS_STEP_1 = 12;
    public static final int REQUEST_INDUSTRY_ACTIVITY_FEEDBACK_UPSERT = 13;
    public static final int REQUEST_LIST_ACTIVITY = 14;
    public static final int REQUEST_LIST_PURPOSE = 15;
    public static final int REQUEST_LIST_ASAQ = 16;
    public static final int REQUEST_ENGAGEMENT_CONTACT_LIST = 17;
    public static final int REQUEST_ENGAGEMENT_EVIDENCE_LIST = 18;
    public static final int REQUEST_LIST_INDUSTRY_ENGAGEMENT = 19;
    public static final int REQUEST_INDUSTRY_ACTIVITY_FEEDBACK_LIST = 20;
    public static final int REQUEST_EVIDENCE_UPSERT = 21;
    public static final int REQUEST_EVIDENCE_LIST = 22;
    public static final int REQUEST_LIST_FINDOUT = 23;
    public static final int REQUEST_ADD_EXISTING_CONTACTS = 24;
    public static final int REQUEST_DELETE_ENGAGEMENT_EVIDENCE = 25;
    public static final int REQUEST_LIST_COMPETENCIES = 26;
    public static final int REQUEST_EVIDENCE_DATA = 27;
    public static final int REQUEST_UPSERT_STANDARD_COMPETENCIES = 28;
    public static final int REQUEST_LIST_ACCRED = 29;
    public static final int REQUEST_LIST_STANDARD_COMPETENCIES = 30;
    public static final int REQUEST_SAVE_PROGRESS_STEP_5 = 31;
    public static final int REQUEST_SAVE_PROGRESS_STEP_2 = 32;
    public static final int REQUEST_SAVE_PROGRESS_STEP_3 = 33;
    public static final int REQUEST_SAVE_PROGRESS_STEP_4 = 34;
    public static final int REQUEST_GET_UPDATED = 35;


//    public static final String DIRECTORY = Environment.getExternalStorageDirectory()+"/"+"SMP/";


    public static String TAG = "Rabin is testing ";
    public final static int SOCKET_TIME_OUT = 12000;


    public static class Dirs{
        public static String SIGNATURE = "signature";
        public static String FONTS = "fonts";
    }


    public static class SharedPrefKeys {

        public static String IS_LOGGED_IN = "isLoggedIn";
        public static String USER_ID = "userId";
        public static String FIRST_NAME = "firstName";
        public static String LAST_NAME = "lastName";
        public static String AIRLINES_NAME = "airlines_name";
        public static String USER_EMAIL = "userEmail";  // this is the original username
        public static String CONTACT_NO = "contactNo";
        public static String ADDRESS = "address";
        public static String HASH_CODE = "hashCode";
        public static String DEVICE_ID = "deviceId";

        public static String AUTH_KEY = "auth_key";
    }

}
