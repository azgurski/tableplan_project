package com.zgurski.controller;


import com.zgurski.controller.requests.RestaurantCreateRequest;
import com.zgurski.controller.requests.RestaurantUpdateRequest;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.exception.IllegalRequestException;
import com.zgurski.util.CustomErrorMessageGenerator;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.RestaurantRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    private final CustomErrorMessageGenerator messageGenerator;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @GetMapping()
    public ResponseEntity<Object> findAllRestaurants() {

        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        checkIfEmptyList(allRestaurants);

        return new ResponseEntity<>(Collections.singletonMap("restaurants",
                allRestaurants), HttpStatus.OK);
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<Object> findAllRestaurantsPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        Page<Restaurant> restaurantPage = restaurantRepository.findAll(PageRequest.of(page, size));
        checkIfEmptyPage(restaurantPage);

        return new ResponseEntity<>(Collections.singletonMap("restaurants", restaurantPage), HttpStatus.OK);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<Object> findRestaurantById(@PathVariable String restaurantId) {

        Long id = Long.parseLong(restaurantId);
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);

        checkResultListIfNotNull(restaurantId, restaurant);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class) //над create, update, delete
    @PostMapping
    public ResponseEntity<Object> saveRestaurant(
            @Valid @RequestBody RestaurantCreateRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new IllegalRequestException(bindingResult);
        }

        Restaurant restaurant = conversionService.convert(request, Restaurant.class);

        restaurant = restaurantRepository.save(restaurant);
        return new ResponseEntity<>(restaurant, HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class) //над create, update, delete
    @PutMapping
    public ResponseEntity<Object> updateRestaurant(
            @Valid @RequestBody RestaurantUpdateRequest request) {

        Restaurant restaurant = conversionService.convert(request, Restaurant.class);

        restaurant = restaurantRepository.save(restaurant);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    //для @PatchMapping будет не (patchRequest), а Map<String,Object>
    // мапа будет по ключу, а ключ будет определяться из возможных полей внутри сущности

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class) //над create, update, delete
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Object> deleteSoftRestaurant(
            @PathVariable String restaurantId) {

        Long id = Long.parseLong(restaurantId);
        restaurantRepository.delete(id);

        Optional<Restaurant> restaurant = restaurantRepository.findById(id);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }



    private void checkIfEmptyList(List<Restaurant> allRestaurants) {
        if (allRestaurants.isEmpty()) {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(Restaurant.class));
        }
    }

    private void checkIfEmptyPage(Page<Restaurant> restaurantPage) {
        if (restaurantPage.isEmpty()) {
            throw new EntityNotFoundException("Invalid page number.");
        }
    }

    private void checkResultListIfNotNull(String restaurantId, Optional<Restaurant> restaurant) {
        if (!restaurant.isPresent()) {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Restaurant.class, restaurantId));
        }
    }
}