package com.kirillpolyakov.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kirillpolyakov.dto.ResponseResult;
import com.kirillpolyakov.model.SimpleUser;
import com.kirillpolyakov.service.SimpleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("simpleUser")
public class SimpleUserController {

    private SimpleUserService simpleUserService;

    private ObjectMapper objectMapper;

    @Autowired
    public void setSimpleUserService(SimpleUserService simpleUserService) {
        this.simpleUserService = simpleUserService;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        {
            objectMapper.registerModule(new JavaTimeModule());
        }
    }

    /**
     * добавить пользователя
     */
    @PostMapping
    public void add(HttpServletResponse response, @RequestBody SimpleUser simpleUser) {
        try {
            try {
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(200);
                objectMapper.writerFor(new TypeReference<SimpleUser>() {
                }).writeValue(response.getWriter(),
                        this.simpleUserService.add(simpleUser));
            } catch (IllegalArgumentException e) {
                response.setStatus(400);
                objectMapper.writeValue(response.getWriter(), new ResponseResult<>(e.getMessage(), null));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public void get(HttpServletResponse response) {
        try {
            try {
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(200);
                objectMapper.writerFor(new TypeReference<List<SimpleUser>>() {
                }).writeValue(response.getWriter(),
                        this.simpleUserService.get());
            } catch (IllegalArgumentException e) {
                response.setStatus(400);
                objectMapper.writeValue(response.getWriter(), new ResponseResult<>(e.getMessage(), null));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
