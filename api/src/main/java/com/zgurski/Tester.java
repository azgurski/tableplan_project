package com.zgurski;

import com.zgurski.domain.hibernate.Restaurant;

import java.time.ZoneId;
import java.util.TimeZone;

public class Tester {
    public static void main(String[] args) {

        System.out.println(TimeZone.getTimeZone("Europe/London"));
    }
}
