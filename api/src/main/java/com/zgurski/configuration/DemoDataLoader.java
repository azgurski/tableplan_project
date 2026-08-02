package com.zgurski.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.demo-data.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DemoDataLoader implements ApplicationRunner {

    private static final int DEFAULT_CAPACITY = 20;

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Integer restaurantCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM restaurants",
                Integer.class
        );

        if (restaurantCount != null && restaurantCount > 0) {
            log.info("Demo data loading skipped: database already contains restaurants");
            return;
        }

        log.info("Loading development demo data");

        long restaurantId = createRestaurant();
        createDefaultTimes();
        createWeeklySchedule(restaurantId);
        createCalendarAndTimeslots(restaurantId);
        createReservations(restaurantId);

        log.info("Development demo data loaded successfully");
    }

    private long createRestaurant() {
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO restaurants (
                    restaurant_name,
                    contact_email,
                    phone,
                    address,
                    postal_code,
                    city,
                    country,
                    website,
                    restaurant_language,
                    restaurant_timezone,
                    created,
                    changed,
                    is_deleted,
                    default_timeslot_capacity
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, FALSE, ?)
                RETURNING restaurant_id
                """,
                Long.class,
                "Demo Bistro",
                "contact@demo-bistro.example",
                "+33000000000",
                "1 Example Street",
                "67000",
                "Strasbourg",
                "France",
                "https://demo-bistro.example",
                "Fr",
                "Europe/Paris",
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                DEFAULT_CAPACITY
        );
    }

    private void createDefaultTimes() {
        List<LocalTime> times = List.of(
                LocalTime.of(12, 0),
                LocalTime.of(12, 30),
                LocalTime.of(13, 0),
                LocalTime.of(19, 0),
                LocalTime.of(19, 30),
                LocalTime.of(20, 0)
        );

        jdbcTemplate.batchUpdate(
                "INSERT INTO default_times (local_time) VALUES (?) ON CONFLICT (local_time) DO NOTHING",
                times,
                times.size(),
                (statement, localTime) -> statement.setTime(1, Time.valueOf(localTime))
        );
    }

    private void createWeeklySchedule(long restaurantId) {
        LocalDateTime now = LocalDateTime.now();

        for (DayOfWeek day : DayOfWeek.values()) {
            boolean open = day != DayOfWeek.MONDAY && day != DayOfWeek.TUESDAY;

            Long weekDayId = jdbcTemplate.queryForObject(
                    """
                    INSERT INTO default_week_days (
                        restaurant_id,
                        day_of_week,
                        is_open,
                        created,
                        changed,
                        is_deleted
                    )
                    VALUES (?, ?, ?, ?, ?, FALSE)
                    RETURNING default_week_day_id
                    """,
                    Long.class,
                    restaurantId,
                    day.ordinal(),
                    open,
                    Timestamp.valueOf(now),
                    Timestamp.valueOf(now)
            );

            if (open && weekDayId != null) {
                jdbcTemplate.update(
                        """
                        INSERT INTO l_default_week_days_times (default_week_day_id, default_time_id)
                        SELECT ?, default_time_id
                        FROM default_times
                        """,
                        weekDayId
                );
            }
        }
    }

    private void createCalendarAndTimeslots(long restaurantId) {
        LocalDate startDate = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for (int offset = 0; offset < 14; offset++) {
            LocalDate date = startDate.plusDays(offset);
            boolean open = date.getDayOfWeek() != DayOfWeek.MONDAY
                    && date.getDayOfWeek() != DayOfWeek.TUESDAY;

            Long calendarDayId = jdbcTemplate.queryForObject(
                    """
                    INSERT INTO calendar_days (
                        local_date,
                        restaurant_id,
                        is_open,
                        created,
                        changed,
                        is_deleted
                    )
                    VALUES (?, ?, ?, ?, ?, FALSE)
                    RETURNING calendar_day_id
                    """,
                    Long.class,
                    Date.valueOf(date),
                    restaurantId,
                    open,
                    Timestamp.valueOf(now),
                    Timestamp.valueOf(now)
            );

            if (open && calendarDayId != null) {
                createTimeslots(calendarDayId, now);
            }
        }
    }

    private void createTimeslots(long calendarDayId, LocalDateTime now) {
        List<LocalTime> times = List.of(
                LocalTime.of(12, 0),
                LocalTime.of(12, 30),
                LocalTime.of(13, 0),
                LocalTime.of(19, 0),
                LocalTime.of(19, 30),
                LocalTime.of(20, 0)
        );

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO timeslots (
                    calendar_day_id,
                    local_time,
                    is_available,
                    current_slot_capacity,
                    max_slot_capacity,
                    created,
                    changed,
                    is_deleted
                )
                VALUES (?, ?, TRUE, 0, ?, ?, ?, FALSE)
                """,
                times,
                times.size(),
                (statement, localTime) -> {
                    statement.setLong(1, calendarDayId);
                    statement.setTime(2, Time.valueOf(localTime));
                    statement.setInt(3, DEFAULT_CAPACITY);
                    statement.setTimestamp(4, Timestamp.valueOf(now));
                    statement.setTimestamp(5, Timestamp.valueOf(now));
                }
        );
    }

    private void createReservations(long restaurantId) {
        LocalDate reservationDate = findNextOpenDate(LocalDate.now());
        LocalDateTime now = LocalDateTime.now();

        List<DemoReservation> reservations = List.of(
                new DemoReservation(
                        "DEM001",
                        reservationDate,
                        LocalTime.of(12, 0),
                        2,
                        "Alice Martin",
                        "alice@example.com",
                        "+33000000001",
                        "Window table preferred",
                        "en",
                        "CONFIRMED"
                ),
                new DemoReservation(
                        "DEM002",
                        reservationDate,
                        LocalTime.of(12, 30),
                        4,
                        "Thomas Bernard",
                        "thomas@example.com",
                        "+33000000002",
                        "One vegetarian guest",
                        "fr",
                        "UNREAD"
                ),
                new DemoReservation(
                        "DEM003",
                        findNextOpenDate(reservationDate.plusDays(1)),
                        LocalTime.of(19, 30),
                        3,
                        "Emma Fischer",
                        "emma@example.com",
                        "+33000000003",
                        null,
                        "de",
                        "NOT_CONFIRMED"
                )
        );

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO reservations (
                    pnr,
                    restaurant_id,
                    local_date,
                    local_time,
                    party_size,
                    guest_full_name,
                    guest_email,
                    guest_phone,
                    guest_note,
                    guest_language,
                    status,
                    created,
                    changed,
                    is_deleted
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, FALSE)
                """,
                reservations,
                reservations.size(),
                (statement, reservation) -> {
                    statement.setString(1, reservation.pnr());
                    statement.setLong(2, restaurantId);
                    statement.setDate(3, Date.valueOf(reservation.date()));
                    statement.setTime(4, Time.valueOf(reservation.time()));
                    statement.setInt(5, reservation.partySize());
                    statement.setString(6, reservation.guestName());
                    statement.setString(7, reservation.guestEmail());
                    statement.setString(8, reservation.guestPhone());
                    statement.setString(9, reservation.note());
                    statement.setString(10, reservation.language());
                    statement.setString(11, reservation.status());
                    statement.setTimestamp(12, Timestamp.valueOf(now));
                    statement.setTimestamp(13, Timestamp.valueOf(now));
                }
        );

        jdbcTemplate.update(
                """
                UPDATE timeslots t
                SET current_slot_capacity = occupancy.occupied,
                    changed = CURRENT_TIMESTAMP
                FROM (
                    SELECT cd.calendar_day_id,
                           r.local_time,
                           SUM(r.party_size)::INTEGER AS occupied
                    FROM reservations r
                    JOIN calendar_days cd
                      ON cd.restaurant_id = r.restaurant_id
                     AND cd.local_date = r.local_date
                    WHERE r.restaurant_id = ?
                      AND r.status = 'CONFIRMED'
                      AND r.is_deleted = FALSE
                    GROUP BY cd.calendar_day_id, r.local_time
                ) occupancy
                WHERE t.calendar_day_id = occupancy.calendar_day_id
                  AND t.local_time = occupancy.local_time
                """,
                restaurantId
        );
    }

    private LocalDate findNextOpenDate(LocalDate date) {
        LocalDate candidate = date;
        while (candidate.getDayOfWeek() == DayOfWeek.MONDAY
                || candidate.getDayOfWeek() == DayOfWeek.TUESDAY) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    private record DemoReservation(
            String pnr,
            LocalDate date,
            LocalTime time,
            int partySize,
            String guestName,
            String guestEmail,
            String guestPhone,
            String note,
            String language,
            String status
    ) {
    }
}
