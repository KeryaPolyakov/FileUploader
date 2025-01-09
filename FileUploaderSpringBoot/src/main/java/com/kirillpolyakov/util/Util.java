package com.kirillpolyakov.util;

import com.kirillpolyakov.model.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.file.Path;
import java.util.List;

public class Util {

    public static void checkPermission(String path, long placeIdInPath, String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        long id = userDetails.getId();
        Path checkId = Path.of(path).getName((int) placeIdInPath);
        List<GrantedAuthority> roles = (List<GrantedAuthority>) userDetails.getAuthorities();
        if (!String.valueOf(id).equals(checkId.toString())
                && !roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new IllegalArgumentException(message);
        }
    }
}
