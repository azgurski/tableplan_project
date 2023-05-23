package com.zgurski.controller;


import com.zgurski.controller.requests.RestaurantCreateRequest;
import com.zgurski.controller.requests.RestaurantUpdateRequest;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.exception.IllegalRequestException;
import com.zgurski.service.RestaurantService;
import com.zgurski.util.CustomErrorMessageGenerator;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.RestaurantRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @GetMapping()
    public ResponseEntity<Object> findAllRestaurants() {
            return new ResponseEntity<>(Collections.singletonMap("restaurants",
                    restaurantService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<Object> findAllRestaurantsPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("restaurants",
                restaurantService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<Object> findRestaurantById(@PathVariable Long restaurantId) {

        return new ResponseEntity<>(Collections.singletonMap("restaurant",
                restaurantService.findById(restaurantId)), HttpStatus.OK);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping
    public ResponseEntity<Object> saveRestaurant(
            @Valid @RequestBody RestaurantCreateRequest request) {

        Restaurant restaurant = conversionService.convert(request, Restaurant.class);

        return new ResponseEntity<>(Collections.singletonMap("restaurant",
                restaurantService.save(restaurant)), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping
    public ResponseEntity<Object> updateRestaurant(@Valid @RequestBody RestaurantUpdateRequest request) {

        Restaurant restaurant = conversionService.convert(request, Restaurant.class);

        return new ResponseEntity<>(Collections.singletonMap("restaurant",
                restaurantService.update(restaurant)), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Object> deleteSoftRestaurant(
            @PathVariable Long restaurantId) {

        return new ResponseEntity<>(Collections.singletonMap("successMessage",
                "Restaurant with id={" + restaurantService.deleteSoft(restaurantId) +
                        "} is deleted."), HttpStatus.OK);
    }
}