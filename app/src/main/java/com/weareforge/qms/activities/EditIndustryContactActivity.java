package com.weareforge.qms.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.weareforge.qms.Objects.IndustryContactDetails;
import com.weareforge.qms.R;
import com.weareforge.qms.alerts.Alerts;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.db.IndustryContactsDb;
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

import java.util.HashMap;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MinLength;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

import static eu.inmite.android.lib.validations.form.annotations.RegExp.EMAIL;

/**
 * Created by Prajeet on 12/30/2015.
 */
public class EditIndustryContactActivity extends BaseActivity implements AsyncInterface, View.OnClickListener {

    @NotEmpty(messageId = R.string.err_msg_title, order = 2)
    private EditText edtTitle;

    @NotEmpty(messageId = R.string.err_msg_name, order = 1)
    private EditText edtName;

    @NotEmpty(messageId = R.string.err_msg_organization, order = 3)
    private EditText edtOrganization;

    //@NotEmpty(messageId = R.string.err_msg_street_address, order = 4)
    private EditText edtStreetAddress;

  //  @NotEmpty(messageId = R.string.err_msg_suburb, order = 5)
    private EditText edtSuburb;

    @NotEmpty(messageId = R.string.err_msg_postal_code, order = 6)
    @MinLength(value = 3, messageId =  R.string.err_msg_valid_postal_code, order = 7)
    @RegExp(value = "^[0-9]+$", messageId = R.string.err_msg_valid_postal_code)
    private EditText edtPostalCode;

    @NotEmpty(messageId = R.string.err_msg_phone, order = 8)
    @RegExp(value = "^[0-9]+$", messageId = R.string.err_msg_phone)
    private EditText edtPhone;

    @NotEmpty(messageId = R.string.err_msg_email, order = 10)
    @RegExp(value = EMAIL, messageId = R.string.err_msg_valid_email,order = 11)
    private EditText edtEmail;

   // @NotEmpty(messageId = R.string.err_msg_qualification, order = 12)
    private EditText edtQualification;

   // @NotEmpty(messageId = R.string.err_msg_comment, order = 13)
    private EditText edtComment;

  //  @NotEmpty(messageId = R.string.err_msg_opportunities, order = 14)
    private EditText edtOppprtunities;

  //  @NotEmpty(messageId = R.string.err_msg_action, order = 15)
    private EditText edtActionRequired;

  //  @NotEmpty(messageId = R.string.msg_empty, order = 16)
    private EditText edtActivityRecommendation;

    private Button btnAdd;

    private TextView txtBack;
    private ImageButton btn_back;

    private ImageView questionQualification;
    private ImageView questionComments;
    private ImageView questionOpportunities;
    private ImageView questionAction;
    private ImageView questionActivityRecommended;

    private TextView tvName;
    private TextView tvTitle;
    private TextView tvOrganization;
    private TextView tvStreetAddress;
    private TextView tvSuburb;
    private TextView tvPostCode;
    private TextView tvPhone;
    private TextView tvEmail;
    private TextView tvQualification;
    private TextView tvComments;
    private TextView tvOpportunities;
    private TextView tvActionRequired;
    private TextView tvActivityRecommended;

    private VolleyRequest vsr;
    private HashMap<String , String> params;

    private SharedPreference sharedPreference;
    private ConnectionMngr connectionMngr;
    private Opener opener;
    private UrlHelper urlHelper;
    Alerts alerts;
    private FontHelper fontHelper;

   // private FetchDataFromServer fetchData = new FetchDataFromServer();

    private  String userid;
    private  String token;
    private String industry_contact_id;

    private IndustryContactDetails industryContactDetails;

    private IndustryContactsDb dbHelper;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_edit_industry_contacts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();  //Removing action bar
        CommonMethods.setupUI(findViewById(R.id.relativeLayoutAddContact), this);

        //Full screen Window
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        sharedPreference = new SharedPreference(this);
        connectionMngr = new ConnectionMngr(this);
        opener = new Opener(this);
        alerts = new Alerts(this);
        dbHelper = new IndustryContactsDb(this);
        industryContactDetails = new IndustryContactDetails();

        this.tvName = (TextView) findViewById(R.id.tvName);
        this.tvTitle = (TextView) findViewById(R.id.tvTitle);
        this.tvOrganization = (TextView) findViewById(R.id.tvOrganization);
        this.tvStreetAddress = (TextView) findViewById(R.id.tvStreetAddress);
        this.tvSuburb = (TextView) findViewById(R.id.tvSuburb);
        this.tvPostCode = (TextView) findViewById(R.id.tvPostCode);
        this.tvPhone = (TextView) findViewById(R.id.tvPhone);
        this.tvEmail = (TextView) findViewById(R.id.tvEmail);
        this.tvQualification = (TextView) findViewById(R.id.tvQualification);
        this.tvComments = (TextView) findViewById(R.id.tvComments);
        this.tvOpportunities = (TextView) findViewById(R.id.tvOpportunities);
        this.tvActionRequired = (TextView) findViewById(R.id.tvActionRequired);
        this.tvActivityRecommended = (TextView) findViewById(R.id.tvActivityRecommended);

        this.edtTitle = (EditText) findViewById(R.id.edtTitle);
        this.edtName= (EditText) findViewById(R.id.edtName);
        this.edtOrganization = (EditText) findViewById(R.id.edtOrganization);
        this.edtStreetAddress = (EditText) findViewById(R.id.edtStreetAddress);
        this.edtSuburb = (EditText) findViewById(R.id.edtSuburb);
        this.edtPostalCode = (EditText) findViewById(R.id.edtPostalCode);
        this.edtPhone = (EditText) findViewById(R.id.edtPhone);
        this.edtEmail = (EditText) findViewById(R.id.edtEmail);
        this.edtQualification = (EditText) findViewById(R.id.edtQualification);
        this.edtComment = (EditText) findViewById(R.id.edtComment);
        this.edtOppprtunities = (EditText) findViewById(R.id.edtOpportunities);
        this.edtActionRequired = (EditText) findViewById(R.id.edtActionRequired);
        this.edtActivityRecommendation = (EditText) findViewById(R.id.edtActivityRecommendation);

        this.questionQualification= (ImageView)findViewById(R.id.questionQualification);
        this.questionComments= (ImageView) findViewById(R.id.questionComment);
        this.questionOpportunities= (ImageView) findViewById(R.id.questionOpportunities);
        this.questionAction= (ImageView)findViewById(R.id.questionAction);
        this.questionActivityRecommended= (ImageView)findViewById(R.id.questionActivityRecommended);

        this.btn_back = (ImageButton) findViewById(R.id.back_img);
        this.txtBack = (TextView) findViewById(R.id.txt_back);

        //Visible Buttons
        this.btn_back.setVisibility(View.VISIBLE);
        this.txtBack.setVisibility(View.VISIBLE);

        this.btnAdd = (Button) findViewById(R.id.btnAdd);
        this.btnAdd.setText("UPDATE");

        //Font Helpers
        fontHelper = new FontHelper(this);
        this.tvName.setTypeface(fontHelper.getDefaultFont());
        this.tvTitle.setTypeface(fontHelper.getDefaultFont());
        this.tvOrganization.setTypeface(fontHelper.getDefaultFont());
        this.tvStreetAddress.setTypeface(fontHelper.getDefaultFont());
        this.tvSuburb.setTypeface(fontHelper.getDefaultFont());
        this.tvPostCode.setTypeface(fontHelper.getDefaultFont());
        this.tvPhone.setTypeface(fontHelper.getDefaultFont());
        this.tvEmail.setTypeface(fontHelper.getDefaultFont());
        this.tvQualification.setTypeface(fontHelper.getDefaultFont());
        this.tvComments.setTypeface(fontHelper.getDefaultFont());
        this.tvOpportunities.setTypeface(fontHelper.getDefaultFont());
        this.tvActionRequired.setTypeface(fontHelper.getDefaultFont());
        this.tvActivityRecommended.setTypeface(fontHelper.getDefaultFont());
        this.edtName.setTypeface(fontHelper.getDefaultFont());
        this.edtTitle.setTypeface(fontHelper.getDefaultFont());
        this.edtOrganization.setTypeface(fontHelper.getDefaultFont());
        this.edtStreetAddress.setTypeface(fontHelper.getDefaultFont());
        this.edtSuburb.setTypeface(fontHelper.getDefaultFont());
        this.edtPostalCode.setTypeface(fontHelper.getDefaultFont());
        this.edtPhone.setTypeface(fontHelper.getDefaultFont());
        this.edtEmail.setTypeface(fontHelper.getDefaultFont());
        this.edtQualification.setTypeface(fontHelper.getDefaultFont());
        this.edtComment.setTypeface(fontHelper.getDefaultFont());
        this.edtOppprtunities.setTypeface(fontHelper.getDefaultFont());
        this.edtActionRequired.setTypeface(fontHelper.getDefaultFont());
        this.edtActivityRecommendation.setTypeface(fontHelper.getDefaultFont());
        this.txtBack.setTypeface(fontHelper.getDefaultFont("bold"));
        this.btnAdd.setTypeface(fontHelper.getDefaultFont());

        //shared preferences
        industry_contact_id = String.valueOf(sharedPreference.getIntValues("industry_contact_id"));
        userid= sharedPreference.getStringValues("userid");
        token = sharedPreference.getStringValues("token");

        if(connectionMngr.hasConnection()) {
            LoadContactDetails();
        }
        else Toast.makeText(this,"No Internet Connection", Toast.LENGTH_LONG).show();

        this.btn_back.setOnClickListener(this);
        this.txtBack.setOnClickListener(this);
        this.questionQualification.setOnClickListener(this);
        this.questionComments.setOnClickListener(this);
        this.questionOpportunities.setOnClickListener(this);
        this.questionAction.setOnClickListener(this);
        this.questionActivityRecommended.setOnClickListener(this);
        this.btnAdd.setOnClickListener(this);

        /*this.questionQualification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerts.showOkMessage("this is msg");
            }
        });

        this.questionComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerts.showOkMessage("this is msg");
            }
        });
        this.questionOpportunities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerts.showOkMessage("this is msg");
            }
        });
        this.questionAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerts.showOkMessage("this is msg");
            }
        });
        this.questionActivityRecommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerts.showOkMessage("this is msg");
            }
        });

        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isValid = FormValidator.validate(EditIndustryContactActivity.this, new SimpleErrorPopupCallback(EditIndustryContactActivity.this,true));
                if (isValid) {
                    if (connectionMngr.hasConnection()) {
                        params = new HashMap<>();
                        params.put("token", token);
                        params.put("userid", userid);
                        params.put("industry_contact_id", industry_contact_id);
                        params.put("title", edtTitle.getText().toString());
                        params.put("name",edtName.getText().toString());
                        params.put("organization", edtOrganization.getText().toString());
                        params.put("street_Address", edtStreetAddress.getText().toString());
                        params.put("suburb", edtSuburb.getText().toString());
                        params.put("postal_code", edtPostalCode.getText().toString());
                        params.put("phone", edtPhone.getText().toString());
                        params.put("email", edtEmail.getText().toString());
                        params.put("qualification", edtQualification.getText().toString());
                        params.put("comment", edtComment.getText().toString());
                        params.put("opportunities", edtOppprtunities.getText().toString());
                        params.put("action_required", edtActionRequired.getText().toString());
                        params.put("activity_recommendation", edtActivityRecommendation.getText().toString());


                        vsr = new VolleyRequest(EditIndustryContactActivity.this, Request.Method.POST, params, UrlHelper.INDUSTRY_CONTACT_EDIT, false, CommonDef.REQUEST_INDUSTRY_CONTACT_EDIT);
                        vsr.asyncInterface = EditIndustryContactActivity.this;
                        vsr.request();
                    }
                }
            }
        });
*/
    }

    private void LoadContactDetails() {
            if(connectionMngr.hasConnection()) {
                params = new HashMap();

                params.put("token", token);
                params.put("userid", userid);
                params.put("industry_contact_id", industry_contact_id);

                vsr = new VolleyRequest(this, Request.Method.POST, params, urlHelper.INDUSTRY_CONTACT_LIST, false, CommonDef.REQUEST_INDUSTRY_CONTACT_LIST);
                vsr.asyncInterface = this;
                vsr.request();
            }

        }


    @Override
    public void processFinish(String result, int requestCode) {
        switch (requestCode) {
            case CommonDef.REQUEST_INDUSTRY_CONTACT_EDIT:
                JSONObject jObjResult;
                try {
                    jObjResult = new JSONObject(result);
                    String response = jObjResult.getString("response");
                    String responseText = jObjResult.getString("responseText");
                    int response_int = Integer.parseInt(response);
                    if(response_int==0) {
                        Toast.makeText(this,"Contact updated successfully",Toast.LENGTH_LONG).show();
                        opener.MyIndustryContacts();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case CommonDef.REQUEST_INDUSTRY_CONTACT_LIST:
                loadIndustryContactList(result);
                break;

        }
    }

    private void loadIndustryContactList(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            JSONArray contactData = jObjResult.getJSONArray("contactData");
            for (int i = 0; i < contactData.length(); i++) {
                try {
                    JSONObject industryContact = contactData.getJSONObject(i);
                    this.edtTitle.setText( industryContact.getString("Title"));
                    this.edtName.setText(industryContact.getString("Name"));
                    this.edtOrganization.setText(industryContact.getString("Organization"));
                    this.edtStreetAddress.setText(industryContact.getString("Street_Address"));
                    this.edtSuburb.setText(industryContact.getString("Suburb"));
                    this.edtPostalCode.setText(industryContact.getString("Postal_Code"));
                    this.edtPhone.setText(industryContact.getString("Phone"));
                    this.edtEmail.setText(industryContact.getString("Email"));
                    this.edtQualification.setText(industryContact.getString("Qualification"));
                    this.edtComment.setText(industryContact.getString("Comment"));
                    this.edtOppprtunities.setText(industryContact.getString("Opportunities"));
                    this.edtActionRequired.setText(industryContact.getString("Action_Required"));
                    this.edtActivityRecommendation.setText(industryContact.getString("Activity_Recommendation"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  overridePendingTransition(R.anim.left_out, R.anim.right_in);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btn_back.getId())
        {
            v.startAnimation(buttonClick);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        else  if(v.getId() == txtBack.getId())
        {
            v.startAnimation(buttonClick);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        else  if(v.getId() == questionQualification.getId())
        {
//            alerts.showOkWhatMessage("this is msg");
        }

        else  if(v.getId() == questionOpportunities.getId())
        {
            alerts.showOkWhatMessage(this.getResources().getString(R.string.text_opportunities));
        }

        else  if(v.getId() == questionComments.getId())
        {
            alerts.showOkWhatMessage(this.getResources().getString(R.string.text_comment));
        }

        else  if(v.getId() == questionActivityRecommended.getId())
        {
//            alerts.showOkWhatMessage("this is msg");
        }

        else  if(v.getId() == questionAction.getId())
        {
            alerts.showOkWhatMessage(this.getResources().getString(R.string.text_actionRequired));
        }
        else  if(v.getId() == btnAdd.getId())
        {
            final boolean isValid = FormValidator.validate(EditIndustryContactActivity.this, new SimpleErrorPopupCallback(EditIndustryContactActivity.this,true));
            if (isValid) {
                if (connectionMngr.hasConnection()) {
                    params = new HashMap<>();
                    params.put("token", token);
                    params.put("userid", userid);
                    params.put("industry_contact_id", industry_contact_id);
                    params.put("title", edtTitle.getText().toString());
                    params.put("name",edtName.getText().toString());
                    params.put("organization", edtOrganization.getText().toString());
                    params.put("street_Address", edtStreetAddress.getText().toString());
                    params.put("suburb", edtSuburb.getText().toString());
                    params.put("postal_code", edtPostalCode.getText().toString());
                    params.put("phone", edtPhone.getText().toString());
                    params.put("email", edtEmail.getText().toString());
                    params.put("qualification", edtQualification.getText().toString());
                    params.put("comment", edtComment.getText().toString());
                    params.put("opportunities", edtOppprtunities.getText().toString());
                    params.put("action_required", edtActionRequired.getText().toString());
                    params.put("activity_recommendation", edtActivityRecommendation.getText().toString());


                    vsr = new VolleyRequest(EditIndustryContactActivity.this, Request.Method.POST, params, UrlHelper.INDUSTRY_CONTACT_EDIT, false, CommonDef.REQUEST_INDUSTRY_CONTACT_EDIT);
                    vsr.asyncInterface = EditIndustryContactActivity.this;
                    vsr.request();
                }
            }
        }
    }
}
