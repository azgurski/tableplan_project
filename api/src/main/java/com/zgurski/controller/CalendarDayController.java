package com.zgurski.controller;

import com.zgurski.controller.requests.CalendarDayCreateRequest;
import com.zgurski.controller.requests.CalendarDayUpdateRequest;
import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.service.CalendarDayService;
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
import java.time.LocalDate;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class CalendarDayController {

    private final CalendarDayService calendarDayService;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @Value("${crud_limit_offset.default-page-size-by-month}")
    private Integer monthPageSize;

    @GetMapping("/availability")
    public ResponseEntity<Object> findAllForAllRestaurants() {
        return new ResponseEntity<>(Collections.singletonMap("availabilities",
                calendarDayService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/availability/page/{page}")
    public ResponseEntity<Object> findAllForAllRestaurantsPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("availabilities",
                calendarDayService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/availability")
    public ResponseEntity<Object> findAllForNextSixtyDays(@PathVariable Long restaurantId) {

        return new ResponseEntity<>(Collections.singletonMap("calendarDay",
                calendarDayService.findAllForNextSixtyDays(restaurantId)), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/availability/{year}/{month}")
    public ResponseEntity<Object> findAllByMonth(
            @PathVariable Long restaurantId, @PathVariable int year, @PathVariable int month) {

        return new ResponseEntity<>(Collections.singletonMap("calendarDay",
                calendarDayService.findAllByMonth(restaurantId, year, month)), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}")
    public ResponseEntity<Object> findByDateAndRestaurantId
            (@PathVariable Long restaurantId, @PathVariable int year, @PathVariable int month, @PathVariable int day) {

        return new ResponseEntity<>(Collections.singletonMap("calendarDay",
                calendarDayService.findByDateAndRestaurantId(restaurantId, year, month, day)), HttpStatus.OK);
    }


    //TODO findTimesByWeekDay

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping("/restaurants/{restaurantId}/availability")
    public ResponseEntity<Object> saveAvailability(
            @Valid @RequestBody CalendarDayCreateRequest request, @PathVariable Long restaurantId) {

        CalendarDay calendarDay = conversionService.convert(request, CalendarDay.class);
        CalendarDay savedCalendarDay = calendarDayService.save(restaurantId, calendarDay);

        return new ResponseEntity<>(Collections.singletonMap("calendarDay", savedCalendarDay), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/availability")
    public ResponseEntity<Object> updateAvailability(@Valid @RequestBody CalendarDayUpdateRequest request,
                                                    @PathVariable Long restaurantId) {

        CalendarDay calendarDay = conversionService.convert(request, CalendarDay.class);
        CalendarDay updatedCalendarDay = calendarDayService.update(restaurantId, calendarDay);

        return new ResponseEntity<>(Collections.singletonMap("calendarDay", updatedCalendarDay), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/restaurants/{restaurantId}/availability/{calendarDayId}")
    public ResponseEntity<Object> deleteAvailability(
            @PathVariable Long restaurantId, @PathVariable Long calendarDayId) {

        return new ResponseEntity<>(Collections.singletonMap("successMessage",
                "CalendarDay with id={" + calendarDayService.deleteSoft(restaurantId, calendarDayId) +
                        "} is deleted."), HttpStatus.OK);

    } //TODO return message with Calendar Date
}