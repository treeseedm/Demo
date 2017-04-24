package com.csja.smlocked;

import android.content.Context;
import android.util.AttributeSet;

import com.sankuai.zbar.BarcodeFormat;
import com.sankuai.zbar.ZBarScannerView;

import java.util.Collection;

/**
 * Created by qylk on 16/9/14.
 * https://static.aminer.org/pdf/PDF/000/315/304/improving_performance_of_the_decoder_for_two_dimensional_barcode_symbology.pdf
 * http://zbar.sourceforge.net/iphone/sdkdoc/optimizing.html
 */
public class ScannerView extends ZBarScannerView {
    private OnCameraOpenFailedListener callBack;

    public ScannerView(Context context) {
        super(context);
    }

    public ScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onCameraOpenFailed() {
        super.onCameraOpenFailed();
        if (callBack != null) {
            callBack.OnCameraOpenFailed();
        }
    }

    public interface OnCameraOpenFailedListener {
        void OnCameraOpenFailed();
    }

    public void setOnCameraOpenFailedListener(OnCameraOpenFailedListener listener) {
        this.callBack = listener;
    }

    @Override
    public Collection<BarcodeFormat> getFormats() {
        //定制支持的编码格式
        Collection<BarcodeFormat> formats = super.getFormats();
        formats.remove(BarcodeFormat.ISBN10);//ISBN13会被错误的识别成ISBN10
        return formats;
    }
}
