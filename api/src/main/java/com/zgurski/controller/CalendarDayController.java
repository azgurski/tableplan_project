package com.zgurski.controller;

import com.zgurski.controller.hateoas.CalendarDayModelAssembler;
import com.zgurski.controller.openapi.calendarday.CalendarDayDeleteSoftOpenApi;
import com.zgurski.controller.openapi.calendarday.CalendarDayFindAllByMonthOpenApi;
import com.zgurski.controller.openapi.calendarday.CalendarDayFindAllForNextSixtyDays;
import com.zgurski.controller.openapi.calendarday.CalendarDaySaveOpenApi;
import com.zgurski.controller.openapi.calendarday.CalendarDayUpdateOpenApi;
import com.zgurski.controller.openapi.calendarday.CalendarDayFindOneByDateAndRestaurantIdOpenApi;
import com.zgurski.controller.requests.CalendarDayCreateRequest;
import com.zgurski.controller.requests.CalendarDayUpdateRequest;
import com.zgurski.domain.entities.CalendarDay;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.service.CalendarDayService;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Tag(name = "Calendar Day", description = "Managing restaurant's availability by calendar date.")
public class CalendarDayController {

    private final CalendarDayService calendarDayService;

    private final CalendarDayModelAssembler calendarDayAssembler;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @Operation(hidden = true)
    @GetMapping("/restaurants/{restaurantId}/availability/{calendarDateId}")
    public ResponseEntity<EntityModel<CalendarDay>> findOneByCalendarDateIdAndRestaurantId
            (@PathVariable Long restaurantId, @PathVariable Long calendarDateId) {

        CalendarDay calendarDay = calendarDayService.findById(restaurantId, calendarDateId).get();
        EntityModel<CalendarDay> calendarDayEntityModel = calendarDayAssembler.toModel(calendarDay);

        return ResponseEntity.ok(calendarDayEntityModel);
    }

    @CalendarDayFindOneByDateAndRestaurantIdOpenApi
    @GetMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}")
    public ResponseEntity<EntityModel<CalendarDay>> findOneByDateAndRestaurantId
            (@PathVariable Long restaurantId, @PathVariable int year, @PathVariable int month, @PathVariable int day) {

        CalendarDay calendarDay = calendarDayService
                .findByDateAndRestaurantId(restaurantId, year, month, day).get();
        EntityModel<CalendarDay> calendarDayEntityModel = calendarDayAssembler.toModel(calendarDay);

        return ResponseEntity.ok(calendarDayEntityModel);
    }

    @CalendarDayFindAllByMonthOpenApi
    @GetMapping("/restaurants/{restaurantId}/availability/{year}/{month}")
    public ResponseEntity<List<EntityModel<CalendarDay>>> findAllByMonth(
            @PathVariable Long restaurantId, @PathVariable int year, @PathVariable int month) {

        List<EntityModel<CalendarDay>> calendarDays = calendarDayService
                .findAllByMonth(restaurantId, year, month).stream()
                .map(calendarDayAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(calendarDays);
    }

    @CalendarDayFindAllForNextSixtyDays
    @GetMapping("/restaurants/{restaurantId}/availability")
    public ResponseEntity<List<EntityModel<CalendarDay>>> findAllForNextSixtyDays(@PathVariable Long restaurantId) {

        List<EntityModel<CalendarDay>> calendarDays = calendarDayService
                .findAllForNextSixtyDays(restaurantId).stream()
                .map(calendarDayAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(calendarDays);
    }

    /* CRUD */

    @Operation(hidden = true)
    @GetMapping("/availability")
    public ResponseEntity<Object> findAll() {

        return new ResponseEntity<>(Collections.singletonMap("availabilities",
                calendarDayService.findAll()), HttpStatus.OK);
    }

    @Operation(hidden = true)
    @GetMapping("/availability/page/{page}")
    public ResponseEntity<Object> findAllPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("availabilities",
                calendarDayService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @CalendarDaySaveOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping("/restaurants/{restaurantId}/availability")
    public ResponseEntity<Object> save(
            @Valid @RequestBody CalendarDayCreateRequest request, @PathVariable Long restaurantId) {

        CalendarDay calendarDay = conversionService.convert(request, CalendarDay.class);
        CalendarDay savedCalendarDay = calendarDayService.save(restaurantId, calendarDay);

        return new ResponseEntity<>(Collections.singletonMap("calendarDay", savedCalendarDay), HttpStatus.CREATED);
    }

    @CalendarDayUpdateOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/availability")
    public ResponseEntity<Object> update(
            @Valid @RequestBody CalendarDayUpdateRequest request, @PathVariable Long restaurantId) {

        CalendarDay calendarDay = conversionService.convert(request, CalendarDay.class);
        CalendarDay updatedCalendarDay = calendarDayService.update(restaurantId, calendarDay);

        return new ResponseEntity<>(Collections.singletonMap("calendarDay", updatedCalendarDay), HttpStatus.CREATED);
    }

    @CalendarDayDeleteSoftOpenApi
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/restaurants/{restaurantId}/availability/{calendarDayId}")
    public ResponseEntity<Object> deleteSoft(@PathVariable Long restaurantId, @PathVariable Long calendarDayId) {

        return new ResponseEntity<>(Collections.singletonMap("successMessage",
                "CalendarDay with id={" + calendarDayService.deleteSoft(restaurantId, calendarDayId) +
                        "} is deleted."), HttpStatus.OK);
    }
}