package com.weareforge.qms.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognito.internal.util.StringUtils;
import com.weareforge.qms.Objects.EngagementEvidenceData;
import com.weareforge.qms.R;
import com.weareforge.qms.utils.FontHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Admin on 12/23/2015.
 */
public class ExistingIndustryArrayAdapter extends BaseAdapter {

    private Context cntx;
    private LayoutInflater myinflater;
    private ArrayList<EngagementEvidenceData> evidenceList;

    private TextView txtDate;
    private TextView txtRefTitle;
    private TextView txtAccreditation;
    private TextView txtAccNumber;
    private TextView txtHourse;
    private TextView txtHourseNumber;
    private TextView txtStatus;

    private FontHelper fontHelper;

    public ExistingIndustryArrayAdapter(Context context , ArrayList<EngagementEvidenceData> list)
    {
        this.cntx = context;
        this.evidenceList = list;
        myinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return evidenceList.size();
    }

    @Override
    public Object getItem(int position) {
        return evidenceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EngagementEvidenceData data = (EngagementEvidenceData) getItem(position);
            if (convertView == null) {
                convertView = myinflater.inflate(R.layout.existing_industry_list, parent, false);
            }
        txtStatus = (TextView) convertView.findViewById(R.id.txt_status);
        this.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
        this.txtRefTitle = (TextView) convertView.findViewById(R.id.txt_reference_title);
        this.txtAccreditation = (TextView) convertView.findViewById(R.id.txt_accreditation);
        this.txtAccNumber = (TextView) convertView.findViewById(R.id.txt_accreditationNumber);
        this.txtHourse = (TextView) convertView.findViewById(R.id.txt_hourse);
        this.txtHourseNumber = (TextView) convertView.findViewById(R.id.txt_hourseNumber);

        convertView.setBackgroundColor(Color.parseColor(position % 2 != 0 ? "#f6f5f4" : "#ffffff"));

        //setText
        try{
            String date =data.getDateTime();
            String[] splitStr = date.split(" ");
            this.txtDate.setText(splitStr[0].replace("-","/"));
        }
        catch (Exception exx){
            exx.printStackTrace();
        }

        this.txtRefTitle.setText(data.getReference());
        this.txtHourseNumber.setText(data.getHours()+"");
        if(!data.getStatus().toString().equalsIgnoreCase("1"))
        {
            this.txtStatus.setText("IN PROGRESS");
            this.txtStatus.setTextColor(Color.parseColor("#000000"));
        }
        else
        {
            this.txtStatus.setText("COMPLETED");
            this.txtStatus.setTextColor(Color.parseColor("#46c4d6"));
        }

        //Fonts
        fontHelper = new FontHelper((Activity) this.cntx);
        this.txtDate.setTypeface(fontHelper.getDefaultFont());
        this.txtRefTitle.setTypeface(fontHelper.getDefaultFont());
        this.txtHourseNumber.setTypeface(fontHelper.getDefaultFont());
        this.txtAccNumber.setTypeface(fontHelper.getDefaultFont());
        this.txtAccreditation.setTypeface(fontHelper.getDefaultFont());
        this.txtHourse.setTypeface(fontHelper.getDefaultFont());
        txtStatus.setTypeface(fontHelper.getDefaultFont());

        return convertView;
    }
}
