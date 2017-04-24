package com.csja.smlocked;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.sankuai.zbar.BarcodeFormat;
import com.sankuai.zbar.Result;
import com.sankuai.zbar.ZBarScannerView;

import java.util.List;


/**
 * Created by qylk on 16/9/13.
 */
public class CodeCaptureActivity extends Activity implements ZBarScannerView.ResultHandler, ScannerView.OnCameraOpenFailedListener {
    public static final String KEY_VALIDATE = "validate";
    public static final String KEY_RESULT = "result";
    private static final int PERMISSION_REQUEST_CAMERA = 100;

    private ScannerView mScannerView;
    private BeepManager beepManager;
    private boolean mResumed;
    private Handler mHandler;
    private boolean validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        getWindow().setAttributes(attributes);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_capture);
        mScannerView = (ScannerView) findViewById(R.id.scannerView);
        mScannerView.setOnCameraOpenFailedListener(this);

        //set support formats
        List<BarcodeFormat> formats = BarcodeFormat.ALL_FORMATS;
        formats.remove(BarcodeFormat.ISBN10);//ISBN13会被错误的识别成ISBN10
        mScannerView.setFormats(formats);

        beepManager = new BeepManager(this);
        mHandler = new Handler(Looper.getMainLooper());
        validate = getIntent().getBooleanExtra(KEY_VALIDATE, true);
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mResumed = true;
        mScannerView.setResultHandler(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mScannerView.startCamera();
        } else if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mResumed = false;
        mScannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beepManager != null) {
            beepManager.release();
            beepManager = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public boolean isActivityResumed() {
        return mResumed;
    }

    @Override
    public void handleResult(Result result) {
        Log.d("CodeCaptureActivity", result.getContents());
        Intent intent = new Intent();
        intent.putExtra("classes", result.getContents());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mScannerView.startCamera();
            } else {
                Toast.makeText(CodeCaptureActivity.this, R.string.camera_open_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void OnCameraOpenFailed() {
        Toast.makeText(this, R.string.camera_open_failed, Toast.LENGTH_SHORT).show();
        findViewById(R.id.tip).setVisibility(View.INVISIBLE);
    }
}
