package com.zgurski.service;

import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.domain.hibernate.Timeslot;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.CalendarDayRepository;
import com.zgurski.util.CustomErrorMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarDayServiceImpl implements CalendarDayService {

    private final RestaurantService restaurantService;

    public final CalendarDayRepository calendarDayRepository;

    public final CustomErrorMessageGenerator messageGenerator;


    public List<CalendarDay> findAll() {

        List<CalendarDay> allCalendarDays = calendarDayRepository.findAll();
        return checkIfCalendarDayListIsNotEmpty(allCalendarDays);
    }

    public Page<CalendarDay> findAllPageable(Pageable pageable) {

        Page<CalendarDay> calendarDayPage = calendarDayRepository.findAll(pageable);
        return checkIfPageCalendarDayIsNotEmpty(calendarDayPage);
    }

    public List<CalendarDay> findAllByRestaurantId(Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);

        List<CalendarDay> allCalendarDays = calendarDayRepository.
                findCalendarDaysByRestaurant_RestaurantIdOrderByLocalDate(restaurantId);

        checkIfCalendarDayListIsNotEmpty(allCalendarDays);

        return allCalendarDays;
    }


    //TODO наверное это и не надо, так как открытые данные
    public Optional<CalendarDay> findByCalendarDayIdAndRestaurantId(Long calendarDayId, Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);
        checkIfCalendarDayExistsById(calendarDayId);
        checkBelongingCalendarDayToRestaurant(restaurantId, calendarDayId);

        return calendarDayRepository.findById(calendarDayId);
    }

    public Optional<CalendarDay> findByDateAndRestaurantId(Long restaurantId, int year, int month, int day) {

        LocalDate localDate = LocalDate.of(year, month, day);
        restaurantService.checkIfRestaurantExistsById(restaurantId);

        Optional<CalendarDay> calendarDay = calendarDayRepository
                .findCalendarDayByLocalDateAndRestaurant_RestaurantId(localDate, restaurantId);

        return checkIfAvailabilityExistsByDate(localDate, calendarDay);
    }

    public CalendarDay save(Long restaurantId, CalendarDay calendarDay) {

        if (calendarDayRepository.existsByLocalDateAndRestaurant_RestaurantId(calendarDay.getLocalDate(), restaurantId)) {
            throw new EntityNotFoundException(messageGenerator
                    .createNoDuplicatesAllowedByLocalTime(CalendarDay.class, calendarDay.getLocalDate().toString()));
        }

        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        calendarDay.setRestaurant(restaurant);

        return calendarDayRepository.save(calendarDay);
    }

    public CalendarDay update(Long restaurantId, CalendarDay calendarDay) {

        Long calendarDayId = calendarDay.getCalendarDayId();
        Restaurant restaurant = restaurantService.findById(restaurantId).get();

//        checkIfCalendarDayExistsById(calendarDayId); в Update конвертере повторяется?
        checkBelongingCalendarDayToRestaurant(restaurantId, calendarDayId);

        calendarDay.setRestaurant(restaurant);

        return calendarDayRepository.save(calendarDay);
    }


    public Long deleteSoft(Long restaurantId, Long calendarDayId) {

        findByCalendarDayIdAndRestaurantId(calendarDayId, restaurantId);
        calendarDayRepository.deleteSoft(calendarDayId);

        return calendarDayId;
    }


    /* Verifications, custom exceptions */

    public Boolean checkBelongingCalendarDayToRestaurant(Long restaurantId, Long calendarDayId) {

        Optional<CalendarDay> calendarDay = calendarDayRepository
                .findCalendarDayByCalendarDayIdAndRestaurant_RestaurantId(calendarDayId, restaurantId);

        if (calendarDay.isPresent()) {
            return true;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(CalendarDay.class, calendarDayId.toString()));
        }
    }

    private Optional<CalendarDay> checkIfAvailabilityExistsByDate(LocalDate localDate, Optional<CalendarDay> calendarDay) {
        if (calendarDay.isPresent()) {
            return calendarDay;
        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundByLocalDateMessage(CalendarDay.class, localDate));
        }
    }

    public Boolean checkIfCalendarDayExistsById(Long id) {

        if (calendarDayRepository.existsByCalendarDayId(id)) {
            return true;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(CalendarDay.class, id.toString()));
        }
    }

    private List<CalendarDay> checkIfCalendarDayListIsNotEmpty(List<CalendarDay> allCalendarDays) {

        if (!allCalendarDays.isEmpty() && allCalendarDays != null) {
            return allCalendarDays;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(CalendarDay.class));
        }
    }

    private Page<CalendarDay> checkIfPageCalendarDayIsNotEmpty(Page<CalendarDay> calendarDayPage) {

        if (!calendarDayPage.isEmpty() && calendarDayPage != null) {
            return calendarDayPage;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Page.class, calendarDayPage.toString()));
        }
    }

    //TODO exception with calendarDay.getDayOfWeekValue
}
