package com.baidu.idl.face.main.finance.model;

import android.graphics.Bitmap;

/**
 * Created by liujialu on 2020/3/3.
 */

public class ImportFeatureResult {
    private float result;
    private Bitmap bitmap;

    public ImportFeatureResult(float result, Bitmap bitmap) {
        this.result = result;
        this.bitmap = bitmap;
    }

    public float getResult() {
        return result;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
