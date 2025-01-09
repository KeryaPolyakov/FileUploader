package com.kirillpolyakov.service;

import com.kirillpolyakov.model.Type;
import com.kirillpolyakov.model.UserDetailsImpl;
import com.kirillpolyakov.model.UserFile;
import com.kirillpolyakov.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserFileServiceImpl implements UserFileService {

    private UserDirectoryService userDirectoryService;

    @Autowired
    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    /**
     * Загрузка файла(файлов) на сервер
     */
    @Override
    public void upload(String path, MultipartFile document) {
        try {
            if (document != null) {
                Util.checkPermission(path, 0, "Можно загружать файлы только в свою иерархию");
                File check = new File("C:\\fileUploaderSpring", path);
                if (!this.userDirectoryService.get(check.getAbsolutePath())) {
                    throw new IllegalArgumentException("Директория с таким названием не существует");
                }

                String name = document.getOriginalFilename();
                File file = new File(check.getAbsolutePath(), name);
                byte[] bytes = document.getBytes();
                try (BufferedOutputStream bufferedOutputStream
                             = new BufferedOutputStream(new FileOutputStream(file))) {
                    bufferedOutputStream.write(bytes);

                }
            } else {
                throw new IllegalArgumentException("Отсутствуют файлы для загрузки");
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Выгрузка файла с сервера
     */
    @Override
    public void download(HttpServletResponse response, String path) {
        Util.checkPermission(path, 0, "Можно выгружать файлы только из своей иерархии");
        File file = new File("C:\\fileUploaderSpring", path);
        if (!this.userDirectoryService.get(file.getAbsolutePath())) {
            throw new IllegalArgumentException("Файл не существует");
        }
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            response.getOutputStream().write(stream.readAllBytes());
            String mime = Files.probeContentType(file.toPath());
            response.setContentType(mime);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error file uploading");
        }
    }

    @Override
    public List<UserFile> getInfo(String path) {
        Util.checkPermission(path, 0, "Можно получать информацию только о своей иерархии");
        List<UserFile> userFiles = new ArrayList<>();
        File file = new File("C:\\fileUploaderSpring", path);
        if (this.userDirectoryService.get(file.getAbsolutePath())) {
            getFilesAndDir(userFiles, file);
        }
        return userFiles;
    }

    @Override
    public List<UserFile> getClientInfo(String path) {
        List<UserFile> userFiles = new ArrayList<>();
        String[] split = path.split("/");
        String path2 = path.replace(split[0] + "/", "");

        File file;
        if (split.length == 1) {
            file = new File(split[0] + "\\");
        } else {
            file = new File(split[0] + "\\", path2);
        }
        getFilesAndDir(userFiles, file);
        return userFiles;
    }

    private void getFilesAndDir(List<UserFile> userFiles, File file) {
        File[] files = file.listFiles();
        userFiles.add(new UserFile("...", Type.DIRECTORY));
        for (File fileInDir : files) {
            if (fileInDir.isFile()) {
                userFiles.add(new UserFile(fileInDir.getName(), Type.FILE));
                continue;
            }
            userFiles.add(new UserFile(fileInDir.getName(), Type.DIRECTORY));
        }
    }

}
