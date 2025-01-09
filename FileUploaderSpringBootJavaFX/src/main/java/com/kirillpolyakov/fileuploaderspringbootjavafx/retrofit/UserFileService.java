package com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit;

import com.kirillpolyakov.fileuploaderspringbootjavafx.dto.ResponseResult;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.UserFile;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserFileService {
    @Multipart
    @POST("userFile")
    Call<ResponseResult<String>> upload(
            @Part("path") RequestBody id,
            @Part MultipartBody.Part documents);

    @GET("userFile")
    Call<ResponseBody> download(@Query("path") String path);

    @GET("userFile/information")
    Call<ResponseResult<List<UserFile>>> getInfo(@Query("path") String path);

    @GET("userFile/client_information")
    Call<ResponseResult<List<UserFile>>> getInfoClient(@Query("path") String path);

}
