package com.kirillpolyakov.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface UserDirectoryService {

    void add(String path, String name);

    void addToClient(String path, String name);

    boolean get(String path);

    void update(String path, String name);

    void updateClient(String path, String name);

    void delete(String path);

    void deleteClient(String path);

    void upload(MultipartFile document, String pathIn);

    void download(HttpServletResponse response, String path);
}
