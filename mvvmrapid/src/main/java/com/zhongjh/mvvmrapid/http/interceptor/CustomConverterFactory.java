package com.zhongjh.mvvmrapid.http.interceptor;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 重写父类的发送数据、返回数据等格式
 * Created by zhongjh on 2017/6/1.
 */
public final class CustomConverterFactory extends Converter.Factory {

    public static CustomConverterFactory create() {
        return create(new Gson());
    }

    public static CustomConverterFactory create(Gson gson) {
        return new CustomConverterFactory(gson);
    }

    private final Gson gson;

    private CustomConverterFactory(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        this.gson = gson;
    }

    /**
     * 需要重写父类中responseBodyConverter，该方法用来转换服务器返回数据
     */
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NotNull Type type, @NotNull Annotation[] annotations, @NotNull Retrofit retrofit) {
        return new CustomResponseBodyConverter();
    }


    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NotNull Type type,
                                                          @NotNull Annotation[] parameterAnnotations, @NotNull Annotation[] methodAnnotations, @NotNull Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }

}




