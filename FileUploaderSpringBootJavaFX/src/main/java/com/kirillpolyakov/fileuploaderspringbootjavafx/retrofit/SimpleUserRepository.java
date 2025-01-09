package com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirillpolyakov.fileuploaderspringbootjavafx.dto.ResponseResult;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.SimpleUser;
import com.kirillpolyakov.fileuploaderspringbootjavafx.security.BasicAuthInterceptor;
import com.kirillpolyakov.fileuploaderspringbootjavafx.util.Constants;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

public class SimpleUserRepository {

    private final ObjectMapper objectMapper;
    private SimpleUserService service;

    public SimpleUserRepository() {
        objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
        this.service = retrofit.create(SimpleUserService.class);
    }

    public SimpleUserRepository(String userName, String password) {
        objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(userName, password)).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
        this.service = retrofit.create(SimpleUserService.class);
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
    private <T> T getData2(Response<ResponseResult<T>> execute) throws IOException {
        if (execute.code() != 200) {
            String message = objectMapper.readValue(execute.errorBody().string(),
                    new TypeReference<ResponseResult<T>>() {
                    }).getMessage();
            throw new IllegalArgumentException(message);
        }
        return execute.body().getData();
    }

    public SimpleUser post(SimpleUser simpleUser) throws IOException {
        Response<SimpleUser> execute = this.service.post(simpleUser).execute();
        return getData(execute);
    }

    public List<SimpleUser> get() throws IOException {
        Response<List<SimpleUser>> execute = this.service.get().execute();
        return getData(execute);
    }
}
