package com.weareforge.qms.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.weareforge.qms.fragments.StandardCompetenciesFragment;
import com.weareforge.qms.fragments.EvidenceFragment;
import com.weareforge.qms.fragments.IndustryContactFragment;
import com.weareforge.qms.fragments.IndustryActivityFragment;
import com.weareforge.qms.fragments.IndustryEngagementFragment;

/**
 * Created by Admin on 1/11/2016.
 */
 public class MyPageAdapter extends FragmentPagerAdapter {

    private int pageLimit;

    public MyPageAdapter(FragmentManager fm, int pageLimit) {
        super(fm);
        this.pageLimit = pageLimit;
    }

    public void currentPage(int pageLimit){
        this.pageLimit = pageLimit;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position)
        {
            case 0:
                fragment = new IndustryEngagementFragment();
                break;
            case 1:

                fragment = new IndustryContactFragment();
                break;
            case 2:

                fragment = new IndustryActivityFragment();
                break;
            case 3:

                fragment = new EvidenceFragment();
                break;
            case 4:

                fragment = new StandardCompetenciesFragment();
                break;
        }
        return fragment;
    }


    @Override
    public long getItemId(int position) {
        if (position >= 0) {
            // The current data matches the data in this active fragment, so let it be as it is.
            return position;
        } else {
            // Returning POSITION_NONE means the current data does not matches the data this fragment is showing right now.  Returning POSITION_NONE constant will force the fragment to redraw its view layout all over again and show new data.
            return POSITION_NONE;
        }
    }


    /*  @Override
    public int getItemPosition(Object object) {

        if (position >= 0) {
            // The current data matches the data in this active fragment, so let it be as it is.
            return position;
        } else {
            // Returning POSITION_NONE means the current data does not matches the data this fragment is showing right now.  Returning POSITION_NONE constant will force the fragment to redraw its view layout all over again and show new data.
            return POSITION_NONE;
        }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }*/

    @Override
    public int getCount() {
      /*  if(pageLimit <5)
        {
            return pageLimit + 1;
        }
        else
        {
            return pageLimit;
        }*/
        return 5;
    }

    public IndustryContactFragment getFragment(int index) {
        return new IndustryContactFragment();
    }
}



