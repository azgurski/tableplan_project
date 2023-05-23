package com.zgurski.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Component
public class CustomErrorMessageGenerator {

    public String createNotFoundByIdMessage(Class clazz, String idValue) {
        return clazz.getSimpleName() + " with {id=" + idValue + "} not found.";
    }

    public String createNoEntityFoundMessage(Class clazz) {
        return "No " + clazz.getSimpleName().toString().toLowerCase() + "(s) found.";
    }

}
