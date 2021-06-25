package com.zhongjh.mvvmrapid.http.interceptor;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;


/**
 * 自定义返回的数据源
 * 更加复杂包括是否成功的可以参考公司项目的此类
 * Created by zhongjh on 2017/6/1.
 */
public class CustomResponseBodyConverter implements Converter<ResponseBody, String> {

    @Override
    public String convert(ResponseBody value) throws IOException {

//        String httpResult = "";
//
//        JSONObject jObject;

        return value.string();
    }

}