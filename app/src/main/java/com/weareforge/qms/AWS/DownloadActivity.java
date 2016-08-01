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

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.weareforge.qms.Objects.UploadedImage;
import com.weareforge.qms.R;
import com.weareforge.qms.activities.BaseActivity;
import com.weareforge.qms.helpers.CommonDef;
import com.weareforge.qms.helpers.CommonMethods;
import com.weareforge.qms.helpers.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DownloadActivity displays a list of download records and a bunch of buttons
 * for managing the downloads.
 */
public class DownloadActivity extends BaseActivity implements OnClickListener {

    private static final int DOWNLOAD_SELECTION_REQUEST_CODE = 1;

    // Indicates no row element has beens selected
    private static final int INDEX_NOT_CHECKED = -1;
    //S3 client
    public AmazonS3Client s3Client;
    String checedkstate = "";
    ProgressDialog progressBar;
    ArrayList<String> list;
    private Button btnDownload;
    private Button btnPause;
    private Button btnResume;
    private Button btnCancel;
    private Button btnDelete;
    private Button btnPauseAll;
    private Button btnCancelAll;
    private Button downloadOwnTAS;
    private TextView txtBack;
    private ImageButton btn_back;
    private GridView gridView;
    private GridView gridviewDownloadedImages;
    private String filePath;
    private ArrayList<String> downloadedImages;
    // This is the main class for interacting with the Transfer Manager
    private TransferUtility transferUtility;
    // The SimpleAdapter adapts the data about transfers to rows in the UI
    private SimpleAdapter simpleAdapter;
    // A List of all transfers
    private List<TransferObserver> observers;
    //GridView itemClickListner
    OnItemClickListener onMyclickListner = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            try {
                if (observers.get(position).getState().toString().equalsIgnoreCase("PAUSED")) {
                    TransferObserver resumed = transferUtility.resume(observers.get(position)
                            .getId());
                } else if (observers.get(position).getState().toString().equalsIgnoreCase("IN PROGRESS")) {
                    Boolean paused = transferUtility.pause(observers.get(position).getId());
                }
            } catch (Exception exx) {

            }

        }
    };
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);
    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, Object>> transferRecordMaps;
    private ArrayList<String> etaglist;
    private int checkedIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializes TransferUtility, always do this before using it.

        getSupportActionBar().hide();
        //Full screen Window
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        transferUtility = Util.getTransferUtility(this);
        initData();
        initUI();

//        if(!UploadedImage.isDownloaded) {
        Intent i = getIntent();
        list = new ArrayList<String>();
        list = i.getStringArrayListExtra("listEtag");

        Object[] st = list.toArray();
        for (Object s : st) {
            if (list.indexOf(s) != list.lastIndexOf(s)) {
                list.remove(list.lastIndexOf(s));
            }
        }

        ListObject listObject = new ListObject();
        listObject.execute();
        // }
    }

    /**
     * Gets all relevant transfers from the Transfer Service for populating the
     * UI
     */
    private void initData() {
        s3Client = Util.getS3Client(this);
        downloadedImages = new ArrayList<String>();
        checkedIndex = INDEX_NOT_CHECKED;
        transferRecordMaps = new ArrayList<HashMap<String, Object>>();
        observers = new ArrayList<TransferObserver>();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUI() {
        /**
         * This adapter takes the data in transferRecordMaps and displays it,
         * with the keys of the map being related to the columns in the adapter
         */
        this.btn_back = (ImageButton) findViewById(R.id.back_img);
        this.txtBack = (TextView) findViewById(R.id.txt_back);
        this.progressBar = new ProgressDialog(this);

        //Visible Buttons
        this.btn_back.setVisibility(View.VISIBLE);
        this.txtBack.setVisibility(View.VISIBLE);

        this.gridView = (GridView) findViewById(R.id.gridDownloadList);
        this.downloadOwnTAS = (Button) findViewById(R.id.buttonDownloadTAS);
        this.gridviewDownloadedImages = (GridView) findViewById(R.id.gridAlreadyDowloadList);

        this.btn_back.setOnClickListener(this);
        this.txtBack.setOnClickListener(this);

        this.downloadOwnTAS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        gridView.setOnItemClickListener(onMyclickListner);

        simpleAdapter = new SimpleAdapter(this, transferRecordMaps,
                R.layout.record_item, new String[]{
                "checked", "fileName", "fileName", "fileName", "progress", "bytes", "state", "percentage"
        },
                new int[]{
                        R.id.radioButton1, R.id.UploadVideo, R.id.UploadImage, R.id.btnPlay, R.id.progressBar1, R.id.textBytes,
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


                    case R.id.UploadImage:
                        ImageView imageView = (ImageView) view;
                        String extension = CommonMethods.getExtension((String) data);
                        if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png")) {
                            view.setVisibility(View.VISIBLE);
                            System.out.println("Data " + (String) data);
                            if (checedkstate.equalsIgnoreCase("COMPLETED")) {
                                String thumbnailPath = getThumbnailPath((String) data);
                                if (view != null) {
//                                    BitmapDrawable bitmapDrawable = new BitmapDrawable(DownloadActivity.this.getResources(),thumbnailPath);
//                                    imageView.setBackground(bitmapDrawable);
                                    //    imageView.setImageURI(Uri.parse((String) data));
                                    //  simpleAdapter.notifyDataSetChanged();
                                    File f = new File((String) data);

                                    Picasso.with(DownloadActivity.this)
                                            .load(f)
                                            .resize(100, 90)
                                            .into(imageView);
                                }
                             //   checedkstate = "";
                                imageView.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final CharSequence[] items = {"BROWSE", "CHOOSE", "Cancel"};
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DownloadActivity.this);
                                        builder.setTitle("Select Options");
                                        builder.setItems(items, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int item) {
                                                switch (item) {
                                                    case 0:
                                                        File file = new File((String) data);
                                                        final Dialog dialog1 = new Dialog(DownloadActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                                                        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                        dialog1.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                                        //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog1.setContentView(R.layout.alert_display_image);
                                                        ImageView img = (ImageView) dialog1.findViewById(R.id.displayImage);

                                                        img.setImageURI(Uri.parse("file://" + file.getAbsolutePath()));
                                                        ImageView cross = (ImageView) dialog1.findViewById(R.id.crossImage);
                                                        cross.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dialog1.dismiss();
                                                            }
                                                        });
                                                        //  dialog.setCancelable(true);
                                                        dialog1.show();

                                                        break;
                                                    case 1:
                                                        File file1 = new File((String) data);
                                                        filePath = file1.getAbsolutePath();
                                                        UploadedImage.filepath = filePath;
                                                        GetEtag etag = new GetEtag();
                                                        etag.execute((String) data);
                                                        transferUtility.cancelAllWithType(TransferType.DOWNLOAD);
                                                        break;
                                                    case 2:

                                                        break;
                                                }
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();

                                    }
                                });
                            } else {
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.loading));
                                imageView.setOnClickListener(null);
                              //  checedkstate = "";
                            }
                        }
                        else if (extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("txt") && extension.equalsIgnoreCase("docx")) {
                        view.setVisibility(View.VISIBLE);
                        System.out.println("Data " + (String) data);
                        if (checedkstate.equalsIgnoreCase("COMPLETED")) {
                            String thumbnailPath = getThumbnailPath((String) data);
                            if (view != null) {
                                imageView.setImageDrawable(getResources().getDrawable(R.drawable.document));
                            }
                            imageView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final CharSequence[] items = {"BROWSE", "CHOOSE", "Cancel"};
                                    AlertDialog.Builder builder = new AlertDialog.Builder(DownloadActivity.this);
                                    builder.setTitle("Select Options");
                                    builder.setItems(items, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int item) {
                                            switch (item) {
                                                case 0:
                                                    File file = new File((String) data);
                                                    Intent intent = new Intent();
                                                    intent.setAction(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.parse("file://"+file.getAbsolutePath()), "application/pdf");
                                                    System.out.println("path==" +(String) data+"--"+file.getAbsolutePath());
                                                    startActivity(intent);

                                                    break;
                                                case 1:
                                                    File file1 = new File((String) data);
                                                    filePath = file1.getAbsolutePath();
                                                    UploadedImage.filepath = filePath;
                                                    GetEtag etag = new GetEtag();
                                                    etag.execute((String) data);
                                                    transferUtility.cancelAllWithType(TransferType.DOWNLOAD);
                                                    break;
                                                case 2:

                                                    break;
                                            }
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                }
                            });
                        } else {
                            imageView.setImageDrawable(getResources().getDrawable(R.drawable.loading));
                            imageView.setOnClickListener(null);
                            //  checedkstate = "";
                        }
                    }
                        else {
                            imageView.setVisibility(View.GONE);
                            imageView.setOnClickListener(null);
                          //  checedkstate = "";
                        }

                        return true;

                    case R.id.UploadVideo:
                        extension = CommonMethods.getExtension((String) data);
                        VideoView videoFile = (VideoView) view;
                        if (extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mpeg")) {
                            if (checedkstate.equalsIgnoreCase("COMPLETED")) {
                                videoFile.setVisibility(View.VISIBLE);
                                Bitmap thumb = ThumbnailUtils.createVideoThumbnail((String) data,
                                        MediaStore.Images.Thumbnails.MINI_KIND);
                                BitmapDrawable bitmapDrawable = new BitmapDrawable(DownloadActivity.this.getResources(), thumb);
                                if (view != null) {
                                    videoFile.setBackground(bitmapDrawable);
                                }
                             //   checedkstate = "";
                                    videoFile.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final CharSequence[] items = {"BROWSE", "CHOOSE", "Cancel"};
                                            AlertDialog.Builder builder = new AlertDialog.Builder(DownloadActivity.this);
                                            builder.setTitle("Select Options");
                                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int item) {
                                                    switch (item) {
                                                        case 0:
                                                            File file = new File((String) data);
                                                            filePath = file.getAbsolutePath();
                                                            final Dialog dialog1 = new Dialog(DownloadActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                                                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                            dialog1.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                                            //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                                            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                            dialog1.setContentView(R.layout.alert_play_video);
                                                            final VideoView video = (VideoView) dialog1.findViewById(R.id.playVideo);
                                                            video.setVideoPath(filePath);
                                                            video.start();
                                                            ImageView cross = (ImageView) dialog1.findViewById(R.id.crossImage);
                                                            cross.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialog1.dismiss();
                                                                }
                                                            });
                                                            dialog1.setCancelable(true);
                                                            dialog1.show();
                                                            break;
                                                        case 1:
                                                            File file1 = new File((String) data);
                                                            filePath = file1.getAbsolutePath();
                                                            UploadedImage.filepath = filePath;
                                                            GetEtag etag = new GetEtag();
                                                            etag.execute((String) data);

                                                            transferUtility.cancelAllWithType(TransferType.DOWNLOAD);

                                                            break;
                                                        case 2:

                                                            break;
                                                    }
                                                }
                                            });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                    });
                            } else {
                                videoFile.setVisibility(View.GONE);
                            }

                        } else {
                            videoFile.setVisibility(View.GONE);
                        }
                        return true;

                    case R.id.btnPlay:
                        ImageView btnPlay = (ImageView) view;
                        extension = CommonMethods.getExtension((String) data);
                        if (extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mpeg")) {
                            btnPlay.setVisibility(View.VISIBLE);
                        if (checedkstate.equalsIgnoreCase("COMPLETED")) {
                                btnPlay.setVisibility(View.VISIBLE);
                                btnPlay.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final CharSequence[] items = {"PLAY", "CHOOSE", "Cancel"};
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DownloadActivity.this);
                                        builder.setTitle("Select Options");
                                        builder.setItems(items, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int item) {
                                                switch (item) {
                                                    case 0:
                                                        File file = new File((String) data);
                                                        filePath = file.getAbsolutePath();
                                                        final Dialog dialog1 = new Dialog(DownloadActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                                                        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                        dialog1.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                                        //   dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog1.setContentView(R.layout.alert_play_video);
                                                        final VideoView video = (VideoView) dialog1.findViewById(R.id.playVideo);
                                                        video.setVideoPath(filePath);
                                                        video.setMediaController(new MediaController(DownloadActivity.this));
                                                        video.start();
                                                        ImageView cross = (ImageView) dialog1.findViewById(R.id.crossImage);
                                                        cross.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dialog1.dismiss();
                                                            }
                                                        });
                                                        dialog1.setCancelable(true);
                                                        dialog1.show();
                                                        break;
                                                    case 1:
                                                        File file1 = new File((String) data);
                                                        filePath = file1.getAbsolutePath();
                                                        UploadedImage.filepath = filePath;
                                                        GetEtag etag = new GetEtag();
                                                        etag.execute((String) data);
                                                        transferUtility.cancelAllWithType(TransferType.DOWNLOAD);

                                                        break;
                                                    case 2:

                                                        break;
                                                }
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });
                            } else {
                                btnPlay.setVisibility(View.GONE);
                            }
                        } else {
                            btnPlay.setVisibility(View.GONE);
                        }
                        return true;

                    case R.id.textState:
                        TextView state = (TextView) view;
                        state.setText((String) data);
                        checedkstate = state.getText().toString();
                        return true;

                    case R.id.progressBar1:
                        ProgressBar progress = (ProgressBar) view;
                        progress.setProgress((Integer) data);
                        return true;
                    case R.id.textBytes:
                        TextView bytes = (TextView) view;
                        bytes.setText((String) data);
                        return true;

                    case R.id.textPercentage:
                        TextView percentage = (TextView) view;
                        percentage.setText((String) data);
                        return true;
                }
                return false;
            }
        });
        gridView.setAdapter(simpleAdapter);

        this.gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.v("onScrollStateChanged", "onScrollStateChanged");
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // isScrollStop = true;

                 //   simpleAdapter.notifyDataSetChanged();
                } else {
                    //   isScrollStop = false;
                    //   isScrollStop = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.v("onScroll", "onScroll");
            }
        });

        btnDownload = (Button) findViewById(R.id.buttonDownload);
        btnPause = (Button) findViewById(R.id.buttonPause);
        btnResume = (Button) findViewById(R.id.buttonResume);
        btnCancel = (Button) findViewById(R.id.buttonCancel);
        btnDelete = (Button) findViewById(R.id.buttonDelete);
        btnPauseAll = (Button) findViewById(R.id.buttonPauseAll);
        btnCancelAll = (Button) findViewById(R.id.buttonCancelAll);

        // Launches an activity for the user to select an object in their S3
        // bucket to download
        btnDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadActivity.this, DownloadSelectionActivity.class);
                startActivityForResult(intent, DOWNLOAD_SELECTION_REQUEST_CODE);
            }
        });

        btnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Make sure the user has selected a transfer
                if (checkedIndex >= 0 && checkedIndex < observers.size()) {
                    Boolean paused = transferUtility.pause(observers.get(checkedIndex)
                            .getId());
                    /**
                     * If paused does not return true, it is likely because the
                     * user is trying to pause a download that is not in a
                     * pausable state (For instance it is already paused, or
                     * canceled).
                     */
                    if (!paused) {
                        Toast.makeText(
                                DownloadActivity.this,
                                "Cannot Pause transfer.  You can only pause transfers in a WAITING or IN_PROGRESS state.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnResume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Make sure the user has selected a transfer
                if (checkedIndex >= 0 && checkedIndex < observers.size()) {
                    TransferObserver resumed = transferUtility.resume(observers.get(checkedIndex)
                            .getId());

                    /**
                     * If resume returns null, it is likely because the transfer
                     * is not in a resumable state (For instance it is already
                     * running).
                     */
                    if (resumed == null) {
                        Toast.makeText(
                                DownloadActivity.this,
                                "Cannot resume transfer.  You can only resume transfers in a PAUSED state.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure a transfer is selected
                if (checkedIndex >= 0 && checkedIndex < observers.size()) {
                    Boolean canceled = transferUtility.cancel(observers.get(checkedIndex).getId());
                    /**
                     * If cancel returns false, it is likely because the
                     * transfer is already canceled
                     */
                    if (!canceled) {
                        Toast.makeText(
                                DownloadActivity.this,
                                "Cannot cancel transfer.  You can only resume transfers in a PAUSED, WAITING, or IN_PROGRESS state.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure a transfer is selected
                if (checkedIndex >= 0 && checkedIndex < observers.size()) {
                    // Deletes a record but the file is not deleted.
                    transferUtility.deleteTransferRecord(observers.get(checkedIndex).getId());
                    observers.remove(checkedIndex);
                    transferRecordMaps.remove(checkedIndex);
                    checkedIndex = INDEX_NOT_CHECKED;
                    updateButtonAvailability();
                    updateList();
                }
            }
        });

        btnPauseAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                transferUtility.pauseAllWithType(TransferType.DOWNLOAD);
            }
        });

        btnCancelAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                transferUtility.cancelAllWithType(TransferType.DOWNLOAD);
            }
        });

        updateButtonAvailability();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DOWNLOAD_SELECTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Start downloading with the key they selected in the
                // DownloadSelectionActivity screen.
                String key = data.getStringExtra("key");
                System.out.println("Cheking key: " + key);
                beginDownload(key);
            }
        }
    }

    public String getThumbnailPath(String path) {
        long imageId = -1;

        String[] projection = new String[]{MediaStore.MediaColumns._ID};
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[]{path};
        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            imageId = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
        }

        String result = null;
        cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(this.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            cursor.close();
        }

        return result;
    }

    /*
     * Begins to download the file specified by the key in the bucket.
     */
    private void beginDownload(String key) {
        // Location to download files from S3 to. You can choose any accessible
        // file.
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/QMSDownload");
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/QMSDownload/" + key);

        File f = new File(file.getAbsolutePath());
        if (f.exists() && f.length() != 0) {
            TransferObserver observer = null;

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", -1);
            map.put("checked", false);
            map.put("fileName", file.getAbsolutePath());
            map.put("progress", 100);
            map.put("bytes", "");
            map.put("state", "COMPLETED");
            map.put("percentage", 100 + "%");
            observers.add(observer);
            transferRecordMaps.add(map);
            simpleAdapter.notifyDataSetChanged();

        } else {
            System.out.println("Rabin is testing:  --- " + f.getAbsolutePath());
            TransferObserver observer = transferUtility.download(CommonDef.BUCKET_NAME, key, file);
            // Initiate the download
            // Add the new download to our list of TransferObservers
            observers.add(observer);
            HashMap<String, Object> map = new HashMap<String, Object>();
            // Fill the map with the observers data
            Util.fillMap(map, observer, false);
            // Add the filled map to our list of maps which the simple adapter uses
            transferRecordMaps.add(map);
            observer.setTransferListener(new DownloadListener());
            simpleAdapter.notifyDataSetChanged();
        }
    }

    /*
     * Updates the ListView according to observers, by making transferRecordMap
     * reflect the current data in observers.
     */
    private void updateList() {
        TransferObserver observer = null;
        HashMap<String, Object> map = null;
        for (int i = 0; i < observers.size(); i++) {
            observer = observers.get(i);
            map = transferRecordMaps.get(i);
            if (observer != null) {
                Util.fillMap(map, observer, i == checkedIndex);
            }
            simpleAdapter.notifyDataSetChanged();
        }

    }

    /*
     * Enables or disables buttons according to checkedIndex.
     */
    private void updateButtonAvailability() {
        boolean availability = checkedIndex >= 0;
        btnPause.setEnabled(availability);
        btnResume.setEnabled(availability);
        btnCancel.setEnabled(availability);
        btnDelete.setEnabled(availability);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == this.btn_back.getId()) {
            v.startAnimation(buttonClick);
            finish();
            observers = transferUtility.getTransfersWithType(TransferType.DOWNLOAD);
            transferUtility.cancelAllWithType(TransferType.DOWNLOAD);
            for (TransferObserver observer : observers) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                Util.fillMap(map, observer, false);
                transferUtility.deleteTransferRecord(observer.getId());
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }

        } else if (v.getId() == this.txtBack.getId()) {
            v.startAnimation(buttonClick);
            observers = transferUtility.getTransfersWithType(TransferType.DOWNLOAD);
            transferUtility.cancelAllWithType(TransferType.DOWNLOAD);
            for (TransferObserver observer : observers) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                Util.fillMap(map, observer, false);
                transferUtility.deleteTransferRecord(observer.getId());
            }
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    public void DeleteTransfer() {
        List<TransferObserver> observers;
        TransferUtility transferUtility;
        transferUtility = Util.getTransferUtility(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Deleting deleting = new Deleting();
//        deleting.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Deleting deleting = new Deleting();
//        deleting.execute();
    }

    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */
    private class DownloadListener implements TransferListener {
        // Simply updates the list when notified.
        @Override
        public void onError(int id, Exception e) {
            updateList();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            updateList();
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            updateList();
        }
    }


    //Getting etag of choosed files
    public class GetEtag extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setMessage("Please wait...");
            progressBar.show();
        }

        @Override
        protected String doInBackground(String... params) {
            File file = new File(params[0]);
            S3Object imagess = s3Client.getObject(CommonDef.BUCKET_NAME, file.getName());
            ObjectMetadata myObj = imagess.getObjectMetadata();
            String tag = myObj.getETag();
            Log.d("ETag==", tag);

            observers = transferUtility.getTransfersWithType(TransferType.DOWNLOAD);
            transferUtility.cancelAllWithType(TransferType.DOWNLOAD);
            for (TransferObserver observer : observers) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                Util.fillMap(map, observer, false);
                transferUtility.deleteTransferRecord(observer.getId());
            }

            return tag;
        }

        @Override
        protected void onPostExecute(String tag) {
            super.onPostExecute(tag);
            progressBar.dismiss();
            Intent result = new Intent();
            //   UploadedImage.filepath = filePath;
            result.putExtra("etag", tag);
            setResult(1, result);
            finish();
        }
    }

    class ListObject extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            etaglist = new ArrayList<String>();
            progressBar.setMessage("Downloading...");
            progressBar.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<S3ObjectSummary> listobject = s3Client.listObjects(CommonDef.BUCKET_NAME).getObjectSummaries();

                for (S3ObjectSummary image : listobject) {
                    System.out.println("filename==" + image.getKey());
                    for (int k = 0; k < list.size(); k++) {
                        if (list.get(k).equalsIgnoreCase(image.getETag()))
                            etaglist.add(image.getKey());
                    }
                }
            } catch (Exception exx) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (int j = 0; j < etaglist.size(); j++) {

                System.out.println("Cheking key: " + etaglist.get(j));
                beginDownload(etaglist.get(j));
            }
            UploadedImage.isDownloaded = true;
            progressBar.dismiss();
        }
    }

    class Deleting extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            observers = transferUtility.getTransfersWithType(TransferType.DOWNLOAD);
            transferUtility.cancelAllWithType(TransferType.DOWNLOAD);
            for (TransferObserver observer : observers) {
                transferUtility.deleteTransferRecord(observer.getId());
            }
            return null;
        }
    }
}
