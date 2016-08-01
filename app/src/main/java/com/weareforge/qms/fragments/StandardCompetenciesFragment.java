package com.weareforge.qms.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.android.volley.Request;
import com.weareforge.qms.Objects.OfflineData;
import com.weareforge.qms.Objects.UploadedImage;
import com.weareforge.qms.R;
import com.weareforge.qms.activities.NewIndustyActivity;
import com.weareforge.qms.alerts.Alerts;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.db.EngagementEvidenceOfflineContent;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.CommonMethods;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.helpers.Util;
import com.weareforge.qms.interfaces.AsyncInterface;
import com.weareforge.qms.utils.ConnectionMngr;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.Opener;
import com.weareforge.qms.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

/**
 * Created by Prajeet on 1/7/2016.
 */
public class StandardCompetenciesFragment extends Fragment {

    private Button btnSave;
    private TextView tvASAQ;
    private TextView tvCompentencies;
    private TextView tvMeasure;
    private TextView tvHaveYou;
    private TextView tvFragmentTitle;
    private TextView tvMoreInfo;
    private TextView tvClickHere;

    private Button btnViewTAS;

    @NotEmpty(messageId = R.string.err_msg_have_you, order = 1)
    private EditText edtHaveYou;

    private LinearLayout linearLayoutStandard;
    private LinearLayout linearLayoutCompetencies;

    private FontHelper fontHelper;

    private HashMap<String, String> ASAQParams;
    private HashMap<String, String> CompetenciesParams;
    private HashMap<String, String> params;

    private ArrayList<String> checkedlistASAQ;
    private ArrayList<String> checkedCompentencies;

    private static ArrayList<String> checkedStandardIds;
    private static ArrayList<String> checkedCompetenciesIds;

    private HashMap<String, String> competencyKeyValue;

    private VolleyRequest vsrASAQ;
    private VolleyRequest vsrCompetencies;
    private VolleyRequest vsr;


    private SharedPreference sharedPreference;
    private UrlHelper urlHelper;
    private ConnectionMngr connectionMngr;
    private Alerts alerts;
    private Opener opener;

    private EngagementEvidenceOfflineContent dbOfflineDataHelper;
    private ArrayList<OfflineData> asaqStandardData;

    private String token;
    private String userid;
    private String EngagementEvidenceId;
    private String standardCompetenciesId = "";

    private String tasPathId = "";
    private String filepath = "";

    private String save="Saving...";

    private ProgressDialog progressDialog;
    // A List of all transfers
    private TransferUtility transferUtility;
    //S3 client
    public AmazonS3Client s3Client;

    private int flag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_standard_competencies, container, false);
        // CommonMethods.setupUI(rootView.findViewById(R.id.relativeLayoutStandardCompetencies), getActivity());

        this.tvASAQ = (TextView) rootView.findViewById(R.id.tvASQA);
        this.tvCompentencies = (TextView) rootView.findViewById(R.id.tvCompetencies);
        this.tvMeasure = (TextView) rootView.findViewById(R.id.tvMeasure);
        this.tvHaveYou = (TextView) rootView.findViewById(R.id.tvHaveYou);
        this.edtHaveYou = (EditText) rootView.findViewById(R.id.edtHaveYou);
        this.tvFragmentTitle = (TextView) rootView.findViewById(R.id.tvFragmentTitle);
        this.tvMoreInfo = (TextView) rootView.findViewById(R.id.tvMoreInfo);
        this.btnViewTAS = (Button) rootView.findViewById(R.id.btnView);
        this.btnSave = (Button) rootView.findViewById(R.id.btnSave);
        this.tvClickHere = (TextView) rootView.findViewById(R.id.tvClickhere);

        this.linearLayoutCompetencies = (LinearLayout) rootView.findViewById(R.id.linearLayoutCompetencies);
        this.linearLayoutStandard = (LinearLayout) rootView.findViewById(R.id.linearLayoutStandard);

        //Font Helper
        fontHelper = new FontHelper(getActivity());
        this.tvASAQ.setTypeface(fontHelper.getDefaultFont());
        this.tvCompentencies.setTypeface(fontHelper.getDefaultFont());
        this.tvMeasure.setTypeface(fontHelper.getDefaultFont());
        this.tvHaveYou.setTypeface(fontHelper.getDefaultFont());
        this.tvFragmentTitle.setTypeface(fontHelper.getDefaultFont());
        this.edtHaveYou.setTypeface(fontHelper.getDefaultFont());
        this.btnSave.setTypeface(fontHelper.getDefaultFont());
        this.btnViewTAS.setTypeface(fontHelper.getDefaultFont());
        this.tvMoreInfo.setTypeface(fontHelper.getDefaultFont());
        this.tvClickHere.setTypeface(fontHelper.getDefaultFont());

        connectionMngr = new ConnectionMngr(getActivity());
        sharedPreference = new SharedPreference(getActivity());
        opener = new Opener(getActivity());

        alerts = new Alerts(getActivity());
        token = sharedPreference.getStringValues("token");
        userid = sharedPreference.getStringValues("userid");
        EngagementEvidenceId = sharedPreference.getStringValues("Engagement_Evidence_Id");

        flag = sharedPreference.getIntValues("flag");
        //  standardCompetenciesId = sharedPreference.getStringValues("Standard_Competency_Id");
        dbOfflineDataHelper = new EngagementEvidenceOfflineContent(getActivity());

        dbOfflineDataHelper.Open();
        asaqStandardData = dbOfflineDataHelper.GetASAQStandard();

        checkedlistASAQ = new ArrayList<String>();
        checkedCompentencies = new ArrayList<String>();
        checkedStandardIds = new ArrayList<String>();
        checkedCompetenciesIds = new ArrayList<String>();

        s3Client = Util.getS3Client(getActivity());
        transferUtility = Util.getTransferUtility(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        competencyKeyValue = new HashMap<String, String>();

        //   loadFragment();
        if (flag == 1) {
            loadData();
        } else
            loadFragment();

        this.tvClickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opener.MoreInfoASAQ();
            }
        });

        this.btnViewTAS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(!tasPathId.equalsIgnoreCase("")  && UploadedImage.filepath.equalsIgnoreCase(""))
                {
                    ListObject listObject = new ListObject();
                    listObject.execute();
                }

                if (!UploadedImage.filepath.equals(null) && !UploadedImage.filepath.equalsIgnoreCase("")) {

                    String extension = CommonMethods.getExtension(UploadedImage.filepath);
                    if(extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {

                        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.alert_display_image);
                        ImageView img = (ImageView) dialog.findViewById(R.id.displayImage);
                        File file = new File(UploadedImage.filepath);
                        img.setImageURI(Uri.parse("file://" + UploadedImage.filepath));
                        ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
                        cross.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setCancelable(true);

                        dialog.show();
                    }
                    else if(extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mpeg"))
                    {
                        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.alert_play_video);
                        final VideoView video = (VideoView) dialog.findViewById(R.id.playVideo);
                        File file = new File(UploadedImage.filepath);
                        video.setVideoPath(UploadedImage.filepath);
                        video.start();
                        ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
                        cross.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                    else
                    {
                        File file = new File(UploadedImage.filepath);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + UploadedImage.filepath), "application/pdf");
                        // System.out.println("path==" + (String) data + "--" + file.getAbsolutePath());
                        startActivity(intent);
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if (!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {
                    final boolean isValid = FormValidator.validate(StandardCompetenciesFragment.this, new SimpleErrorPopupCallback(getActivity(), true));
                    if (isValid) {
                        if (connectionMngr.hasConnection()) {

                            params = new LinkedHashMap<String, String>();
                            params.put("token", token);
                            params.put("userid", userid);
                            params.put("engagement_evidence_id", sharedPreference.getStringValues("Engagement_Evidence_Id"));
                            if (!standardCompetenciesId.equalsIgnoreCase("")) {
                                params.put("standard_competency_id", sharedPreference.getStringValues("Standard_Competency_Id"));
                                save = "Updating...";
                            }
                            for (int i = 0; i < checkedlistASAQ.size(); i++) {
                                params.put("asqa_standard_id[" + i + "]", checkedlistASAQ.get(i));
                            }

                            for (int i = 0; i < checkedCompentencies.size(); i++) {
                                params.put("competency_id[" + i + "]", checkedCompentencies.get(i));
                            }

                            for (int i = 0; i < checkedCompentencies.size(); i++) {
                                EditText editText = (EditText) rootView.findViewWithTag(checkedCompentencies.get(i));
                                params.put("competency_description_array[" + checkedCompentencies.get(i) + "]", editText.getText().toString());
                            }
                            params.put("goal_achievement_detail", edtHaveYou.getText().toString());

                            vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, urlHelper.STANDARD_COMPENTENCIES_UPSERT, false, CommonDef.REQUEST_UPSERT_STANDARD_COMPETENCIES,save);
                            vsr.asyncInterface = (NewIndustyActivity) getActivity();
                            vsr.request();

                        }
                    }
                } else
                    alerts.showCreateEvidenceOkMessage("Please create evidence before proceed");
            }
        });


        return rootView;
    }

    public void loadData() {
        if (connectionMngr.hasConnection()) {
            params = new HashMap<String, String>();
            params.put("token", token);
            params.put("userid", userid);
            params.put("engagement_evidence_id", EngagementEvidenceId);
            vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, urlHelper.LIST_STANDARD_COMPETENCIES, false, CommonDef.REQUEST_LIST_STANDARD_COMPETENCIES,getResources().getString(R.string.loadingFromQMS));
            vsr.asyncInterface = (NewIndustyActivity) getActivity();
            vsr.request();
        }else
        {
            listASAQ();
        }
    }

    private void loadFragment() {
        listASAQ();
        if (connectionMngr.hasConnection()) {
            CompetenciesParams = new HashMap<String, String>();

            CompetenciesParams.put("token", token);
            CompetenciesParams.put("userid", userid);

            vsrCompetencies = new VolleyRequest(getActivity(), Request.Method.POST, CompetenciesParams, urlHelper.MY_DETAILS, false, CommonDef.REQUEST_LIST_COMPETENCIES,getResources().getString(R.string.loadingFromQMS));
            vsrCompetencies.asyncInterface = (NewIndustyActivity) getActivity();
            vsrCompetencies.request();
        }
    }


    public void SaveProgress(int id) {

        String Industry_Engagement_Id = sharedPreference.getStringValues("Industry_Engagement_Id");
        String Feedback_Id = sharedPreference.getStringValues("Industry_Activity_Feedback_Id");
        String Evidence_Id = sharedPreference.getStringValues("Evidence_Id");
        if (!sharedPreference.getStringValues("Standard_Competency_Id").equalsIgnoreCase("")) {
            if (!Industry_Engagement_Id.equalsIgnoreCase("") && !Feedback_Id.equalsIgnoreCase("") && !Evidence_Id.equalsIgnoreCase("")) {
                if (connectionMngr.hasConnection()) {
                    params = new HashMap<String, String>();
                    params.put("token", token);
                    params.put("userid", userid);
                    params.put("engagement_evidence_id", sharedPreference.getStringValues("Engagement_Evidence_Id"));
                    params.put("title", "");
                    params.put("status", "1");
                    if(sharedPreference.getIntValues("Current_step")<5) {
                        params.put("current_step", "5");
                    }
                    else
                    {
                        params.put("current_step",String.valueOf(sharedPreference.getIntValues("Current_step")));
                    }
                    vsr = new VolleyRequest(getActivity(), Request.Method.POST, params, urlHelper.ENGAGEMENT_EVIDENCE, false, CommonDef.REQUEST_SAVE_PROGRESS_STEP_5,getResources().getString(R.string.saveProgress));
                    vsr.asyncInterface = ((NewIndustyActivity) getContext());
                    vsr.request();
                }
                if(standardCompetenciesId.equalsIgnoreCase(""))
                {
                    sharedPreference.setKeyValues("Standard_Competency_Id", id);
                    Toast.makeText(getActivity(), "Your engagement evidence has been submitted successfully.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Your engagement evidence has been submitted successfully.", Toast.LENGTH_LONG).show();
                }
            } else {
                alerts.showOkMessage("Please complete all the process to save the progress");
            }
        } else
            alerts.showOkMessage("Please complete this step before proceed.");
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
                EngagementEvidenceId = String.valueOf(engagementData.getInt("Engagement_Evidence_Id"));
                Toast.makeText(getActivity(), "Progress saved.", Toast.LENGTH_LONG).show();
                sharedPreference.setKeyValues("Current_step",5);
                opener.ExistingIndustry();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void listASAQ() {
        JSONObject jObjResult;
                for (int i = 0; i < asaqStandardData.size(); i++) {
                    RelativeLayout relativeLayout1 = new RelativeLayout(getActivity());
                    CheckBox checkBox2 = new CheckBox(getContext());
                    //setcheckbox
                    if (checkedStandardIds != null && checkedStandardIds.contains(String.valueOf(asaqStandardData.get(i).getId()))) {
                        checkedlistASAQ.add(String.valueOf(asaqStandardData.get(i).getId()));
                        checkBox2.setChecked(true);
                    }
                    checkBox2.setText(asaqStandardData.get(i).getTitle());
                    checkBox2.setTypeface(fontHelper.getDefaultFont());
                    checkBox2.setButtonDrawable(R.drawable.checkbox_selector);
                    checkBox2.setPadding(60, 0, 0, 0);
                    final int finalI = i;
                    checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                checkedlistASAQ.add(String.valueOf(asaqStandardData.get(finalI).getId()));
                            } else {
                                checkedlistASAQ.remove(checkedlistASAQ.indexOf(String.valueOf(asaqStandardData.get(finalI).getId())));
                            }
                        }
                    });

                    RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    params2.setMargins(0, 20, 40, 0);

                    relativeLayout1.addView(checkBox2, params2);

                    linearLayoutStandard.addView(relativeLayout1);
                }
            }



    public void listCompentencies(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            linearLayoutCompetencies.removeAllViews();
            checkedCompentencies = new ArrayList<String>();
            if (response_int == 0) {
                JSONArray listArrayActivity = jObjResult.getJSONArray("teacher");
                final JSONObject listActivity = listArrayActivity.getJSONObject(IndustryEngagementFragment.accredId);
                JSONArray compentencies = listActivity.getJSONArray("unitsOfCompetency");
                for (int i = 0; i < compentencies.length(); i++) {
                    final JSONObject competency = compentencies.getJSONObject(i);
                    LinearLayout linearLayout = new LinearLayout(getContext());
                    CheckBox checkBox1 = new CheckBox(getContext());

                    //SetEditText
                    final EditText editText = new EditText(getContext());
                    editText.setTag(competency.getString("training_package_code"));
                    editText.setBackground(getResources().getDrawable(R.drawable.rounded_edittext));
                    editText.setVisibility(View.GONE);
                    editText.setLines(3);
                    editText.setGravity(Gravity.TOP | Gravity.LEFT);
                    editText.setPadding(CommonMethods.DpToPx(getContext(), 10), 0, 0, 0);
                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, CommonMethods.DpToPx(getContext(), 100));
                    params1.setMargins(0, 20, 40, 20);

                    //setcheckbox
                    if (checkedCompetenciesIds != null && checkedCompetenciesIds.contains(competency.getString("training_package_code"))) {
                        checkedCompentencies.add(competency.getString("training_package_code"));
                        checkBox1.setChecked(true);
                        editText.setVisibility(View.VISIBLE);
                        editText.setText(competencyKeyValue.get(competency.getString("training_package_code")));
                    }
                    checkBox1.setText(competency.getString("unit_of_competency"));
                    checkBox1.setTypeface(fontHelper.getDefaultFont());
                    checkBox1.setButtonDrawable(R.drawable.checkbox_selector);
                    checkBox1.setPadding(60, 0, 0, 0);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //   params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    params.setMargins(0, 20, 40, 20);

                    //Checkbox ClickListner
                    checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                try {
                                    checkedCompentencies.add(competency.getString("training_package_code"));
                                    CommonMethods.expand(editText);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    checkedCompentencies.remove(checkedCompentencies.indexOf(competency.getString("training_package_code")));
                                    CommonMethods.collapse(editText);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.addView(checkBox1, params);
                    linearLayout.addView(editText, params1);

                    linearLayoutCompetencies.addView(linearLayout);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void upsertStandardCompitencies(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            if (response.equalsIgnoreCase("0")) {
                JSONObject standardAndCompetencyData = jObjResult.getJSONObject("standardAndCompetencyData");
                sharedPreference.setKeyValues("Standard_Competency_Id", standardAndCompetencyData.getString("Standard_Competency_Id"));
                standardCompetenciesId = standardAndCompetencyData.getString("Standard_Competency_Id");
                SaveProgress(Integer.parseInt(standardCompetenciesId));
              }
        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }


    public void loadStandardCompetenciesData(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
          //  Toast.makeText(getActivity(), responseText, Toast.LENGTH_LONG).show();
            checkedStandardIds = new ArrayList<String>();
            checkedCompetenciesIds = new ArrayList<String>();
            Iterator iterator = jObjResult.keys();

            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                //   Log.d("KEY", key);

                if (key.equals("tasFileId")) {
                    if (!jObjResult.getString("tasFileId").equalsIgnoreCase("false")) {
                        tasPathId = jObjResult.getString("tasFileId");
                    }
                }
            }
            if (response.equalsIgnoreCase("0")) {
                if (!jObjResult.getString("standardAndCompetencyData").equalsIgnoreCase("")) {
                    JSONArray standardAndCompetencyData = jObjResult.getJSONArray("standardAndCompetencyData");
                    JSONObject standardData = standardAndCompetencyData.getJSONObject(0);
                    edtHaveYou.setText(standardData.getString("Goal_Achievement_Detail"));
                    standardCompetenciesId = standardData.getString("Standard_Competency_Id");
                    sharedPreference.setKeyValues("Standard_Competency_Id", standardData.getString("Standard_Competency_Id"));

                    //activity checked list
                    Iterator iterator1 = jObjResult.keys();

                    while (iterator1.hasNext()) {
                        String key = (String) iterator1.next();
                        Log.d("KEY", key);

                        if (key.equals("asqaStandardData")) {

                            JSONObject asqaStandardData = jObjResult.getJSONObject("asqaStandardData");
                            if (asqaStandardData.getString("ASQA_Standard_Id") != "false") {
                                JSONArray jCheckedData = asqaStandardData.getJSONArray("ASQA_Standard_Id");

                                for (int i = 0; i < jCheckedData.length(); i++) {
                                    checkedStandardIds.add(jCheckedData.getString(i));
                                }
                            }
                        }
                    }
                    if (jObjResult.getString("competencyData") != "") {
                        JSONArray competencyData = jObjResult.getJSONArray("competencyData");
                        for (int i = 0; i < competencyData.length(); i++) {
                            JSONObject competenciesData = competencyData.getJSONObject(i);
                            checkedCompetenciesIds.add(competenciesData.getString("Competency_Id"));
                            competencyKeyValue.put(competenciesData.getString("Competency_Id"), competenciesData.getString("Description"));
                        }
                    }

                }
                loadFragment();
            }

        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }

    public void downloadTas() {

    }

    //Download the images from AWS and view images.
    class ListObject extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Downloading...");
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                List<S3ObjectSummary> listobject = s3Client.listObjects(CommonDef.BUCKET_NAME).getObjectSummaries();

                for (S3ObjectSummary image : listobject) {
                    System.out.println("filename==" + image.getKey());

                    if (tasPathId.equalsIgnoreCase(image.getETag())) {
                        S3Object obj = s3Client.getObject(CommonDef.BUCKET_NAME, image.getKey());
                        filepath = obj.getKey();
                        break;
                    }
                }
                // Location to download files from S3 to. You can choose any accessible
                // file.
                File dir = new File(Environment.getExternalStorageDirectory().toString()  + "/QMSDownload");
                if(dir.exists() == false) {
                    dir.mkdirs();
                }
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/QMSDownload/" + filepath);

                UploadedImage.filepath = file.getAbsolutePath();
                File f = new File(UploadedImage.filepath);
                if (f.exists()) {
                    return 1;
                }else{

                    return 0;
                }
                // Initiate the download
            } catch (Exception exx) {
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result==1)
            {
                progressDialog.dismiss();
                UploadedImage.Tasid = tasPathId;
                String extension = CommonMethods.getExtension(UploadedImage.filepath);
                if(extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
                    //  UploadedImage.filepath = filepath;
                    UploadedImage.Tasid = tasPathId;
                    final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_display_image);
                    ImageView img = (ImageView) dialog.findViewById(R.id.displayImage);
                    File file = new File(UploadedImage.filepath);
                    img.setImageURI(Uri.parse("file://" + UploadedImage.filepath));
                    ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    //  dialog.setCancelable(true);
                    dialog.show();
                }
                else if(extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mpeg"))
                {
                    final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_play_video);
                    final VideoView video = (VideoView) dialog.findViewById(R.id.playVideo);
                    video.setVideoPath(UploadedImage.filepath);
                    video.start();
                    ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setCancelable(true);
                    dialog.show();
                }

                else
                {
                    File file = new File(UploadedImage.filepath);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + UploadedImage.filepath), "application/pdf");
                   // System.out.println("path==" + (String) data + "--" + file.getAbsolutePath());
                    startActivity(intent);
                }
            }
            else
            {
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/QMSDownload/" + filepath);
                TransferObserver observer = transferUtility.download(CommonDef.BUCKET_NAME, filepath, file);

                // Add the new download to our list of TransferObservers
                HashMap<String, Object> map = new HashMap<String, Object>();
                // Fill the map with the observers data
                Util.fillMap(map, observer, false);
                // Add the filled map to our list of maps which the simple adapter uses
                observer.setTransferListener(new DownloadListener1());
            }

        }
    }

    /*
  * A TransferListener class that can listen to a download task and be
  * notified when the status changes.
  */
    private class DownloadListener1 implements TransferListener {
        // Simply updates the list when notified.
        @Override
        public void onError(int id, Exception e) {

        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            double temp = (double)bytesCurrent/bytesTotal;
            long Percentage = (long) (temp*100);
            progressDialog.setProgress((int) Percentage);
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if(state.toString().equalsIgnoreCase("COMPLETED"))
            {
                progressDialog.dismiss();
                String extension = CommonMethods.getExtension(UploadedImage.filepath);
                if(extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
                    //  UploadedImage.filepath = filepath;
                    UploadedImage.Tasid = tasPathId;
                    final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_display_image);
                    ImageView img = (ImageView) dialog.findViewById(R.id.displayImage);
                    File file = new File(UploadedImage.filepath);
                    img.setImageURI(Uri.parse("file://" + UploadedImage.filepath));
                    ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    //  dialog.setCancelable(true);
                    dialog.show();
                }
                else if(extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mpeg"))
                {
                    final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_play_video);
                    final VideoView video = (VideoView) dialog.findViewById(R.id.playVideo);
                    video.setVideoPath(UploadedImage.filepath);
                    video.start();
                    ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
                    cross.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setCancelable(true);
                    dialog.show();
                }
                else
                {
                    File file = new File(UploadedImage.filepath);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + UploadedImage.filepath), "application/pdf");
                    // System.out.println("path==" + (String) data + "--" + file.getAbsolutePath());
                    startActivity(intent);
                }
            }

        }
    }
}
