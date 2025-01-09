package com.kirillpolyakov.service;

import com.kirillpolyakov.model.Admin;
import com.kirillpolyakov.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{

    private AdminRepository adminRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setAdminRepository(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Admin add(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        try {
            this.adminRepository.save(admin);
            return admin;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User with same username is already exist");
        }
    }
}
