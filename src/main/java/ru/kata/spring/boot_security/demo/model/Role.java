package ru.kata.spring.boot_security.demo.model;

import org.springframework.security.core.GrantedAuthority;

public class Rore implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return "";
    }
}
