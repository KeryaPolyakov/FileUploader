package com.kirillpolyakov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirillpolyakov.dto.ResponseResult;
import com.kirillpolyakov.model.UserFile;
import com.kirillpolyakov.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("userFile")
public class UserFileController {

    private UserFileService userFileService;

    @Autowired
    public void setUserFileService(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    /**
     * Загрузка файла(файлов) на сервер
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseResult<String>> save(@RequestPart String path,
                                                         @RequestPart MultipartFile document) {
        try {
            this.userFileService.upload(path, document);
            return new ResponseEntity<>(new ResponseResult<>(null, "File is uploaded"),
                    HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Выгрузка файла с сервера
     */
    @GetMapping
    public void download(HttpServletResponse response,
                        @RequestParam String path) throws IOException {
        try {
            this.userFileService.download(response, path);
        } catch (IllegalArgumentException e) {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(400);
            new ObjectMapper().writeValue(response.getWriter(),
                    new ResponseResult<>(e.getMessage(), null));
        }
    }

    /**
     * Получения информации о директории
     */
    @GetMapping("information")
    public ResponseEntity<ResponseResult<List<UserFile>>> getInfo(@RequestParam String path) {
        try {
            return new ResponseEntity<>(new ResponseResult<>(null, this.userFileService.getInfo(path)),
                    HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("client_information")
    public ResponseEntity<ResponseResult<List<UserFile>>> getInfoClient(@RequestParam String path) {
        try {
            return new ResponseEntity<>(new ResponseResult<>(null, this.userFileService.getClientInfo(path)),
                    HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
