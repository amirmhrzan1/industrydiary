package com.weareforge.qms.helpers;

import android.app.Activity;

/**
 * Created by prajit on 4/1/16.
 */
public class ProgressDialog {

    private static android.app.ProgressDialog pd;
    private static boolean showPd = false;

    public static void showPd(Activity activity , String msg)
    {
        pd = new android.app.ProgressDialog(activity);
        pd.setMessage(msg);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    public static void hidePd()
    {
        pd.hide();
    }
}
