package com.weareforge.qms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.weareforge.qms.Objects.EngagementEvidenceData;
import com.weareforge.qms.Objects.OfflineData;
import com.weareforge.qms.Objects.spinnerObject;
import com.weareforge.qms.R;
import com.weareforge.qms.activities.NewIndustyActivity;
import com.weareforge.qms.adapters.MyPageAdapter;
import com.weareforge.qms.alerts.Alerts;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.db.EngagementEvidenceDbHandler;
import com.weareforge.qms.db.EngagementEvidenceOfflineContent;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.CommonMethods;
import com.weareforge.qms.helpers.DatePickerFragment;
import com.weareforge.qms.helpers.TimePickerFragment;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.utils.ConnectionMngr;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

/**
 * Created by Prajeet on 1/26/2016.
 */
public class IndustryEngagementFragment extends Fragment {

    private TextView tvTitle;
    private TextView tvTitleReference;
    private TextView tvSelectAccrediatation;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvNoOfHours;
    private TextView tvVenue;
    private TextView tvFindOut;
    private TextView tvActivity;
    private TextView tvPurpose;
    private String save = "Saving...";

    private static int cuurentHours = 0;

    @NotEmpty(messageId = R.string.err_msg_ref_title, order = 1)
    private EditText edtTitleReference;

    @NotEmpty(messageId = R.string.err_msg_date, order = 2)
    private EditText edtDate;

    @NotEmpty(messageId = R.string.err_msg_time, order = 3)
    private EditText edtTime;

    @NotEmpty(messageId = R.string.msg_empty_hours, order = 4)
    private EditText edtNoOfHours;

    @NotEmpty(messageId = R.string.err_msg_venue1, order = 5)
    private EditText edtVenue;

    @NotEmpty(messageId = R.string.err_msg_findOut, order = 6)
    private EditText edtFindOut;

    @NotEmpty(messageId = R.string.err_msg_otherActivity, order = 7)
    private EditText edtOtherActivity;

    @NotEmpty(messageId = R.string.err_msg_otherPurpose, order = 8)
    private EditText edtOtherPurpose;


    private Button btbSaveAndNext;
    private Button SaveProgress;

    private Context cntx;

    private Spinner spinnerAccred;
    private Spinner spinnerFindOut;

    LinearLayout linearLayoutActivity;
    LinearLayout linearLayoutPurpopse;

    private VolleyRequest vsrActivity;
    private VolleyRequest vsrPurpose;
    private VolleyRequest vsrFindOut;
    private VolleyRequest vsrAcc;
    private VolleyRequest vsr;
    private HashMap<String, String> params;
    private HashMap<String, String> accredParams;

    private static Boolean flagAccred = false;
    private static Boolean flagFindOut = false;
    private static int flag;

    private UrlHelper urlHelper;
    private SharedPreference sharedPreference;

    private Alerts alerts;
    private FontHelper fontHelper;

    public static ArrayList<String> findOutList;
    public static ArrayList<String> AccredList;
    public static ArrayList<Integer> activityIds;
    public static ArrayList<Integer> purposeIds;

    public static ArrayList<spinnerObject> arrayListFindOut;
    public static ArrayList<spinnerObject> arrayListAccred;

    public static ArrayList<spinnerObject> arrayListActivity;
    public static ArrayList<spinnerObject> arrayListPurpose;

    private static ArrayList<String> checkedPurposeIds;
    private static ArrayList<String> checkedActivityIds;
    private int findOutId;

    public static ArrayAdapter<String> spinnerAdapterFindOut;
    public static ArrayAdapter<String> spinnerAdapterAccred;

    public static int accredId = 0;
    private String selectedDate;
    public static String selectedTime = "";

    private String token;
    private String userid;
    private String EngagementEvidenceId;
    private String IndustryEngagementId;
    private ConnectionMngr connectionMngr;

    private EngagementEvidenceDbHandler dbHelper;
    private EngagementEvidenceOfflineContent dbOfflineDataHelper;
    private EngagementEvidenceData engagementEvidenceData;

    private ArrayList<OfflineData> activityData;
    private ArrayList<OfflineData> purposeOfActivityData;
    private ArrayList<OfflineData> findOutData;

    private JSONObject jObjResult;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_industry_engagement, container, false);
        CommonMethods.setupUI(rootView.findViewById(R.id.relativeLayoutIndstryEngagement), getActivity());

        //Initalize
        this.tvTitle = (TextView) rootView.findViewById(R.id.txtFragmentTitle);
        this.tvTitleReference = (TextView) rootView.findViewById(R.id.tvReferenceTitle);
        this.tvSelectAccrediatation = (TextView) rootView.findViewById(R.id.tvSelectAccreditation);
        this.tvDate = (TextView) rootView.findViewById(R.id.tvDate);
        this.tvTime = (TextView) rootView.findViewById(R.id.tvTime);
        this.tvNoOfHours = (TextView) rootView.findViewById(R.id.tvNoOfHours);
        this.tvVenue = (TextView) rootView.findViewById(R.id.tvVenue);
        this.tvFindOut = (TextView) rootView.findViewById(R.id.tvFindOut);
        this.tvActivity = (TextView) rootView.findViewById(R.id.tvActivity);
        this.tvPurpose = (TextView) rootView.findViewById(R.id.tvPurpose);
        this.edtTitleReference = (EditText) rootView.findViewById(R.id.edtReferenceTitle);
        this.edtDate = (EditText) rootView.findViewById(R.id.edtDate);
        this.edtTime = (EditText) rootView.findViewById(R.id.edtTime);
        this.edtNoOfHours = (EditText) rootView.findViewById(R.id.edtNoOfHours);
        this.edtVenue = (EditText) rootView.findViewById(R.id.edtVenue);
        this.btbSaveAndNext = (Button) rootView.findViewById(R.id.btnSaveAndNext);
        this.SaveProgress = (Button) getActivity().findViewById(R.id.btnSaveProgress);
        this.edtFindOut = (EditText) rootView.findViewById(R.id.edtFindOut);
        this.edtOtherActivity = (EditText) rootView.findViewById(R.id.edtOtherActivity);
        this.edtOtherPurpose = (EditText) rootView.findViewById(R.id.edtOtherPurpose);

        this.linearLayoutActivity = (LinearLayout) rootView.findViewById(R.id.linearLayoutActivity);
        this.linearLayoutPurpopse = (LinearLayout) rootView.findViewById(R.id.linearLayoutPurpose);
        this.spinnerAccred = (Spinner) rootView.findViewById(R.id.spinnerAccred);
        this.spinnerFindOut = (Spinner) rootView.findViewById(R.id.spinnerFindOut);

        alerts = new Alerts(getActivity());
        connectionMngr = new ConnectionMngr(getActivity());
        //Get Shared Preference Value
        sharedPreference = new SharedPreference(getActivity());
        flag = sharedPreference.getIntValues("flag");

        //focus off
        edtDate.setFocusable(true);
        edtTime.setFocusable(true);

        token = sharedPreference.getStringValues("token");
        userid = sharedPreference.getStringValues("userid");
        EngagementEvidenceId = sharedPreference.getStringValues("Engagement_Evidence_Id");
        IndustryEngagementId = sharedPreference.getStringValues("Industry_Engagement_Id");

        findOutList = new ArrayList<String>();
        purposeIds = new ArrayList<Integer>();
        checkedActivityIds = new ArrayList<String>();
        checkedPurposeIds = new ArrayList<String>();
        activityIds = new ArrayList<Integer>();

        arrayListFindOut = new ArrayList<spinnerObject>();
        arrayListAccred = new ArrayList<spinnerObject>();

        arrayListActivity = new ArrayList<>();
        arrayListPurpose = new ArrayList<>();


        dbHelper = new EngagementEvidenceDbHandler(getActivity());
        engagementEvidenceData = new EngagementEvidenceData();
        dbOfflineDataHelper = new EngagementEvidenceOfflineContent(getActivity());

        dbOfflineDataHelper.Open();
        purposeOfActivityData = dbOfflineDataHelper.GetAllPurposeAOfActivity();
        activityData = dbOfflineDataHelper.GetAllActivity();
        findOutData = dbOfflineDataHelper.GetFindOutData();

        if (connectionMngr.hasConnection()) {
            loadSpinner();
        } else {
            listPurpose();
            listActivity();
            listFindOut();
        }

        //Font Helpers
        fontHelper = new FontHelper(getActivity());
        tvTitle.setTypeface(fontHelper.getDefaultFont());
        this.tvTitleReference.setTypeface(fontHelper.getDefaultFont());
        this.tvSelectAccrediatation.setTypeface(fontHelper.getDefaultFont());
        this.tvDate.setTypeface(fontHelper.getDefaultFont());
        this.tvTime.setTypeface(fontHelper.getDefaultFont());
        this.tvNoOfHours.setTypeface(fontHelper.getDefaultFont());
        this.tvVenue.setTypeface(fontHelper.getDefaultFont());
        this.tvFindOut.setTypeface(fontHelper.getDefaultFont());
        this.tvActivity.setTypeface(fontHelper.getDefaultFont());
        this.tvPurpose.setTypeface(fontHelper.getDefaultFont());
        this.edtTitleReference.setTypeface(fontHelper.getDefaultFont());
        this.edtDate.setTypeface(fontHelper.getDefaultFont());
        this.edtTime.setTypeface(fontHelper.getDefaultFont());
        this.edtNoOfHours.setTypeface(fontHelper.getDefaultFont());
        this.edtVenue.setTypeface(fontHelper.getDefaultFont());
        this.btbSaveAndNext.setTypeface(fontHelper.getDefaultFont());

        //Date  Picker on click edittext
        edtDate.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                DialogFragment datePickerFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Log.d("QMS", "onDateSet");
                        SimpleDateFormat dt2 = new SimpleDateFormat("MMM dd,yyyy");
                        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
                        //In which you need put here
                        //  SimpleDateFormat df = new SimpleDateFormat(myFormat, Locale.US);
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        edtDate.setText(dt2.format(c.getTime()));
                        edtDate.setError(null);
                        selectedDate = dt1.format(c.getTime());
                        //nextField.requestFocus(); //moves the focus to something else after dialog is closed
                    }
                };
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

            }
        });

        //Time picker On click edittext
        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtTime.setFocusable(false);
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
//                new TimePickerFragment(edtTime, getActivity());
                new TimePickerFragment(edtTime, getActivity(), selectedTime);
                edtTime.setError(null);

            }
        });

        edtTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtTime.setFocusable(false);
                return false;
            }
        });

        edtDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtDate.setFocusable(false);
                return false;
            }
        });

        //Save Button ClickListner
        this.btbSaveAndNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  SaveProgress();
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                SaveAndNext();
            }
        });

        this.spinnerAccred.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                accredId = position;
                loadCompetencies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.spinnerFindOut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerAdapterFindOut.getItem(position).equalsIgnoreCase("Other - Specify")) {
                    edtFindOut.setVisibility(View.VISIBLE);
                } else {
                    edtFindOut.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    //Load Competencies in standard and competencies fragment on choose accredation
    private void loadCompetencies() {
        if (connectionMngr.hasConnection()) {

            params = new HashMap<String, String>();

            params.put("token", token);
            params.put("userid", userid);

            vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, urlHelper.MY_DETAILS, false, CommonDef.REQUEST_LIST_COMPETENCIES, getResources().getString(R.string.loadingFromQMS));
            vsr.asyncInterface = (NewIndustyActivity) getActivity();
            vsr.request();
        }
    }

    private void loadData() {
        if (connectionMngr.hasConnection()) {
            params = new HashMap<String, String>();

            params.put("token", token);
            params.put("userid", userid);
            params.put("engagement_evidence_id", EngagementEvidenceId);

            vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, urlHelper.LIST_INDUSTRY_ENGAGEMENT, false, CommonDef.REQUEST_LIST_INDUSTRY_ENGAGEMENT, getResources().getString(R.string.loadingFromQMS));
            vsr.asyncInterface = ((NewIndustyActivity) getContext());
            vsr.request();
        }
    }

    public void loadFragment() {

        //list data from database
        listPurpose();
        listActivity();
        listFindOut();
    }

    private void loadSpinner() {
        accredParams = new HashMap<>();

        accredParams.put("token", token);
        accredParams.put("userid", userid);

        vsrAcc = new VolleyRequest(getActivity(), Request.Method.POST, accredParams, urlHelper.MY_DETAILS, false, CommonDef.REQUEST_LIST_ACCRED, getResources().getString(R.string.loadingFromQMS));
        vsrAcc.asyncInterface = ((NewIndustyActivity) getContext());
        vsrAcc.request();
    }


    private void SaveAndNext() {
        if (connectionMngr.hasConnection()) {

            if(arrayListActivity.size() >0)
            {
                if(arrayListPurpose.size()==0)
                {
                    alerts.showOkMessage("Please select at least one purpose for activity.");
                    return;
                }
            }

            final boolean isValid = FormValidator.validate(IndustryEngagementFragment.this, new SimpleErrorPopupCallback(getActivity(), true));
            if (isValid) {
                params = new LinkedHashMap<String, String>();
                params.put("token", token);
                params.put("userid", userid);

                if (!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {
                    params.put("engagement_evidence_id", sharedPreference.getStringValues("Engagement_Evidence_Id"));
                    params.put("industry_engagement_id", IndustryEngagementId);
                    this.save = "Updating...";
                }
                params.put("reference", edtTitleReference.getText().toString());
                engagementEvidenceData.setReference(edtTitleReference.getText().toString());

                params.put("datetime", selectedDate + " " + selectedTime);
                engagementEvidenceData.setDatetime(edtTime.getText().toString());

                params.put("hours", edtNoOfHours.getText().toString());
                engagementEvidenceData.setHours(edtNoOfHours.getText().toString());

                params.put("venue", edtVenue.getText().toString());
                engagementEvidenceData.setVenue(edtVenue.getText().toString());

                for (int m = 0; m < arrayListFindOut.size(); m++) {
                    if (arrayListFindOut.get(m).getName().contains(spinnerFindOut.getSelectedItem().toString())) {
//Do whatever you want here
                        params.put("findout_id", String.valueOf(arrayListFindOut.get(m).getId()));
                    }
                }
                if (spinnerFindOut.getSelectedItem().toString().contains("Other - Specify")) {
                    params.put("findout_other_text", edtFindOut.getText().toString());
                }

                for (int m = 0; m < arrayListAccred.size(); m++) {
                    if (arrayListAccred.get(m).getName().contains(spinnerAccred.getSelectedItem().toString())) {
//Do whatever you want here
                        params.put("accreditation_id", String.valueOf(arrayListAccred.get(m).getId()));
                    }
                }

//                for (int j = 0; j < purposeIds.size(); j++) {
//                    params.put("purpose_activity_id[" + j + "]", String.valueOf(purposeIds.get(j)));
//                }
                params.put("purpose_activity_text","");

                for (int j = 0; j < arrayListPurpose.size(); j++) {
                    params.put("purpose_activity_id[" + j + "]", String.valueOf(arrayListPurpose.get(j).getId()));
                    if(arrayListPurpose.get(j).getName().equalsIgnoreCase("Other"))
                    {
                        params.put("purpose_activity_text",edtOtherPurpose.getText().toString());
                    }
                }

//                for (int i = 0; i < activityIds.size(); i++) {
//                    params.put("activity_id[" + i + "]", String.valueOf(activityIds.get(i)));
//                }
                params.put("activity_text","");

                for (int i = 0; i < arrayListActivity.size(); i++) {
                    params.put("activity_id[" + i + "]", String.valueOf(arrayListActivity.get(i).getId()));

                    if(arrayListActivity.get(i).getName().equalsIgnoreCase("Other"))
                    {
                        params.put("activity_text",edtOtherActivity.getText().toString());
                    }
                }

                vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, urlHelper.INDUSTRY_ENGAGEMENT_ADD_EDIT, false, CommonDef.REQUEST_INDUSTRY_ENGAGEMENT_ADD, this.save);
                vsr.asyncInterface = ((NewIndustyActivity) getContext());
                vsr.request();
            }
        }
    }


    public void listPurpose() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < purposeOfActivityData.size(); i++) {

            View view = inflater.inflate(R.layout.layout_checklist, null, false);
            final RelativeLayout relativeLayout2 = new RelativeLayout(getContext());
            final CheckBox checkBox2 = (CheckBox) view.findViewById(R.id.cb_check);
            final ImageView imageView2 = (ImageView) view.findViewById(R.id.iv_ic_quest);

            final spinnerObject spinnerObj;
            spinnerObj = new spinnerObject();
            spinnerObj.setId(purposeOfActivityData.get(i).getId());
            spinnerObj.setName(purposeOfActivityData.get(i).getTitle());
            //setcheckbox
            if (checkedPurposeIds != null && checkedPurposeIds.contains(String.valueOf(purposeOfActivityData.get(i).getId()))) {
                checkBox2.setChecked(true);
                if(spinnerObj.getName().equalsIgnoreCase("Other"))
                {
                    edtOtherPurpose.setVisibility(View.VISIBLE);
//                    edtOtherPurpose.setText("other text");
                }

                arrayListPurpose.add(spinnerObj);
//                purposeIds.add(purposeOfActivityData.get(i).getId());
            }
            checkBox2.setText(purposeOfActivityData.get(i).getTitle());
            checkBox2.setButtonDrawable(R.drawable.checkbox_selector);
            final int finalI = i;

            checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
//                        purposeIds.add(purposeOfActivityData.get(finalI).getId());

                        arrayListPurpose.add(spinnerObj);
                        if(purposeOfActivityData.get(finalI).getTitle().equalsIgnoreCase("Other"))
                        {
                            CommonMethods.expand(edtOtherPurpose);
//                            edtOtherPurpose.setVisibility(View.VISIBLE);
                        }
                    } else {
                        arrayListPurpose.remove(arrayListPurpose.indexOf(spinnerObj));
//                        purposeIds.remove(purposeIds.indexOf(purposeOfActivityData.get(finalI).getId()));
                        if(purposeOfActivityData.get(finalI).getTitle().equalsIgnoreCase("Other"))
                        {
                            CommonMethods.collapse(edtOtherPurpose);
//                            edtOtherPurpose.setVisibility(View.GONE);
                        }
                    }
                }
            });
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alerts.showOkWhatMessage(purposeOfActivityData.get(finalI).getHelpString());
                }
            });

            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params2.setMargins(30, 20, 20, 0);
            linearLayoutPurpopse.addView(view);
        }
    }



    public void listActivity() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < activityData.size(); i++) {
            View view = inflater.inflate(R.layout.layout_checklist, null, false);
            RelativeLayout relativeLayout1 = new RelativeLayout(getActivity());
            final CheckBox checkBox1 = (CheckBox) view.findViewById(R.id.cb_check);
            final ImageView imageView1 = (ImageView) view.findViewById(R.id.iv_ic_quest);

            final spinnerObject spinnerObj;
            spinnerObj = new spinnerObject();
            spinnerObj.setId(activityData.get(i).getId());
            spinnerObj.setName(activityData.get(i).getTitle());

            if (checkedActivityIds != null && checkedActivityIds.contains(String.valueOf(activityData.get(i).getId()))) {
//                activityIds.add(activityData.get(i).getId());

                if(spinnerObj.getName().equalsIgnoreCase("Other"))
                {
                    edtOtherActivity.setVisibility(View.VISIBLE);
//                    edtOtherActivity.setText("other text");
                }

                arrayListActivity.add(spinnerObj);
                checkBox1.setChecked(true);
            }
            checkBox1.setText(activityData.get(i).getTitle());
            final int finalI = i;
            checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        arrayListActivity.add(spinnerObj);
//                        activityIds.add(activityData.get(finalI).getId());
//                        arrayListActivity.add()
                        if(activityData.get(finalI).getTitle().equalsIgnoreCase("Other"))
                        {
                            CommonMethods.expand(edtOtherActivity);
//                            edtOtherActivity.setVisibility(View.VISIBLE);
                        }
                    } else {
                        arrayListActivity.remove(arrayListActivity.indexOf(spinnerObj));
//                        activityIds.remove(activityIds.indexOf(activityData.get(finalI).getId()));
                        if(activityData.get(finalI).getTitle().equalsIgnoreCase("Other"))
                        {
                            CommonMethods.collapse(edtOtherActivity);
//                            edtOtherActivity.setVisibility(View.GONE);
                        }
                    }
                }
            });

            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alerts.showOkWhatMessage(activityData.get(finalI).getHelpString());
                }
            });

            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params1.setMargins(20, 20, 60, 0);

            linearLayoutActivity.addView(view);

        }
    }

    public void requestSaveProgress(String result) {
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            if (response_int == 0) {
                JSONObject engagementData = jObjResult.getJSONObject("engagementEvidenceData");
                Toast.makeText(this.cntx, "Progress saved", Toast.LENGTH_LONG).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        this.cntx = activity;
    }

    public void listIndustryengagement(String result) {
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            if (response_int == 0) {
                JSONArray JSONlist = jObjResult.getJSONArray("industryEngagementData");
                final JSONObject jObject = JSONlist.getJSONObject(0);
                EngagementEvidenceId = jObject.getString("Engagement_Evidence_Id");
                IndustryEngagementId = jObject.getString("Industry_Engagement_Id");
                edtTitleReference.setText(jObject.getString("Reference"));

                edtOtherActivity.setText(jObject.getString("Activity_Text"));
                edtOtherPurpose.setText(jObject.getString("Purpose_Activity_Text"));

                String[] parts = jObject.getString("Datetime").split(" ");
                System.out.println("Rabin is testing: " + parts.toString());

                SimpleDateFormat dt2 = new SimpleDateFormat("MMM dd,yyyy");
                SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
                Date dt;
                try {
                    dt = dt1.parse(parts[0]);
                    String newFormat = dt2.format(dt);
                    System.out.println(newFormat);
                    edtDate.setText(newFormat);
                    selectedDate = parts[0];
                } catch (Exception exx) {
                    exx.printStackTrace();
                }

                SimpleDateFormat tf1 = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat tf2 = new SimpleDateFormat("hh:mm aa");

                try {
                    dt = tf1.parse(parts[1]);
                    String newFormat = tf2.format(dt);
                    System.out.println(newFormat);
                    edtTime.setText(newFormat);
                    selectedTime = parts[1];
                } catch (Exception exx) {
                    exx.printStackTrace();
                }

                edtNoOfHours.setText(jObject.getString("Hours"));
                cuurentHours = Integer.parseInt(jObject.getString("Hours"));
                edtVenue.setText(jObject.getString("Venue"));
                //activity checked list
                JSONObject jactivityData = jObjResult.getJSONObject("activityData");
                if (jactivityData.getString("Activity_Id") != "false") {
                    JSONArray jCheckedData = jactivityData.getJSONArray("Activity_Id");

                    for (int i = 0; i < jCheckedData.length(); i++) {
                        checkedActivityIds.add(jCheckedData.getString(i));
                    }
                }
                //purpose checked list

                JSONObject jpurposeData = jObjResult.getJSONObject("purposeActivityData");
                if (jpurposeData.getString("Purpose_Activity_Id") != "false") {
                    JSONArray jData = jpurposeData.getJSONArray("Purpose_Activity_Id");
                    for (int i = 0; i < jData.length(); i++) {
//                    String temp = jCheckedData.getString(i);
                        checkedPurposeIds.add(jData.getString(i));
                    }
                }

                spinnerAccred.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int m = 0; m < arrayListAccred.size(); m++) {
                                if (arrayListAccred.get(m).getId() == Integer.parseInt(jObject.getString("Accreditation_Id"))) {

                                    int selectionPosition = spinnerAdapterAccred.getPosition(arrayListAccred.get(m).getName());
                                    spinnerAccred.setSelection(selectionPosition);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                spinnerFindOut.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int m = 0; m < arrayListFindOut.size(); m++) {
                                if (arrayListFindOut.get(m).getId() == Integer.parseInt(jObject.getString("Findout_Id"))) {

                                    int selectionPosition = spinnerAdapterFindOut.getPosition(arrayListFindOut.get(m).getName());
                                    spinnerFindOut.setSelection(selectionPosition);
                                    if (arrayListFindOut.get(m).getName().equalsIgnoreCase("Other - Specify")) {
                                        edtFindOut.setVisibility(View.VISIBLE);
                                        edtFindOut.setText(jObject.getString("Findout_Other_Text"));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            loadFragment();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void industryEngagementAddEdit(String result) {
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            if (response_int == 0) {
                JSONObject engagementData = jObjResult.getJSONObject("industryEngagementData");
                String industryEngagementId = engagementData.getString("Industry_Engagement_Id");
                String Engagement_Evidence_Id = engagementData.getString("Engagement_Evidence_Id");

                sharedPreference.setKeyValues("Industry_Engagement_Id", industryEngagementId);
                sharedPreference.setKeyValues("Engagement_Evidence_Id", Engagement_Evidence_Id);
                //  EngagementEvidenceId = Engagement_Evidence_Id;

                engagementEvidenceData.setEngagementEvidenceId(Integer.parseInt(Engagement_Evidence_Id));
                engagementEvidenceData.setIndustryEngagementId(Integer.parseInt(industryEngagementId));

                if (!EngagementEvidenceId.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Step 1 information updated", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Step 1 information saved", Toast.LENGTH_LONG).show();
                }

                NewIndustyActivity newIndustyActivity = new NewIndustyActivity();
                System.out.println("prajit is testing step 1: " + sharedPreference.getIntValues("Current_Step"));
                if (sharedPreference.getIntValues("Current_Step") < 1) {
                    sharedPreference.setKeyValues("Current_Step", 1);
                    MyPageAdapter adapter = (MyPageAdapter) newIndustyActivity.mPager.getAdapter();
                    newIndustyActivity.currentStep = 1;
                    newIndustyActivity.mPager.getAdapter().notifyDataSetChanged();
                    adapter.currentPage(1);
                }
                newIndustyActivity.mPager.setCurrentItem(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void listFindOut() {
        for (int i = 0; i < findOutData.size(); i++) {
            spinnerObject spinnerObject = new spinnerObject();
            findOutList.add(findOutData.get(i).getTitle());

            spinnerObject.setId(findOutData.get(i).getId());
            spinnerObject.setName(findOutData.get(i).getTitle());

            arrayListFindOut.add(spinnerObject);
        }
        //Set FindOut Spinner
        spinnerAdapterFindOut = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, findOutList);
// Specify the layout to use when the list of choices appears
        spinnerAdapterFindOut.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerFindOut.setAdapter(spinnerAdapterFindOut);

        flagFindOut = true;
        if (flagAccred && flagFindOut) {
            if (flag == 1) {//load from existing industry
                //   loadData();
            } else {
                // loadFragment();
            }
            flagFindOut = false;
        }

    }

    public void listAccred(String result) {
        JSONObject jsonResult;
        try {
            jsonResult = new JSONObject(result);
            int response = jsonResult.getInt("response");
            AccredList = new ArrayList<String>();
            if (response == 0) {
                JSONArray jArrayResult = jsonResult.getJSONArray("teacher");
                for (int i = 0; i < jArrayResult.length(); i++) {
                    spinnerObject spinnerObject = new spinnerObject();
                    JSONObject jArrayObject = jArrayResult.getJSONObject(i);
                    AccredList.add(jArrayObject.getString("accreditationName"));

                    spinnerObject.setId(Integer.parseInt(jArrayObject.getString("accreditationID")));
                    spinnerObject.setName(jArrayObject.getString("accreditationName"));

                    arrayListAccred.add(spinnerObject);
                }
                //Set FindOut Spinner
                spinnerAdapterAccred = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, AccredList);
// Specify the layout to use when the list of choices appears
                spinnerAdapterAccred.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the spinnerAdapterAccred to the spinner
                spinnerAccred.setAdapter(spinnerAdapterAccred);

                flagAccred = true;
                if (flag == 1) {//load from existing industry
                    loadData();
                } else {
                    loadFragment();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
