package com.kirillpolyakov.service;

import com.kirillpolyakov.model.User;
import com.kirillpolyakov.model.UserDetailsImpl;
import com.kirillpolyakov.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Optional<User> user = this.userRepository.findByUserName(s);

        user.orElseThrow(() -> new UsernameNotFoundException("Not found " + s));

        return user.map(UserDetailsImpl::new).get();
    }
}
