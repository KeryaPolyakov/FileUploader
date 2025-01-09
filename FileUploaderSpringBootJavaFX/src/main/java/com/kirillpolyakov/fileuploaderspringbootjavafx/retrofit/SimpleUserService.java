package com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit;

import com.kirillpolyakov.fileuploaderspringbootjavafx.model.SimpleUser;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface SimpleUserService {

    @POST("simpleUser")
    Call<SimpleUser> post(@Body SimpleUser simpleUser);

    @GET("simpleUser")
    Call<List<SimpleUser>> get();
}
