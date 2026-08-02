CREATE OR REPLACE FUNCTION occupancy_table_by_hour(
    p_restaurant_id BIGINT,
    p_year INTEGER,
    p_month INTEGER,
    p_day INTEGER
)
RETURNS TABLE
(
    reservation_time TIME WITHOUT TIME ZONE,
    occupancy BIGINT
)
LANGUAGE sql
STABLE
AS
$$
    SELECT r.local_time AS reservation_time,
           SUM(r.party_size)::BIGINT AS occupancy
    FROM reservations r
    WHERE r.restaurant_id = p_restaurant_id
      AND r.local_date = make_date(p_year, p_month, p_day)
      AND r.status = 'CONFIRMED'
      AND r.is_deleted = FALSE
    GROUP BY r.local_time
    ORDER BY r.local_time;
$$;
