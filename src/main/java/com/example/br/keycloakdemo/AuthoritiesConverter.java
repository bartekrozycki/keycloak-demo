package com.example.br.keycloakdemo;

import java.util.Collection;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;

interface AuthoritiesConverter extends
    Converter<Map<String, Object>, Collection<GrantedAuthority>> {

}
