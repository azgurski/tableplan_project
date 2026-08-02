CREATE UNIQUE INDEX reservations_pnr_uk
    ON reservations (pnr);

CREATE INDEX reservations_restaurant_date_time_idx
    ON reservations (restaurant_id, local_date, local_time)
    WHERE is_deleted = FALSE;

CREATE INDEX reservations_restaurant_status_date_idx
    ON reservations (restaurant_id, status, local_date, local_time)
    WHERE is_deleted = FALSE;

CREATE INDEX reservations_guest_email_idx
    ON reservations (guest_email);

CREATE INDEX calendar_days_restaurant_open_date_idx
    ON calendar_days (restaurant_id, is_open, local_date)
    WHERE is_deleted = FALSE;

CREATE INDEX timeslots_calendar_day_time_idx
    ON timeslots (calendar_day_id, local_time)
    WHERE is_deleted = FALSE;

CREATE INDEX timeslots_available_time_idx
    ON timeslots (is_available, local_time)
    WHERE is_deleted = FALSE;

CREATE INDEX default_week_days_restaurant_day_idx
    ON default_week_days (restaurant_id, day_of_week)
    WHERE is_deleted = FALSE;
