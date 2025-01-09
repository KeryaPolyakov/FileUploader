package com.kirillpolyakov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirillpolyakov.dto.ResponseResult;
import com.kirillpolyakov.service.UserDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping("userDirectory")
public class UserDirectoryController {

    private UserDirectoryService userDirectoryService;

    @Autowired
    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    /**
     * добавить директорию
     */
    @PostMapping
    public ResponseEntity<ResponseResult<String>> add(@RequestParam String path, @RequestParam String name) {
        try {
            this.userDirectoryService.add(path, name);
            return new ResponseEntity<>(new ResponseResult<>(null, "OK"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * добавить директорию
     */
    @PostMapping("client")
    public ResponseEntity<ResponseResult<String>> addToClient(@RequestParam String path, @RequestParam String name) {
        try {
            this.userDirectoryService.addToClient(path, name);
            return new ResponseEntity<>(new ResponseResult<>(null, "OK"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * проверка наличия директории
     */
    @GetMapping
    public ResponseEntity<ResponseResult<Boolean>> get(@RequestParam String path) {
        try {
            return new ResponseEntity<>(new ResponseResult<>(null, this.userDirectoryService.get(path)), HttpStatus.OK);
        } catch (
                IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Переименование директории
     */
    @PutMapping
    public ResponseEntity<ResponseResult<String>> update(@RequestParam String path, @RequestParam String name) {
        try {
            this.userDirectoryService.update(path, name);
            return new ResponseEntity<>(new ResponseResult<>(null, "Updated"), HttpStatus.OK);
        } catch (
                IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Переименование директории клиента
     */
    @PutMapping("client")
    public ResponseEntity<ResponseResult<String>> updateClient(@RequestParam String path, @RequestParam String name) {
        try {
            this.userDirectoryService.updateClient(path, name);
            return new ResponseEntity<>(new ResponseResult<>(null, "Updated"), HttpStatus.OK);
        } catch (
                IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Удаление директории
     */
    @DeleteMapping
    public ResponseEntity<ResponseResult<String>> delete(@RequestParam String path) {
        try {
            this.userDirectoryService.delete(path);
            return new ResponseEntity<>(new ResponseResult<>(null, "Deleted"), HttpStatus.OK);
        } catch (
                IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("client")
    public ResponseEntity<ResponseResult<String>> deleteClient(@RequestParam String path) {
        try {
            this.userDirectoryService.deleteClient(path);
            return new ResponseEntity<>(new ResponseResult<>(null, "Deleted"), HttpStatus.OK);
        } catch (
                IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Загрузка дериктории на сервер
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, value = "upload")
    public ResponseEntity<ResponseResult<String>> upload(@RequestPart MultipartFile document, @RequestPart String path) {
        try {
            this.userDirectoryService.upload(document, path);
            return new ResponseEntity<>(new ResponseResult<>(null, "OK"), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Выгрузка дериктории с сервера
     */
    @GetMapping("download")
    public void getFile(HttpServletResponse response,
                        @RequestParam String path) throws IOException {
        try {
            this.userDirectoryService.download(response, path);
        } catch (IllegalArgumentException e) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(400);
            new ObjectMapper().writeValue(response.getWriter(),
                    new ResponseResult<>(e.getMessage(), null));
        }
    }
}
