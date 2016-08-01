/*
 * Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.weareforge.qms.AWS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.android.volley.Request;
import com.weareforge.qms.Objects.UploadedImage;
import com.weareforge.qms.R;
import com.weareforge.qms.activities.BaseActivity;
import com.weareforge.qms.alerts.Alerts;
import com.weareforge.qms.asyncTasks.VolleyRequest;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.CommonMethods;
import com.weareforge.qms.helpers.UrlHelper;
import com.weareforge.qms.helpers.Util;
import com.weareforge.qms.interfaces.AsyncInterface;
import com.weareforge.qms.utils.ConnectionMngr;
import com.weareforge.qms.utils.FontHelper;
import com.weareforge.qms.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * UploadActivity is a ListActivity of uploading, and uploaded records as well
 * as buttons for managing the uploads and creating new ones.
 */
public class UploadActivity extends BaseActivity implements OnClickListener, AsyncInterface {

    // Indicates that no upload is currently selected
    private static final int INDEX_NOT_CHECKED = -1;

    // TAG for logging;
    private static final String TAG = "UploadActivity";

    ProgressDialog progressBar;

    private Button btnUploadTas;
    private TextView txtUpload;
    private Button btnViewTasPlan;
    private TextView txtUploadInfo;
    private  TextView txtUploadTitle;

    private TextView txtBack;
    private ImageButton btn_back;
    GridView gridView;
    private int flag;

    private FontHelper fontHelper;

    private ConnectionMngr connectionMngr;
    private Alerts alerts;
    private HashMap<String,String> paramsData;
    private VolleyRequest vsrData;
    private UrlHelper urlHelper;

    private static String uploadedFilePath = "";


    private SharedPreference sharedPreference;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;

    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;

    // A List of all transfers
    private List<TransferObserver> observers;

    //S3 client
    public AmazonS3Client s3Client;

    private String path;
    private String userid;
    private String token;

    private static String checkState = null;

    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, Object>> transferRecordMaps;

    // Which row in the UI is currently checked (if any)
    private int checkedIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_upload_tas;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        //Full screen Window
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initializes TransferUtility, always do this before using it.
        transferUtility = Util.getTransferUtility(this);
        s3Client = Util.getS3Client(this);

        // Get the data from any transfer's that have already happened,
        initUI();
        initData();
       // checkUpload();

//        gridView.setOnItemClickListener(onMyclickListner);
    }

    /**
     * Gets all relevant transfers from the Transfer Service for populating the
     * UI
     */
    private void initData() {

        sharedPreference = new SharedPreference(this);
        this.token = sharedPreference.getStringValues("token");
        this.userid = sharedPreference.getStringValues("userid");
        progressBar = new ProgressDialog(this);
        connectionMngr = new ConnectionMngr(this);
        flag = sharedPreference.getIntValues("Uploadflag");
        checkedIndex = INDEX_NOT_CHECKED;
        transferRecordMaps = new ArrayList<HashMap<String, Object>>();

        // Use TransferUtility to get all upload transfers.
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
        for (TransferObserver observer : observers) {

            // For each transfer we will will create an entry in
            // transferRecordMaps which will display
            // as a single row in the UI
            HashMap<String, Object> map = new HashMap<String, Object>();
            Util.fillMap(map, observer, false);
            transferRecordMaps.add(map);
            // We only care about updates to transfers that are in a
            // non-terminal state
            if (!TransferState.COMPLETED.equals(observer.getState())
                    && !TransferState.FAILED.equals(observer.getState())
                    && !TransferState.CANCELED.equals(observer.getState())) {

                observer.setTransferListener(new UploadListener());
            }
        }
    }

    private void initUI() {
        /**
         * This adapter takes the data in transferRecordMaps and displays it,
         * with the keys of the map being related to the columns in the adapter
         *
         */
        this.btnUploadTas = (Button) findViewById(R.id.btnUploadTAS);
        this.btnViewTasPlan = (Button) findViewById(R.id.btnViewTas);
        this.txtUploadInfo = (TextView) findViewById(R.id.txtUploadInfo);
        this.txtUploadTitle = (TextView) findViewById(R.id.uploadTitle);


        this.btn_back = (ImageButton) findViewById(R.id.back_img);
        this.txtBack = (TextView) findViewById(R.id.txt_back);
        this.gridView = (GridView) findViewById(R.id.gridlist);

        //Visible Buttons
        this.btn_back.setVisibility(View.VISIBLE);
        this.txtBack.setVisibility(View.VISIBLE);

        //Font Helper
        fontHelper = new FontHelper(this);
        this.txtUploadTitle.setTypeface(fontHelper.getDefaultFont());
        this.txtBack.setTypeface(fontHelper.getDefaultFont("bold"));
        this.txtUploadInfo.setTypeface(fontHelper.getDefaultFont("bold"));
        this.btnUploadTas.setTypeface(fontHelper.getDefaultFont("bold"));
        this.btnViewTasPlan.setTypeface(fontHelper.getDefaultFont("bold"));

        //Onclick Listner
        this.btnUploadTas.setOnClickListener(this);
        this.btn_back.setOnClickListener(this);
        this.txtBack.setOnClickListener(this);

        simpleAdapter = new SimpleAdapter(this, transferRecordMaps,
                R.layout.record_item, new String[]{
                "checked", "fileName","fileName","fileName", "progress", "bytes", "state", "percentage"
        },
                new int[]{
                        R.id.radioButton1, R.id.UploadVideo,R.id.UploadImage,R.id.btnPlay,R.id.progressBar1, R.id.textBytes,
                        R.id.textState, R.id.textPercentage
                });

        simpleAdapter.setViewBinder(new ViewBinder() {

            @Override
            public boolean setViewValue(View view, final Object data,
                                        String textRepresentation) {
                switch (view.getId()) {
                    case R.id.radioButton1:
                        RadioButton radio = (RadioButton) view;
                        radio.setChecked((Boolean) data);
                        return true;
                    case R.id.UploadVideo:
                        String extension = CommonMethods.getExtension((String) data);
                        if(extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mpeg")) {
                            VideoView videoFile = (VideoView) view;
                            videoFile.setVisibility(View.VISIBLE);
                            Bitmap thumb = ThumbnailUtils.createVideoThumbnail((String) data,
                                    MediaStore.Images.Thumbnails.MINI_KIND);
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                            videoFile.setBackgroundDrawable(bitmapDrawable);

                            videoFile.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    File file = new File((String) data);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(file), "video/*");
                                    startActivity(intent);
                                }
                            });

                        }
                        return true;

                    case R.id.btnPlay:
                        extension = CommonMethods.getExtension((String) data);
                        if(extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mpeg")) {
                            ImageView imageView = (ImageView) view;
                            imageView.setVisibility(View.VISIBLE);

                            imageView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    File file = new File((String) data);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(file), "video/*");
                                    startActivity(intent);
                                }
                            });
                        }
                        return true;

                    case R.id.UploadImage:
                        extension = CommonMethods.getExtension((String) data);
                        if(extension.equalsIgnoreCase("jpg") ||extension.equalsIgnoreCase("png")){
                            ImageView imageFile = (ImageView) view;
                            view.setVisibility(View.VISIBLE);
                            final String thumbnailPath = getThumbnailPath((String) data);
                            imageFile.setImageURI(Uri.parse(thumbnailPath));
                            if (checkState == "COMPLETED") {
                                imageFile.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                   /* Boolean paused = transferUtility.pause(observers.indexOf()
                                            .getId());

                                    if(!paused)
                                    {
                                        TransferObserver resumed = transferUtility.resume(observers.get(checkedIndex)
                                                .getId());
                                    }*/
                                    /*File file = new File((String) data);
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.parse("file://"+file.getAbsolutePath()), "image*//*");
                                    System.out.println("path==" +(String) data+"--"+file.getAbsolutePath());
                                    startActivity(intent);*/
//                                        GetEtag getEtag = new GetEtag();
//                                        getEtag.execute();
                                    }
                                });
                            }

                            if (checkState == "COMPLETED") {
                                txtUploadTitle.setText("You have already Uploaded a TAS plan");
                                btnViewTasPlan.setVisibility(View.VISIBLE);
                                btnUploadTas.setText("UPLOAD NEW TAS PLAN");
                                txtUploadInfo.setVisibility(View.VISIBLE);
                                File file = new File((String) data);
                                uploadedFilePath = file.getAbsolutePath();

                                btnViewTasPlan.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

//                                        Dialog dialog = new Dialog(UploadActivity.this);
                                        final Dialog dialog = new Dialog(UploadActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                     //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.alert_display_image);
                                        ImageView img = (ImageView) dialog.findViewById(R.id.displayImage);
                                        File file = new File((String) data);
                                        img.setImageURI(Uri.parse("file://"+file.getAbsolutePath()));
                                        ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
                                        cross.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.setCancelable(true);

                                        dialog.show();


                                       /* File file = new File((String) data);
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.parse("file://"+file.getAbsolutePath()), "image*//*");
                                        System.out.println("path==" +(String) data+"--"+file.getAbsolutePath());
                                        startActivity(intent);*/
                                    }
                                });


                            }

                        }
                        return true;

                    case R.id.progressBar1:
                        ProgressBar progress = (ProgressBar) view;
                        progress.setProgress((Integer) data);
                        return true;
                    case R.id.textBytes:
                        TextView bytes = (TextView) view;
                        bytes.setText((String) data);
                        return true;
                    case R.id.textState:
                        TextView state = (TextView) view;
                        state.setText(((TransferState) data).toString());
                        checkState = state.getText().toString();

                        return true;
                    case R.id.textPercentage:
                        TextView percentage = (TextView) view;
                        percentage.setText((String) data);
                        return true;
                }
                return false;
            }
        });
//        gridView.setAdapter(simpleAdapter);
    }

    public String getThumbnailPath(String path)
    {
        long imageId = -1;

        String[] projection = new String[] { MediaStore.MediaColumns._ID };
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[] { path };
        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst())
        {
            imageId = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
        }

        String result = null;
        cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(this.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            cursor.close();
        }

        return result;
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==this.btnUploadTas.getId()) {


           PickImage();

        }

        else if (v.getId() == this.btn_back.getId()) {
            v.startAnimation(buttonClick);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        } else if (v.getId() == this.txtBack.getId()) {
            v.startAnimation(buttonClick);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    //Image picker form device
    private void PickImage()
    {
        if(this.btnViewTasPlan.isShown()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Uploading a new TAS plan will delete your current TAS plan.");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DeleteAndUplopadNew uploadNew = new DeleteAndUplopadNew();
                            uploadNew.execute();

                        }
                    });

            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        else
        {
           ChooseImage();
        }

    }

    public class DeleteAndUplopadNew extends AsyncTask<Void,Void,Integer>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(UploadActivity.this);
            progressBar.setMessage("Deleting...");
            progressBar.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int integer;
            for (int i = 0; i < observers.size(); i++) {
                try {
                    //deleting the file form AWS bucket and updating the GridView
                    Util.getS3Client(getApplication()).deleteObject(new DeleteObjectRequest(CommonDef.BUCKET_NAME, new File(observers.get(i).getAbsoluteFilePath()).getName()));
                    transferUtility.deleteTransferRecord(observers.get(i).getId());
                    observers.remove(i);
                    transferRecordMaps.remove(i);
                } catch (AmazonServiceException ase) {
                    System.out.println("Caught an AmazonServiceException.");
                    System.out.println("Error Message:    " + ase.getMessage());
                    System.out.println("HTTP Status Code: " + ase.getStatusCode());
                    System.out.println("AWS Error Code:   " + ase.getErrorCode());
                    System.out.println("Error Type:       " + ase.getErrorType());
                    System.out.println("Request ID:       " + ase.getRequestId());
                } catch (AmazonClientException ace) {
                    System.out.println("Caught an AmazonClientException.");
                    System.out.println("Error Message: " + ace.getMessage());
                }
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressBar.dismiss();
            sharedPreference.setKeyValues("Uploadflag", 0);
            flag  = 0;
            btnViewTasPlan.setVisibility(View.GONE);
            btnUploadTas.setText("Upload TAS Plan");
            txtUploadInfo.setVisibility(View.GONE);
            simpleAdapter.notifyDataSetChanged();
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= 19) {
                // For Android KitKat, we use a different intent to ensure
                // we can
                // get the file path from the returned intent URI
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                intent.setType("image/*|pdf/*");
                intent.setType("*/*");
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*|pdf/*");
                intent.setType("file/*");

            }
            startActivityForResult(intent, 0);
        }
    }



    @Override
    public void processFinish(String result, int requestCode) {
        switch (requestCode)
        {
            case CommonDef.REQUEST_EVIDENCE_UPSERT:
                UploadTasResponse(result);
                break;
        }
    }

    public void UploadTasResponse(String result) {
        JSONObject jObjResult;
        try {
            jObjResult = new JSONObject(result);
            String response = jObjResult.getString("response");
            String responseText = jObjResult.getString("responseText");
            int response_int = Integer.parseInt(response);
            if(response_int==0) {
                Toast.makeText(this,"File Uploaded Successfully",Toast.LENGTH_LONG).show();
            }
            else
            {
                 Toast.makeText(this,"File Upload Failed",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ChooseImage()
    {

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
            startActivityForResult(intent, 0);
        }


    public class GetEtag extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setMessage("Getting Etag...");
//            progressBar.show();
        }

        @Override
        protected String doInBackground(String... params) {
            File file = new File(params[0]);
            S3Object imagess = s3Client.getObject(CommonDef.BUCKET_NAME,file.getName());
            ObjectMetadata myObj = imagess.getObjectMetadata();
            String tag = myObj.getETag();
            Log.d("ETag==" , tag);

            return tag;
        }

        @Override
        protected void onPostExecute(String tag) {
            super.onPostExecute(tag);
//            progressBar.dismiss();
            UploadTas(tag);
        }
    }

    private void UploadTas(String fileEtag) {

            if (connectionMngr.hasConnection()) {
                paramsData = new HashMap<String, String>();

                paramsData.put("token", token);
                paramsData.put("userid", userid);


                paramsData.put("text_evidence", "");
                paramsData.put("links", "");
                paramsData.put("file_id", fileEtag);
                paramsData.put("tas", 1 + "");

                vsrData = new VolleyRequest(this, Request.Method.POST, paramsData, urlHelper.EVIDENCE_UPSERT, false, CommonDef.REQUEST_EVIDENCE_UPSERT,getResources().getString(R.string.save));
                vsrData.asyncInterface = this;
                vsrData.request();

            }
    }


    /*
     * Updates the ListView according to the observers.
     */
    private void updateList() {
        TransferObserver observer = null;
        HashMap<String, Object> map = null;
        for (int i = 0; i < observers.size(); i++) {
            observer = observers.get(i);
            map = transferRecordMaps.get(i);
            Util.fillMap(map, observer, i == checkedIndex);
        }
        simpleAdapter.notifyDataSetChanged();

    }

    /*
     * A TransferListener class that can listen to a upload task and be notified
     * when the status changes.
     */
    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
//            updateList();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            updateList();

            double temp = (double)bytesCurrent/bytesTotal;
            long Percentage = (long) (temp*100);
            progressBar.setProgress((int) Percentage);

        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
//            updateList();
            if(newState.toString().equalsIgnoreCase("COMPLETED"))
            {
                    progressBar.dismiss();

                    GetEtag getEtag = new GetEtag();
                    getEtag.execute(path);

                txtUploadTitle.setText("You have already Uploaded a TAS plan");
                btnViewTasPlan.setVisibility(View.VISIBLE);
                btnUploadTas.setText("UPLOAD NEW TAS PLAN");
                txtUploadInfo.setVisibility(View.VISIBLE);

                btnViewTasPlan.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                    /* *//*                   Dialog dialog = new Dialog(UploadActivity.this);
//                        final Dialog dialog = new Dialog(UploadActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                        //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                        dialog.setContentView(R.layout.alert_display_image);
//                        ImageView img = (ImageView) dialog.findViewById(R.id.displayImage);
//                        File file = new File(path);
//                        img.setImageURI(Uri.parse("file://"+file.getAbsolutePath()));
//                        ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
//                        cross.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.setCancelable(true);
//
//                        dialog.show();
                        String extension = CommonMethods.getExtension(path);

                        if(extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {

                            final Dialog dialog = new Dialog(UploadActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                            //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.alert_display_image);
                            ImageView img = (ImageView) dialog.findViewById(R.id.displayImage);
                            File file = new File(path);
                            img.setImageURI(Uri.parse("file://" + file.getAbsolutePath()));
                            ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
                            cross.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.setCancelable(true);

                            dialog.show();
                        }
                        else if(extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mpeg"))
                        {
                            final Dialog dialog = new Dialog(UploadActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                            //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.alert_play_video);
                            final VideoView video = (VideoView) dialog.findViewById(R.id.playVideo);
                            File file = new File(path);
                            video.setVideoPath(file.getAbsolutePath());
                            video.start();
                            ImageView cross = (ImageView) dialog.findViewById(R.id.crossImage);
                            cross.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.setCancelable(true);
                            dialog.show();
                        }
                        else
                        {
                            File file = new File(path);
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()), "application/pdf");
                            // System.out.println("path==" + (String) data + "--" + file.getAbsolutePath());
                            startActivity(intent);
                        }


                                       /* File file = new File((String) data);
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.parse("file://"+file.getAbsolutePath()), "image*//*");
                                        System.out.println("path==" +(String) data+"--"+file.getAbsolutePath());
                                        startActivity(intent);*/
                    }
                });


            }

            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        try {
                            path = getPath(uri);
                            sharedPreference.setKeyValues("Uploadflag", 0);
                            flag  = 0;
                            btnViewTasPlan.setVisibility(View.GONE);
                            btnUploadTas.setText("Upload TAS Plan");
                            txtUploadInfo.setVisibility(View.GONE);
                            simpleAdapter.notifyDataSetChanged();
                            beginUpload(path);
                         //   checkUpload();

                        } catch (Exception e) {
                            Toast.makeText(this,
                                    "Unable to get the file from the given URI.  See error log for details",
                                    Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Unable to upload file from the given uri", e);
                        }
                    }
                }
                else
                {
                    Uri uri = data.getData();
                    try {
                        sharedPreference.setKeyValues("Uploadflag", 0);
                        flag  = 0;
                        btnViewTasPlan.setVisibility(View.GONE);
                        btnUploadTas.setText("Upload TAS Plan");
                        txtUploadInfo.setVisibility(View.GONE);
                        simpleAdapter.notifyDataSetChanged();
                        path = getPath(uri);
                        beginUpload(path);
                   //     checkUpload();

                    } catch (URISyntaxException e) {
                        Toast.makeText(this,
                                "Unable to get the file from the given URI.  See error log for details",
                                Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Unable to upload file from the given uri", e);
                    }
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
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            progressBar.dismiss();
            return;
        }
        File file = new File(filePath);
        TransferObserver observer = transferUtility.upload(CommonDef.BUCKET_NAME, file.getName(),
                file);


        observers.add(observer);
        HashMap<String, Object> map = new HashMap<String, Object>();
        Util.fillMap(map, observer, false);
        transferRecordMaps.add(map);
        observer.setTransferListener(new UploadListener());
//        simpleAdapter.notifyDataSetChanged();

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
        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
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
                cursor = getContentResolver()
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

}
