package com.zjh.facedetection.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baidu.idl.face.platform.utils.Base64Utils;

/**
 * @author zhongjh
 * @date 2021/5/31
 */
public class BitmapUtils {

    /**
     * base64转换bitmap
     *
     * @param base64Data base64
     * @return bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
