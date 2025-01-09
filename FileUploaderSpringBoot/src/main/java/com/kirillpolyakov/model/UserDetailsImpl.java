package com.kirillpolyakov.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * UserDetailsImpl – объект пользователя для работы с аутентификацией в SpringSecurity.
 * Имеет поля логина, парол яи списка ролей, создается на основе объекта User.
 * Роль в списке ролей будет только одна исходя из типа класса наследника(ROLE_ADMIN, ROLE_APPRENTICE, ROLE_TRAINER)
 * и определяется по типу ROLE + название класса, объект которого записан в переменной User
 */
public class UserDetailsImpl implements UserDetails {

    private long id;
    private String userName;
    private String password;

    private List<GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getClass().getSimpleName().toUpperCase()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public long getId() {
        return id;
    }
}
