package com.weareforge.qms.adapters;

import com.weareforge.qms.Objects.IndustryContacts;

import java.util.Comparator;

/**
 * Created by Admin on 1/14/2016.
 */
public class MyComparator implements Comparator<IndustryContacts> {


    @Override
    public int compare(IndustryContacts o1, IndustryContacts o2) {
        String StudentName1 = o1.getName().toUpperCase();
        String StudentName2 = o2.getName().toUpperCase();

        //ascending order
        return StudentName1.compareTo(StudentName2);
    }

}