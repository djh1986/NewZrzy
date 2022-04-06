package com.zrzyyzt.runtimeviewer.Widgets.AboutWidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zrzyyzt.runtimeviewer.BMOD.MapModule.BaseWidget.BaseWidget;
import com.zrzyyzt.runtimeviewer.R;

import gisluq.lib.Util.AppUtils;

public class AboutWidget extends BaseWidget {
    public View aboutView = null;//
    private Context context;

    @Override
    public void create() {
        this.context = super.context;
        LayoutInflater mLayoutInflater = LayoutInflater.from(super.context);
        aboutView = mLayoutInflater.inflate(R.layout.widget_view_about,null);


        TextView textView = (TextView)aboutView.findViewById(R.id.activity_about_versionTxt);
        String version = AppUtils.getVersionName(context);
        textView.setText("版本号:"+version);

        TextView txtContext = (TextView)aboutView.findViewById(R.id.activity_about_txtContext);
        String txtContextText = "版权所有:";
        String companyName = context.getResources().getString(R.string.app_company) ;
        txtContext.setText(txtContextText + companyName);
    }

    @Override
    public void active() {
        super.active();
        super.showWidget(aboutView);
    }

    @Override
    public void inactive() {
        super.inactive();
    }
}
