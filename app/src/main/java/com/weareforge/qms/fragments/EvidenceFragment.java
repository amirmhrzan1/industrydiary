package com.weareforge.qms.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.android.volley.Request;
import com.weareforge.qms.AWS.DownloadActivity;

import com.weareforge.qms.Objects.EngagementEvidenceData;
import com.weareforge.qms.Objects.UploadedImage;
import com.weareforge.qms.R;
import com.weareforge.qms.activities.NewIndustyActivity;
import com.weareforge.qms.adapters.MyPageAdapter;
import com.weareforge.qms.alerts.Alerts;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.db.EngagementEvidenceDbHandler;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.helpers.Util;
import com.weareforge.qms.utils.ConnectionMngr;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.Opener;
import com.weareforge.qms.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

/**
 * Created by Admin on 1/7/2016.
 */
public class EvidenceFragment extends Fragment {

    private Button btnSave;
    public static Button btnUpload;
    Intent intent;

    private TextView tvFrgmentTitle;
    private TextView tvTextEvidence;
    private TextView tvLinks;
    private TextView textEvidenceInfo;

    private String path;

    @NotEmpty(messageId = R.string.err_msg_evidence, order = 1)
    private EditText edtTextEvidence;

    @NotEmpty(messageId = R.string.err_msg_links, order = 2)
    private EditText edtLinks;

    public static TextView selectedImage;

    private String save = "Saving...";

    private FontHelper fontHelper;
    private ConnectionMngr connectionMngr;
    private VolleyRequest vsrData;
    private VolleyRequest vsr;
    private HashMap<String,String> paramsData;
    private HashMap<String,String> params;

    private SharedPreference sharedPreference;
    private Alerts alerts;
    private UrlHelper urlHelper;
    private TransferUtility transferUtility;
    private EngagementEvidenceDbHandler dbHelper;

    private ArrayList<String> listEtag;
    private String fileEtag="";
    private String userid;
    private String token;
    private String Engagement_Evidence_Id;
    private String evidenceId;
    private int flag;

    private EngagementEvidenceData engagementEvidenceData;

    public static String filePath= "";

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    //S3 client
    public AmazonS3Client s3Client;

    ProgressDialog progressBar;

    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, Object>> transferRecordMaps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_evidence, container, false);
        //  CommonMethods.setupUI(rootView.findViewById(R.id.relativeLayoutEvidence), getActivity());

        //Initialize
        this.tvFrgmentTitle = (TextView) rootView.findViewById(R.id.tvFragmentTitle);
        this.tvTextEvidence = (TextView) rootView.findViewById(R.id.tvTextEvidence);
        this.tvLinks = (TextView) rootView.findViewById(R.id.tvLinks);
        this.edtTextEvidence = (EditText) rootView.findViewById(R.id.edtTextEvidence);
        this.edtLinks = (EditText) rootView.findViewById(R.id.edtLinks);
        this.textEvidenceInfo = (TextView) rootView.findViewById(R.id.tvTextEvidenceInfo);

        this.btnUpload = (Button) rootView.findViewById(R.id.imgBtn_upload);
        this.selectedImage = (TextView) rootView.findViewById(R.id.txtSelectedImage);
        this.btnSave = (Button) rootView.findViewById(R.id.btnSave);

        //Font Helpers
        progressBar = new ProgressDialog(getActivity());
        fontHelper = new FontHelper(getActivity());

        this.tvFrgmentTitle.setTypeface(fontHelper.getDefaultFont());
        this.tvTextEvidence.setTypeface(fontHelper.getDefaultFont());
        this.tvLinks.setTypeface(fontHelper.getDefaultFont());
        this.edtTextEvidence.setTypeface(fontHelper.getDefaultFont());
        this.edtLinks.setTypeface(fontHelper.getDefaultFont());
        this.btnSave.setTypeface(fontHelper.getDefaultFont());
        this.textEvidenceInfo.setTypeface(fontHelper.getDefaultFont());
        this.btnUpload.setTypeface(fontHelper.getDefaultFont("bold"));

        // Initializes TransferUtility, always do this before using it.
        transferUtility = Util.getTransferUtility(getActivity());
        s3Client = Util.getS3Client(getActivity());

        sharedPreference = new SharedPreference(getActivity());
        this.token = sharedPreference.getStringValues("token");
        this.userid = sharedPreference.getStringValues("userid");
        flag = sharedPreference.getIntValues("flag");
        this.Engagement_Evidence_Id = sharedPreference.getStringValues("Engagement_Evidence_Id");
        this.evidenceId=sharedPreference.getStringValues("Evidence_Id");

        connectionMngr = new ConnectionMngr(getActivity());
        alerts = new Alerts(getActivity());
        dbHelper = new EngagementEvidenceDbHandler(getActivity());
        engagementEvidenceData = new EngagementEvidenceData();

        transferRecordMaps = new ArrayList<HashMap<String, Object>>();

        if(flag==1) {
            loadData();
        }
        loadEvidence();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if (!sharedPreference.getStringValues("Engagement_Evidence_Id").equalsIgnoreCase("")) {
                    if (!fileEtag.equalsIgnoreCase("")) {
                        saveAndNext();
                    } else {
                        alerts.showOkMessage("Please choose photo, video or document");
                    }
                } else
                    alerts.showCreateEvidenceOkMessage("Please create evidence before proceed");
            }
        });


        this.btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*    final CharSequence[] items = {"AWS Webservice", "From Device", "Cancel"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Choose Files From");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item) {
                                case 0:
                                    intent = new Intent(getActivity(), DownloadActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putStringArrayListExtra("listEtag", listEtag);
                                    startActivityForResult(intent, 1);


                                    break;
                                case 1:
                                    intent = new Intent(getActivity(), UploadActivity.class);
                                    intent.putExtra("mBool", true);
                                    startActivityForResult(intent, 1);
                                    // startActivityForResult(intent, CommonDef.PICTURE_REQUEST_CODE);

                                    break;
                                case 2:
                                    break;
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();*/
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= 19) {
                    // For Android KitKat, we use a different intent to ensure
                    // we can
                    // get the file path from the returned intent URI

                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    intent.setType("*/*");
                } else {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                }
                startActivityForResult(intent, CommonDef.PICTURE_REQUEST_CODE);
            }

        });

        return rootView;
    }



    private void loadEvidence() {
        if(connectionMngr.hasConnection()) {
            paramsData = new HashMap<String, String>();
            paramsData.put("token", token);
            paramsData.put("userid", userid);
            paramsData.put("engagement_evidence_id", "");
            paramsData.put("tas",1+"");

            vsrData = new VolleyRequest(getActivity(), Request.Method.POST, paramsData, urlHelper.EVIDENCE_LIST, false, CommonDef.REQUEST_EVIDENCE_LIST,getResources().getString(R.string.loadingFromQMS));
            vsrData.asyncInterface = (NewIndustyActivity) getActivity();
            vsrData.request();
        }
    }

    private void loadData() {
        if(connectionMngr.hasConnection())
        {
            paramsData = new HashMap<String,String>();
            params = new HashMap<String,String>();

            params.put("token", token);
            params.put("userid", userid);
            params.put("engagement_evidence_id", sharedPreference.getStringValues("Engagement_Evidence_Id"));

            vsr = new VolleyRequest(getActivity(),Request.Method.POST, params,urlHelper.EVIDENCE_LIST,false,CommonDef.REQUEST_EVIDENCE_DATA,getResources().getString(R.string.loadingFromQMS));
            vsr.asyncInterface = (NewIndustyActivity) getActivity();
            vsr.request();
        }
    }

    private void saveAndNext() {
        final boolean isValid = FormValidator.validate(EvidenceFragment.this, new SimpleErrorPopupCallback(getActivity(), true));
        if (isValid) {
            if (connectionMngr.hasConnection()) {
                paramsData = new HashMap<String, String>();

                paramsData.put("token", token);
                paramsData.put("userid", userid);
                if(!evidenceId.equalsIgnoreCase(""))
                {
                    paramsData.put("evidence_id", String.valueOf(evidenceId));
                    this.save="Updating...";
                }
                paramsData.put("engagement_evidence_id", sharedPreference.getStringValues("Engagement_Evidence_Id"));
                paramsData.put("text_evidence", edtTextEvidence.getText().toString());
                paramsData.put("links", edtLinks.getText().toString());
                paramsData.put("file_id", fileEtag);
                paramsData.put("tas", 1 + "");

                vsrData = new VolleyRequest(getActivity(), Request.Method.POST, paramsData, urlHelper.EVIDENCE_UPSERT, false, CommonDef.REQUEST_EVIDENCE_UPSERT,this.save);
                vsrData.asyncInterface = (NewIndustyActivity) getActivity();
                vsrData.request();

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CommonDef.PICTURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                try {
                    path = getPath(uri);
                    beginUpload(path);

                } catch (URISyntaxException e) {
                }
            }
        }
    }

    /*
   * Begins to upload the file specified by the file path.
   */
    private void beginUpload(String filePath) {
        progressBar.setMessage("Uploading...");
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setCancelable(false);
        progressBar.setIndeterminate(false);
        progressBar.show();
        if (filePath == null) {
            Toast.makeText(getActivity(), "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        TransferObserver observer = transferUtility.upload(CommonDef.BUCKET_NAME, file.getName(),
                file);
        observer.setTransferListener(new UploadListener());
    }


    public void evidenceUpsert(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            if(response_int==0) {
                JSONObject evidenceData = jObjResult.getJSONObject("evidenceData");
                if(evidenceId.equalsIgnoreCase(""))
                {
                    Toast.makeText(getActivity(), "Step 4 information saved", Toast.LENGTH_LONG).show();
                    evidenceId = evidenceData.getString("Evidence_Id");
                    sharedPreference.setKeyValues("Evidence_Id",evidenceId);
                }
                else
                {
                    Toast.makeText(getActivity(), "Step 4 information updated", Toast.LENGTH_LONG).show();
                }
                NewIndustyActivity newIndustyActivity = new NewIndustyActivity();
                if(sharedPreference.getIntValues("Current_Step") < 4)
                {
                    sharedPreference.setKeyValues("Current_Step",4);
                    MyPageAdapter adapter = (MyPageAdapter)newIndustyActivity.mPager.getAdapter();
                    newIndustyActivity.currentStep = 4;
                    newIndustyActivity.mPager.getAdapter().notifyDataSetChanged();
                    adapter.currentPage(4);
                }
                newIndustyActivity.mPager.setCurrentItem(4);
            }
            else
            {
               // Toast.makeText(getActivity(),responseText,Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadEvidenceList(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            listEtag = new ArrayList<String>();
            if(response_int==0) {
                JSONArray JSONlist = jObjResult.getJSONArray("evidenceData");
                for(int i=0;i<JSONlist.length();i++)
                {
                    JSONObject etag = JSONlist.getJSONObject(i);
                    listEtag.add(etag.getString("file_id"));
                }
                Opener opener = new Opener(getActivity());

            }
            System.out.println("Prajit is testing, Etag==" + listEtag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
            }

    public void loadEvidenceData(String result) {

        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            if(response_int==0) {
                JSONArray JSONlist = jObjResult.getJSONArray("evidenceData");
                JSONObject jObject = JSONlist.getJSONObject(0);
                edtTextEvidence.setText(jObject.getString("Text_Evidence"));
                edtLinks.setText(jObject.getString("Links"));
                fileEtag = jObject.getString("file_id");
                this.evidenceId = jObject.getString("Evidence_Id");
                sharedPreference.setKeyValues("Evidence_Id", this.evidenceId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestSaveProgress(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            if (response_int == 0) {
                JSONObject engagementData = jObjResult.getJSONObject("engagementEvidenceData");
                Toast.makeText(getActivity(), "Progress saved", Toast.LENGTH_LONG).show();

                dbHelper.Open();
                dbHelper.UpdateEngagementEvidenceData(engagementEvidenceData);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    * Gets the file path of the given Uri.
    */
    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getActivity().getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[] {
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /*
    * A TransferListener class that can listen to a upload task and be notified
    * when the status changes.
    */
    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e("TAG", "Error during upload: " + id, e);

        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            double temp = (double)bytesCurrent/bytesTotal;
            long Percentage = (long) (temp*100);
            progressBar.setProgress((int) Percentage);
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {

            if(newState.toString().equalsIgnoreCase("COMPLETED"))
            {
                GetEtag getEtag = new GetEtag();
                getEtag.execute(path);
                UploadedImage.filepath = path;

            }
        }
    }

    public class GetEtag extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            File file = new File(params[0]);
            S3Object imagess = s3Client.getObject(CommonDef.BUCKET_NAME, file.getName());
            ObjectMetadata myObj = imagess.getObjectMetadata();
            String tag = myObj.getETag();
            Log.d("ETag==", tag);

            return tag;
        }

        @Override
        protected void onPostExecute(String tag) {
            super.onPostExecute(tag);
            progressBar.dismiss();
            fileEtag = tag;
            // Toast.makeText(getApplicationContext(),"Etag==" + tag , Toast.LENGTH_LONG).show();

        }
    }
}
