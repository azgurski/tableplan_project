package com.zgurski.service;

import com.zgurski.domain.hibernate.Timeslot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeslotService {

    List<Timeslot> findAll();

    Page<Timeslot> findAllPageable(Pageable pageable);

    List<Timeslot> findAllByCalendarDay(Long restaurantId, int year, int month, int day);

    List<Timeslot> findAllByIsAvailable(Long restaurantId, int year, int month, int day, Boolean isAvailable);

    Optional<Timeslot> findOneByLocalTime
            (Long restaurantId, int year, int month, int day, LocalTime localTime);

    Timeslot save(Long restaurantId, int year, int month, int day, Timeslot timeslot);

    Timeslot update(Long restaurantId, int year, int month, int day, Timeslot timeslot);

    Boolean checkIfTimeslotExistsById(Long id);

    Long deleteSoft(Long restaurantId, int year, int month, int day, Long timeslotId);
}