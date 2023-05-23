package com.zgurski.service;

import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.repository.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {

    List<Restaurant> findAll();

    Page<Restaurant> findAllPageable(Pageable pageable);

    Optional<Restaurant> findById(Long id);

    Restaurant save(Restaurant restaurant);

    Restaurant update(Restaurant restaurant);

    Optional<Restaurant> deleteSoft(Long restaurantId);

    Boolean checkIfRestaurantExistsById(Long id);
}
