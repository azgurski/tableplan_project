package com.zgurski.repository;

import com.zgurski.domain.entities.CalendarDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarDayRepository extends JpaRepository<CalendarDay, Long>,
        PagingAndSortingRepository<CalendarDay, Long>, CrudRepository<CalendarDay, Long> {

    @Query(value = "select cd from CalendarDay cd where cd.restaurant.restaurantId = :restaurantId " +
            "and cd.isOpen = true and extract(month from cd.localDate) = :month " +
            "and extract(year from cd.localDate) = :year")
    List<CalendarDay> findAllOpenDaysByMonth(Long restaurantId, int year, int month);

    @Query(value = """
                select cd
                from CalendarDay cd
                where cd.restaurant.restaurantId = :restaurantId
                  and cd.isOpen = true
                  and cd.localDate between :dateFrom and :dateTo
            """)
    List<CalendarDay> findAllOpenDaysBetween(
            @Param("restaurantId") Long restaurantId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo
    );

    Optional<CalendarDay> findById(Long calendarDayId);

    Optional<CalendarDay> findCalendarDayByLocalDateAndRestaurant_RestaurantId
            (LocalDate localDate, Long restaurantId);

    Boolean existsByLocalDateAndRestaurant_RestaurantId(LocalDate localDate, Long restaurantId);

    Boolean existsByCalendarDayId(Long calendarDayId);

    @Modifying
    @Query(value = "update CalendarDay cd set cd.isOpen = false, cd.isDeleted = true, " +
            "cd.changed = CURRENT_TIMESTAMP where cd.calendarDayId = :calendarDayId")
    void deleteSoft(Long calendarDayId);
}