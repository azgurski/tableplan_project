create or replace function occupancy_table_by_hour(id bigint, year integer, month integer, day integer)
    returns TABLE(reservation_time time without time zone, occupancy integer)
    language plpgsql
as
$$DECLARE
    new_record record;

BEGIN

    FOR new_record IN (

        SELECT local_time as group_time,
               SUM(party_size) as group_occupancy

        FROM reservations

        WHERE reservations.restaurant_id = id
          AND reservations.local_date = make_date(year, month, day)
          AND reservations.status = 'CONFIRMED'

        GROUP BY local_time
        ORDER BY local_time

    )
        LOOP reservation_time := new_record.group_time ;
    occupancy := new_record.group_occupancy;

    RETURN  NEXT;

        END LOOP;

END;

$$;

alter function occupancy_table_by_hour(bigint, integer, integer, integer) owner to dev;