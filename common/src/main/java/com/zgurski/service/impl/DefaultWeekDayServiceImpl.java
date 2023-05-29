package com.zgurski.service.impl;

import com.zgurski.domain.entities.DefaultTime;
import com.zgurski.domain.entities.DefaultWeekDay;
import com.zgurski.domain.entities.Restaurant;
import com.zgurski.exception.EntityIncorrectOwnerException;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.DefaultTimeRepository;
import com.zgurski.repository.DefaultWeekDayRepository;
import com.zgurski.service.DefaultWeekDayService;
import com.zgurski.service.RestaurantService;
import com.zgurski.util.CustomErrorMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultWeekDayServiceImpl implements DefaultWeekDayService {

    private final RestaurantService restaurantService;

    public final DefaultWeekDayRepository weekDayRepository;

    public final DefaultTimeRepository defaultTimeRepository;

    public final CustomErrorMessageGenerator messageGenerator;


    public List<DefaultWeekDay> findAll() {

        List<DefaultWeekDay> allDefaultWeekDays = weekDayRepository.findAll();
        return checkIfDefaultWeekDayListIsNotEmpty(allDefaultWeekDays);
    }

    public Page<DefaultWeekDay> findAllPageable(Pageable pageable) {

        Page<DefaultWeekDay> weekDayPage = weekDayRepository.findAll(pageable);
        return checkIfPageDefaultWeekDayIsNotEmpty(weekDayPage);
    }

    public List<DefaultWeekDay> findScheduleByRestaurantId(Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);

        List<DefaultWeekDay> allDefaultWeekDays = weekDayRepository.
                findDefaultWeekDaysByRestaurant_RestaurantIdOrderByDayOfWeek(restaurantId);

        checkIfDefaultWeekDayListIsNotEmpty(allDefaultWeekDays);

        return allDefaultWeekDays;
    }

    public List<DefaultTime> findAllDefaultTimes() {
        return defaultTimeRepository.findAllTimes();
    }

    public Optional<DefaultWeekDay> findByDefaultWeekDayIdAndRestaurantId(Long defaultWeekDayId, Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);
        checkIfDefaultWeekDayExistsById(defaultWeekDayId);

        DayOfWeek dayOfWeek = weekDayRepository.findById(defaultWeekDayId).get().getDayOfWeek();
        checkBelongingDefaultWeekDayToRestaurant(restaurantId, defaultWeekDayId, dayOfWeek);

        return weekDayRepository.findById(defaultWeekDayId);
    }

    public Optional<DefaultWeekDay> findDefaultWeekDayByDayOfWeekIsOpenAndRestaurant_RestaurantId(
            DayOfWeek dayOfWeek, Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);

        Optional<DefaultWeekDay> defaultWeekDay = weekDayRepository
                .findDefaultWeekDayByDayOfWeekAndIsOpenAndRestaurant_RestaurantId(dayOfWeek, true, restaurantId);

        checkIfDefaultDayIsPresent(defaultWeekDay);

        return defaultWeekDay;
    }

    public Optional<DefaultWeekDay> findByDayOfWeekAndRestaurant_RestaurantId(
            DayOfWeek dayOfWeek, Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);

        Optional<DefaultWeekDay> defaultWeekDay = weekDayRepository
                .findByDayOfWeekAndRestaurant_RestaurantIdOrderByDayOfWeek(dayOfWeek, restaurantId);

        checkIfDefaultDayIsPresent(defaultWeekDay);

        return defaultWeekDay;
    }

    public DefaultWeekDay save(Long restaurantId, DefaultWeekDay defaultWeekDay) {

        Boolean isWeekDayAlreadyExists = weekDayRepository.existsByDayOfWeekAndRestaurant_RestaurantId(
                defaultWeekDay.getDayOfWeek(), restaurantId);

        if (isWeekDayAlreadyExists) {
            throw new EntityNotFoundException(messageGenerator
                    .createNoDuplicatesAllowedByLocalTimeMessage
                            (DefaultWeekDay.class, defaultWeekDay.getDayOfWeek().toString()));
        }

        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        defaultWeekDay.setRestaurant(restaurant);

        return weekDayRepository.save(defaultWeekDay);
    }

    public DefaultWeekDay update(Long restaurantId, DefaultWeekDay defaultWeekDay) {

        Long defaultWeekDayId = defaultWeekDay.getDefaultWeekDayId();
        DayOfWeek dayOfWeek = defaultWeekDay.getDayOfWeek();
        Restaurant restaurant = restaurantService.findById(restaurantId).get();

        checkIfDefaultWeekDayExistsById(defaultWeekDayId);
        checkBelongingDefaultWeekDayToRestaurant(restaurantId, defaultWeekDayId, dayOfWeek);

        defaultWeekDay.setRestaurant(restaurant);

        return weekDayRepository.save(defaultWeekDay);
    }


    public Long deleteSoft(Long restaurantId, Long reservationId) {

        findByDefaultWeekDayIdAndRestaurantId(reservationId, restaurantId);
        weekDayRepository.deleteSoft(reservationId);

        return reservationId;
    }


    /* Verifications, custom exceptions */

    public Optional<DefaultWeekDay> checkIfDefaultDayIsPresent(Optional<DefaultWeekDay> defaultWeekDay) {
        if (defaultWeekDay.isPresent()) {
            return defaultWeekDay;
        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(DefaultWeekDay.class));
        }
    }

    public Boolean checkBelongingDefaultWeekDayToRestaurant(Long restaurantId, Long defaultWeekDayId,
                                                            DayOfWeek dayOfWeek) {

        Optional<DefaultWeekDay> defaultWeekDay = weekDayRepository
                .findDefaultWeekDayByDefaultWeekDayIdAndRestaurant_RestaurantId(defaultWeekDayId, restaurantId);

        if (defaultWeekDay.isPresent()) {
            return true;

        } else {
            throw new EntityIncorrectOwnerException(messageGenerator
                    .createNoCorrectOwnerMessage(Restaurant.class, DefaultWeekDay.class, dayOfWeek.toString()));
        }
    }

    public Boolean checkIfDefaultWeekDayExistsById(Long id) {

        if (weekDayRepository.existsByDefaultWeekDayId(id)) {
            return true;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(DefaultWeekDay.class, id.toString()));
        }
    }

    public List<DefaultWeekDay> checkIfDefaultWeekDayListIsNotEmpty(List<DefaultWeekDay> allDefaultWeekDays) {

        if (!allDefaultWeekDays.isEmpty()) {
            return allDefaultWeekDays;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(DefaultWeekDay.class));
        }
    }

    public Page<DefaultWeekDay> checkIfPageDefaultWeekDayIsNotEmpty(Page<DefaultWeekDay> defaultWeekDayPage) {

        if (!defaultWeekDayPage.isEmpty()) {
            return defaultWeekDayPage;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Page.class, defaultWeekDayPage.toString()));
        }
    }
}