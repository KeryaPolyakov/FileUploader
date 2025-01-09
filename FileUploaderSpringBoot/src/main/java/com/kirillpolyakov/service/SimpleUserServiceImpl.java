package com.kirillpolyakov.service;

import com.kirillpolyakov.model.SimpleUser;
import com.kirillpolyakov.repository.SimpleUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class SimpleUserServiceImpl implements SimpleUserService{

    private SimpleUserRepository simpleUserRepository;

    private PasswordEncoder passwordEncoder;


    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setSimpleUserRepository(SimpleUserRepository simpleUserRepository) {
        this.simpleUserRepository = simpleUserRepository;
    }

    @Override
    public SimpleUser add(SimpleUser simpleUser) {
        simpleUser.setPassword(passwordEncoder.encode(simpleUser.getPassword()));
        try {
            this.simpleUserRepository.save(simpleUser);
            File root = new File("C:\\fileUploaderSpring" + File.separator + simpleUser.getId());
            root.mkdirs();
            return simpleUser;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User with same username is already exist");
        }
    }

    @Override
    public List<SimpleUser> get() {
        return this.simpleUserRepository.findAll();
    }
}
