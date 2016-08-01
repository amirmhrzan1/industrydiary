package com.weareforge.qms.asyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.weareforge.qms.R;
import com.weareforge.qms.activities.AddIndustryContactActivity;
import com.weareforge.qms.activities.EditIndustryContactActivity;
import com.weareforge.qms.activities.MyDetailsActivity;
import com.weareforge.qms.activities.MyIndustryContactsActivity;
import com.weareforge.qms.errorHandler.VolleyErrorHandler;
import com.weareforge.qms.fragments.IndustryContactFragment;
import com.weareforge.qms.helpers.HttpsTrustManager;
import com.weareforge.qms.interfaces.AsyncInterface;
import com.weareforge.qms.utils.AppController;
import com.weareforge.qms.helpers.CommonDef;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rabin on 8/13/2015.
 */
public class VolleyRequest {

    int method, requestCode;
    Activity activity;
    HashMap<String, String> params;
    HashMap<String, String> headers = new HashMap<>();
    boolean postImage = false;
    String url;
    public static AsyncInterface asyncInterface = null;
    VolleyErrorHandler volleyErrorHandler;

    ProgressDialog pd;

    private String loadingInfo = "Please wait...";

    File mImageFile;

    public boolean showPd = true;

    /**
     * @param activity
     * @param method      :: Post or Get
     * @param params      :: params to post
     * @param url         :: url to webservice
     * @param headers     :: headers to post
     * @param postImage   :: boolean value if to post the image or not
     * @param requestCode :: request type for return object
     */
    public VolleyRequest(Activity activity, int method,
                         HashMap<String, String> params, String url,
                         HashMap<String, String> headers, boolean postImage,
                         int requestCode,String loadingInfo) {
        this.activity = activity;
        this.method = method;
        this.params = params;
        this.url = url;
        this.headers = headers;
        this.postImage = postImage;
        this.requestCode = requestCode;
        this.loadingInfo = loadingInfo;
    }
    public VolleyRequest(Activity activity, int method, String url,
                         int requestCode) {
        this.activity = activity;
        this.method = method;
        this.url = url;
        this.requestCode = requestCode;
    }

    public VolleyRequest(Activity activity, int method,
                         HashMap<String, String> params, String url,
                         boolean postImage,
                         int requestCode,String loadingInfo) {
        this.activity = activity;
        this.method = method;
        this.params = params;
        this.url = url;
        this.postImage = postImage;
        this.requestCode = requestCode;
        this.loadingInfo = loadingInfo;

//        initProgressDialog();
    }

    public VolleyRequest(Activity activity, int method,
                         HashMap<String, String> params, String url,
                         boolean postImage,
                         int requestCode) {
        this.activity = activity;
        this.method = method;
        this.params = params;
        this.url = url;
        this.postImage = postImage;
        this.requestCode = requestCode;

//        initProgressDialog();
    }

    public VolleyRequest(Context context, int method,
                         HashMap<String, String> params, String url,
                         boolean postImage,
                         int requestCode,String loadingInfo) {
       // this.activity = (Activity) context;
        this.method = method;
        this.params = params;
        this.url = url;
        this.postImage = postImage;
        this.requestCode = requestCode;
        this.loadingInfo = loadingInfo;
        this.showPd = false;

//        initProgressDialog();
    }



    void initProgressDialog() {
        if (showPd) {
            pd = new ProgressDialog(activity);
            pd.setMessage(this.loadingInfo);
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }
    }

    public void request() {
        initProgressDialog();
        if (method == Request.Method.POST) {
            if (postImage) {
                mImageFile = new File(params.get("img_url").toString());
                doPostRequestWithImage();
            } else
                doPostRequest();
        } else {
            doGetRequest();
        }
    }

    private void doPostRequest() {

        HttpsTrustManager.allowAllSSL();

        System.out.println("Rabin is testing params: " + params.toString());
        System.out.println("Rabin is testing ::: Request sent for: "+requestCode);
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (showPd && pd.isShowing()) {
                    pd.hide();
                }
                System.out.println("Rabin is testing ::: Response catched for: "+requestCode);
                asyncInterface.processFinish(s, requestCode);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyErrorHandler = new VolleyErrorHandler(volleyError, activity);
                volleyErrorHandler.errorHandler();
                if (showPd && pd.isShowing())
                    pd.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                System.out.println("Params: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                System.out.println("Params: " + headers);
                return headers;
            }
        };

        RetryPolicy policy = new DefaultRetryPolicy(CommonDef.SOCKET_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    void doGetRequest() {
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                asyncInterface.processFinish(s, requestCode);
                pd.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyErrorHandler = new VolleyErrorHandler(volleyError, activity);
                volleyErrorHandler.errorHandler();
                pd.hide();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                System.out.println("Headers: " + headers);
                return headers;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(CommonDef.SOCKET_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    void doPostRequestWithImage() {
        new PostImageWithData().execute();
    }

    class PostImageWithData extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage(activity.getResources().getString(R.string.msg_loading));
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            pd.dismiss();
            asyncInterface.processFinish(string, requestCode);
//            System.out.println(CommonDef.TAG+string);
        }

        @Override
        protected String doInBackground(Void... paramss) {
            String result = "";
            return result;
        }
    }

    public int uploadFile(final String sourceFileUri) {
        int serverResponseCode = 0;
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {


            // file not found or source file not exists:


            return 0;

        } else {

            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("");
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                for(String key:params.keySet()){
                    String value=params.get(key);
                    dos.writeBytes("Content-Disposition: form-data; name=\"value\"" + lineEnd);
                    //dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                    //dos.writeBytes("Content-Length: " + name.length() + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(value); // mobile_no is String variable
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                }


//Adding Parameter media file(audio,video and image)

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    System.out.println("Response Msg: "+conn.getResponseMessage());
                    // file upload complete.
                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (final Exception e) {

                Log.e("Upload file Exception",
                        "Exception : " + e.getMessage(), e);
            }
            return serverResponseCode;
        }
    }


}
