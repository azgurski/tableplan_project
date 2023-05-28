package com.zgurski.service.impl;

import com.zgurski.domain.entities.CalendarDay;
import com.zgurski.domain.entities.DefaultTime;
import com.zgurski.domain.entities.DefaultWeekDay;
import com.zgurski.domain.entities.Restaurant;
import com.zgurski.domain.entities.Timeslot;
import com.zgurski.exception.EntityNotAddedException;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.CalendarDayRepository;
import com.zgurski.repository.TimeslotRepository;
import com.zgurski.service.CalendarDayService;
import com.zgurski.service.DefaultWeekDayService;
import com.zgurski.service.RestaurantService;
import com.zgurski.service.TimeslotService;
import com.zgurski.util.CustomErrorMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TimeslotServiceImpl implements TimeslotService {

    private final RestaurantService restaurantService;

    public final TimeslotRepository timeslotRepository;

    public final CalendarDayService calendarDayService;

    public final CalendarDayRepository calendarDayRepository;

    public final DefaultWeekDayService defaultWeekDayService;

    public final CustomErrorMessageGenerator messageGenerator;

    public final EntityManager entityManager;


    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private Integer batchSize;


    public List<Timeslot> findAll() {
        List<Timeslot> allTimeslots = timeslotRepository.findAll();
        return checkIfTimeslotListIsNotEmpty(allTimeslots);
    }


    public Page<Timeslot> findAllPageable(Pageable pageable) {

        Page<Timeslot> calendarDayPage = timeslotRepository.findAll(pageable);
        return checkIfPageTimeslotIsNotEmpty(calendarDayPage);
    }

    public List<Timeslot> findAllWithinThirtyMinutes(Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);

        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now().plusMinutes(30);

        Optional<CalendarDay> calendarDay = calendarDayRepository
                .findCalendarDayByLocalDateAndRestaurant_RestaurantId(localDate, restaurantId);

        calendarDayService.checkIfCalendarDayIsPresentByDay(localDate, calendarDay);

        List<Timeslot> timeslots = timeslotRepository
                .findAllAvailableSlotsByMinutesFromNow(calendarDay.get(), localTime);

        return checkIfTimeslotListIsNotEmpty(timeslots);
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

        checkIfTimeslotOptionalPresent(localTime, timeslot);
        return timeslot;
    }


    public Timeslot save(Long restaurantId, int year, int month, int day, Timeslot timeslot) {

        CalendarDay calendarDay = calendarDayService
                .findByDateAndRestaurantId(restaurantId, year, month, day).get();


        //TODO check all saves
        if (timeslotRepository.existsTimeslotByLocalTimeAndCalendarDay(timeslot.getLocalTime(), calendarDay)) {
            throw new EntityNotFoundException(messageGenerator
                    .createNoDuplicatesAllowedByLocalTimeMessage(Timeslot.class, timeslot.getLocalTime().toString()));

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

        calendarDayService
                .checkBelongingCalendarDayToRestaurant(restaurantId, calendarDay.getCalendarDayId(), calendarDay.getLocalDate());

        timeslot.setCalendarDay(calendarDay);
        Timeslot updatedTimeslot = timeslotRepository.save(timeslot);

        //если нету хоть одного доступного
        if (!(timeslotRepository.existsTimeslotsByIsAvailableAndCalendarDay(true, calendarDay))) {
            calendarDay.setIsOpen(false);
            calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));
            calendarDayRepository.save(calendarDay);

            //если есть хоть один доступный
            //TODO add check closed day = false to true after first update slot = true
        } else if (!calendarDay.getIsOpen() && timeslot.getIsAvailable()) {
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

    public CalendarDay resetAllTimeslots(Long restaurantId, int year, int month, int day) {


        CalendarDay calendarDay = calendarDayService.findByDateAndRestaurantId(
                restaurantId, year, month, day).get();

//        Long calendarDayId = calendarDay.getCalendarDayId();

//        calendarDayService.checkBelongingCalendarDayToRestaurant(
//                calendarDayId, restaurantId, LocalDate.of(year, month, day));

        timeslotRepository.closeAllTimeslots(calendarDay);

        calendarDay.setIsOpen(false);
        calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return calendarDayRepository.save(calendarDay);
    }

    @Transactional
    public CalendarDay setTimeslotsToDefault(Long restaurantId, int year, int month, int day) {

        Restaurant restaurant = restaurantService.findById(restaurantId).get();

        LocalDate localDate = LocalDate.of(year, month, day);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();

        resetCalendarDayTimeslots(localDate, restaurant, restaurantId);

        batchUpdateTimeslotsFromDefault(localDate, dayOfWeek, restaurantId);

        return calendarDayService.findByDateAndRestaurantId(restaurantId, year, month, day).get();
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
        } else if (!calendarDay.getIsOpen() && !deletedTimeslot.getIsDeleted()) {
            calendarDay.setIsOpen(true);
            calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));
            calendarDayRepository.save(calendarDay);
        }

        return timeslotId;
    }

    /* Transfer DefaultDay, DefaultTimes -> CalendarDay, Timeslots */
    private void resetCalendarDayTimeslots(LocalDate localDate, Restaurant restaurant, Long restaurantId) {

        Optional<CalendarDay> calendarDayOptional = calendarDayRepository
                .findCalendarDayByLocalDateAndRestaurant_RestaurantId(localDate, restaurantId);

        if (!calendarDayOptional.isPresent()) {

            CalendarDay calendarDayToCreate = CalendarDay.builder()
                    .localDate(localDate)
                    .isOpen(true)
                    .created(Timestamp.valueOf(LocalDateTime.now()))
                    .changed(Timestamp.valueOf(LocalDateTime.now()))
                    .isDeleted(false)
                    .restaurant(restaurant)
                    .build();

            calendarDayRepository.save(calendarDayToCreate);

        } else {
            calendarDayOptional.get().setIsOpen(true);
            timeslotRepository.closeAllTimeslots(calendarDayOptional.get());
            calendarDayRepository.save(calendarDayOptional.get());
        }
    }


    private void batchUpdateTimeslotsFromDefault(
            LocalDate localDate, DayOfWeek dayOfWeek, Long restaurantId) {

        CalendarDay calendarDay = calendarDayRepository
                .findCalendarDayByLocalDateAndRestaurant_RestaurantId(localDate, restaurantId).get();

        DefaultWeekDay defaultWeekDay = defaultWeekDayService
                .findDefaultWeekDayByDayOfWeekIsOpenAndRestaurant_RestaurantId(dayOfWeek, restaurantId).get();

        int defaultMaxCapacity = calendarDay.getRestaurant().getDefaultTimeslotCapacity();

        Set<DefaultTime> defaultTimes = defaultWeekDay.getDefaultTimes();

        List<LocalTime> localTimeList = defaultTimes.stream().map(
                DefaultTime::getLocalTime).toList();

        for (int index = 0; index < defaultTimes.size(); index++) {

            Timeslot timeslot = Timeslot.builder()
                    .localTime(localTimeList.get(index))
                    .isAvailable(true)
                    .maxSlotCapacity(defaultMaxCapacity)
                    .created(Timestamp.valueOf(LocalDateTime.now()))
                    .changed(Timestamp.valueOf(LocalDateTime.now()))
                    .isDeleted(false)
                    .calendarDay(calendarDay)
                    .build();

            entityManager.persist(timeslot);

            if ((index + 1) % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
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

    private Optional<Timeslot> checkIfTimeslotOptionalPresent(LocalTime localTime, Optional<Timeslot> timeslot) {
        if (timeslot.isPresent()) {
            return timeslot;
        } else {
            throw new EntityNotFoundException(messageGenerator.createNoEntityFoundByLocalTimeMessage(Timeslot.class, localTime));
        }
    }

    private List<Timeslot> checkIfTimeslotListIsNotEmpty(List<Timeslot> allTimeslots) {

        if (!allTimeslots.isEmpty()) {
            return allTimeslots;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(Timeslot.class));
        }
    }

    private Page<Timeslot> checkIfPageTimeslotIsNotEmpty(Page<Timeslot> timeslotPage) {

        if (!timeslotPage.isEmpty()) {
            return timeslotPage;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Page.class, timeslotPage.toString()));
        }
    }

    public Boolean checkTimeslotCapacity(int incrementPartySize, LocalDate localDate, LocalTime localTime, Restaurant restaurant) {

        Optional<Timeslot> timeslot = timeslotRepository
                .findTimeslotByLocalTimeAndCalendarDay_LocalDateAndCalendarDay_Restaurant(
                        localTime, localDate, restaurant);

        checkIfTimeslotOptionalPresent(localTime, timeslot);

        Integer currentSlotCapacity = timeslot.get().getCurrentSlotCapacity();
        currentSlotCapacity += incrementPartySize;

        if (currentSlotCapacity <= timeslot.get().getMaxSlotCapacity()) {
//            timeslotRepository.updateCurrentCapacity(currentSlotCapacity, timeslot.get());
            return true;

        } else {
            throw new EntityNotAddedException(messageGenerator
                    .createEntityNotAvailableByTimeMessage(Restaurant.class, localDate, localTime));
        }
    }

    public Timeslot updateTimeslotCapacity(int incrementPartySize, LocalDate localDate, LocalTime localTime, Restaurant restaurant) {

        Optional<Timeslot> timeslot = timeslotRepository
                .findTimeslotByLocalTimeAndCalendarDay_LocalDateAndCalendarDay_Restaurant(
                        localTime, localDate, restaurant);

        checkIfTimeslotOptionalPresent(localTime, timeslot);
        Long timeslotId = timeslot.get().getTimeslotId();

        Integer currentSlotCapacity = timeslot.get().getCurrentSlotCapacity();
        currentSlotCapacity += incrementPartySize;

        timeslotRepository.updateCurrentCapacity(currentSlotCapacity, timeslot.get());

        return timeslotRepository.findById(timeslotId).get();

//        if (currentSlotCapacity <= timeslot.get().getMaxSlotCapacity()) {
//            timeslotRepository.updateCurrentCapacity(currentSlotCapacity, timeslot.get());
//            return true;

//        } else {
//            throw new EntityNotAddedException(messageGenerator
//                    .createEntityNotAvailableByTimeMessage(Restaurant.class, localDate, localTime));
//        }
    }



//    public Boolean checkTimeslotCapacity(int newReservationPartySize, LocalDate localDate, LocalTime localTime, Restaurant restaurant) {
//
//        Optional<Timeslot> timeslot = timeslotRepository
//                .findTimeslotByLocalTimeAndCalendarDay_LocalDateAndCalendarDay_Restaurant(
//                        localTime, localDate, restaurant);
//
//        checkIfTimeslotPresent(localTime, timeslot);
//
//        Integer currentSlotCapacity = timeslot.get().getCurrentSlotCapacity();
//
//        currentSlotCapacity += newReservationPartySize;
//
//        if (currentSlotCapacity <= timeslot.get().getMaxSlotCapacity()) {
//            timeslotRepository.updateCurrentCapacity(currentSlotCapacity, timeslot.get());
//            return true;
//
//        } else {
//            throw new EntityNotAddedException(messageGenerator
//                    .createEntityNotAvailableByTimeMessage(Restaurant.class, localDate, localTime));
//        }
//    }
}
