package com.weareforge.qms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.weareforge.qms.Objects.spinnerObject;
import com.weareforge.qms.R;

import java.util.ArrayList;

import eu.inmite.android.lib.validations.form.adapters.SpinnerAdapter;

/**
 * Created by prajit on 3/4/16.
 */
public class spinnerAdapter extends SpinnerAdapter {

    Context cntx;
    ArrayList<spinnerObject> spinnerObj;

    public spinnerAdapter(Context context, int textViewResourceId, ArrayList<spinnerObject> spinnerObjects) {

        this.cntx = context;
        this.spinnerObj = spinnerObjects;

    }



    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        LayoutInflater inflater= (LayoutInflater)this.cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.spinner_adapter, parent, false);
        TextView label=(TextView)row.findViewById(R.id.name);

        return row;
    }
}
