package com.weareforge.qms.alerts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weareforge.qms.R;
import com.weareforge.qms.activities.NewIndustyActivity;
import com.weareforge.qms.utils.Opener;
import com.weareforge.qms.utils.SharedPreference;


/**
 * Created by Rabin on 8/14/2015.
 */
public class Alerts extends AlertDialog {

    private final SharedPreference prefs;
    Context context;
    AlertDialog alertDialog;
    Activity activity;
    Opener opener;

    public Alerts(Context context) {
        super(context);
        this.context = context;
        activity = ((Activity)context);
        prefs = new SharedPreference(context);
      //  opener = new Opener(context);
    }


    public void showOkMessage(String title, String msg) {
        Builder builder = initAlertDialog(title, msg);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void showOkMessage(String msg) {
        Builder builder = initAlertDialog("", msg);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    public void showCreateEvidenceOkMessage(String msg) {
        Builder builder = initAlertDialog("", msg);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewIndustyActivity newIndustyActivity = new NewIndustyActivity();
                newIndustyActivity.mPager.setCurrentItem(0);
                dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    public void showOkWhatMessage(String msg)
    {
        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.custom_hint_dialog);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
        RelativeLayout layout = (RelativeLayout) dialog.findViewById(R.id.rlHintDialog);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        String upperString = msg.substring(0,1).toUpperCase() + msg.substring(1);
        text.setText(upperString);

        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void forgotPwdMsg(String msg){
        Builder builder = initAlertDialog("", msg);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
//                Opener.SignIn(activity);
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private Builder initAlertDialog(String title, String msg) {
        Builder builder = new Builder(context);
        if (title.isEmpty()) {
            title = context.getResources().getString(R.string.app_name);
        }
        builder.setTitle(title);
        builder.setMessage(msg);
        return builder;
    }

    public void showThankYouMsg(String msg) {
        Builder builder = initAlertDialog("", msg);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
//                opener.Submitted();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }


//    public void confirmLogout(){
//        Builder builder = initAlertDialog("", context.getString(R.string.msg_confirm_logout));
//        builder.setPositiveButton(context.getResources().getString(R.string.yes), new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dismiss();
//                if (prefs.getBoolValues(CommonDef.SharedPrefKeys.IS_LOGGED_IN)) {
//                    String accessToken = prefs.getStringValues(CommonDef.SharedPrefKeys.ACCESS_TOKEN).toString();
//                    if (!accessToken.isEmpty()) {
//                        //FB logout
//
//                        FacebookSdk.sdkInitialize(context);
//                        LoginManager.getInstance().logOut();
//                    }
//                    UserHelper userHelper = new UserHelper((Activity)context);
//                    userHelper.clearUserInfo();
//                    opener.SplashScreen();
//                }
//            }
//        }).setNegativeButton(context.getString(R.string.no), new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dismiss();
//            }
//        });
//
//        alertDialog = builder.create();
//        alertDialog.show();
//    }
}
