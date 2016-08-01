package com.weareforge.qms.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.weareforge.qms.Objects.EngagementEvidenceData;
import com.weareforge.qms.Objects.IndustryContactDetails;
import com.weareforge.qms.Objects.OfflineData;
import com.weareforge.qms.Objects.TeacherDetails;
import com.weareforge.qms.Objects.updatedData;
import com.weareforge.qms.R;
import com.weareforge.qms.alerts.Alerts;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.db.EngagementEvidenceDbHandler;
import com.weareforge.qms.db.EngagementEvidenceOfflineContent;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.CommonMethods;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.interfaces.AsyncInterface;
import com.weareforge.qms.utils.ConnectionMngr;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.Opener;
import com.weareforge.qms.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MinLength;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

/**
 * Created by Prajeet on 12/21/2015.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,AsyncInterface {

    private Button login;
    private Opener opener;
    private ConnectionMngr connectionMngr;

    private HashMap<String, String> findOutParams;
    private HashMap<String, String> activityParams;
    private HashMap<String, String> purposeParams;
    private HashMap<String, String> ASAQParams;
    private HashMap<String, String> updateParams;
    private HashMap<String, String> params;

    private VolleyRequest vsr;
    private VolleyRequest vsrActivity;
    private VolleyRequest vsrPurpose;
    private VolleyRequest vsrFindOut;
    private VolleyRequest vsrASAQ;
    private VolleyRequest vsrUpdated;

    private UrlHelper urlHelper;
    private Alerts alerts;
    private FontHelper fontHelper;

    private  JSONObject jObjResult;


    private IndustryContactDetails industryContactDetails;
    private ArrayList<IndustryContactDetails> industryContactDetailsArrayList = new ArrayList<IndustryContactDetails>();

    private SharedPreference sharedPreference;  //shared preference
    private EngagementEvidenceDbHandler dbHelper;
    private EngagementEvidenceOfflineContent offlinedbHandler;

    private String token;
    private String userid;

    private TextView tvLoginInfo;

    ArrayList<TeacherDetails> teacher_details;

    @NotEmpty(messageId = R.string.msg_username_required, order = 1)
    private EditText edt_userName;

    @NotEmpty(messageId = R.string.msg_pin_required, order = 2)
    @MinLength(value = 4, messageId = R.string.msg_valid_pin, order = 3)
    @RegExp(value = "^[0-9]+$", messageId = R.string.msg_valid_pin)
    private EditText edt_pin;

    @NotEmpty(messageId = R.string.msg_pin_required, order = 4)
    @MinLength(value = 4, messageId = R.string.msg_valid_pin, order = 5)
    @RegExp(value = "^[0-9]+$", messageId = R.string.msg_valid_pin)
    private EditText edt_identifier;

    private Button btn_login;

    private ArrayList<EngagementEvidenceData> engagementEvidenceList;
    private EngagementEvidenceData engagementEvidenceData;

    private ArrayList<OfflineData> offlineActivityDatas;
    private ArrayList<OfflineData> offlinePurposeActivityDatas;
    private ArrayList<OfflineData> offlineFindOutDatas;
    private ArrayList<updatedData> updatedDataList;

    private OfflineData datas;
    private updatedData updatedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();  //Removing action bar
        setContentView(R.layout.activity_login);
        CommonMethods.setupUI(findViewById(R.id.linearLayoutLogin), this);

        //Full screen Window
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        opener = new Opener(this);
        connectionMngr = new ConnectionMngr(this);
        alerts = new Alerts(this);
        sharedPreference = new SharedPreference(this);

        fontHelper = new FontHelper(this);
        offlinedbHandler = new EngagementEvidenceOfflineContent(this);

        //Initialize
        this.edt_userName = (EditText) findViewById(R.id.edt_userName);
        this.edt_pin = (EditText) findViewById(R.id.edt_Pin);
        this.edt_identifier = (EditText) findViewById(R.id.edt_Identifier);
        this.btn_login = (Button) findViewById(R.id.btn_login);
        this.tvLoginInfo = (TextView) findViewById(R.id.loginInfo);

        //set Fonts
        this.edt_userName.setTypeface(fontHelper.getDefaultFont("regular"));
        this.edt_pin.setTypeface(fontHelper.getDefaultFont("regular"));
        this.edt_identifier.setTypeface(fontHelper.getDefaultFont("regular"));
        this.btn_login.setTypeface(fontHelper.getDefaultFont("regular"));
        this.tvLoginInfo.setTypeface(fontHelper.getDefaultFont("regular"));


        //ClickListners
        this.btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(this, true));
        if (isValid) {
            if (connectionMngr.hasConnection()) {
                params = new HashMap<>();
                params.put("username", edt_userName.getText().toString());
                params.put("pin", edt_pin.getText().toString());
                params.put("identifier", edt_identifier.getText().toString());
                vsr = new VolleyRequest(this, Request.Method.POST, params, urlHelper.LOGIN, false, CommonDef.REQUEST_LOGIN,getResources().getString(R.string.login));
                vsr.asyncInterface = this;
                 vsr.request();
            }
        }
    }

    @Override
    public void processFinish(String result, int requestCode) {

        switch (requestCode) {
            case CommonDef.REQUEST_LOGIN:
                loginInfo(result);
                break;
            case CommonDef.REQUEST_ENGAGEMENT_EVIDENCE_LIST:
                loadExistingEvidence(result);
                break;
            case CommonDef.REQUEST_LIST_ACTIVITY:
                listActivity(result);
                break;

            case CommonDef.REQUEST_LIST_PURPOSE:
                listPurpose(result);
                break;

            case CommonDef.REQUEST_LIST_FINDOUT:
                listFindOut(result);
            break;

            case CommonDef.REQUEST_LIST_ASAQ:
                listASAQ(result);
                break;
            case CommonDef.REQUEST_GET_UPDATED:
                listUpdate(result);
                break;


           /* case CommonDef.REQUEST_INDUSTRY_CONTACT_LIST:
                loadIndustryContactList(result);*/
        }
    }

    private void listUpdate(String result) {
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            offlineFindOutDatas = new ArrayList<>();
            offlinedbHandler.Open();
            if (response_int == 0) {
                updatedData = new updatedData();

                updatedData.setActivity(jObjResult.getInt("activity"));
                updatedData.setFindOut(jObjResult.getInt("find_out"));
                updatedData.setPurposeOfActivity(jObjResult.getInt("purpose_of_activity"));
                updatedData.setAsaqStandard(jObjResult.getInt("asqa_standard"));

                offlinedbHandler.insertDataUpdate(updatedData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void listASAQ(String result) {
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            offlineFindOutDatas = new ArrayList<>();
            offlinedbHandler.Open();
            if (response_int == 0) {
                JSONArray listArrayActivity = jObjResult.getJSONArray("asqaStandards");
                for (int i = 0; i < listArrayActivity.length(); i++) {
                    datas = new OfflineData();
                    final JSONObject listActivity = listArrayActivity.getJSONObject(i);
                    datas.setId(Integer.parseInt(listActivity.getString("ASQA_Standard_Id")));
                    datas.setTitle(listActivity.getString("Title"));
                    datas.setIsActive(listActivity.getString("Is_Active"));
                    datas.setCategory(listActivity.getString("category"));
                    try{
                        datas.setIsDeleted(listActivity.getString("Is_Deleted"));
                    }catch (Exception exx)
                    {

                    }

                    offlineFindOutDatas.add(datas);
                    offlinedbHandler.insertASAQStandard(datas);
//                    loadDataBackground();
                    opener.Home();

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void listFindOut(String result) {
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            offlineFindOutDatas = new ArrayList<>();
            offlinedbHandler.Open();
            if (response_int == 0) {
                JSONArray listArrayActivity = jObjResult.getJSONArray("findoutData");
                for (int i = 0; i < listArrayActivity.length(); i++) {
                    datas = new OfflineData();
                    final JSONObject listActivity = listArrayActivity.getJSONObject(i);
                    datas.setId(Integer.parseInt(listActivity.getString("Findout_Id")));
                    datas.setTitle(listActivity.getString("Title"));
                    datas.setIsActive(listActivity.getString("Is_Active"));
                    try{
                        datas.setIsDeleted(listActivity.getString("Is_Deleted"));
                    }catch (Exception exx)
                    {

                    }

                    offlineFindOutDatas.add(datas);
                    offlinedbHandler.insertFindOutData(datas);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void listPurpose(String result) {
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            offlinedbHandler.Open();
            offlinePurposeActivityDatas = new ArrayList<>();
            if (response_int == 0) {
                JSONArray listArrayActivity = jObjResult.getJSONArray("activityPurposeData");
                for (int i = 0; i < listArrayActivity.length(); i++) {
                    datas = new OfflineData();
                    final JSONObject listActivity = listArrayActivity.getJSONObject(i);
                    datas.setId(Integer.parseInt(listActivity.getString("Purpose_Activity_Id")));
                    datas.setTitle(listActivity.getString("Purpose_Activity_Title"));
                    datas.setIsActive(listActivity.getString("Is_Active"));
                    datas.setIsDeleted(listActivity.getString("Is_Deleted"));
                    datas.setHelpString(listActivity.getString("help_text"));

                    offlinePurposeActivityDatas.add(datas);
                    offlinedbHandler.insertPurposeActivityData(datas);
                }
            }

    } catch (JSONException e) {
        e.printStackTrace();
    }
    }

    private void listActivity(String result) {
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            offlinedbHandler.Open();
            offlineActivityDatas = new ArrayList<>();
            if (response_int == 0) {
                JSONArray listArrayActivity = jObjResult.getJSONArray("activityData");
                for (int i = 0; i < listArrayActivity.length(); i++) {
                    datas = new OfflineData();
                    final JSONObject listActivity = listArrayActivity.getJSONObject(i);
                    datas.setId(Integer.parseInt(listActivity.getString("Activity_Id")));
                    datas.setTitle(listActivity.getString("Title"));
                    datas.setIsActive(listActivity.getString("Is_Active"));
                    datas.setIsDeleted(listActivity.getString("Is_Deleted"));
                    datas.setHelpString(listActivity.getString("help_text"));

                    offlineActivityDatas.add(datas);
                    offlinedbHandler.insertActivityData(datas);
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        opener.exit();
    }

   /* private void LoadAllData() {
    params = new HashMap<>();
    params.put("token", token);
    params.put("userid", userid);

    vsr = new VolleyRequest(this, Request.Method.POST, params, urlHelper.INDUSTRY_CONTACT, false, CommonDef.REQUEST_INDUSTRY_CONTACTS);
    vsr.asyncInterface = this;
    vsr.request();
    }*/

    /*private void LoadIndustryContactList(String token, String userid, String industry_contact_id) {
        params = new HashMap();
        params.put("token", token);
        params.put("userid", userid);
        params.put("industry_contact_id", industry_contact_id);
        vsr = new VolleyRequest(this, Request.Method.POST,params,urlHelper.INDUSTRY_CONTACT_LIST,false,CommonDef.REQUEST_INDUSTRY_CONTACT_LIST);
        vsr.asyncInterface = this;
        vsr.request();

    }*/

    private void loadExistingIndustryAndData() {
        if (connectionMngr.hasConnection()) {
            params = new HashMap<String, String>();

            params.put("token", token);
            params.put("userid", userid);

//            activityParams = new HashMap<String, String>();
//            purposeParams = new HashMap<String, String>();
//            findOutParams = new HashMap<>();
//            ASAQParams = new HashMap<String, String>();
//            updateParams = new HashMap<String, String>();

//            activityParams.put("token", token);
//            activityParams.put("userid", userid);
//
//            purposeParams.put("token", token);
//            purposeParams.put("userid", userid);
//
//            findOutParams.put("token", token);
//            findOutParams.put("userid", userid);
//
//            ASAQParams.put("token", token);
//            ASAQParams.put("userid", userid);
//
//            updateParams.put("token", token);
//            updateParams.put("userid", userid);

            vsrActivity = new VolleyRequest(this, Request.Method.POST, params, urlHelper.LIST_ACTIVITY, false, CommonDef.REQUEST_LIST_ACTIVITY,getResources().getString(R.string.loadingFromQMS));
            vsrPurpose = new VolleyRequest(this, Request.Method.POST, params, urlHelper.LIST_PURPOSE, false, CommonDef.REQUEST_LIST_PURPOSE,getResources().getString(R.string.loadingFromQMS));
            vsrFindOut = new VolleyRequest(this, Request.Method.POST, params, urlHelper.LIST_FIND_OUT, false, CommonDef.REQUEST_LIST_FINDOUT,getResources().getString(R.string.loadingFromQMS));
            vsrUpdated = new VolleyRequest(this, Request.Method.POST, params, urlHelper.GET_UPDATED_DATA, false, CommonDef.REQUEST_GET_UPDATED,getResources().getString(R.string.loadingFromQMS));
            vsrASAQ = new VolleyRequest(this, Request.Method.POST, params, urlHelper.LIST_ASAQ, false, CommonDef.REQUEST_LIST_ASAQ,getResources().getString(R.string.loadingFromQMS));

            vsr = new VolleyRequest(this, Request.Method.POST, params, urlHelper.LIST_ENGAGEMENT_EVIDENCE, false, CommonDef.REQUEST_GET_UPDATED,getResources().getString(R.string.login));
            vsr.asyncInterface = this;
            vsr.request();

            vsrPurpose.asyncInterface = this;
            vsrPurpose.request();

            vsrActivity.asyncInterface = this;
            vsrActivity.request();

            vsrFindOut.asyncInterface = this;
            vsrFindOut.request();

            vsrASAQ.asyncInterface = this;
            vsrASAQ.request();

            vsrUpdated.asyncInterface = this;
            vsrUpdated.request();
        }
    }


    protected void loginInfo(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            if(response_int==0) {
                token = jObjResult.getString("token");
                userid = jObjResult.getString("userid");
                sharedPreference.setKeyValues("token" , token);
                sharedPreference.setKeyValues("userid", userid);

                sharedPreference.setKeyValues("Flag", 1);
//                Toast.makeText(this,"Login Successful",Toast.LENGTH_LONG).show();
                loadExistingIndustryAndData();
            }
            else
            {
                alerts.showOkMessage(responseText);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadExistingEvidence(String result) {
        JSONObject jObjResult;
        engagementEvidenceList = new ArrayList<EngagementEvidenceData>();
        try {
             jObjResult = new JSONObject(result);
            String responseText = jObjResult.getString("responseText");
            int countOfEvidence = jObjResult.getInt("countOfEvidence");
            int totalHours = jObjResult.getInt("totalHours");
            int totalHoursThisYear = jObjResult.getInt("totalHoursThisYear");
            if (countOfEvidence > 0) {
                JSONArray jArrayResult = jObjResult.getJSONArray("evidenceData");
                dbHelper = new EngagementEvidenceDbHandler(this);
                dbHelper.Open();
                for (int i = 0; i < jArrayResult.length(); i++) {
                    JSONObject jArrayObject = jArrayResult.getJSONObject(i);
                    int engagement_Evidence_Id = Integer.parseInt(jArrayObject.getString("Engagement_Evidence_Id"));
                    String title = jArrayObject.getString("Title");
                    String status = jArrayObject.getString("status");
                    int User_id = Integer.parseInt(jArrayObject.getString("User_id"));
                    String Current_Step = jArrayObject.getString("Current_Step");

                    engagementEvidenceData = new EngagementEvidenceData();
                    engagementEvidenceData.setEngagementEvidenceId(engagement_Evidence_Id);
                    engagementEvidenceData.setTitle(title);
                    engagementEvidenceData.setStatus(status);
                    engagementEvidenceData.setUserid(User_id);
                    engagementEvidenceData.setCurrentStep(Current_Step);

                    if(jArrayObject.getString("industryEngagementData")!="false") {
                        JSONObject jsonData = jArrayObject.getJSONObject("industryEngagementData");
                        int Industry_Engagement_Id = Integer.parseInt(jsonData.getString("Industry_Engagement_Id"));
                        String Reference = jsonData.getString("Reference");
                        String Datetime = jsonData.getString("Datetime");
                        String Hours = jsonData.getString("Hours");
                        String Venue = jsonData.getString("Venue");
                        int Findout_Id = Integer.parseInt(jsonData.getString("Findout_Id"));

                        engagementEvidenceData.setIndustryEngagementId(Industry_Engagement_Id);
                        engagementEvidenceData.setReference(Reference);
                        engagementEvidenceData.setDatetime(Datetime);
                        engagementEvidenceData.setHours(Hours);
                        engagementEvidenceData.setVenue(Venue);
                        engagementEvidenceData.setFindOutId(Findout_Id);
                    }
                 //   engagementEvidenceList.add(engagementEvidenceData);
                    dbHelper.InsertEngagementEvidence(engagementEvidenceData);
                    dbHelper.InsertEngagementEvidenceData(engagementEvidenceData);
                }
                dbHelper.InsertEngagementHours(1, totalHours, totalHoursThisYear);
              //  opener.Home();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
   /* private void loadIndustryContact(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String responseText = jObjResult.getString("responseText");
            int countOfContacts = jObjResult.getInt("countOfContacts");

            Toast.makeText(this,"Response: " +responseText,Toast.LENGTH_LONG).show();
            Toast.makeText(this,"No. of Industry Contacts: " + countOfContacts+"",Toast.LENGTH_LONG).show();

            if(countOfContacts > 0)
            {
                JSONArray jArrayResult = jObjResult.getJSONArray("contactList");
                for(int i = 0; i <jArrayResult.length() ; i++) {
                    JSONObject jArrayObject = jArrayResult.getJSONObject(i);
                    String Industry_Contact_Id = jArrayObject.getString("Industry_Contact_Id");
                    sharedPreference.setKeyValues("Industry_Contact_Id", Industry_Contact_Id);
                    LoadIndustryContactList(token, userid, Industry_Contact_Id);
                }
            }
            else
            {
                opener.Home();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

   /* private void loadIndustryContactList(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            JSONArray contactData = jObjResult.getJSONArray("contactData");

            for(int i=0;i<contactData.length();i++) {
                try {
                    JSONObject industryContact = contactData.getJSONObject(i);
                    String industryContactId = industryContact.getString("Industry_Contact_Id");
                    String title = industryContact.getString("Title");
                    String organization = industryContact.getString("Organization");
                    String streetAddress= industryContact.getString("Street_Address");
                    String suburb = industryContact.getString("Suburb");
                    String postalCode= industryContact.getString("Postal_Code");
                    String phone = industryContact.getString("Phone");
                    String email = industryContact.getString("Email");
                    String qualification = industryContact.getString("Qualification");
                    String comment = industryContact.getString("Comment");
                    String opportunities = industryContact.getString("Opportunities");
                    String actionRequired = industryContact.getString("Action_Required");
                    String activityRecommendation = industryContact.getString("Activity_Recommendation");

                    int industrycntId = Integer.parseInt(industryContactId);

                    industryContactDetails = new IndustryContactDetails();
                    industryContactDetails.setIndustry_Contact_Id(industrycntId);
                    industryContactDetails.setTitle(title);
                    industryContactDetails.setOrganization(organization);
                    industryContactDetails.setStreet_Address(streetAddress);
                    industryContactDetails.setSuburb(suburb);
                    industryContactDetails.setPostal_Code(postalCode);
                    industryContactDetails.setPhone(phone);
                    industryContactDetails.setEmail(email);
                    industryContactDetails.setQualification(qualification);
                    industryContactDetails.setComment(comment);
                    industryContactDetails.setOpportunities(opportunities);
                    industryContactDetails.setAction_Required(actionRequired);
                    industryContactDetails.setActivity_Recommendation(activityRecommendation);

//                    dbHelper.Open();
//                    dbHelper.InsertIndustryContact(industryContactDetails);

                    IndustryContactDetailsStatic industryContactDetailsStatic = new IndustryContactDetailsStatic();
                    industryContactDetailsStatic.industryContactDetailsArrayList.add(industryContactDetails);

                    //Set Flags
                    sharedPreference.setKeyValues("Flag" , 1);

                    opener.Home();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/



}
