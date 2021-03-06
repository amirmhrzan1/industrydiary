package com.weareforge.qms.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weareforge.qms.AWS.DownloadActivity;
import com.weareforge.qms.Objects.UploadedImage;
import com.weareforge.qms.R;
import com.weareforge.qms.db.EngagementEvidenceDbHandler;
import com.weareforge.qms.db.QMSDatabaseHelper;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.Opener;
import com.weareforge.qms.utils.SharedPreference;

import static android.app.AlertDialog.*;

/**
 * Created by Admin on 12/22/2015.
 */
public  class HomeActivity extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private ImageButton btn_newIndustry;
    private ImageButton btn_existingIndustry;
    private ImageButton btn_settings;
    private Button btn_events;
    private Button btn_industryContacts;
    private Button btn_uploadTAS;
    private Button btn_myDetails;


    private TextView txt_newIndustry;
    private TextView txt_existingIndustry;

    private RelativeLayout rlNewIndustry;
    private RelativeLayout rlExistingIndustry;

    private Context context;
    private QMSDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    Opener opener;
    FontHelper fonthelper;
    private SharedPreference sharedPreference;

    //button click alpha animation
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;
        opener = new Opener(this);
        sharedPreference = new SharedPreference(this);
        getSupportActionBar().hide();  //Removing action bar
        //Full screen Window
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        this.btn_newIndustry = (ImageButton) findViewById(R.id.btn_new_industry);
        this.btn_existingIndustry = (ImageButton) findViewById(R.id.btn_existing_industry);
        this.btn_settings = (ImageButton) findViewById(R.id.btn_setting);
        this.btn_events = (Button) findViewById(R.id.btn_eventsInfo);
        this.btn_industryContacts = (Button) findViewById(R.id.btn_industryContacts);
        this.btn_uploadTAS = (Button) findViewById(R.id.btn_uploadTps);
        this.btn_myDetails = (Button) findViewById(R.id.btn_myDetails);
        this.rlExistingIndustry = (RelativeLayout) findViewById(R.id.rlExistingIndustry);
        this.rlNewIndustry = (RelativeLayout) findViewById(R.id.rlNewIndustry);
        this.txt_newIndustry = (TextView) findViewById(R.id.txt_new_industry);
        this.txt_existingIndustry = (TextView) findViewById(R.id.txt_existing_industry);

        dbHelper = new QMSDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        //Font Helpers
        fonthelper = new FontHelper(this);
        this.txt_newIndustry.setTypeface(fonthelper.getDefaultFont());
        this.txt_existingIndustry.setTypeface(fonthelper.getDefaultFont());
        this.btn_events.setTypeface(fonthelper.getDefaultFont("bold"));
        this.btn_industryContacts.setTypeface(fonthelper.getDefaultFont("bold"));
        this.btn_uploadTAS.setTypeface(fonthelper.getDefaultFont("bold"));
        this.btn_myDetails.setTypeface(fonthelper.getDefaultFont("bold"));
        this.btn_myDetails.setTypeface(fonthelper.getDefaultFont("bold"));

        //OnclickListners
        this.btn_newIndustry.setOnClickListener(this);
        this.btn_existingIndustry.setOnClickListener(this);
        this.btn_events.setOnClickListener(this);
        this.btn_settings.setOnClickListener(this);
        this.btn_industryContacts.setOnClickListener(this);
        this.btn_uploadTAS.setOnClickListener(this);
        this.btn_myDetails.setOnClickListener(this);
        this.txt_newIndustry.setOnClickListener(this);
        this.txt_existingIndustry.setOnClickListener(this);

        this.rlExistingIndustry.setOnClickListener(this);
        this.rlNewIndustry.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==this.rlNewIndustry.getId())
        {
            v.startAnimation(buttonClick);
            sharedPreference.setKeyValues("flag",0);
            sharedPreference.setKeyValues("Engagement_Evidence_Id","");
            sharedPreference.setKeyValues("Industry_Engagement_Id","");
            sharedPreference.setKeyValues("Industry_Activity_Feedback_Id","");
            sharedPreference.setKeyValues("Evidence_Id","");
            sharedPreference.setKeyValues("Standard_Competency_Id","");
            sharedPreference.setKeyValues("Current_Step",0);
            UploadedImage.isDownloaded = false;
            UploadedImage.filepath = "";
            opener.NewIndustry();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }

        else if(v.getId()==this.rlExistingIndustry.getId())
        {
            v.startAnimation(buttonClick);
            opener.ExistingIndustry();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        else if(v.getId() == this.btn_newIndustry.getId())
        {
            rlNewIndustry.startAnimation(buttonClick);

            sharedPreference.setKeyValues("flag", 0);
            sharedPreference.setKeyValues("Engagement_Evidence_Id", "");
            sharedPreference.setKeyValues("Industry_Engagement_Id","");
            sharedPreference.setKeyValues("Industry_Activity_Feedback_Id","");
            sharedPreference.setKeyValues("Evidence_Id","");
            sharedPreference.setKeyValues("Standard_Competency_Id","");
            sharedPreference.setKeyValues("Current_Step",0);
            UploadedImage.isDownloaded = false;
            UploadedImage.filepath = "";
            opener.NewIndustry();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        else if(v.getId()==this.txt_newIndustry.getId())
        {
            rlNewIndustry.startAnimation(buttonClick);
            sharedPreference.setKeyValues("flag",0);
            sharedPreference.setKeyValues("Engagement_Evidence_Id","");
            sharedPreference.setKeyValues("Industry_Engagement_Id","");
            sharedPreference.setKeyValues("Industry_Activity_Feedback_Id","");
            sharedPreference.setKeyValues("Evidence_Id","");
            sharedPreference.setKeyValues("Standard_Competency_Id", "");
            sharedPreference.setKeyValues("Current_Step",0);
            UploadedImage.isDownloaded = false;
            UploadedImage.filepath = "";
            opener.NewIndustry();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if(v.getId()==this.btn_existingIndustry.getId())
        {
            rlExistingIndustry.startAnimation(buttonClick);
            opener.ExistingIndustry();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        else if(v.getId()==this.txt_existingIndustry.getId())
        {
            rlExistingIndustry.startAnimation(buttonClick);
            opener.ExistingIndustry();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        else if(v.getId()==this.btn_events.getId())
        {
            v.startAnimation(buttonClick);
            opener.EventsAndInfo();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if(v.getId() == this.btn_industryContacts.getId())
        {
            v.startAnimation(buttonClick);
            opener.MyIndustryContacts();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if(v.getId()==this.btn_uploadTAS.getId())
        {
            v.startAnimation(buttonClick);
            opener.UploadTAS();
           // opener.UploadFile();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if(v.getId()==this.btn_myDetails.getId())
        {
            v.startAnimation(buttonClick);
            opener.MyDetails();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if(v.getId() == this.btn_settings.getId())
        {
            v.startAnimation(buttonClick);
            opener.Setting();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
           // PopupMenu popup = new PopupMenu(this, v);

            // This activity implements OnMenuItemClickListener
          /*  popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.settings);
            popup.show();*/
        }
    }




    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                Builder builder1 = new Builder(context);
                builder1.setMessage("Do yo want to Logout?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sharedPreference.setKeyValues("Flag", 0);
                                dbHelper.dropAllTable(db);
                                opener.Login();
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
               return true;

            case R.id.menu_sunc_wifi:
                item.setChecked(true);
                item.setCheckable(true);
                if (item.isChecked())
                {item.setChecked(false);
                   // Toast.makeText(this,"not checked",Toast.LENGTH_LONG).show();
                   }
                else {item.setChecked(true);  Toast.makeText(this,"checked",Toast.LENGTH_LONG).show();}
                return true;

            default:return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        Builder builder1 = new Builder(context);
        builder1.setMessage("Do you want to exit?");
        builder1.setTitle("Industry Diary");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        opener.exit();
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

    @Override
    protected void onResume() {
        super.onResume();
        //overridePendingTransition(R.anim.left_out, R.anim.right_in);
    }
}


