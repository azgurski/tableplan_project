package com.zgurski.controller;

import com.zgurski.controller.requests.DefaultWeekDayCreateRequest;
import com.zgurski.controller.requests.DefaultWeekDayUpdateRequest;
import com.zgurski.domain.hibernate.DefaultWeekDay;
import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.service.DefaultWeekDayService;
import com.zgurski.service.RestaurantService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
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
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class DefaultWeekDayController {

    private final DefaultWeekDayService weekDayService;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @GetMapping("/schedules")
    public ResponseEntity<Object> findAllSchedulesForAllRestaurants() {
        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDays",
                weekDayService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/schedules/page/{page}")
    public ResponseEntity<Object> findAllSchedulesPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDays",
                weekDayService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    //TODO hateoas
    @GetMapping("/restaurants/{restaurantId}/schedules")
    public ResponseEntity<Object> findSchedulesByRestaurantId(@PathVariable Long restaurantId) {

        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDay",
                weekDayService.findScheduleByRestaurantId(restaurantId)), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/schedules/{defaultWeekDayId}")
    public ResponseEntity<Object> findByWeekDayIdAndRestaurantId(
            @PathVariable("defaultWeekDayId") Long weekDayId,
            @PathVariable Long restaurantId) {

        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDay",
                weekDayService.findByDefaultWeekDayIdAndRestaurantId(weekDayId, restaurantId)), HttpStatus.OK);
    }

    //TODO findTimesByWeekDay criteria

    @GetMapping("/default-times")
    public ResponseEntity<Object> findAllDefaultTimes() {

        return new ResponseEntity<>(Collections.singletonMap("defaultTimes",
                weekDayService.findAllDefaultTimes()), HttpStatus.OK);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping("/restaurants/{restaurantId}/schedules")
    public ResponseEntity<Object> saveDefaultWeekDay(
            @Valid @RequestBody DefaultWeekDayCreateRequest request, @PathVariable Long restaurantId) {

        DefaultWeekDay weekDay = conversionService.convert(request, DefaultWeekDay.class);
        DefaultWeekDay savedWeekDay = weekDayService.save(restaurantId, weekDay);

        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDay", savedWeekDay), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/schedules")
    public ResponseEntity<Object> updateDefaultWeekDay(@Valid @RequestBody DefaultWeekDayUpdateRequest request,
                                                       @PathVariable Long restaurantId) {

        DefaultWeekDay weekDay = conversionService.convert(request, DefaultWeekDay.class);
        DefaultWeekDay updatedWeekDay = weekDayService.update(restaurantId, weekDay);

        return new ResponseEntity<>(Collections.singletonMap("defaultWeekDay", updatedWeekDay), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/restaurants/{restaurantId}/schedules/{defaultWeekDayId}")
    public ResponseEntity<Object> deleteSoftWeekDay(
            @PathVariable Long restaurantId, @PathVariable Long defaultWeekDayId) {

        return new ResponseEntity<>(Collections.singletonMap("successMessage",
                "DefaultWeekDay with id={" + weekDayService.deleteSoft(restaurantId, defaultWeekDayId) +
                        "} is deleted."), HttpStatus.OK);
    }
}
