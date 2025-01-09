package com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirillpolyakov.fileuploaderspringbootjavafx.dto.ResponseResult;
import com.kirillpolyakov.fileuploaderspringbootjavafx.model.UserFile;
import com.kirillpolyakov.fileuploaderspringbootjavafx.security.BasicAuthInterceptor;
import com.kirillpolyakov.fileuploaderspringbootjavafx.util.Constants;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class UserFileRepository {

    private final ObjectMapper objectMapper;
    private UserFileService service;

    public UserFileRepository(String userName, String password) {
        objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(userName, password)).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
        this.service = retrofit.create(UserFileService.class);
    }

    private <T> T getData(Response<ResponseResult<T>> execute) throws IOException {
        if (execute.code() != 200) {
            String message = objectMapper.readValue(execute.errorBody().string(),
                    new TypeReference<ResponseResult<T>>() {
                    }).getMessage();
            throw new IllegalArgumentException(message);
        }
        return execute.body().getData();
    }

    public String upload(String path, File file) throws IOException {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(Files.probeContentType(file.toPath())),
                        file
                );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("document", file.getName(), requestFile);

        RequestBody description =
                RequestBody.create(
                        MultipartBody.FORM, path);
        Call<ResponseResult<String>> call = service.upload(description, body);
        Response<ResponseResult<String>> execute = call.execute();
        return getData(execute);
    }

    public <T> void download(String path, String pathIn, String filename) throws IOException {
        Call<ResponseBody> call = this.service.download(path);
        Response<ResponseBody> execute = call.execute();
        if (execute.code() != 200) {
            String message = objectMapper.readValue(execute.errorBody().string(),
                    new TypeReference<ResponseResult<T>>() {
                    }).getMessage();
            throw new IllegalArgumentException(message);
        }
        ResponseBody body = execute.body();
        System.out.println(body);
        File file = new File(pathIn);
        file.mkdirs();
        try (FileOutputStream outputStream = new FileOutputStream(new File(file.getAbsolutePath(), filename))) {
            outputStream.write(body.bytes());
        }
    }

    public List<UserFile> getInfo(String path) throws IOException {
        Response<ResponseResult<List<UserFile>>> execute = this.service.getInfo(path).execute();
        return getData(execute);
    }
    public List<UserFile> getInfoClient(String path) throws IOException {
        Response<ResponseResult<List<UserFile>>> execute = this.service.getInfoClient(path).execute();
        return getData(execute);
    }

}

