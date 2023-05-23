package com.zgurski.repository;

import com.zgurski.domain.hibernate.CalendarDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarDayRepository extends JpaRepository<CalendarDay, Long>,
        PagingAndSortingRepository<CalendarDay, Long>, CrudRepository<CalendarDay, Long> {

    Optional<CalendarDay> findById(Long calendarDayId);

    Optional<CalendarDay> findCalendarDayByCalendarDayIdAndRestaurant_RestaurantId
            (Long calendarDayId, Long restaurantId);

    List<CalendarDay> findCalendarDaysByRestaurant_RestaurantIdOrderByLocalDate(Long restaurantId);

    Boolean existsByCalendarDayId(Long calendarDayId);

    @Modifying
    @Query(value = "update CalendarDay cd set cd.isDeleted = true, cd.changed = NOW() where cd.calendarDayId = :calendarDayId")
    void deleteSoft(Long calendarDayId);
}
