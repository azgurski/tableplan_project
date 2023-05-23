package com.zgurski.service;

import com.zgurski.domain.hibernate.DefaultWeekDay;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.DefaultWeekDayRepository;
import com.zgurski.util.CustomErrorMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultWeekDayServiceImpl implements DefaultWeekDayService {

    private final RestaurantService restaurantService;

    public final DefaultWeekDayRepository weekDayRepository;

    public final CustomErrorMessageGenerator messageGenerator;


    public List<DefaultWeekDay> findAll() {

        List<DefaultWeekDay> allDefaultWeekDays = weekDayRepository.findAll();
        return checkIfDefaultWeekDayListIsNotEmpty(allDefaultWeekDays);
    }

    public Page<DefaultWeekDay> findAllPageable(Pageable pageable) {

        Page<DefaultWeekDay> weekDayPage = weekDayRepository.findAll(pageable);
        return checkIfPageDefaultWeekDayIsNotEmpty(weekDayPage);
    }

    public List<DefaultWeekDay> findScheduleByRestaurantId(Long id) {

        restaurantService.checkIfRestaurantExistsById(id);
        List<DefaultWeekDay> schedule = weekDayRepository.findDefaultWeekDaysByRestaurant_RestaurantIdOrderByDayOfWeek(id);
        return schedule;
    }

    public Optional<DefaultWeekDay> findByDefaultWeekDayIdAndRestaurantId(Long defaultWeekDayId, Long restaurantId) {

        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        Optional<DefaultWeekDay> weekDay = weekDayRepository.findById(defaultWeekDayId);

        return checkIfDefaultWeekDayBelongsToRestaurant(restaurant, weekDay);
    }

    public DefaultWeekDay save(DefaultWeekDay defaultWeekDay) {

        return weekDayRepository.save(defaultWeekDay);
    }

    public DefaultWeekDay update(Long restaurantId, Long weekDayId) {

        Optional<DefaultWeekDay> weekDay = checkBelongingWeekDayToRestaurant(restaurantId, weekDayId);

        return weekDayRepository.save(weekDay.get());
    }


    public Long deleteSoft(Long restaurantId, Long reservationId) {

        findByDefaultWeekDayIdAndRestaurantId(reservationId, restaurantId);
        weekDayRepository.deleteSoft(reservationId);

        return reservationId;
    }


    /* Verifications, custom exceptions */

    private Optional<DefaultWeekDay> checkBelongingWeekDayToRestaurant(Long restaurantId, Long weekDayId) {
        Restaurant restaurant = restaurantService.findById(restaurantId).get();

        checkIfDefaultWeekDayExistsById(weekDayId);
        Optional<DefaultWeekDay> weekDay = weekDayRepository.findById(weekDayId);

        checkIfDefaultWeekDayBelongsToRestaurant(restaurant, weekDay);
        return weekDay;
    }

    private List<DefaultWeekDay> checkIfDefaultWeekDayListIsNotEmpty(List<DefaultWeekDay> allDefaultWeekDays) {

        if (!allDefaultWeekDays.isEmpty() && allDefaultWeekDays != null) {
            return allDefaultWeekDays;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(DefaultWeekDay.class));
        }
    }

    private Page<DefaultWeekDay> checkIfPageDefaultWeekDayIsNotEmpty(Page<DefaultWeekDay> defaultWeekDayPage) {

        if (!defaultWeekDayPage.isEmpty() && defaultWeekDayPage != null) {
            return defaultWeekDayPage;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Page.class, defaultWeekDayPage.toString()));
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

    public Optional<DefaultWeekDay> checkIfDefaultWeekDayBelongsToRestaurant(Restaurant restaurant, Optional<DefaultWeekDay> weekDay) {
        if (restaurant.getDefaultWeekDays().contains(weekDay.get())) {
            return weekDay;

        } else throw new EntityNotFoundException(messageGenerator
                .createNotFoundByIdMessage(DefaultWeekDay.class, weekDay.get().getDefaultWeekDayId().toString()));
    }

    //TODO exception with weekDay.getDayOfWeekValue
}
