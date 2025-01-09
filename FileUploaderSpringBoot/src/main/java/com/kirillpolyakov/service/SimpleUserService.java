package com.kirillpolyakov.service;

import com.kirillpolyakov.model.SimpleUser;

import java.util.List;

public interface SimpleUserService {

    SimpleUser add(SimpleUser simpleUser);

    List<SimpleUser> get();
}
