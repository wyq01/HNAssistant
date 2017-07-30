package com.movitech.HNAInfrastructure.assistant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.movitech.HNAInfrastructure.assistant.util.ScreenUtils;
import com.movitech.HNAInfrastructure.assistant.vlc.VlcListener;
import com.movitech.HNAInfrastructure.assistant.vlc.VlcVideoLibrary;

/**
 * 视频监控页面
 * Created by Joe.Wang on 2017/7/20.
 */
public class VideoActivity extends AppCompatActivity implements VlcListener {
    private String name;
    private String url;

    private TextView titleTv;
    private View closeView;
    private SurfaceView surfaceView;

    private VlcVideoLibrary vlcVideoLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video);

        initData();
        initViews();
    }

    private void initData() {
        name = getIntent().getStringExtra("name");
        url = getIntent().getStringExtra("url");
    }

    private void initViews() {
        titleTv = (TextView) findViewById(R.id.tv_title);
        titleTv.setText(name);

        closeView = findViewById(R.id.btn_close);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        params.width = ScreenUtils.getScreenWidth(this);
        params.height = ScreenUtils.getScreenWidth(this) * 633 / 773;
        surfaceView.setLayoutParams(params);
        vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
        vlcVideoLibrary.play(url);
    }

    @Override
    public void onComplete() {
//        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vlcVideoLibrary.stop();
    }

}