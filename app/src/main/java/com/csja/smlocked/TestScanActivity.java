package com.csja.smlocked;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import com.csja.smlocked.log.MLog;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class TestScanActivity extends Activity implements QRCodeView.Delegate {
    private static final String TAG = TestScanActivity.class.getSimpleName();

    private QRCodeView mQRCodeView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);


        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();


        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        MLog.i(TAG, "result:" + result);
        vibrate();
        mQRCodeView.startSpot();
        Intent intent = new Intent();
        intent.putExtra("classes", result);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

}