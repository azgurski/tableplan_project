package com.zgurski.domain.hibernate;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AuthenticationInfo {

    private String emailUserAuth;

    @JsonIgnore
    private String passwordUserAuth;
}