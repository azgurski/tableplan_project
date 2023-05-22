package com.zgurski.util;

import org.springframework.stereotype.Component;

@Component
public class CustomErrorMessageGenerator {

    public String createNotFoundByIdMessage(Class clazz, String idValue) {
        return clazz.getSimpleName() + " with {id=" + idValue + "} not found.";
    }

    public String createNoEntityFoundMessage(Class clazz) {
        return "No " + clazz.getSimpleName().toString().toLowerCase() + " found.";
    }
}
