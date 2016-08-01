package com.weareforge.qms.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.weareforge.qms.Objects.IndustryContacts;
import com.weareforge.qms.R;
import com.weareforge.qms.utils.FontHelper;

import java.util.ArrayList;

/**
 * Created by prajit on 12/23/2015.
 */
public class ExistingIndustryListArrayAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    private Context cntx;
    private LayoutInflater myinflater;
    public  String[] datas;
    private ArrayList<IndustryContacts> arrayList = new ArrayList<IndustryContacts>();

    private TextView txtName;
    private TextView txtTitle;
    private CheckBox checkSelect;
    private FontHelper fontHelper;

    public SparseBooleanArray mCheckStates;
    //button click alpha animation
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    public ExistingIndustryListArrayAdapter(Context context, ArrayList<IndustryContacts> arrayList)
    {
        this.cntx = context;
        mCheckStates = new SparseBooleanArray(arrayList.size());
        this.arrayList = arrayList;
        myinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
//        return 3;
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
            convertView = myinflater.inflate(R.layout.select_existing_list, parent, false);
        }

        this.txtName = (TextView) convertView.findViewById(R.id.name);
        this.txtTitle= (TextView) convertView.findViewById(R.id.title);
        this.checkSelect = (CheckBox) convertView.findViewById(R.id.listViewCheckBox);


        //alternate changes in listview
        convertView.setBackgroundColor(Color.parseColor("#ffffff"));

        //Set Text
        this.txtName.setText(industryContacts.getName());
        this.txtTitle.setText(industryContacts.getEmail());

       /* if(industryContacts.getName().equalsIgnoreCase("chhh")  || industryContacts.getName().equalsIgnoreCase("fgf"))
        {
            this.checkSelect.setTag(position);
            this.checkSelect.setChecked(mCheckStates.get(position, true));
            this.checkSelect.setEnabled(false);
        }
        else
        {
            this.checkSelect.setTag(position);
            this.checkSelect.setChecked(mCheckStates.get(position, false));
            this.checkSelect.setOnCheckedChangeListener(this);

        }*/
        this.checkSelect.setTag(position);
        this.checkSelect.setChecked(mCheckStates.get(position, false));
        this.checkSelect.setOnCheckedChangeListener(this);

        //Fonts Helper
        fontHelper = new FontHelper((Activity) this.cntx);
        this.txtName.setTypeface(fontHelper.getDefaultFont());
        this.txtTitle.setTypeface(fontHelper.getDefaultFont());

        return convertView;
    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {

        mCheckStates.put(position, isChecked);

    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));

    }

    public int getIndustryContactId(int position)
    {
        IndustryContacts industryContacts = (IndustryContacts) getItem(position);
        return industryContacts.getIndustry_Contact_Id();
    }

    public String getName(int position)
    {
        IndustryContacts industryContacts = (IndustryContacts) getItem(position);
        return industryContacts.getName();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mCheckStates.put((Integer) buttonView.getTag(), isChecked);
    }
}

