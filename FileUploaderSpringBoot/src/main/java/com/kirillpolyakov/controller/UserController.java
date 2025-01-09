package com.kirillpolyakov.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirillpolyakov.dto.ResponseResult;
import com.kirillpolyakov.model.User;
import com.kirillpolyakov.model.UserDetailsImpl;
import com.kirillpolyakov.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("user")
public class UserController {

    private UserService userService;

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @GetMapping(path = "/{id}")
    public void get(HttpServletResponse response, @PathVariable long id) {
        try {
            try {
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(200);
                objectMapper.writerFor(new TypeReference<User>() {
                }).writeValue(response.getWriter(),
                        this.userService.get(id));
            } catch (IllegalArgumentException e) {
                response.setStatus(400);
                objectMapper.writeValue(response.getWriter(), new ResponseResult<>(e.getMessage(), null));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get – осуществляет проверку соответствия логина и пароля для пользователя в базе данных, возвращает
     * объект User(c указанием типа объекта) по объекту Authentification(все аутентифицированные)
     */
    @GetMapping("authentication")
    public void authenticate(HttpServletResponse response, Authentication authentication) {
        try {
            try {
                if (authentication.isAuthenticated()) {
                    long id = ((UserDetailsImpl) authentication.getPrincipal()).getId();
                    response.setCharacterEncoding("utf-8");
                    response.setContentType("application/json;charset=utf-8");
                    response.setStatus(200);
                    objectMapper.writerFor(new TypeReference<User>() {
                    }).writeValue(response.getWriter(),
                            this.userService.get(id));
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(400);
                objectMapper.writeValue(response.getWriter(), new ResponseResult<>(e.getMessage(), null));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
