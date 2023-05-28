package com.zgurski.service.impl;

import com.zgurski.domain.entities.Restaurant;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.RestaurantRepository;
import com.zgurski.service.RestaurantService;
import com.zgurski.util.CustomErrorMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    private final CustomErrorMessageGenerator messageGenerator;

    public List<Restaurant> findAll() {

        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        return checkIfRestaurantListNotEmpty(allRestaurants);
    }

    public Page<Restaurant> findAllPageable(Pageable pageable) {

        Page<Restaurant> restaurantPage = restaurantRepository.findAll(pageable);
        return checkIfPageRestaurantNotEmpty(restaurantPage);
    }

    public Optional<Restaurant> findById(Long id) {

        checkIfRestaurantExistsById(id);
        return restaurantRepository.findByRestaurantId(id);
    }

    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Restaurant update(Restaurant restaurant) {

        checkIfRestaurantExistsById(restaurant.getRestaurantId());
        return restaurantRepository.save(restaurant);
    }

    public Long deleteSoft(Long id) {

        checkIfRestaurantExistsById(id);
        restaurantRepository.deleteSoft(id);

        return id;
    }

    /* Verifications, custom exceptions */
    public Boolean checkIfRestaurantExistsById(Long id) {

        if (restaurantRepository.existsRestaurantByRestaurantId(id)) {
            return true;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Restaurant.class, id.toString()));
        }
    }

    public List<Restaurant> checkIfRestaurantListNotEmpty(List<Restaurant> allRestaurants) {

        if (!allRestaurants.isEmpty()) {
            return allRestaurants;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(Restaurant.class));
        }
    }

    public Page<Restaurant> checkIfPageRestaurantNotEmpty(Page<Restaurant> restaurantPage) {

        if (!restaurantPage.isEmpty()) {
            return restaurantPage;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Page.class, restaurantPage.toString()));
        }
    }
}