package com.kirillpolyakov.service;

import com.kirillpolyakov.util.Util;
import net.lingala.zip4j.ZipFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;

@Service
public class UserDirectoryServiceImpl implements UserDirectoryService {

    /**
     * добавить директорию
     */
    @Override
    public void add(String path, String name) {
        Util.checkPermission(path, 0, "Директорию можно добавлять только в свою иерархию");
        File dir = new File("C:\\fileUploaderSpring", path + File.separator + name);
        if (!this.get(dir.getAbsolutePath())) {
            dir.mkdirs();
        } else {
            throw new IllegalArgumentException("Директория с таким названием уже существует");
        }
    }

    /**
     * добавить директорию клиенту
     */
    @Override
    public void addToClient(String path, String name) {
        File dir = new File(path + File.separator + name);
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            throw new IllegalArgumentException("Директория с таким названием уже существует");
        }
    }

    /**
     * проверка наличия директории
     */
    @Override
    public boolean get(String path) {
        Util.checkPermission(path, 1, "Проверять можно только свою директорию");
        return new File(path).exists();
    }

    /**
     * Переименование директории
     */
    @Override
    public void update(String path, String name) {
        Util.checkPermission(path, 0, "Переименовывать можно только свою директорию");
        File file = new File("C:\\fileUploaderSpring", path);
        if (!this.get(file.getAbsolutePath())) {
            throw new IllegalArgumentException("Директория которую вы хотите переименовать не существует");
        }
        String newPath = file.getParent() + File.separator + name;
        if (!this.get(newPath)) {
            file.renameTo(new File(newPath));
        } else {
            throw new IllegalArgumentException("Директория с таким названием уже существует");
        }
    }

    @Override
    public void updateClient(String path, String name) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("Директория которую вы хотите переименовать не существует");
        }
        String newPath = file.getParent() + File.separator + name;
        if (!new File(newPath).exists()) {
            file.renameTo(new File(newPath));
        } else {
            throw new IllegalArgumentException("Директория с таким названием уже существует");
        }
    }

    /**
     * Удаление директории
     */
    @Override
    public void delete(String path) {
        Util.checkPermission(path, 0, "Удалять можно только свою директорию");
        File file = new File("C:\\fileUploaderSpring", path);
        if (this.get(file.getAbsolutePath())) {
            try {
                Files.walk(Path.of(file.getAbsolutePath()))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Директория которую вы хотите удалить не существует");
        }
    }

    @Override
    public void deleteClient(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                Files.walk(Path.of(file.getAbsolutePath()))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Директория которую вы хотите удалить не существует");
        }
    }

    /**
     * Загрузка директории на сервер
     */
    @Override
    public void upload(MultipartFile document, String path) {
        Util.checkPermission(path, 0, "Загружать директорию можно только свою иерархию");
        File file = new File("C:\\fileUploaderSpring", path);
        if (!this.get(file.getAbsolutePath())) {
            throw new IllegalArgumentException("Директория назначение не существует");
        }
        if (this.get(file.getAbsolutePath())) {
            String name = document.getOriginalFilename();
            try {
                byte[] bytes = document.getBytes();
                try (BufferedOutputStream bufferedOutputStream =
                             new BufferedOutputStream(new FileOutputStream(new File(file.getAbsolutePath(), name)))) {
                    bufferedOutputStream.write(bytes);
                }
                try (ZipFile zipFile = new ZipFile(new File(file.getAbsolutePath(), name))) {
                    zipFile.extractAll(file.getAbsolutePath());
                    zipFile.getFile().delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Директория для загрузки не существует");
        }
    }

    /**
     * Выгрузка дерикториии с сервера
     */
    @Override
    public void download(HttpServletResponse response, String path) {
        Util.checkPermission(path, 0, "Выгружать директорию можно только из своей иерархии");
        File file = new File("C:\\fileUploaderSpring", path);
        File root = new File("C:\\root");
        root.mkdirs();
        if (this.get(file.getAbsolutePath())) {
            try (ZipFile zipFile = new ZipFile(new File(root, file.getName() + ".zip"))) {
                zipFile.addFolder(file);
                try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(zipFile.getFile()))) {
                    zipFile.getFile().delete();
                    response.getOutputStream().write(stream.readAllBytes());
                    String mime = Files.probeContentType(file.toPath());
                    response.setContentType(mime);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Error file uploading");
                }
                zipFile.getFile().delete();
            }  catch (IOException e) {
                throw new IllegalArgumentException("Error file uploading");
            }
        } else {
            throw new IllegalArgumentException("Директория которуя вы хотите скачать отсутствуе");
        }
    }

}
