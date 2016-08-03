package com.util;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.widget.TextView;

import com.won.usb_ch340.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tang on 2016/7/29.
 */
public class getNet extends Activity {
    URL url;
    TextView tv;
    Thread thread=new Thread()
    {
        @Override
        public void run() {
            String strUrl="http://192.168.0.124:8080/Test1/tzf?type=4";
            try{
                url=new URL(strUrl);
                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
                InputStreamReader in=new InputStreamReader(urlConnection.getInputStream());
                BufferedReader br=new BufferedReader(in);
                String result="";
                String readLine=null;
                while((readLine=br.readLine())!=null)
                {
                    result=readLine;
                }
                in.close();
                urlConnection.disconnect();
                tv.setText(result);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        tv= (TextView) findViewById(R.id.textView);
        thread.start();

    }
}
