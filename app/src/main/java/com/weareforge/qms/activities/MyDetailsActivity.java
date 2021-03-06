package com.weareforge.qms.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.JsonObject;
import com.weareforge.qms.R;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.interfaces.AsyncInterface;
import com.weareforge.qms.utils.ConnectionMngr;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Admin on 1/6/2016.
 */
public class MyDetailsActivity extends BaseActivity implements AsyncInterface, View.OnClickListener {

    private TextView txtName;
    private TextView txtTeachingNo;
    private TextView txtSchool;
    private TextView txtRTO;
    private TextView txtEmail;
    private TextView tvTeachingNo;
    private TextView tvSchool;
    private TextView tvRTO;
    private TextView tvAccreditations;
    private TextView tvEmail;


    private TextView txtBack;
    private ImageButton btn_back;
    private TextView tvTitle;
    private TextView tvDetailInfo;
    private LinearLayout linearLayoutAccred;


    private SharedPreference sharedPreference;
    private ConnectionMngr connectionMngr;
    private FontHelper fontHelper;


    private String userid,token;
    private HashMap<String, String> params;
    private VolleyRequest vsr;
    private UrlHelper urlHelper;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();  //Removing action bar

        //Full screen Window
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        this.txtName = (TextView) findViewById(R.id.txtName);
        this.txtTeachingNo = (TextView) findViewById(R.id.txtTeachingNo);
        this.txtSchool = (TextView) findViewById(R.id.txtSchool);
        this.txtRTO = (TextView) findViewById(R.id.txtRTO);
        this.txtEmail = (TextView) findViewById(R.id.txtEmail);
        this.tvTitle = (TextView) findViewById(R.id.txtTitle);
        this.tvDetailInfo = (TextView) findViewById(R.id.tvMydetailInfo);
        this.tvSchool = (TextView) findViewById(R.id.txtSchoolTitle);
        this.tvRTO = (TextView) findViewById(R.id.txtRTOTitle);
        this.tvAccreditations = (TextView) findViewById(R.id.txtAccredTitle);
        this.tvEmail = (TextView) findViewById(R.id.txtEmailTitle);
        this.tvTeachingNo = (TextView) findViewById(R.id.txtTeachingNoTitle);
        this.linearLayoutAccred = (LinearLayout) findViewById(R.id.linearLayoutAccred);

        this.btn_back = (ImageButton) findViewById(R.id.back_img);
        this.txtBack = (TextView) findViewById(R.id.txt_back);

        //Visible Buttons
        this.btn_back.setVisibility(View.VISIBLE);
        this.txtBack.setVisibility(View.VISIBLE);

        //Font Helpers
        fontHelper = new FontHelper(this);
        this.tvTitle.setTypeface(fontHelper.getDefaultFont("regular"));
        this.tvDetailInfo.setTypeface(fontHelper.getDefaultFont("regular"));
        this.txtName.setTypeface(fontHelper.getDefaultFont("regular"));
        this.tvTeachingNo.setTypeface(fontHelper.getDefaultFont("regular"));
        this.tvSchool.setTypeface(fontHelper.getDefaultFont("regular"));
        this.tvRTO.setTypeface(fontHelper.getDefaultFont("regular"));
        this.tvAccreditations.setTypeface(fontHelper.getDefaultFont("regular"));
        this.tvEmail.setTypeface(fontHelper.getDefaultFont("regular"));
        this.txtName.setTypeface(fontHelper.getDefaultFont("regular"));
        this.txtSchool.setTypeface(fontHelper.getDefaultFont("regular"));
        this.txtRTO.setTypeface(fontHelper.getDefaultFont("regular"));
        this.txtEmail.setTypeface(fontHelper.getDefaultFont("regular"));
        this.txtBack.setTypeface(fontHelper.getDefaultFont("bold"));
        this.txtTeachingNo.setTypeface(fontHelper.getDefaultFont("regular"));

        connectionMngr = new ConnectionMngr(this);
        sharedPreference = new SharedPreference(this);
        userid= sharedPreference.getStringValues("userid");
        token = sharedPreference.getStringValues("token");

        this.btn_back.setOnClickListener(this);
        this.txtBack.setOnClickListener(this);

        getMyDetails();
    }

    private void getMyDetails() {

        if(connectionMngr.hasConnection()) {
            params = new HashMap<String, String>();
            params.put("userid", userid);
            params.put("token", token);

            vsr = new VolleyRequest(this, Request.Method.POST, params, urlHelper.MY_DETAILS, false, CommonDef.REQUEST_MY_DETAILS);
            vsr.asyncInterface = this;
            vsr.request();
        }
    }

    @Override
    public void processFinish(String result, int requestCode) {
        switch(requestCode)
        {
            case CommonDef.REQUEST_MY_DETAILS:
                JSONObject jsonResult;
                try {
                    jsonResult = new JSONObject(result);
                    int response = jsonResult.getInt("response");
                    if(response==0)
                    {
                        JSONArray jArrayResult= jsonResult.getJSONArray("teacher");
//
                        int i=0;
                        JSONObject jArrayObject1 = jArrayResult.getJSONObject(0);
                        this.txtName.setText(jArrayObject1.getString("teacherFirstName")+" "+jArrayObject1.getString("teacherLastName"));
                        this.txtSchool.setText(jArrayObject1.getString("schoolName"));
                        if(!jArrayObject1.getString("rto_code").equalsIgnoreCase("0")) {
                            this.txtRTO.setText(jArrayObject1.getString("rto_code"));
                        }
                        else
                        {
                            this.txtRTO.setText("N/A");
                        }
                        this.txtTeachingNo.setText(jArrayObject1.getString("staffingCode"));

                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        for(int j = 0; j <jArrayResult.length() ; j++) {
                            JSONObject jArrayObject2 = jArrayResult.getJSONObject(j);
                            TextView tv=new TextView(this);
                            lparams.setMargins(10,10,10,10);
                            tv.setTypeface(fontHelper.getDefaultFont());
                            tv.setTextColor(getResources().getColor(R.color.color_edittext));
                            tv.setTextSize(15);
                            tv.setLayoutParams(lparams);
                            tv.setText(jArrayObject2.getString("accreditationName"));
                            this.linearLayoutAccred.addView(tv);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.btn_back.getId()) {
            v.startAnimation(buttonClick);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        } else if (v.getId() == this.txtBack.getId()) {
            v.startAnimation(buttonClick);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
