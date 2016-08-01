package com.weareforge.qms.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.weareforge.qms.R;
import com.weareforge.qms.utils.AndroidBug5497Workaround;

/**
 * Created by Prajeet on 2/5/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private LayoutInflater inflater;
    protected abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_top_action_bar);


        getSupportActionBar().hide();
        //Full screen Window
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AndroidBug5497Workaround.assistActivity(this);*/

        inflater = (LayoutInflater) getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(getLayoutId(), null, false);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container);
        frameLayout.addView(view);
        }
    }

