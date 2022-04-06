package com.zrzyyzt.runtimeviewer.Widgets.LayerManagerWidget.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zrzyyzt.runtimeviewer.R;

public class ResultDialog extends DialogFragment {
    private static final String TAG = "ResultDialog";
    TextView layerNameTextView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_view_query_mapquery, container);
        layerNameTextView = view.findViewById(R.id.widget_view_query_mapquery_txtLayerName);
        Log.d(TAG, "onCreateView: " + layerNameTextView);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        return dialog;
    }

    public void setLayerName(String layerName) {
        this.layerNameTextView.setText(layerName);
    }
}
