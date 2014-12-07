package com.example.francesco.tunnel;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.francesco.tunnel.activity.HearStoryTellerActivity;


public class TunnelHearActivity extends HearStoryTellerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getResources().getString(R.string.l_title));
        super.onCreate(savedInstanceState);
    }

}
