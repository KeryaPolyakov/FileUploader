package com.kirillpolyakov.service;

import com.kirillpolyakov.model.UserFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserFileService {

    void upload(String path, MultipartFile document);

    void download(HttpServletResponse response, String path);

    List<UserFile> getInfo(String path);

    List<UserFile> getClientInfo(String path);

}
