package com.zgurski.controller.converters;

import com.zgurski.controller.requests.RestaurantUpdateRequest;

import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.RestaurantRepository;
import com.zgurski.util.CustomErrorMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityExistsException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantUpdateConverter extends RestaurantBaseConverter<RestaurantUpdateRequest, Restaurant> {

    private final RestaurantRepository repository;

    private final CustomErrorMessageGenerator messageGenerator;

    @Override
    public Restaurant convert(RestaurantUpdateRequest request) {

        Optional<Restaurant> restaurant = repository.findById(request.getRestaurantId());

        if (!restaurant.isPresent()) {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Restaurant.class, request.getRestaurantId().toString()));
        }


        restaurant.get().setChanged(Timestamp.valueOf(LocalDateTime.now()));


        return doConvert(restaurant.get(), request);
    }
}
