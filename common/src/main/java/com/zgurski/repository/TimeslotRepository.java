package com.zgurski.repository;

import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeslotRepository extends JpaRepository<Timeslot, Long>,
        PagingAndSortingRepository<Timeslot, Long>, CrudRepository<Timeslot, Long> {

    List<Timeslot> findAllByCalendarDay_LocalDateAndIsAvailableOrderByLocalTime(LocalDate localDate, Boolean isAvailable);

    List<Timeslot> findAllByCalendarDayOrderByLocalTime(CalendarDay calendarDay);



//    List<Timeslot> findAllByIsAvailableAndCalendarDay(Boolean isAvailable, CalendarDay calendarDay);

    Optional<Timeslot> findByLocalTimeAndCalendarDay_LocalDate(LocalTime localTime, LocalDate localDate);

    Boolean existsTimeslotByLocalTimeAndCalendarDay(LocalTime localTime, CalendarDay calendarDay);

    Boolean existsTimeslotsByIsAvailableAndAndCalendarDay(Boolean isAvailable, CalendarDay calendarDay);

    Boolean existsTimeslotByIsDeletedAndCalendarDay(Boolean isDeleted, CalendarDay calendarDay);

//    Boolean existsTimeslotsByIsAvailableAndCalendarDay_IsOpen(Boolean isAvailableSlot, Boolean isOpenDay);

    Boolean existsByTimeslotId(Long timeslotId);

    @Modifying
    @Query(value = "update Timeslot tsl set tsl.isAvailable = false, tsl.isDeleted = true, tsl.changed = NOW() " +
            "where tsl.timeslotId = :timeslotId")
    void deleteSoft(Long timeslotId);
}
