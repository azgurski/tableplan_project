package com.zgurski.controller;

import com.zgurski.controller.requests.TimeslotCreateRequest;
import com.zgurski.controller.requests.TimeslotSearchLocalTimeCriteria;
import com.zgurski.controller.requests.TimeslotUpdateRequest;
import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.Timeslot;
import com.zgurski.exception.FailedTransactionException;
import com.zgurski.exception.InvalidInputValueException;
import com.zgurski.repository.TimeslotRepository;
import com.zgurski.service.TimeslotService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalTime;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
public class TimeslotController {

    private final TimeslotService timeslotService;

    private final ConversionService conversionService;

    @Value("${spring.data.rest.default-page-size}")
    private Integer size;

    @GetMapping("/timeslots")
    public ResponseEntity<Object> findAllTimeslotsForAllRestaurants() {
        return new ResponseEntity<>(Collections.singletonMap("timeslots",
                timeslotService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/timeslots/page/{page}")
    public ResponseEntity<Object> findAllTimeslotsPageable(
            @Parameter(name = "page", example = "1", required = true)
            @PathVariable("page") int page) {

        return new ResponseEntity<>(Collections.singletonMap("timeslots",
                timeslotService.findAllPageable(PageRequest.of(page, size))), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/timeslots")
    public ResponseEntity<Object> findAllByCalendarDate(
            @PathVariable Long restaurantId, @PathVariable int year, @PathVariable int month, @PathVariable int day) {

        List<Timeslot> timeslots = timeslotService.findAllByCalendarDay(restaurantId, year, month, day);

        return new ResponseEntity<>(Collections.singletonMap("timeslots", timeslots), HttpStatus.OK);
    }


    @GetMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/timeslots/status")
    public ResponseEntity<Object> findAllByIsAvailableByDate(
            @PathVariable Long restaurantId, @PathVariable int year, @PathVariable int month, @PathVariable int day,
            @Valid @RequestParam Boolean isAvailable) {

        List<Timeslot> timeslots = timeslotService.findAllByIsAvailable(restaurantId, year, month, day, isAvailable);

        return new ResponseEntity<>(Collections.singletonMap("timeslots", timeslots), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/availability/timeslots/within-thirty-minutes")
    public ResponseEntity<Object> findAllAvailableToday(@PathVariable Long restaurantId) {

        List<Timeslot> timeslots = timeslotService.findAllWithinThirtyMinutes(restaurantId);

        return new ResponseEntity<>(Collections.singletonMap("timeslots", timeslots), HttpStatus.OK);
    }

    @GetMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/timeslots/begin-time")
    public ResponseEntity<Object> findOneByLocalTime(
            @PathVariable Long restaurantId, @PathVariable int year, @PathVariable int month, @PathVariable int day,
            @Valid @ModelAttribute TimeslotSearchLocalTimeCriteria criteria, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new InvalidInputValueException();
        }

        LocalTime localTime = LocalTime.parse(criteria.getLocalTime().replace("_", ":"));
        Optional<Timeslot> timeslot = timeslotService.findOneByLocalTime(restaurantId, year, month, day, localTime);

        return new ResponseEntity<>(Collections.singletonMap("timeslots", timeslot), HttpStatus.OK);
    }

    //TODO batch update
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/timeslots")
    public ResponseEntity<Object> saveTimeslot(
            @PathVariable Long restaurantId,  @Min(value = 2023) @Max(value = 2024) @PathVariable int year,
            @PathVariable int month, @PathVariable int day,
            @Valid @RequestBody TimeslotCreateRequest request) {

        Timeslot timeslot = conversionService.convert(request, Timeslot.class);
        Timeslot savedTimeslot = timeslotService.save(restaurantId, year, month, day, timeslot);

        return new ResponseEntity<>(Collections.singletonMap("timeslot", savedTimeslot), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PostMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/timeslots/set-to-default")
    public ResponseEntity<Object> saveTimeslotsAccordingToDefault(
            @PathVariable Long restaurantId, @Min(value = 2023) @Max(value = 2024) @PathVariable int year,
            @PathVariable int month, @PathVariable int day) {

        CalendarDay calendarDay = timeslotService.setTimeslotsToDefault(restaurantId, year, month, day);

        return new ResponseEntity<>(Collections.singletonMap("calendarDay", calendarDay), HttpStatus.CREATED);
    }

    //TODO batch update
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/timeslots")
    public ResponseEntity<Object> updateTimeslot(
            @PathVariable Long restaurantId, @Min(value = 2023) @Max(value = 2024) @PathVariable int year,
            @PathVariable int month, @PathVariable int day,
            @Valid @RequestBody TimeslotUpdateRequest request) {

        Timeslot timeslot = conversionService.convert(request, Timeslot.class);
        Timeslot updatedTimeslot = timeslotService.update(restaurantId, year, month, day, timeslot);

        return new ResponseEntity<>(Collections.singletonMap("timeslot", updatedTimeslot), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @PutMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/timeslots/reset-all")
    public ResponseEntity<Object> resetTimeslots(
            @PathVariable Long restaurantId, @Min(value = 2023) @Max(value = 2024) @PathVariable int year,
            @PathVariable int month, @PathVariable int day) {

        CalendarDay updatedDay = timeslotService.resetAllTimeslots(restaurantId, year, month, day);

        return new ResponseEntity<>(Collections.singletonMap("calendarDay", updatedDay), HttpStatus.CREATED);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = FailedTransactionException.class)
    @DeleteMapping("/restaurants/{restaurantId}/availability/{year}/{month}/{day}/timeslots/{timeslotId}")
    public ResponseEntity<Object> deleteAvailability(
            @PathVariable Long restaurantId, @Min(value = 2023) @Max(value = 2024) @PathVariable int year,
            @PathVariable int month, @PathVariable int day,
            @PathVariable Long timeslotId) {

        return new ResponseEntity<>(Collections.singletonMap("successMessage",
                "Timeslot with id={" + timeslotService.deleteSoft(restaurantId, year, month, day, timeslotId) +
                        "} is deleted."), HttpStatus.OK);

    }

}