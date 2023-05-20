package com.zgurski.controller;


import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.repository.RestaurantRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @GetMapping()
    public ResponseEntity<Object> getAllRestaurants() {
        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        return new ResponseEntity<>(allRestaurants, HttpStatus.OK);
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<Object> getAllRestaurantsPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("result",
                restaurantRepository.findAll(PageRequest.of(page, size))
        ), HttpStatus.OK);
    }
}