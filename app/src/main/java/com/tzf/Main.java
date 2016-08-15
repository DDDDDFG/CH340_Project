package com.tzf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.agent.activity.AgentActivity;
import com.won.usb_ch340.MainActivity;
import com.won.usb_ch340.R;

/**
 * Created by Tang on 2016/8/1.
 */
public class Main extends Activity {
    Button btn_TJWD;
    Button btn_MonitoringControl;
    Button btn_Setting;
    Button btn_InitServer;
    public final String TAG="tzf";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main1);
        initView();
    }

    private void initView() {
        btn_MonitoringControl= (Button) findViewById(R.id.btn_MonitoringControl);
        btn_TJWD= (Button) findViewById(R.id.btn_InitTimeJW);
        btn_TJWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(Main.this, "----------进入MainActivity-----------", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onClick: 进入MainActivity");
                Main.this.startActivity(new Intent(Main.this,MainActivity.class));
            }
        });
        btn_Setting= (Button) findViewById(R.id.btn_Setting);
        btn_InitServer= (Button) findViewById(R.id.btn_InitServer);
        btn_InitServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Main.this, AgentActivity.class);
                startActivity(intent);
            }
        });

    }


}
