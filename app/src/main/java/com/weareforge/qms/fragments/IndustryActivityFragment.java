package com.weareforge.qms.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.weareforge.qms.Objects.EngagementEvidenceData;
import com.weareforge.qms.R;
import com.weareforge.qms.activities.NewIndustyActivity;
import com.weareforge.qms.adapters.MyPageAdapter;
import com.weareforge.qms.alerts.Alerts;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.db.EngagementEvidenceDbHandler;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.utils.ConnectionMngr;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

/**
 * Created by Admin on 1/7/2016.
 */
public class IndustryActivityFragment extends Fragment {

    private Button btnSave;
    private TextView tvFragmentTitle;
    private TextView tvDetails;
    private TextView tvFeedback;
    private TextView tvSkills;
    private TextView tvFuture;
    private TextView tvTraining;
    private TextView tvGeneral;
    private TextView tvBestPractice;
    private TextView tvRating;
    private TextView tvAreas;

    private String save ="Saving...";

    @NotEmpty(messageId = R.string.err_msg_details, order = 1)
    private EditText edtDetails;

    @NotEmpty(messageId = R.string.err_msg_feedback, order = 2)
    private EditText edtFeedback;

    @NotEmpty(messageId = R.string.err_msg_skills, order = 3)
    private EditText edtSkills;

    @NotEmpty(messageId = R.string.err_msg_future, order = 4)
    private EditText edtFuture;

    @NotEmpty(messageId = R.string.err_msg_training, order = 5)
    private EditText edtTraining;

    @NotEmpty(messageId = R.string.err_msg_general, order = 6)
    private EditText edtGeneral;

    @NotEmpty(messageId = R.string.err_msg_best_practice, order = 7)
    private EditText edtBestPractice;

    @NotEmpty(messageId = R.string.errr_msg_rating, order = 8)
    private EditText edtRating;

    @NotEmpty(messageId = R.string.err_msg_areas, order = 9)
    private EditText edtAreas;

    private FontHelper fontHelper;
    private VolleyRequest vsr;
    private UrlHelper urlHelper;
    private SharedPreference sharedPreference;
    private ConnectionMngr connectionMngr;
    private HashMap<String, String> params;
    private Alerts alerts;
    private EngagementEvidenceDbHandler dbHelper;

    private EngagementEvidenceData engagementEvidenceData;

    private String userid;
    private String token;
    private JSONObject jsonObject;
    private int flag;
    private String Industry_Engagement_Id;
    private String industry_activity_feedback_id;
    private String Engagement_Evidence_Id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_industry_activity, container, false);
     //   CommonMethods.setupUI(rootView.findViewById(R.id.relativeLayoutIndustryActivity), getActivity());

        this.tvFragmentTitle = (TextView) rootView.findViewById(R.id.tvFragmentTitle);
        this.tvDetails = (TextView) rootView.findViewById(R.id.tvDetails);
        this.tvFeedback = (TextView) rootView.findViewById(R.id.tvFeedback);
        this.tvSkills = (TextView) rootView.findViewById(R.id.tvSkills);
        this.tvFuture = (TextView) rootView.findViewById(R.id.tvFuture);
        this.tvTraining = (TextView) rootView.findViewById(R.id.tvTraining);
        this.tvGeneral = (TextView) rootView.findViewById(R.id.tvTraining);
        this.tvBestPractice = (TextView) rootView.findViewById(R.id.tvBestPractice);
        this.tvRating = (TextView) rootView.findViewById(R.id.tvRating);
        this.tvAreas = (TextView) rootView.findViewById(R.id.tvAreas);

        this.edtDetails = (EditText) rootView.findViewById(R.id.edtDetails);
        this.edtFeedback = (EditText) rootView.findViewById(R.id.edtFeedback);
        this.edtSkills = (EditText) rootView.findViewById(R.id.edtSkills);
        this.edtFuture = (EditText) rootView.findViewById(R.id.edtFuture);
        this.edtTraining = (EditText) rootView.findViewById(R.id.edtTraining);
        this.edtGeneral = (EditText) rootView.findViewById(R.id.edtGeneral);
        this.edtBestPractice= (EditText) rootView.findViewById(R.id.edtBestPractice);
        this.edtRating= (EditText) rootView.findViewById(R.id.edtRating);
        this.edtAreas = (EditText) rootView.findViewById(R.id.edtAreas);

        this.btnSave = (Button) rootView.findViewById(R.id.btnSave);

        this.connectionMngr = new ConnectionMngr(getActivity());
        this.alerts = new Alerts(getActivity());
        this.engagementEvidenceData = new EngagementEvidenceData();
        this.dbHelper = new EngagementEvidenceDbHandler(getActivity());

        this.sharedPreference = new SharedPreference(getActivity());
        this.token = sharedPreference.getStringValues("token");
        this.userid = sharedPreference.getStringValues("userid");
        flag = sharedPreference.getIntValues("flag");

        this.Engagement_Evidence_Id = sharedPreference.getStringValues("Engagement_Evidence_Id");
        this.Industry_Engagement_Id = sharedPreference.getStringValues("Industry_Engagement_Id");
        this.industry_activity_feedback_id = sharedPreference.getStringValues("Industry_Activity_Feedback_Id");

        //Font Helper
        fontHelper = new FontHelper(getActivity());
        this.tvFragmentTitle.setTypeface(fontHelper.getDefaultFont());
        this.tvDetails.setTypeface(fontHelper.getDefaultFont());
        this.tvFeedback.setTypeface(fontHelper.getDefaultFont());
        this.tvSkills.setTypeface(fontHelper.getDefaultFont());
        this.tvFuture.setTypeface(fontHelper.getDefaultFont());
        this.tvTraining.setTypeface(fontHelper.getDefaultFont());
        this.tvGeneral.setTypeface(fontHelper.getDefaultFont());
        this.tvBestPractice.setTypeface(fontHelper.getDefaultFont());
        this.tvRating.setTypeface(fontHelper.getDefaultFont());
        this.tvAreas.setTypeface(fontHelper.getDefaultFont());
        this.tvRating.setTypeface(fontHelper.getDefaultFont());
        this.edtDetails.setTypeface(fontHelper.getDefaultFont());
        this.edtFeedback.setTypeface(fontHelper.getDefaultFont());
        this.edtSkills.setTypeface(fontHelper.getDefaultFont());
        this.edtFuture.setTypeface(fontHelper.getDefaultFont());
        this.edtTraining.setTypeface(fontHelper.getDefaultFont());
        this.edtGeneral.setTypeface(fontHelper.getDefaultFont());
        this.edtBestPractice.setTypeface(fontHelper.getDefaultFont());
        this.edtRating.setTypeface(fontHelper.getDefaultFont());
        this.edtAreas.setTypeface(fontHelper.getDefaultFont());
        this.btnSave.setTypeface(fontHelper.getDefaultFont());

        if(flag==1) {
            loadData();
        }

        this.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                SaveAndNext();
            }
        });
        return rootView;
    }

    private void loadData() {
        if(connectionMngr.hasConnection()) {
            params = new HashMap<String, String>();
            params.put("token", token);
            params.put("userid", userid);
            params.put("engagement_evidence_id",String.valueOf(Engagement_Evidence_Id));

            vsr = new VolleyRequest(getActivity(), Request.Method.POST, params , urlHelper.INDUSTRY_ACTIVIY_FEEDBACK_LIST,false , CommonDef.REQUEST_INDUSTRY_ACTIVITY_FEEDBACK_LIST,getResources().getString(R.string.loadingFromQMS));
            vsr.asyncInterface = (NewIndustyActivity) getActivity();
            vsr.request();
        }
    }

    private void SaveAndNext() {
        if(!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {
            final boolean isValid = FormValidator.validate(IndustryActivityFragment.this, new SimpleErrorPopupCallback(getActivity(), true));
            if (isValid) {
                if (connectionMngr.hasConnection()) {
                    params = new HashMap<String, String>();
                    params.put("token", token);
                    params.put("userid", userid);
                    if(!industry_activity_feedback_id.equalsIgnoreCase(""))
                    {
                        params.put("industry_activity_feedback_id", industry_activity_feedback_id);
                        this.save = "Updating...";
                    }
                    params.put("engagement_evidence_id", String.valueOf(sharedPreference.getStringValues("Engagement_Evidence_Id")));
                    params.put("description", edtDetails.getText().toString());
                    params.put("feedback_industry", edtFeedback.getText().toString());
                    params.put("skills", edtSkills.getText().toString());
                    params.put("future", edtFuture.getText().toString());
                    params.put("training", edtTraining.getText().toString());
                    params.put("general", edtGeneral.getText().toString());
                    params.put("best_practice_identified", edtBestPractice.getText().toString());
                    params.put("rating", edtRating.getText().toString());
                    params.put("area", edtAreas.getText().toString());

                    vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, urlHelper.INDUSTRY_ACTIVITY_FEEDBACK, false, CommonDef.REQUEST_INDUSTRY_ACTIVITY_FEEDBACK_UPSERT,this.save);
                    vsr.asyncInterface = (NewIndustyActivity) getActivity();
                    vsr.request();
                }
            }
        }
        else
            alerts.showCreateEvidenceOkMessage("Please create evidence before proceed");
    }

    public void industryActivityList(String result) {
        try {
            jsonObject = new JSONObject(result);
            String response = jsonObject.getString("response");
            String responseText = jsonObject.getString("responseText");
            int response_int = Integer.parseInt(response);
            if(response_int==0) {
                JSONArray JSONlist = jsonObject.getJSONArray("industryActivityFeedback");
                JSONObject jObject = JSONlist.getJSONObject(0);
                edtDetails.setText(jObject.getString("Description"));
                edtFeedback.setText(jObject.getString("Feedback_Industry"));
                edtSkills.setText(jObject.getString("Skills"));
                edtFuture.setText(jObject.getString("Future"));
                edtTraining.setText(jObject.getString("Training"));
                edtGeneral.setText(jObject.getString("General"));
                edtBestPractice.setText(jObject.getString("Best_Practice_Identified"));
                edtRating.setText(jObject.getString("Rating"));
                edtAreas.setText(jObject.getString("Area"));
                industry_activity_feedback_id = jObject.getString("Industry_Activity_Feedback_Id");
                sharedPreference.setKeyValues("Industry_Activity_Feedback_Id", industry_activity_feedback_id);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void industryActivityUpsert(String result) {
        try {
            jsonObject = new JSONObject(result);
            String response = jsonObject.getString("response");
            String responseText = jsonObject.getString("responseText");
            int response_int = Integer.parseInt(response);
            if(response_int==0) {
                JSONObject industryActivityFeedback = jsonObject.getJSONObject("industryActivityFeedback");
                String IndustryActiviyFeedbackId = industryActivityFeedback.getString("Industry_Activity_Feedback_Id");

                sharedPreference.setKeyValues("Industry_Activity_Feedback_Id", IndustryActiviyFeedbackId);

                if(industry_activity_feedback_id.equalsIgnoreCase(""))
                {
                    Toast.makeText(getActivity(), "Step 3 information saved", Toast.LENGTH_LONG).show();
                    industry_activity_feedback_id = IndustryActiviyFeedbackId;
                }
                else
                {
                    Toast.makeText(getActivity(), "Step 3 information updated", Toast.LENGTH_LONG).show();
                }
                NewIndustyActivity newIndustyActivity = new NewIndustyActivity();
                if(sharedPreference.getIntValues("Current_Step") < 3)
                {
                    sharedPreference.setKeyValues("Current_Step",3);
                    MyPageAdapter adapter = (MyPageAdapter)newIndustyActivity.mPager.getAdapter();
                    newIndustyActivity.currentStep = 3;
                    newIndustyActivity.mPager.getAdapter().notifyDataSetChanged();
                    adapter.currentPage(3);
                }
                newIndustyActivity.mPager.setCurrentItem(3);
            }
            else
            {
             //   Toast.makeText(getActivity(),responseText,Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
}
