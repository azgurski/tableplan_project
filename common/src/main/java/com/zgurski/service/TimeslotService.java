package com.zgurski.service;

import com.zgurski.domain.entities.CalendarDay;
import com.zgurski.domain.entities.Restaurant;
import com.zgurski.domain.entities.Timeslot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeslotService {

    List<Timeslot> findAll();

    Page<Timeslot> findAllPageable(Pageable pageable);

    List<Timeslot> findAllByCalendarDay(Long restaurantId, int year, int month, int day);

    List<Timeslot> findAllWithinThirtyMinutes(Long restaurantId);

    List<Timeslot> findAllByIsAvailable(Long restaurantId, int year, int month, int day, Boolean isAvailable);

    Optional<Timeslot> findOneByLocalTime
            (Long restaurantId, int year, int month, int day, LocalTime localTime);

    Timeslot save(Long restaurantId, int year, int month, int day, Timeslot timeslot);

    Timeslot update(Long restaurantId, int year, int month, int day, Timeslot timeslot);

    Boolean checkIfTimeslotExistsById(Long id);

    CalendarDay resetAllTimeslots(Long restaurantId, int year, int month, int day);

    Boolean checkTimeslotCapacity(int incrementPartySize, LocalDate localDate, LocalTime localTime, Restaurant restaurant);

    Timeslot updateTimeslotCapacity(int incrementPartySize, LocalDate localDate, LocalTime localTime, Restaurant restaurant);

    CalendarDay setTimeslotsToDefault(Long restaurantId, int year, int month, int day);

    Long deleteSoft(Long restaurantId, int year, int month, int day, Long timeslotId);
}