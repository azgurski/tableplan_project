package com.zgurski.controller.converters.create;

import com.zgurski.controller.converters.base.RestaurantBaseConverter;
import com.zgurski.controller.requests.RestaurantCreateRequest;
import com.zgurski.domain.entities.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RestaurantCreateConverter extends RestaurantBaseConverter<RestaurantCreateRequest, Restaurant> {

    @Override
    public Restaurant convert(RestaurantCreateRequest request) {

        Restaurant restaurant = new Restaurant();

        /* System fields filling */
        restaurant.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        restaurant.setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(restaurant, request);
    }
}