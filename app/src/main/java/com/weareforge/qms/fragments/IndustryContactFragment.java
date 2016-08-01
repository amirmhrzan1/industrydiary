package com.weareforge.qms.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.weareforge.qms.Objects.EngagedIndustryContact;
import com.weareforge.qms.Objects.EngagementEvidenceData;
import com.weareforge.qms.Objects.IndustryContacts;
import com.weareforge.qms.Objects.IndustryContactsStatic;
import com.weareforge.qms.R;
import com.weareforge.qms.activities.NewIndustyActivity;
import com.weareforge.qms.adapters.ExistingIndustryListArrayAdapter;
import com.weareforge.qms.adapters.MyIndustryContactArrayAdapter;
import com.weareforge.qms.adapters.MyPageAdapter;
import com.weareforge.qms.alerts.Alerts;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.db.EngagementEvidenceDbHandler;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.interfaces.AsyncInterface;
import com.weareforge.qms.utils.ConnectionMngr;
import com.weareforge.qms.utils.FontHelper;
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

import static eu.inmite.android.lib.validations.form.annotations.RegExp.EMAIL;

/**
 * Created by Admin on 1/7/2016.
 *
 */

public class IndustryContactFragment extends Fragment implements AsyncInterface {

    private Button btnNewContact;
    private Button btnSelectExisting;
    private Button btnSaveContact;
    private ImageButton imgBtnBack;
    private TextView txtBack;
    private Button btnSaveAndNext;
    private ScrollView layoutIndustryContact;
    private LinearLayout layoutAddNewContact;
    private RelativeLayout layoutback;

    public LinearLayout myContactList;
    private static ListView industry_list;
    private static MyIndustryContactArrayAdapter adapter;

    public EngagedIndustryContact engagedIndustryContact;
    public ArrayList<EngagedIndustryContact> listEngagedIndustry =null;

    private ListView mContactList;

    private View listPopUpView;
    Dialog dialog;

    private VolleyRequest vsr;
    private HashMap<String, String> params;
    private SharedPreference sharedPreference;
    private UrlHelper urlHelper;
    private Alerts alerts;
    private ConnectionMngr connectionMngr;
    private EngagementEvidenceDbHandler dbHelper;


    private String token;
    private String userid;
    private String Engagement_Evidence_Id;
    private String Industry_contact_Id="";

    private ImageView questionQualification;
    private ImageView questionComments;
    private ImageView questionOpportunities;
    private ImageView questionAction;
    private ImageView questionActivityRecommended;

    private TextView tvFragmentTitle;;
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

    private boolean _hasLoadedOnce= false;

    @NotEmpty(messageId = R.string.err_msg_title, order = 2)
    private EditText edtTitle;

    @NotEmpty(messageId = R.string.err_msg_name, order = 1)
    private EditText edtName;

    @NotEmpty(messageId = R.string.err_msg_organization, order = 3)
    private EditText edtOrganization;

  //  @NotEmpty(messageId = R.string.err_msg_street_address, order = 4)
    private EditText edtStreetAddress;

   // @NotEmpty(messageId = R.string.err_msg_suburb, order = 5)
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


    private EditText edtQualification;

  //  @NotEmpty(messageId = R.string.err_msg_comment, order = 13)
    private EditText edtComment;

  //  @NotEmpty(messageId = R.string.err_msg_opportunities, order = 14)
    private EditText edtOppprtunities;

  //  @NotEmpty(messageId = R.string.err_msg_action, order = 15)
    private EditText edtActionRequired;

   // @NotEmpty(messageId = R.string.msg_empty, order = 16)
    private EditText edtActivityRecommendation;

   private FontHelper fontHelper;
    private int selectedIndex;

    private static MyContact contactListAdapter;
    private static IndustryContactsStatic industryContactsStatic = new IndustryContactsStatic();
    private static IndustryContacts industryContacts;
    private EngagementEvidenceData engagementEvidenceData;

    static ArrayList<IndustryContacts> industryContactStatic = new ArrayList<IndustryContacts>();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_industry_contact, container, false);
        //   CommonMethods.setupUI(rootView.findViewById(R.id.relativeLayoutindustryContact), getActivity());

        //Initializing
        this.layoutAddNewContact = (LinearLayout) rootView.findViewById(R.id.layoutAddNew);
        this.layoutIndustryContact = (ScrollView) rootView.findViewById(R.id.layoutIndustryContact);
        this.layoutback = (RelativeLayout) rootView.findViewById(R.id.rlback);
        this.btnNewContact = (Button) rootView.findViewById(R.id.btn_addNewContact);
        this.btnSelectExisting = (Button) rootView.findViewById(R.id.btn_selectExisting);
        this.btnSaveContact = (Button) rootView.findViewById(R.id.btnSaveContact);
        this.imgBtnBack = (ImageButton) rootView.findViewById(R.id.backImg);
        this.txtBack = (TextView) rootView.findViewById(R.id.txtBack);
        this.btnSaveAndNext = (Button) rootView.findViewById(R.id.btnSaveAndNext);

        this.questionQualification= (ImageView) rootView.findViewById(R.id.questionQualification);
        this.questionComments= (ImageView) rootView.findViewById(R.id.questionComment);
        this.questionOpportunities= (ImageView) rootView.findViewById(R.id.questionOpportunities);
        this.questionAction= (ImageView) rootView.findViewById(R.id.questionAction);
        this.questionActivityRecommended= (ImageView) rootView.findViewById(R.id.questionActivityRecommended);

        this.tvFragmentTitle = (TextView) rootView.findViewById(R.id.tvFragmentTitle);
        this.tvName = (TextView) rootView.findViewById(R.id.tvName);
        this.tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        this.tvOrganization = (TextView) rootView.findViewById(R.id.tvOrganization);
        this.tvStreetAddress = (TextView) rootView.findViewById(R.id.tvStreetAddress);
        this.tvSuburb = (TextView) rootView.findViewById(R.id.tvSuburb);
        this.tvPostCode = (TextView) rootView.findViewById(R.id.tvPostCode);
        this.tvPhone = (TextView) rootView.findViewById(R.id.tvPhone);
        this.tvEmail = (TextView) rootView.findViewById(R.id.tvEmail);
        this.tvQualification = (TextView) rootView.findViewById(R.id.tvQualification);
        this.tvComments = (TextView) rootView.findViewById(R.id.tvComments);
        this.tvOpportunities = (TextView) rootView.findViewById(R.id.tvOpportunities);
        this.tvActionRequired = (TextView) rootView.findViewById(R.id.tvActionRequired);
        this.tvActivityRecommended = (TextView) rootView.findViewById(R.id.tvActivityRecommended);

        this.edtTitle = (EditText) rootView.findViewById(R.id.edtTitle);
        this.edtName = (EditText) rootView.findViewById(R.id.edtName);
        this.edtOrganization = (EditText) rootView.findViewById(R.id.edtOrganization);
        this.edtStreetAddress = (EditText) rootView.findViewById(R.id.edtStreetAddress);
        this.edtSuburb = (EditText) rootView.findViewById(R.id.edtSuburb);
        this.edtPostalCode = (EditText) rootView.findViewById(R.id.edtPostalCode);
        this.edtPhone = (EditText) rootView.findViewById(R.id.edtPhone);
        this.edtEmail = (EditText) rootView.findViewById(R.id.edtEmail);
        this.edtQualification = (EditText) rootView.findViewById(R.id.edtQualification);
        this.edtComment = (EditText) rootView.findViewById(R.id.edtComment);
        this.edtOppprtunities = (EditText) rootView.findViewById(R.id.edtOpportunities);
        this.edtActionRequired = (EditText) rootView.findViewById(R.id.edtActionRequired);
        this.edtActivityRecommendation = (EditText) rootView.findViewById(R.id.edtActivityRecommendation);
        this.myContactList = (LinearLayout) rootView.findViewById(R.id.listContact);

        sharedPreference = new SharedPreference(getActivity());
        token = sharedPreference.getStringValues("token");
        userid = sharedPreference.getStringValues("userid");
        int flag = sharedPreference.getIntValues("flag");
        Engagement_Evidence_Id = sharedPreference.getStringValues("Engagement_Evidence_Id");

        alerts = new Alerts(getActivity());
        engagementEvidenceData = new EngagementEvidenceData();
        connectionMngr = new ConnectionMngr(getContext());
        dbHelper = new EngagementEvidenceDbHandler(getActivity());

        this.industry_list = new ListView(getActivity());
        listEngagedIndustry = new ArrayList<EngagedIndustryContact>();


        if(flag==1) {
            listContactEngaged();
        }

        //Font Helpers
        fontHelper = new FontHelper(getActivity());
        this.tvFragmentTitle.setTypeface(fontHelper.getDefaultFont());
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
        this.btnNewContact.setTypeface(fontHelper.getDefaultFont());
        this.btnSelectExisting.setTypeface(fontHelper.getDefaultFont());
        this.btnSaveContact.setTypeface(fontHelper.getDefaultFont());
        this.txtBack.setTypeface(fontHelper.getDefaultFont("bold"));
        this.btnSaveAndNext.setTypeface(fontHelper.getDefaultFont());

        listContactEngaged();

        //Click Listners
        btnNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAddNewContact.setVisibility(View.VISIBLE);
                layoutIndustryContact.setVisibility(View.GONE);
                layoutback.setVisibility(View.VISIBLE);
                Industry_contact_Id = "";
                edtName.setText("");
                edtTitle.setText("");
                edtOrganization.setText("");
                edtStreetAddress.setText("");
                edtSuburb.setText("");
                edtPostalCode.setText("");
                edtPhone.setText("");
                edtEmail.setText("");
                edtQualification.setText("");
                edtComment.setText("");
                edtOppprtunities.setText("");
                edtActionRequired.setText("");
                edtActivityRecommendation.setText("");
                btnSaveContact.setText("SUBMIT");
            }
        });

        btnSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {
                    saveContact();
                }
                else
                    alerts.showCreateEvidenceOkMessage("Please create evidence before proceed");

            }
        });

        this.imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                layoutAddNewContact.setVisibility(View.GONE);
                layoutIndustryContact.setVisibility(View.VISIBLE);
                layoutback.setVisibility(View.GONE);

            }
        });

        this.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                layoutAddNewContact.setVisibility(View.GONE);
                layoutIndustryContact.setVisibility(View.VISIBLE);
                layoutback.setVisibility(View.GONE);
            }
        });

        btnSelectExisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionMngr.hasConnection()) {
                    if(!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {
                        LoadContacts();
                    //    showPopupDialog();
                    }
                    else
                        alerts.showCreateEvidenceOkMessage("Please create evidence before proceed");
                }
            }
        });

        this.questionQualification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alerts.showOkWhatMessage(getActivity().getResources().getString(R.string.text_actionRequired));
            }
        });

        this.questionComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerts.showOkWhatMessage(getActivity().getResources().getString(R.string.text_comment));
            }
        });
        this.questionOpportunities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerts.showOkWhatMessage(getActivity().getResources().getString(R.string.text_opportunities));
            }
        });
        this.questionAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerts.showOkWhatMessage(getActivity().getResources().getString(R.string.text_actionRequired));
            }
        });
        this.questionActivityRecommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alerts.showOkWhatMessage("this is msg");
            }
        });

        this.btnSaveAndNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewIndustyActivity newIndustyActivity = new NewIndustyActivity();
                System.out.println("prajit is testing step 2: " + sharedPreference.getIntValues("Current_Step"));
                if(sharedPreference.getIntValues("Current_Step") < 2)
                {
                    sharedPreference.setKeyValues("Current_Step",2);
                    MyPageAdapter adapter = (MyPageAdapter)newIndustyActivity.mPager.getAdapter();
                    newIndustyActivity.currentStep = 2;
                    newIndustyActivity.mPager.getAdapter().notifyDataSetChanged();
                    adapter.currentPage(2);
                }
                newIndustyActivity.mPager.setCurrentItem(2);
//                Toast.makeText(getActivity(), "Step 2 information saved", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    /*
     * Save new Contact after form fill up
     */
    private void saveContact() {
        if(!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {
            final boolean isValid = FormValidator.validate(IndustryContactFragment.this, new SimpleErrorPopupCallback(getActivity(), true));
            if (isValid) {
                if (connectionMngr.hasConnection()) {
                    params = new HashMap<>();
                    params.put("token", token);
                    params.put("userid", userid);
                    params.put("engagement_evidence_id", sharedPreference.getStringValues("Engagement_Evidence_Id"));
                    params.put("title", edtTitle.getText().toString());
                    params.put("name", edtName.getText().toString());
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

                    if(!Industry_contact_Id.equalsIgnoreCase(""))
                    {
                        params.put("industry_contact_id",Industry_contact_Id);
                        vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, UrlHelper.INDUSTRY_CONTACT_EDIT, false, CommonDef.REQUEST_INDUSTRY_CONTACT_ADD,getResources().getString(R.string.save));
                    }
                    else
                    {
                        vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, UrlHelper.INDUSTRY_CONTACT_ADD, false, CommonDef.REQUEST_INDUSTRY_CONTACT_ADD,getResources().getString(R.string.save));
                    }
                    vsr.asyncInterface = ((NewIndustyActivity) getContext());
                    vsr.request();
                }
            }
        }
        else
            alerts.showCreateEvidenceOkMessage("Please create evidence before proceed");
    }

    /*
    * list contact enagaged in the evidence id
     */
    public void listContactEngaged()
    {
            if(connectionMngr.hasConnection())
            {
                params = new HashMap<String, String>();
                params.put("token",token);
                params.put("userid",userid);
                params.put("engagement_evidence_id",sharedPreference.getStringValues("Engagement_Evidence_Id"));
                vsr = new VolleyRequest(getActivity(),Request.Method.POST,params,urlHelper.LIST_ENGAGEMENT_CONTACT,false,CommonDef.REQUEST_ENGAGEMENT_CONTACT_LIST);
                vsr.asyncInterface = ((NewIndustyActivity)getContext());
                vsr.request();
            }
        }


    //Display the selected contacts in the list
    private void displayContacts(int industryContactId) {
        params = new HashMap();
        params.put("token", token);
        params.put("userid", userid);
        params.put("industry_contact_id", industryContactId + "");

        vsr = new VolleyRequest(getActivity(), Request.Method.POST,params,urlHelper.INDUSTRY_CONTACT_LIST,false,CommonDef.REQUEST_INDUSTRY_CONTACT_LIST);
        vsr.asyncInterface = ((NewIndustyActivity)getContext());
        vsr.request();
    }

    //Load the existing contacts of the user
    private void LoadContacts() {
        params = new HashMap<>();
        params.put("token", token);
        params.put("userid", userid);
        vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, urlHelper.INDUSTRY_CONTACT, false, CommonDef.REQUEST_INDUSTRY_CONTACTS,getResources().getString(R.string.loadingFromQMS));
        vsr.asyncInterface = ((NewIndustyActivity)getContext());
        vsr.request();
    }

    //Load  contacts
    public  void LoadIndustryContacts(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String responseText = jObjResult.getString("responseText");
            int countOfContacts = jObjResult.getInt("countOfContacts");
            industryContactsStatic.industryContactsArrayList.clear();
            if(countOfContacts > 0)
            {
                JSONArray jArrayResult = jObjResult.getJSONArray("contactList");
                for(int i = 0; i <jArrayResult.length() ; i++) {
                    JSONObject jArrayObject = jArrayResult.getJSONObject(i);
                    String Industry_Contact_Id = jArrayObject.getString("Industry_Contact_Id");
                    String Title = jArrayObject.getString("Title");
                    String Name = jArrayObject.getString("Name");
                    String Email = jArrayObject.getString("Email");

                    industryContacts = new IndustryContacts();
                    industryContacts.setIndustry_Contact_Id(Integer.parseInt(Industry_Contact_Id));
                    industryContacts.setName(Name);
                    industryContacts.setTitle(Title);
                    industryContacts.setEmail(Email);
                    //Add IndustryContact Object in Array
                    industryContactsStatic.industryContactsArrayList.add(industryContacts);
                }
                showPopupDialog();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //PopUp Dialog
    private void showPopupDialog() {
        industryContactStatic = IndustryContactsStatic.industryContactsArrayList;
        final ExistingIndustryListArrayAdapter adapter = new ExistingIndustryListArrayAdapter(getActivity(),industryContactStatic);
        dialog = new Dialog(getActivity(), android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_popup_dialog);
        dialog.setCancelable(true);
        final ListView list = (ListView) dialog.findViewById(R.id.listItemContact);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                ArrayList<Integer> selectedContact = new ArrayList<Integer>();
                StringBuilder result = new StringBuilder();
                ArrayList<Integer> engagedContactId = new ArrayList<Integer>();
                for(int i=0;i<listEngagedIndustry.size();i++)
                {
                    engagedContactId.add(listEngagedIndustry.get(i).getIndustry_Contact_Id());
                }
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.mCheckStates.get(i) == true) {
                        int id = adapter.getIndustryContactId(i);
                        if(!engagedContactId.contains(adapter.getIndustryContactId(i)))
                        {
                            selectedContact.add(adapter.getIndustryContactId(i));
                        }
//                        Toast.makeText(getActivity(), id+" items", Toast.LENGTH_LONG).show();
                        count++;
                    }
                }
                selectIndustyContact(selectedContact);

            }
        });


        ImageButton iv_cross = (ImageButton) dialog.findViewById(R.id.ibtn_cross);
        iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void SaveProgress() {
        if (connectionMngr.hasConnection()) {
            if(!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {
                params = new HashMap<String, String>();
                params.put("token", token);
                params.put("userid", userid);
                params.put("engagement_evidence_id", Engagement_Evidence_Id);
                params.put("title", "");
                params.put("status", "0");
                if(sharedPreference.getIntValues("Current_step")<2) {
                    params.put("current_step", "2");
                }
                else
                {
                    params.put("current_step",String.valueOf(sharedPreference.getIntValues("Current_step")));
                }

                engagementEvidenceData.setEngagementEvidenceId(Integer.parseInt(sharedPreference.getStringValues("Engagement_Evidence_Id")));
                engagementEvidenceData.setTitle("");
                engagementEvidenceData.setStatus("0");
                engagementEvidenceData.setCurrentStep("2");
                engagementEvidenceData.setUserid(Integer.parseInt(userid));

                vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, urlHelper.ENGAGEMENT_EVIDENCE, false, CommonDef.REQUEST_SAVE_PROGRESS_STEP_2,getResources().getString(R.string.saveProgress));
                vsr.asyncInterface = ((NewIndustyActivity) getContext());
                vsr.request();
            }
            else
            {
                alerts.showOkMessage("Please create evidence before proceed");
            }
        }
    }

    //Send  multiple contacts to add in the server
    private void selectIndustyContact(ArrayList<Integer> selectedContact) {
        if(connectionMngr.hasConnection())
        {
            params = new HashMap<String,String>();
            params.put("token",token);
            params.put("userid",userid);
            params.put("engagement_evidence_id", sharedPreference.getStringValues("Engagement_Evidence_Id"));
            for(int i=0;i<selectedContact.size();i++) {
                params.put("industry_contact_id[" + i + "]", String.valueOf(selectedContact.get(i)));
            }
            vsr = new VolleyRequest(getActivity(),Request.Method.POST,params,urlHelper.ADD_EXISTING_CONTACTS,false,CommonDef.REQUEST_ADD_EXISTING_CONTACTS);
            vsr.asyncInterface = ((NewIndustyActivity)getContext());
            vsr.request();
        }
    }


    //populate the selected contact list
    public void loadIndustryContactList(String result) {
        JSONObject jObjResult;
        layoutAddNewContact.setVisibility(View.VISIBLE);
        layoutIndustryContact.setVisibility(View.GONE);
        layoutback.setVisibility(View.VISIBLE);
        btnSaveContact.setText("UPDATE");
        try {
            jObjResult = new JSONObject(result);
            JSONArray contactData = jObjResult.getJSONArray("contactData");
            for (int i = 0; i < contactData.length(); i++) {
                try {
                    JSONObject industryContact = contactData.getJSONObject(i);
                    edtTitle.setText( industryContact.getString("Title"));
                    edtName.setText(industryContact.getString("Name"));
                    edtOrganization.setText(industryContact.getString("Organization"));
                    edtStreetAddress.setText(industryContact.getString("Street_Address"));
                    edtSuburb.setText(industryContact.getString("Suburb"));
                    edtPostalCode.setText(industryContact.getString("Postal_Code"));
                    edtPhone.setText(industryContact.getString("Phone"));
                    edtEmail.setText(industryContact.getString("Email"));
                    edtQualification.setText(industryContact.getString("Qualification"));
                    edtComment.setText(industryContact.getString("Comment"));
                    edtOppprtunities.setText(industryContact.getString("Opportunities"));
                    edtActionRequired.setText(industryContact.getString("Action_Required"));
                    edtActivityRecommendation.setText(industryContact.getString("Activity_Recommendation"));

                //    popup.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    @Override
    public void processFinish(String result, int requestCode) {
        JSONObject jsonObject;
        switch (requestCode) {
            case CommonDef.REQUEST_INDUSTRY_CONTACT_DELETE:
                try {
                    jsonObject = new JSONObject(result);
                    String response = jsonObject.getString("response");
                    String responseText = jsonObject.getString("responseText");
                    if (response.equalsIgnoreCase("0")) {
                        listEngagedIndustry.remove(contactListAdapter.getItem(selectedIndex));
                        this.contactListAdapter.notifyDataSetChanged();
                        this.myContactList.removeAllViews();
                        final int adapterCount = contactListAdapter.getCount();

                        for (int i = 0; i < adapterCount; i++) {
                            View item = contactListAdapter.getView(i, null, null);
                            myContactList.addView(item);
                        }

                        if(adapterCount > 0)
                        {
                            this.btnSaveAndNext.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            this.btnSaveAndNext.setVisibility(View.GONE);
                        }

                        Toast.makeText(getActivity(), "Contact deleted successfully.", Toast.LENGTH_LONG).show();
                    } else
                    {
                    //    Toast.makeText(getActivity(), responseText, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case CommonDef.REQUEST_INDUSTRY_CONTACT_LIST:
                loadIndustryContactList(result);
        }
    }

    //response after save progress
    public void requestSaveProgress(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            if (response_int == 0) {
                JSONObject engagementData = jObjResult.getJSONObject("engagementEvidenceData");
                Toast.makeText(getActivity(), "Progress saved", Toast.LENGTH_LONG).show();

                dbHelper.Open();
                dbHelper.UpdateEngagementEvidenceData(engagementEvidenceData);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //response after adding new industry contacts
    public void requestIndustryContact(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            int response = Integer.parseInt(jsonObject.getString("response"));
            String responseText = jsonObject.getString("responseText");
            if(response==0)
            {
                listContactEngaged();
                layoutAddNewContact.setVisibility(View.GONE);
                layoutIndustryContact.setVisibility(View.VISIBLE);
                layoutback.setVisibility(View.GONE);

                edtActionRequired.setText("");
                edtActivityRecommendation.setText("");
                edtComment.setText("");
                edtEmail.setText("");
                edtName.setText("");
                edtOppprtunities.setText("");
                edtPhone.setText("");
                edtPostalCode.setText("");
                edtOrganization.setText("");
                edtStreetAddress.setText("");
                edtSuburb.setText("");
                edtTitle.setText("");
                edtQualification.setText("");

                if(!Industry_contact_Id.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Contact updated successfully to your engagement", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Contact added successfully to your engagement", Toast.LENGTH_LONG).show();
                }

            }
            else
            {
             //   Toast.makeText(getActivity(),responseText,Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //popoulate in Engaged COntacts in the view
    public void loadEngagementConactList(String result) {
        JSONObject jsonObject;
        myContactList.removeAllViews();
        listEngagedIndustry = new ArrayList<EngagedIndustryContact>();
        try {
            jsonObject = new JSONObject(result);
            String response = jsonObject.getString("response");
            String responseText = jsonObject.getString("responseText");
            int response_int = Integer.parseInt(response);
            if(response_int==0)
            {
                int countOfContacts = Integer.parseInt(jsonObject.getString("countOfContacts"));
                if(countOfContacts >0)
                {
                    JSONArray contactsIds = jsonObject.getJSONArray("contactIDs");
                    for(int i=0;i<contactsIds.length();i++)
                    {
                        engagedIndustryContact = new EngagedIndustryContact();
                        JSONObject jArrayObj = contactsIds.getJSONObject(i);
                        engagedIndustryContact.setIndustryContactEngagementId(Integer.parseInt(jArrayObj.getString("Industry_Engagement_Contact_Id")));
                        engagedIndustryContact.setEngagementEvidenceId(Integer.parseInt(jArrayObj.getString("Engagement_Evidence_Id")));
                        engagedIndustryContact.setIndustryContactId(Integer.parseInt(jArrayObj.getString("Industry_Contact_Id")));
                        engagedIndustryContact.setName(jArrayObj.getString("name"));

                        listEngagedIndustry.add(engagedIndustryContact);
                    }
                    contactListAdapter= new MyContact(getActivity(), listEngagedIndustry);
                    final int adapterCount = contactListAdapter.getCount();

                    for (int i = 0; i < adapterCount; i++) {
                        View item = contactListAdapter.getView(i, null, null);
                        myContactList.addView(item);
                    }

                    if(adapterCount >0)
                    {
                        this.btnSaveAndNext.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        this.btnSaveAndNext.setVisibility(View.GONE);
                    }
                }
//                else Toast.makeText(getActivity(), "No Contacts", Toast.LENGTH_SHORT).show();
            }
            else
            {
//                Toast.makeText(getActivity(), responseText, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //response after adding existing contact/s in the server

    public void addExistingContacts(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String response = jsonObject.getString("response");
            String responseText = jsonObject.getString("responseText");
            int response_int = Integer.parseInt(response);
            if(response_int==0)
            {
                listContactEngaged();
                dialog.dismiss();
                Toast.makeText(getActivity(), "Contact added Successfully to your engagement", Toast.LENGTH_LONG).show();

            }
            else
            {
                dialog.dismiss();
              //  Toast.makeText(getActivity(),responseText,Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Adapter to populate the engaged contact list
    public class MyContact extends BaseAdapter {

        private Context cntx;
        private LayoutInflater myinflater;
        private ArrayList<IndustryContacts> arrayList = new ArrayList<IndustryContacts>();

        private TextView txtName;
        private ImageView imgbtnDelete;
        private ImageButton imgbtnBrowse;
        private ImageButton imgbtnEdit;

        private FontHelper fontHelper;
        private SharedPreference sharedPreference;
        private UrlHelper urlHelper;

        private String token;
        private String userid;


        private HashMap<String, String> params;
        private VolleyRequest vsr;

        private ArrayList<EngagedIndustryContact> contactList;
        android.app.Fragment fragment;
        //button click alpha animation
        private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

        public MyContact(Context context, ArrayList<EngagedIndustryContact> list) {
            this.cntx = context;
            this.contactList = list;
            this.sharedPreference = new SharedPreference(context);
            token = sharedPreference.getStringValues("token");
            userid = sharedPreference.getStringValues("userid");
            // this.arrayList = arrayList;
            myinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return this.contactList.size();
        }

        @Override
        public Object getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private int getIndex()
        {
            return selectedIndex;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final EngagedIndustryContact contacts = (EngagedIndustryContact) getItem(position);
            if (convertView == null) {
                convertView = myinflater.inflate(R.layout.my_contact_list, parent, false);
            }

            this.txtName = (TextView) convertView.findViewById(R.id.profilename);
            this.imgbtnDelete = (ImageView) convertView.findViewById(R.id.delete);
            this.imgbtnEdit = (ImageButton) convertView.findViewById(R.id.edit);

            //Set Text
            this.txtName.setText(contacts.getName().substring(0, 1).toUpperCase() + contacts.getName().substring(1));


            //Fonts Helper
            fontHelper = new FontHelper((Activity) this.cntx);
            this.txtName.setTypeface(fontHelper.getDefaultFont());

            //listners
            this.imgbtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(buttonClick);
                    if(connectionMngr.hasConnection()) {
                        params = new HashMap();

                        params.put("token", token);
                        params.put("userid", userid);
                        params.put("industry_contact_id", String.valueOf(contacts.getIndustry_Contact_Id()));

                        vsr = new VolleyRequest((Activity) cntx, Request.Method.POST, params, urlHelper.INDUSTRY_CONTACT_LIST, false, CommonDef.REQUEST_INDUSTRY_CONTACT_LIST);
                        vsr.asyncInterface = IndustryContactFragment.this;
                        vsr.request();

                        Industry_contact_Id = String.valueOf(contacts.getIndustry_Contact_Id());
                    }

                }
            });

            this.imgbtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(buttonClick);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setTitle("Industry Diary");
                    builder1.setMessage("Are you sure you want to delete?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (connectionMngr.hasConnection()) {
                                        params = new HashMap<String, String>();
                                        params.put("token", token);
                                        params.put("userid", userid);
                                        params.put("industry_engagement_contact_id", String.valueOf(contacts.getIndustryContactEngagementId()));

                                        vsr = new VolleyRequest((Activity) cntx, Request.Method.POST, params, urlHelper.INDUSTRY_ENGAGEMENT_CONTACT_DELETE, false, CommonDef.REQUEST_INDUSTRY_CONTACT_DELETE);
                                        vsr.asyncInterface = IndustryContactFragment.this;
                                        vsr.request();

                                        selectedIndex = position;
                                    }
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            });
            return convertView;
        }
    }
}