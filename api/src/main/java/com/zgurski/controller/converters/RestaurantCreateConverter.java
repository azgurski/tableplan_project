package com.zgurski.controller.converters;

import com.zgurski.controller.requests.RestaurantCreateRequest;
import com.zgurski.domain.hibernate.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RestaurantCreateConverter extends RestaurantBaseConverter<RestaurantCreateRequest, Restaurant> {

//    private final PasswordEncoder encoder;

//    private final RestaurantFieldsGenerator generator;

    @Override
    public Restaurant convert(RestaurantCreateRequest request) {
        Restaurant restaurant = new Restaurant();

        /* System fields filling */
        restaurant.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        restaurant.setChanged(Timestamp.valueOf(LocalDateTime.now()));

//        String generatedEmailUserAuth = generator.generateEmail();
//        String generatedPasswordUserAuth = generator.generatePassword();
//
//        AuthenticationInfo info = new AuthenticationInfo(generatedEmailUserAuth, generatedPasswordUserAuth);
//
//        restaurant.setAuthenticationInfo(info);



        return doConvert(restaurant, request);
    }
}
