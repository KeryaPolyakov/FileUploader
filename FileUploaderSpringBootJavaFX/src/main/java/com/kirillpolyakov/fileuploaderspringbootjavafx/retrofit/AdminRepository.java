package com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirillpolyakov.fileuploaderspringbootjavafx.dto.ResponseResult;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.Admin;
import com.kirillpolyakov.fileuploaderspringbootjavafx.security.BasicAuthInterceptor;
import com.kirillpolyakov.fileuploaderspringbootjavafx.util.Constants;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class AdminRepository {

    private final ObjectMapper objectMapper;
    private AdminService service;

    public AdminRepository() {
        objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
        this.service = retrofit.create(AdminService.class);
    }

    public AdminRepository(String userName, String password) {
        objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(userName, password)).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
        this.service = retrofit.create(AdminService.class);
    }

    private <T> T getData(Response<T> execute) throws IOException {
        if (execute.code() != 200) {
            String message = objectMapper.readValue(execute.errorBody().string(),
                    new TypeReference<ResponseResult<T>>() {
                    }).getMessage();
            throw new IllegalArgumentException(message);
        }
        return execute.body();
    }

    public Admin post(Admin admin) throws IOException {
        Response<Admin> execute = this.service.post(admin).execute();
        return getData(execute);
    }
}
