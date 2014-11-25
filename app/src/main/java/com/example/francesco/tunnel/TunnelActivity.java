package com.example.francesco.tunnel;

import android.os.Bundle;
import android.widget.Button;

import com.example.francesco.tunnel.activity.VoiceStoryTellerActivity;


public class TunnelActivity extends VoiceStoryTellerActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getResources().getString(R.string.l_title));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tunnel);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }
}
