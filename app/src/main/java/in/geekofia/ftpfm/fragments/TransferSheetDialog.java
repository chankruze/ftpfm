/*
 * Created by chankruze (Chandan Kumar Mandal) on 7/12/19 4:17 AM
 *
 * Copyright (c) Geekofia 2019 and beyond . All rights reserved.
 */

package in.geekofia.ftpfm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import in.geekofia.ftpfm.R;
import in.geekofia.ftpfm.services.DownloadService;
import in.geekofia.ftpfm.services.RemoteFileDownloadService;
import in.geekofia.ftpfm.utils.TransferResultReceiver;

public class TransferSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener, TransferResultReceiver.TransferProgressReceiver {

    private TransferResultReceiver transferResultReceiver;
    IntentFilter filter;

    private ProgressBar progressBar;
    private TextView textViewFileName, progressText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_sheet, container, false);

        initViews(view);

        filter = new IntentFilter(DownloadService.ACTION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(progressReceiver, filter);
        // Register the intent service in the activity
//        registerService();

        return view;
    }

    private void initViews(View view) {
        // views
        textViewFileName = view.findViewById(R.id.transfer_file_name);
        progressBar = view.findViewById(R.id.transfer_progress_bar);
        progressText = view.findViewById(R.id.transfer_progress_text);
    }


    @Override
    public void onClick(View v) {
        // views on click
    }

    private void registerService() {
        Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), RemoteFileDownloadService.class);

        // pass the ResultReceiver via the intent to the intent service
        transferResultReceiver = new TransferResultReceiver(new Handler(), this);
        intent.putExtra("transferProgressReceiver", transferResultReceiver);
        getActivity().startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        // Handle the results from the intent service here!
    }

    @Override
    public void onStop() {
        super.onStop();

        if(transferResultReceiver != null) {
            transferResultReceiver.setTransferProgressReceiver(null);
        }
    }

    // Define the callback for what to do when message is received
    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            String fileName = intent.getStringExtra("file");

            if (progress == 100){
                progressBar.setVisibility(View.GONE);
                progressText.setText("Done");
            } else {
                textViewFileName.setText(fileName);
                progressBar.setProgress(progress);
                progressText.setText(String.valueOf(progress) + "%");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(DownloadService.ACTION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(progressReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
    }
}
