package com.zgurski.controller;

import com.zgurski.controller.hateoas.RestaurantModelAssembler;
import com.zgurski.controller.openapi.restaurant.RestaurantDeleteSoftOpenApi;
import com.zgurski.controller.openapi.restaurant.RestaurantFindAllOpenApi;
import com.zgurski.controller.openapi.restaurant.RestaurantFindOneByIdOpenApi;
import com.zgurski.controller.openapi.restaurant.RestaurantSaveOpenApi;
import com.zgurski.controller.openapi.restaurant.RestaurantUpdateOpenApi;
import com.zgurski.controller.requests.RestaurantCreateRequest;
import com.zgurski.controller.requests.RestaurantUpdateRequest;
import com.zgurski.domain.entities.Restaurant;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/restaurants")
@Tag(name = "Restaurant", description = "Managing restaurant's profile.")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    private final RestaurantModelAssembler restaurantAssembler;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @RestaurantFindOneByIdOpenApi
    @GetMapping("/{restaurantId}")
    public ResponseEntity<EntityModel<Restaurant>> findOneById(@PathVariable Long restaurantId) {

        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        EntityModel<Restaurant> restaurantEntityModel = restaurantAssembler.toModel(restaurant);

        return ResponseEntity.ok(restaurantEntityModel);
    }

    @RestaurantFindAllOpenApi
    @GetMapping()
    public ResponseEntity<Object> findAll() {

        return new ResponseEntity<>(Collections.singletonMap("restaurants",
                restaurantService.findAll()), HttpStatus.OK);
    }

    @Operation(hidden = true)
    @GetMapping("/page/{page}")
    public ResponseEntity<Object> findAllPageable(@Parameter(name = "page", example = "1", required = true)
                                                  @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("restaurants",
                restaurantService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @RestaurantSaveOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody RestaurantCreateRequest request) {

        Restaurant restaurant = conversionService.convert(request, Restaurant.class);

        return new ResponseEntity<>(Collections.singletonMap("restaurant",
                restaurantService.save(restaurant)), HttpStatus.CREATED);
    }

    @RestaurantUpdateOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping
    public ResponseEntity<Object> update(@Valid @RequestBody RestaurantUpdateRequest request) {

        Restaurant restaurant = conversionService.convert(request, Restaurant.class);

        return new ResponseEntity<>(Collections.singletonMap("restaurant",
                restaurantService.update(restaurant)), HttpStatus.CREATED);
    }

    @RestaurantDeleteSoftOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Object> deleteSoft(@PathVariable Long restaurantId) {

        return new ResponseEntity<>(Collections.singletonMap("successMessage",
                "Restaurant with id={" + restaurantService.deleteSoft(restaurantId) +
                        "} is deleted."), HttpStatus.OK);
    }
}