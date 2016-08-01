package com.weareforge.qms.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.weareforge.qms.Objects.IndustryContacts;
import com.weareforge.qms.Objects.IndustryContactsStatic;
import com.weareforge.qms.Objects.UploadedImage;
import com.weareforge.qms.R;
import com.weareforge.qms.adapters.MyIndustryContactArrayAdapter;
import com.weareforge.qms.adapters.MyPageAdapter;
import com.weareforge.qms.alerts.Alerts;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.fragments.EvidenceFragment;
import com.weareforge.qms.fragments.IndustryActivityFragment;
import com.weareforge.qms.fragments.IndustryContactFragment;
import com.weareforge.qms.fragments.IndustryEngagementFragment;
import com.weareforge.qms.fragments.StandardCompetenciesFragment;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.CommonMethods;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.interfaces.AsyncInterface;
import com.weareforge.qms.utils.ConnectionMngr;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.Opener;
import com.weareforge.qms.utils.SharedPreference;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 1/7/2016.
 *
 */

public class NewIndustyActivity extends FragmentActivity implements View.OnClickListener, AsyncInterface {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    public static ViewPager mPager = null;

    private MyIndustryContactArrayAdapter adapter;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private LinearLayout PgRelativeLayout;

    private ListView industry_list;
    private ImageView homeIcon;
    private Button btnSaveProgess;

    private TextView txtProgress;

    private VolleyRequest vsr;
    private HashMap<String, String> params;
    private Alerts alerts;

    private ConnectionMngr connectionMngr;
    private String userid;
    private String token;
    private UrlHelper urlHelper;


    private Opener opener;
    private FontHelper fontHelper;
    private SharedPreference sharedPreference;

    public static int currentStep;


    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);

    private  IndustryContactsStatic industryContactsStatic = new IndustryContactsStatic();
    private IndustryContacts industryContacts;

    ArrayList<IndustryContacts> industryContactStatic = new ArrayList<IndustryContacts>();
    private IndustryEngagementFragment industryEngagementFragment;
    private IndustryContactFragment industryContactFragment;
    private IndustryActivityFragment industryActivityFragment;
    private EvidenceFragment evidenceFragment;
    private StandardCompetenciesFragment standardCompetenciesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Full screen Window
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AndroidBug5497Workaround.assistActivity();
*/

        setContentView(R.layout.activity_new_industry);
        CommonMethods.setupUI(findViewById(R.id.relativeLayoutNewIndustry), this);

        //Initialize
        this.btnSaveProgess = (Button) findViewById(R.id.btnSaveProgress);
        this.txtProgress = (TextView) findViewById(R.id.txtProgress);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        this.homeIcon = (ImageView) findViewById(R.id.homeIcon);

        this.industry_list = new ListView(this);
        this.opener = new Opener(this);
        this.alerts = new Alerts(this);
        this.connectionMngr = new ConnectionMngr(this);

        this.sharedPreference = new SharedPreference(this);
        token = sharedPreference.getStringValues("token");
        userid = sharedPreference.getStringValues("userid");

        this.currentStep = sharedPreference.getIntValues("Current_Step");

        FragmentManager fragmentManager = getSupportFragmentManager();
        mPager.setAdapter(new MyPageAdapter(fragmentManager,this.currentStep));
        mPager.setOffscreenPageLimit(5);


        //Font Helper
        fontHelper = new FontHelper(this);
        this.btnSaveProgess.setTypeface(fontHelper.getDefaultFont());
        this.txtProgress.setTypeface(fontHelper.getDefaultFont("bold"));

        btnSaveProgess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProgress();
            }
        });

        //    mPager.addView();
        final ArrayList<Button> numButtons = new ArrayList<Button>();
        this.PgRelativeLayout = (LinearLayout) findViewById(R.id.PlrelativeLayout);

        for (int i = 0; i < 5; i++) {
            Button btn_Pg = new Button(this);
            btn_Pg.setBackground( ContextCompat.getDrawable(this, R.drawable.one_icon));
            ContextCompat.getDrawable(this, R.drawable.one_icon);
            final float scale = getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams rel_button1 = new LinearLayout.LayoutParams((int) Math.round(30*scale), (int) Math.round(55*scale));
            rel_button1.setMargins((int) Math.round(11 * scale), 0, 0, 0);
            btn_Pg.setLayoutParams(rel_button1);

            if (i == 0) {
                btn_Pg.setBackground(ContextCompat.getDrawable(this, R.drawable.selecte_one_icon));
            }else if(i==1)
            {
                btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.two_icon));
//                if(i<=currentStep)
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.two_icon));
//                }
//                else
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.ic_two_icon));
//                }
            }

            else if(i==2) {
                btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.three_icon));
//                if(i<=currentStep)
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.three_icon));
//                }
//                else
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.ic_three_icon));
//                }

            } else if(i==3) {
                btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.four_icon));
//                if(i<=currentStep)
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.four_icon));
//                }
//                else
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.ic_four_icon));
//                }

            }
            else if(i==4) {
                btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.five_icon));
//                if (i <=currentStep) {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.five_icon));
//                } else {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.ic_five_icon));
//                }
            }

            final int finalI = i;
//            if(i<=this.currentStep) {
                btn_Pg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPager.setCurrentItem(finalI);
                        View view = NewIndustyActivity.this.getCurrentFocus();
                        if (view != null) {
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(NewIndustyActivity.this.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }catch (Exception exx){}
                        }
                    }
                });
//            }
            this.PgRelativeLayout.addView(btn_Pg);
            numButtons.add(btn_Pg);
        }

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

                System.out.println("My current pos is: " + position);

                Button imageView = numButtons.get(position);
                for (int i = 0; i < 5; i++) {
                    Button btn_Pg = numButtons.get(i);
                    if (i == 0) {
                        btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.one_icon));
                    } else if(i==1)
                    {
                        btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.two_icon));
//                if(i<=currentStep)
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.two_icon));
//                }
//                else
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.ic_two_icon));
//                }
                    }

                    else if(i==2) {
                        btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.three_icon));
//                if(i<=currentStep)
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.three_icon));
//                }
//                else
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.ic_three_icon));
//                }

                    } else if(i==3) {
                        btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.four_icon));
//                if(i<=currentStep)
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.four_icon));
//                }
//                else
//                {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.ic_four_icon));
//                }

                    }
                    else if(i==4) {
                        btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.five_icon));
//                if (i <=currentStep) {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.five_icon));
//                } else {
//                    btn_Pg.setBackground(ContextCompat.getDrawable(NewIndustyActivity.this, R.drawable.ic_five_icon));
//                }
                    }
                }

                final int finalI = position;
//                if (finalI <= currentStep) {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onPageSelected(finalI);
                            mPager.setCurrentItem(finalI);
                        }
                    });

                    if (position == 0) {
                        imageView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selecte_one_icon));
                        //    imageView.setTextColor(Color.WHITE);
                    } else if (position == 1) {
                        imageView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.select_two_icon));
                    } else if (position == 2) {
                        imageView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.select_three_icon));
                    } else if (position == 3) {
                        imageView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selecte_four_icon));
                    } else if (position == 4) {
                        imageView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selecte_five_icon));
                    }

                   /* else if(position ==5) {
                        imageView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selecte_six_icon));
                    }*/
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        this.homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {

                    saveProgress();

                    IndustryEngagementFragment.activityIds = new ArrayList<Integer>();
                    IndustryEngagementFragment.purposeIds = new ArrayList<Integer>();
                    sharedPreference.setKeyValues("flag", 0);
                    sharedPreference.setKeyValues("Engagement_Evidence_Id", "");
                    sharedPreference.setKeyValues("Industry_Engagement_Id", "");
                    sharedPreference.setKeyValues("Industry_Activity_Feedback_Id", "");
                    sharedPreference.setKeyValues("Evidence_Id", "");
                    sharedPreference.setKeyValues("Standard_Competency_Id", "");
                    sharedPreference.setKeyValues("Current_Step", 0);
                    UploadedImage.isDownloaded = false;
                    UploadedImage.filepath = "";
                    //      DownloadActivity downloadActivity = new DownloadActivity();
                    //      downloadActivity.DeleteTransfer();

                    // sharedPreference.setKeyValues("Industry_Engagement_Id","");

                    opener.Home();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
                else
                {
                    opener.Home();
                }
            }
        });
    }

    private void saveProgress() {
        //  FragmentPagerAdapter adapter = (FragmentPagerAdapter)mPager.getAdapter();
        int index = mPager.getCurrentItem();

        MyPageAdapter adapter = ((MyPageAdapter)mPager.getAdapter());
        IndustryContactFragment fragment = (IndustryContactFragment) adapter.getItem(1);

        android.support.v4.app.Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + fragment);

              /*  if(index == 0)
                {
                    ((IndustryEngagementFragment) page).SaveProgress();
                }
                else if(index == 1)
                {
                    ((IndustryContactFragment) page).SaveProgress();
                }
                else if(index == 2)
                {
                    ((IndustryActivityFragment) page).SaveProgress();
                }
                else if(index == 3)
                {
                    ((EvidenceFragment) page).SaveProgress();
                }
                else if(index == 4)
                {
                    ((StandardCompetenciesFragment) page).SaveProgress();
                }*/

        if(!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {
            if (connectionMngr.hasConnection()) {
                params = new HashMap<String, String>();
                params.put("token", token);
                params.put("userid", userid);
                params.put("engagement_evidence_id", sharedPreference.getStringValues("Engagement_Evidence_Id"));
                params.put("title", "");

                if(sharedPreference.getStringValues("Standard_Competency_Id") != "")
                {
                    params.put("current_step", "5");

                    if(sharedPreference.getStringValues("Evidence_Id")!="" && sharedPreference.getStringValues("Industry_Activity_Feedback_Id") != "")
                    {
                        params.put("status", "1");
                    }
                    else
                    {
                        params.put("status", "0");
                    }
                }

                else if(sharedPreference.getStringValues("Evidence_Id") != "")
                {
                    params.put("current_step", "4");
                    params.put("status", "0");
                }

                else if(sharedPreference.getStringValues("Industry_Activity_Feedback_Id") != "")
                {
                    params.put("current_step", "3");
                    params.put("status", "0");
                }


                else if((IndustryContactFragment) page!= null && ((IndustryContactFragment) page).listEngagedIndustry != null && !((IndustryContactFragment) page).listEngagedIndustry.isEmpty())
                {
                    params.put("current_step", "2");
                    params.put("status", "0");
                }

                else
                {
                    params.put("current_step", "1");
                    params.put("status", "0");
                }
                vsr = new VolleyRequest(NewIndustyActivity.this, Request.Method.POST, params, urlHelper.ENGAGEMENT_EVIDENCE, false, CommonDef.REQUEST_SAVE_PROGRESS_STEP_1,getResources().getString(R.string.saveProgress));
                vsr.asyncInterface = NewIndustyActivity.this;
                vsr.request();
            }
        }
        else
        {
            alerts.showCreateEvidenceOkMessage("Please create evidence before proceed");
        }
    }

    @Override
    public void onBackPressed() {
        if(!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {

            saveProgress();

            IndustryEngagementFragment.activityIds = new ArrayList<Integer>();
            IndustryEngagementFragment.purposeIds = new ArrayList<Integer>();
            sharedPreference.setKeyValues("flag", 0);
            sharedPreference.setKeyValues("Engagement_Evidence_Id", "");
            sharedPreference.setKeyValues("Industry_Engagement_Id", "");
            sharedPreference.setKeyValues("Industry_Activity_Feedback_Id", "");
            sharedPreference.setKeyValues("Evidence_Id", "");
            sharedPreference.setKeyValues("Standard_Competency_Id", "");
            sharedPreference.setKeyValues("Current_Step", 0);
            UploadedImage.isDownloaded = false;
            UploadedImage.filepath = "";
            //      DownloadActivity downloadActivity = new DownloadActivity();
            //      downloadActivity.DeleteTransfer();

            // sharedPreference.setKeyValues("Industry_Engagement_Id","");

            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        else
        {
            finish();
        }

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void processFinish(String result, int requestCode) {
        if(industryEngagementFragment==null)
        {

            industryEngagementFragment = (IndustryEngagementFragment) mPager.getAdapter().instantiateItem(mPager, 0);
        }
        if(industryContactFragment==null)
        {

            industryContactFragment = (IndustryContactFragment) mPager.getAdapter().instantiateItem(mPager,1);
        }
        if(industryActivityFragment==null)
        {

            industryActivityFragment = (IndustryActivityFragment) mPager.getAdapter().instantiateItem(mPager,2);
        }
        if(evidenceFragment==null)
        {

            evidenceFragment = (EvidenceFragment) mPager.getAdapter().instantiateItem(mPager,3);
        }
        if(standardCompetenciesFragment==null)
        {

            standardCompetenciesFragment = (StandardCompetenciesFragment) mPager.getAdapter().instantiateItem(mPager,4);
        }

        switch (requestCode) {
            //Industry Engagement Fragment
            case CommonDef.REQUEST_INDUSTRY_ENGAGEMENT_ADD:
                industryEngagementFragment.industryEngagementAddEdit(result);
                break;
            case CommonDef.REQUEST_LIST_INDUSTRY_ENGAGEMENT:
                industryEngagementFragment.listIndustryengagement(result);
                break;
            case CommonDef.REQUEST_SAVE_PROGRESS_STEP_1:
                industryEngagementFragment.requestSaveProgress(result);
                break;

            case CommonDef.REQUEST_LIST_ACTIVITY:
               // industryEngagementFragment.listActivity(result);
                break;

            case CommonDef.REQUEST_LIST_PURPOSE:
             //  industryEngagementFragment.listPurpose(result);
                break;

            case CommonDef.REQUEST_LIST_ACCRED:
                industryEngagementFragment.listAccred(result);

            case CommonDef.REQUEST_LIST_FINDOUT:
              //  industryEngagementFragment.listFindOut(result);
            //Industry Contact Fragment
            case CommonDef.REQUEST_INDUSTRY_CONTACTS:
                industryContactFragment.LoadIndustryContacts(result);
                break;
            case CommonDef.REQUEST_INDUSTRY_CONTACT_LIST:
                industryContactFragment.loadIndustryContactList(result);
                break;
            case CommonDef.REQUEST_ENGAGEMENT_CONTACT_LIST:
                industryContactFragment.loadEngagementConactList(result);
                break;
            case CommonDef.REQUEST_INDUSTRY_CONTACT_ADD:
                industryContactFragment.requestIndustryContact(result);
                break;
            case CommonDef.REQUEST_ADD_EXISTING_CONTACTS:
                industryContactFragment.addExistingContacts(result);
                break;

            case CommonDef.REQUEST_SAVE_PROGRESS_STEP_2:
                industryContactFragment.requestSaveProgress(result);
                break;

            //Industry Activity Feedback
            case CommonDef.REQUEST_INDUSTRY_ACTIVITY_FEEDBACK_UPSERT:
                industryActivityFragment.industryActivityUpsert(result);
                break;

            case CommonDef.REQUEST_INDUSTRY_ACTIVITY_FEEDBACK_LIST:
                industryActivityFragment.industryActivityList(result);
                break;

            case CommonDef.REQUEST_SAVE_PROGRESS_STEP_3:
                industryActivityFragment.requestSaveProgress(result);
                break;

            //Evidence Fragement
            case CommonDef.REQUEST_EVIDENCE_UPSERT:
                evidenceFragment.evidenceUpsert(result);
                break;

            case CommonDef.REQUEST_EVIDENCE_LIST:
                evidenceFragment.loadEvidenceList(result);
                break;

            case CommonDef.REQUEST_EVIDENCE_DATA:
                evidenceFragment.loadEvidenceData(result);
                break;

            case CommonDef.REQUEST_SAVE_PROGRESS_STEP_4:
                evidenceFragment.requestSaveProgress(result);
                break;

            //Standard Fragment
            case CommonDef.REQUEST_LIST_ASAQ:
              //  standardCompetenciesFragment.listASAQ(result);
                break;

            case CommonDef.REQUEST_LIST_COMPETENCIES:
                standardCompetenciesFragment.listCompentencies(result);
                break;

            case CommonDef.REQUEST_UPSERT_STANDARD_COMPETENCIES:
                standardCompetenciesFragment.upsertStandardCompitencies(result);
                break;

            case CommonDef.REQUEST_LIST_STANDARD_COMPETENCIES:
                standardCompetenciesFragment.loadStandardCompetenciesData(result);
                break;

            case CommonDef.REQUEST_SAVE_PROGRESS_STEP_5:
                standardCompetenciesFragment.requestSaveProgress(result);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IndustryEngagementFragment.activityIds.clear();
        IndustryEngagementFragment.purposeIds.clear();
    }
}
