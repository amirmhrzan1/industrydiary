package com.weareforge.qms.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.weareforge.qms.Objects.IndustryContactDetails;
import com.weareforge.qms.Objects.IndustryContacts;
import com.weareforge.qms.R;
import com.weareforge.qms.utils.FontHelper;

import java.util.ArrayList;

/**
 * Created by Admin on 12/23/2015.
 */
public class MyIndustryContactArrayAdapter extends BaseAdapter {

    private Context cntx;
    private LayoutInflater myinflater;
    private String[] datas;
    private ArrayList<IndustryContacts> arrayList = new ArrayList<IndustryContacts>();

    private TextView txtName;
    private TextView txtTitle;
    private TextView txtOrganization;
    private ImageButton imageButtonMail;
    private ImageButton imageButtonCall;

    private FontHelper fontHelper;

    //button click alpha animation
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    public MyIndustryContactArrayAdapter(Context context , ArrayList<IndustryContacts> arrayList)
    {
        this.cntx = context;
       // this.datas = data;
        this.arrayList = arrayList;
        myinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        IndustryContacts industryContacts = (IndustryContacts) getItem(position);
        if (convertView == null) {
            convertView = myinflater.inflate(R.layout.industry_contact_list, parent, false);
        }


        this.imageButtonMail = (ImageButton) convertView.findViewById(R.id.mail);
        this.imageButtonCall = (ImageButton) convertView.findViewById(R.id.call);
        this.txtName = (TextView) convertView.findViewById(R.id.name);
        this.txtTitle= (TextView) convertView.findViewById(R.id.title);
        this.txtOrganization= (TextView) convertView.findViewById(R.id.organization);

        //Set Text
        this.txtName.setText(industryContacts.getName());
        this.txtTitle.setText(industryContacts.getTitle());
        this.txtOrganization.setText(industryContacts.getOrganization());


        //alternate changes in listview
        convertView.setBackgroundColor(Color.parseColor(position % 2 != 0 ?"#f6f5f4" : "#ffffff"));
        this.imageButtonMail.setImageResource((position % 2 != 0 ? R.drawable.mail_icon : R.drawable.grey_mail_icon));
        this.imageButtonCall.setImageResource((position % 2 != 0 ? R.drawable.white_phone_icon : R.drawable.grey_phone_icon));

        //Fonts Helper
        fontHelper = new FontHelper((Activity) this.cntx);
        this.txtName.setTypeface(fontHelper.getDefaultFont());
        this.txtOrganization.setTypeface(fontHelper.getDefaultFont());
        this.txtTitle.setTypeface(fontHelper.getDefaultFont());

        //Listners
        this.imageButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
            }
        });

        this.imageButtonMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
            }
        });
        return convertView;
    }
}

