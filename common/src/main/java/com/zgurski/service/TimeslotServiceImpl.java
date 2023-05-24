package com.zgurski.service;

import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.domain.hibernate.Timeslot;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.exception.InvalidInputValueException;
import com.zgurski.repository.CalendarDayRepository;
import com.zgurski.repository.TimeslotRepository;
import com.zgurski.util.CustomErrorMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimeslotServiceImpl implements TimeslotService {

    private final RestaurantService restaurantService;

    public final TimeslotRepository timeslotRepository;

    public final CalendarDayService calendarDayService;

    public final CalendarDayRepository calendarDayRepository;

    public final CustomErrorMessageGenerator messageGenerator;

    public List<Timeslot> findAll() {
        List<Timeslot> allTimeslots = timeslotRepository.findAll();
        return checkIfTimeslotListIsNotEmpty(allTimeslots);
    }


    public Page<Timeslot> findAllPageable(Pageable pageable) {

        Page<Timeslot> calendarDayPage = timeslotRepository.findAll(pageable);
        return checkIfPageTimeslotIsNotEmpty(calendarDayPage);
    }

    //TODO check
    public List<Timeslot> findAllByCalendarDay(Long restaurantId, int year, int month, int day) {

        CalendarDay calendarDay = calendarDayService.findByDateAndRestaurantId(restaurantId, year, month, day).get();

        return timeslotRepository.findAllByCalendarDayOrderByLocalTime(calendarDay);
    }

    public List<Timeslot> findAllByIsAvailable(Long restaurantId, int year, int month, int day, Boolean isAvailable) {

        checkIfRestaurantIsOpenByCalendarDay(restaurantId, year, month, day);

        return timeslotRepository.findAllByCalendarDay_LocalDateAndIsAvailableOrderByLocalTime(LocalDate.of(year, month, day), isAvailable);
    }

    public Optional<Timeslot> findOneByLocalTime
            (Long restaurantId, int year, int month, int day, LocalTime localTime) {

        Optional<Timeslot> timeslot = timeslotRepository.findByLocalTimeAndCalendarDay_LocalDate
                (localTime, LocalDate.of(year, month, day));

        checkIfTimeslotPresent(localTime, timeslot);
        return timeslot;
    }



    public Timeslot save(Long restaurantId, int year, int month, int day, Timeslot timeslot) {

        CalendarDay calendarDay = calendarDayService
                .findByDateAndRestaurantId(restaurantId, year, month, day).get();

        if (timeslotRepository.existsTimeslotByLocalTimeAndCalendarDay(timeslot.getLocalTime(), calendarDay)) {
            throw new EntityNotFoundException(messageGenerator
                    .createNoDuplicatesAllowedByLocalTime(Timeslot.class, timeslot.getLocalTime().toString()));

        } else if (calendarDay.getIsOpen()) {
            timeslot.setCalendarDay(calendarDay);

        } else {
            calendarDay.setIsOpen(true);
            timeslot.setCalendarDay(calendarDay);
        }

        return timeslotRepository.save(timeslot);
    }

    public Timeslot update(Long restaurantId, int year, int month, int day, Timeslot timeslot) {

        //на конвертере уже прошла if exists by id

        CalendarDay calendarDay = calendarDayService
                .findByDateAndRestaurantId(restaurantId, year, month, day).get();

        calendarDayService.checkBelongingCalendarDayToRestaurant(restaurantId, calendarDay.getCalendarDayId());
        timeslot.setCalendarDay(calendarDay);
        Timeslot updatedTimeslot = timeslotRepository.save(timeslot);

        //если нету хоть одного доступного
        if (!(timeslotRepository.existsTimeslotsByIsAvailableAndAndCalendarDay(true, calendarDay))) {
            calendarDay.setIsOpen(false);
            calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));
            calendarDayRepository.save(calendarDay);

            //если есть хоть один доступный
            //TODO add check closed day = false to true after first update slot = true
        } else if (calendarDay.getIsOpen() == false && timeslot.getIsAvailable() == true) {
            calendarDay.setIsOpen(true);
            calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));
            calendarDayRepository.save(calendarDay);
        }


//        List<Timeslot> isAvailableSlotList = timeslotRepository
//                .findAllByIsAvailableAndCalendarDay(true, calendarDay);
//
//        if (isAvailableSlotList.isEmpty() && isAvailableSlotList != null) {
//            calendarDay.setIsOpen(false);
//            calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));
//            calendarDayRepository.save(calendarDay);
//        }

        return updatedTimeslot;
    }

    public Long deleteSoft(Long restaurantId, int year, int month, int day, Long timeslotId) {

        CalendarDay calendarDay = calendarDayService
                .findByDateAndRestaurantId(restaurantId, year, month, day).get();

        timeslotRepository.deleteSoft(timeslotId);

        Timeslot deletedTimeslot = timeslotRepository.findById(timeslotId).get();

        //если никого не нашли isDeleted=false, значит, все удалены, значит день деактивировать
        if (!(timeslotRepository.existsTimeslotByIsDeletedAndCalendarDay(false, calendarDay))) {
            calendarDay.setIsOpen(false);
            calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));
            calendarDayRepository.save(calendarDay);

            //если есть хоть один доступный
            //TODO add check closed day = false to true after first update slot = true
        } else if (calendarDay.getIsOpen() == false && deletedTimeslot.getIsDeleted() == false) {
            calendarDay.setIsOpen(true);
            calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));
            calendarDayRepository.save(calendarDay);
        }

        return timeslotId;
    }







    /* Verifications, custom exceptions */

    private Boolean checkIfRestaurantIsOpenByCalendarDay(Long restaurantId, int year, int month, int day) {

        Optional<CalendarDay> calendarDay = calendarDayService.findByDateAndRestaurantId(restaurantId, year, month, day);

        if (calendarDay.get().getIsOpen()) {
            return true;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createEntityIsUnavailableMessage(Restaurant.class, calendarDay.get().getLocalDate()));
        }
    }

    public Boolean checkIfTimeslotExistsById(Long id) {

        if (timeslotRepository.existsByTimeslotId(id)) {
            return true;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Timeslot.class, id.toString()));
        }
    }

//    private Optional<CalendarDay> getCalendarDayIfRestaurantIsOpen(Long restaurantId, int year, int month, int day) {
//
//        Optional<CalendarDay> calendarDay = calendarDayService.findByDateAndRestaurantId(restaurantId, year, month, day);
//
//        if (calendarDay.get().getIsOpen()) {
//            return calendarDay;
//
//        } else {
//            throw new EntityNotFoundException(messageGenerator
//                    .createEntityIsUnavailableMessage(Restaurant.class, calendarDay.get().getLocalDate()));
//        }
//    }

    private Optional<Timeslot> checkIfTimeslotPresent(LocalTime localTime, Optional<Timeslot> timeslot) {
        if (timeslot.isPresent()) {
            return timeslot;
        } else {
            throw new EntityNotFoundException(messageGenerator.createNoEntityFoundByLocalTimeMessage(Timeslot.class, localTime));
        }
    }

    private List<Timeslot> checkIfTimeslotListIsNotEmpty(List<Timeslot> allTimeslots) {

        if (!allTimeslots.isEmpty() && allTimeslots != null) {
            return allTimeslots;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(Timeslot.class));
        }
    }

    private Page<Timeslot> checkIfPageTimeslotIsNotEmpty(Page<Timeslot> timeslotPage) {

        if (!timeslotPage.isEmpty() && timeslotPage != null) {
            return timeslotPage;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Page.class, timeslotPage.toString()));
        }
    }
}
