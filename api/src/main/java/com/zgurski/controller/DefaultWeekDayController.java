package com.zgurski.controller;

import com.zgurski.controller.hateoas.DefaultWeekDayModelAssembler;
import com.zgurski.controller.openapi.defaultweekday.DefaultTimesFindAllOpenApi;
import com.zgurski.controller.openapi.defaultweekday.DefaultWeekDayDeleteSoftOpenApi;
import com.zgurski.controller.openapi.defaultweekday.DefaultWeekDayFindAllByRestaurantIdOpenApi;
import com.zgurski.controller.openapi.defaultweekday.DefaultWeekDayFindOneByDayOfWeekAndRestaurantIdOpenApi;
import com.zgurski.controller.openapi.defaultweekday.DefaultWeekDaySaveOpenApi;
import com.zgurski.controller.openapi.defaultweekday.DefaultWeekDayUpdateOpenApi;
import com.zgurski.controller.requests.DefaultWeekDayCreateRequest;
import com.zgurski.controller.requests.DefaultWeekDayUpdateRequest;
import com.zgurski.domain.entities.DefaultWeekDay;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.service.DefaultWeekDayService;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Default Week Day", description = "Managing default schedule for MONDAYs, TUESDAYs etc.")
@RequiredArgsConstructor
public class DefaultWeekDayController {

    private final DefaultWeekDayService weekDayService;

    private final DefaultWeekDayModelAssembler defaultWeekDayAssembler;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @DefaultWeekDayFindOneByDayOfWeekAndRestaurantIdOpenApi
    @GetMapping("/restaurants/{restaurantId}/schedules/schedule/{dayOfWeek}")
    public ResponseEntity<EntityModel<DefaultWeekDay>> findOneByDayOfWeekAndRestaurantId(
            @PathVariable("dayOfWeek") String dayOfWeekString, @PathVariable Long restaurantId) {

        DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekString.toUpperCase());

        DefaultWeekDay defaultWeekDay = weekDayService
                .findByDayOfWeekAndRestaurant_RestaurantId(dayOfWeek, restaurantId).get();

        EntityModel<DefaultWeekDay> defaultWeekDayEntityModel = defaultWeekDayAssembler.toModel(defaultWeekDay);

        return ResponseEntity.ok(defaultWeekDayEntityModel);
    }

    @Operation(hidden = true)
    @GetMapping("/restaurants/{restaurantId}/schedules/{defaultWeekDayId}")
    public ResponseEntity<EntityModel<DefaultWeekDay>> findOneByIdAndRestaurantId(
            @PathVariable("defaultWeekDayId") Long weekDayId, @PathVariable Long restaurantId) {

        DefaultWeekDay defaultWeekDay = weekDayService
                .findByDefaultWeekDayIdAndRestaurantId(weekDayId, restaurantId).get();

        EntityModel<DefaultWeekDay> defaultWeekDayEntityModel = defaultWeekDayAssembler.toModel(defaultWeekDay);

        return ResponseEntity.ok(defaultWeekDayEntityModel);
    }

    @DefaultWeekDayFindAllByRestaurantIdOpenApi
    @GetMapping("/restaurants/{restaurantId}/schedules")
    public ResponseEntity<List<EntityModel<DefaultWeekDay>>> findAllByRestaurantId(@PathVariable Long restaurantId) {

        List<EntityModel<DefaultWeekDay>> schedules = weekDayService
                .findScheduleByRestaurantId(restaurantId).stream()
                .map(defaultWeekDayAssembler::toModel).collect(Collectors.toList());

        return ResponseEntity.ok(schedules);
    }

    @DefaultTimesFindAllOpenApi
    @GetMapping("/default-times")
    public ResponseEntity<Object> findAllDefaultTimes() {

        return new ResponseEntity<>(Collections.singletonMap("defaultTimes",
                weekDayService.findAllDefaultTimes()), HttpStatus.OK);
    }

    /* CRUD Methods */

    @Operation(hidden = true)
    @GetMapping("/schedules")
    public ResponseEntity<Object> findAll() {

        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDays",
                weekDayService.findAll()), HttpStatus.OK);
    }

    @Operation(hidden = true)
    @GetMapping("/schedules/page/{page}")
    public ResponseEntity<Object> findAllPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDays",
                weekDayService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @DefaultWeekDaySaveOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping("/restaurants/{restaurantId}/schedules")
    public ResponseEntity<Object> save(
            @Valid @RequestBody DefaultWeekDayCreateRequest request, @PathVariable Long restaurantId) {

        DefaultWeekDay weekDay = conversionService.convert(request, DefaultWeekDay.class);
        DefaultWeekDay savedWeekDay = weekDayService.save(restaurantId, weekDay);

        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDay", savedWeekDay), HttpStatus.CREATED);
    }

    @DefaultWeekDayUpdateOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/schedules")
    public ResponseEntity<Object> update(
            @Valid @RequestBody DefaultWeekDayUpdateRequest request, @PathVariable Long restaurantId) {

        DefaultWeekDay weekDay = conversionService.convert(request, DefaultWeekDay.class);
        DefaultWeekDay updatedWeekDay = weekDayService.update(restaurantId, weekDay);

        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDay", updatedWeekDay), HttpStatus.CREATED);
    }

    @DefaultWeekDayDeleteSoftOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/restaurants/{restaurantId}/schedules/{defaultWeekDayId}")
    public ResponseEntity<Object> deleteSoft(@PathVariable Long restaurantId, @PathVariable Long defaultWeekDayId) {

        return new ResponseEntity<>(Collections.singletonMap("successMessage",
                "DefaultWeekDay with id={" + weekDayService.deleteSoft(restaurantId, defaultWeekDayId) +
                        "} is deleted."), HttpStatus.OK);
    }
}