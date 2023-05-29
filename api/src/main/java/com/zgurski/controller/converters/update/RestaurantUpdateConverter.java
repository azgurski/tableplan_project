package com.zgurski.controller.converters.update;

import com.zgurski.controller.converters.base.RestaurantBaseConverter;
import com.zgurski.controller.requests.RestaurantUpdateRequest;

import com.zgurski.domain.entities.Restaurant;
import com.zgurski.repository.RestaurantRepository;
import com.zgurski.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantUpdateConverter extends RestaurantBaseConverter<RestaurantUpdateRequest, Restaurant> {

    private final RestaurantRepository repository;

    private final RestaurantService service;

    @Override
    public Restaurant convert(RestaurantUpdateRequest request) {

        service.checkIfRestaurantExistsById(request.getRestaurantId());
        Optional<Restaurant> restaurant = repository.findById(request.getRestaurantId());

        restaurant.get().setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(restaurant.get(), request);
    }
}