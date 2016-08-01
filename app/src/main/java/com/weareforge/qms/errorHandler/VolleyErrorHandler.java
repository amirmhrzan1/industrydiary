package com.weareforge.qms.errorHandler;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.weareforge.qms.alerts.Alerts;

/**
 * Created by Rabin on 8/13/2015.
 */
public class VolleyErrorHandler {

    VolleyError error;
    Context context;
    Alerts alerts;

    public VolleyErrorHandler(VolleyError error, Context context) {
        this.error = error;
        this.context = context;
        alerts = new Alerts(context);
    }

    public void errorHandler(){
        if (error instanceof NoConnectionError) {
            alerts.showOkMessage("Internet connection not available.");
//            Toast.makeText(context, "No Connection", Toast.LENGTH_LONG).show();
        }else if (error instanceof TimeoutError) {
            alerts.showOkMessage("Network time out.");
//            Toast.makeText(context, "Network time out", Toast.LENGTH_LONG).show();
        } else if (error instanceof AuthFailureError) {
            alerts.showOkMessage("AuthFailureError");
//            Toast.makeText(context, "AuthFailureError", Toast.LENGTH_LONG).show();
        } else if (error instanceof ServerError) {
            alerts.showOkMessage("ServerError");
//            Toast.makeText(context, "ServerError", Toast.LENGTH_LONG).show();
        } else if (error instanceof NetworkError) {
            alerts.showOkMessage("NetworkError");
//            Toast.makeText(context, "NetworkError", Toast.LENGTH_LONG).show();
        } else if (error instanceof ParseError) {
            alerts.showOkMessage("ParseError");
//            Toast.makeText(context, "ParseError", Toast.LENGTH_LONG).show();
        }
        VolleyLog.e("Volley error: ", error.getMessage());
    }
}
