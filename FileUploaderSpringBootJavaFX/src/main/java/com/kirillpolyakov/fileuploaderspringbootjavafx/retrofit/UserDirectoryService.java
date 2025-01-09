package com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit;

import com.kirillpolyakov.fileuploaderspringbootjavafx.dto.ResponseResult;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserDirectoryService {

    @POST("userDirectory")
    Call<ResponseResult<String>> add(@Query("path") String path, @Query("name") String name);

    @POST("userDirectory/client")
    Call<ResponseResult<String>> addToClient(@Query("path") String path, @Query("name") String name);

    @DELETE("userDirectory")
    Call<ResponseResult<String>> delete(@Query("path") String path);

    @DELETE("userDirectory/client")
    Call<ResponseResult<String>> deleteClient(@Query("path") String path);

    @PUT("userDirectory")
    Call<ResponseResult<String>> update(@Query("path") String path,  @Query("name") String name);

    @PUT("userDirectory/client")
    Call<ResponseResult<String>> updateClient(@Query("path") String path,  @Query("name") String name);

    @Multipart
    @POST("userDirectory/upload")
    Call<ResponseResult<String>> upload(
            @Part MultipartBody.Part documents,
            @Part("path") RequestBody id);

    @GET("userDirectory/download")
    Call<ResponseBody> download(@Query("path") String path);
}
