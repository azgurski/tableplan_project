package com.zgurski.service;

import com.zgurski.domain.entities.DefaultTime;
import com.zgurski.domain.entities.DefaultWeekDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface DefaultWeekDayService {
    List<DefaultWeekDay> findAll();

    Page<DefaultWeekDay> findAllPageable(Pageable pageable);

    Optional<DefaultWeekDay> findByDefaultWeekDayIdAndRestaurantId(Long defaultWeekDayId, Long restaurantId);

    Optional<DefaultWeekDay> findDefaultWeekDayByDayOfWeekIsOpenAndRestaurant_RestaurantId(
            DayOfWeek dayOfWeek, Long restaurantId);

    Optional<DefaultWeekDay> findByDayOfWeekAndRestaurant_RestaurantId(
            DayOfWeek dayOfWeek, Long restaurantId);


    List<DefaultWeekDay> findScheduleByRestaurantId(Long id);

    List<DefaultTime> findAllDefaultTimes();

    DefaultWeekDay save(Long restaurantId, DefaultWeekDay defaultWeekDay);

    DefaultWeekDay update(Long restaurantId, DefaultWeekDay defaultWeekDay);

    Boolean checkIfDefaultWeekDayExistsById(Long id);

    Long deleteSoft(Long restaurantId, Long defaultWeekDayId);
}