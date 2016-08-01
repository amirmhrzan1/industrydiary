package com.weareforge.qms.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.weareforge.qms.Objects.EngagedIndustryContact;
import com.weareforge.qms.Objects.IndustryContactDetails;
import com.weareforge.qms.Objects.IndustryContacts;
import com.weareforge.qms.R;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.fragments.IndustryContactFragment;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.CommonMethods;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.interfaces.AsyncInterface;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 12/23/2015.
 */
public class MyContactList extends BaseAdapter {

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


    private HashMap<String,String> params;
    private VolleyRequest vsr;

    private ArrayList<EngagedIndustryContact> contactList;
    Fragment fragment;
    //button click alpha animation
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    public MyContactList(Context context , ArrayList<EngagedIndustryContact> list, Fragment mFragment)
    {
        this.cntx = context;
        this.contactList = list;
        this.fragment = mFragment;
        this.sharedPreference = new SharedPreference(context);
        token = sharedPreference.getStringValues("token");
        userid = sharedPreference.getStringValues("userid");
        // this.arrayList = arrayList;
        myinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EngagedIndustryContact contacts = (EngagedIndustryContact) getItem(position);
        if (convertView == null) {
            convertView = myinflater.inflate(R.layout.my_contact_list, parent, false);
        }

        this.txtName = (TextView) convertView.findViewById(R.id.profilename);
        this.imgbtnDelete = (ImageView) convertView.findViewById(R.id.delete);
        this.imgbtnEdit = (ImageButton) convertView.findViewById(R.id.edit);

        //Set Text
        this.txtName.setText(contacts.getName());


        //Fonts Helper
        fontHelper = new FontHelper((Activity) this.cntx);
        this.txtName.setTypeface(fontHelper.getDefaultFont());

        //listners
        this.imgbtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
            }
        });

        this.imgbtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                params = new HashMap<String, String>();
                params.put("token",token);
                params.put("userid",userid);
                params.put("industry_engagement_contact_id",String.valueOf(contacts.getIndustryContactEngagementId()));

                vsr = new VolleyRequest((Activity) cntx, Request.Method.POST,params,urlHelper.INDUSTRY_ENGAGEMENT_CONTACT_DELETE,false, CommonDef.REQUEST_INDUSTRY_CONTACT_DELETE);
                vsr.asyncInterface = (AsyncInterface) fragment;
                vsr.request();
            }
        });

        return convertView;
    }
}

