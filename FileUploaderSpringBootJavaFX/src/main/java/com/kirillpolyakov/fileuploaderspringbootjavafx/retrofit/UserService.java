package com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit;


import com.kirillpolyakov.fileuploaderspringbootjavafx.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface UserService {
    @GET("user/authentication")
    Call<User> authenticate();

    @GET("user/{id}")
    Call<User> get(@Path("id") long id);

}