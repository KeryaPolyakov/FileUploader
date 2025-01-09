package com.kirillpolyakov.fileuploaderspringbootjavafx.retrofit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirillpolyakov.fileuploaderspringbootjavafx.dto.ResponseResult;
import com.kirillpolyakov.fileuploaderspringbootjavafx.security.BasicAuthInterceptor;
import com.kirillpolyakov.fileuploaderspringbootjavafx.util.Constants;
import net.lingala.zip4j.ZipFile;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class UserDirectoryRepository {

    private final ObjectMapper objectMapper;
    private UserDirectoryService service;

    public UserDirectoryRepository(String userName, String password) {
        objectMapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(userName, password)).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
        this.service = retrofit.create(UserDirectoryService.class);
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

    public String add(String path, String name) throws IOException {
        Response<ResponseResult<String>> execute = this.service.add(path, name).execute();
        return getData(execute);
    }

    public String addToClient(String path, String name) throws IOException {
        Response<ResponseResult<String>> execute = this.service.addToClient(path, name).execute();
        return getData(execute);
    }

    public void delete(String path) throws IOException {
        Response<ResponseResult<String>> execute = this.service.delete(path).execute();
        getData(execute);
    }

    public void deleteClient(String path) throws IOException {
        Response<ResponseResult<String>> execute = this.service.deleteClient(path).execute();
        getData(execute);
    }

    public void update(String path, String name) throws IOException {
        Response<ResponseResult<String>> execute = this.service.update(path, name).execute();
        getData(execute);
    }

    public void updateClient(String path, String name) throws IOException {
        Response<ResponseResult<String>> execute = this.service.updateClient(path, name).execute();
        getData(execute);
    }
    public String upload(File file, String path) throws IOException {
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
        Call<ResponseResult<String>> call = service.upload(body, description);
        Response<ResponseResult<String>> execute = call.execute();
        return getData(execute);
    }

    public <T> void download(String path, String pathIn, String name) throws IOException {
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
        try (FileOutputStream outputStream = new FileOutputStream(new File(file.getAbsolutePath(), name + ".zip"))) {
            outputStream.write(body.bytes());
        }
        try(ZipFile zipFile = new ZipFile(new File(file.getAbsolutePath(), name + ".zip"))) {
            zipFile.extractAll(file.getAbsolutePath());
            zipFile.getFile().delete();
        }
    }

}
