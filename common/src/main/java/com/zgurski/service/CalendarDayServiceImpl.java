package com.zgurski.service;

import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.CalendarDayRepository;
import com.zgurski.util.CustomErrorMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public List<CalendarDay> findScheduleByRestaurantId(Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);

        List<CalendarDay> allCalendarDays = calendarDayRepository.
                findCalendarDaysByRestaurant_RestaurantIdOrderByLocalDate(restaurantId);

        checkIfCalendarDayListIsNotEmpty(allCalendarDays);

        return allCalendarDays;
    }

    public Optional<CalendarDay> findByCalendarDayIdAndRestaurantId(Long calendarDayId, Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);
        checkIfCalendarDayExistsById(calendarDayId);
        checkBelongingCalendarDayToRestaurant(restaurantId, calendarDayId);

        return calendarDayRepository.findById(calendarDayId);
    }

    public CalendarDay save(Long restaurantId, CalendarDay calendarDay) {
        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        calendarDay.setRestaurant(restaurant);

        return calendarDayRepository.save(calendarDay);
    }

    public CalendarDay update(Long restaurantId, CalendarDay calendarDay) {

        Long calendarDayId = calendarDay.getCalendarDayId();
        Restaurant restaurant = restaurantService.findById(restaurantId).get();

        checkIfCalendarDayExistsById(calendarDayId);
        checkBelongingCalendarDayToRestaurant(restaurantId, calendarDayId);

        calendarDay.setRestaurant(restaurant);

        return calendarDayRepository.save(calendarDay);
    }


    public Long deleteSoft(Long restaurantId, Long reservationId) {

        findByCalendarDayIdAndRestaurantId(reservationId, restaurantId);
        calendarDayRepository.deleteSoft(reservationId);

        return reservationId;
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
