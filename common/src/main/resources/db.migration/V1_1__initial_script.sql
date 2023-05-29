create table if not exists restaurants
(
    restaurant_id             bigserial
        primary key,
    restaurant_name           varchar(100)          not null,
    contact_email             varchar(200)          not null,
    phone                     varchar(20),
    address                   varchar(200),
    postal_code               varchar(10),
    city                      varchar(50),
    country                   varchar(30),
    website                   varchar(100),
    restaurant_language       varchar(5),
    restaurant_timezone       varchar(10),
    image_url                 varchar(500),
    logo_url                  varchar(500),
    created                   timestamp(6)          not null,
    changed                   timestamp(6)          not null,
    is_deleted                boolean default false not null,
    auth_email                varchar(100),
    auth_password             varchar(100),
    default_timeslot_capacity integer               not null
);

alter table restaurants
    owner to dev;

create index if not exists restaurants_city_index
    on restaurants (city);

create index if not exists restaurants_country_index
    on restaurants (country);

create index if not exists restaurants_restaurant_name_index
    on restaurants (restaurant_name);

create index if not exists restaurants_restaurant_id_auth_email_auth_password_restaurant_i
    on restaurants (restaurant_id, auth_email, auth_password);

create index if not exists restaurants_restaurant_language_index
    on restaurants (restaurant_language);

create index if not exists restaurants_restaurant_timezone_index
    on restaurants (restaurant_timezone);

create index if not exists restaurants_website_index
    on restaurants (website);

create index if not exists restaurants_image_url_index
    on restaurants (image_url);

create index if not exists restaurants_logo_url_index
    on restaurants (logo_url);

create index if not exists restaurants_email_index
    on restaurants (contact_email);

create index if not exists restaurants_phone_index
    on restaurants (phone);

create index if not exists restaurants_default_timeslot_capacity_index
    on restaurants (default_timeslot_capacity);

create table if not exists reservations
(
    reservation_id  bigserial
        primary key,
    pnr             varchar(6)                                             not null,
    restaurant_id   bigint                                                 not null
        constraint reservations_restaurants_restaurant_id_fk
            references restaurants,
    local_date      date                                                   not null,
    local_time      time                                                   not null,
    party_size      integer                                                not null,
    guest_full_name varchar(200)                                           not null,
    guest_email     varchar(200)                                           not null,
    guest_phone     varchar(20)                                            not null,
    guest_note      varchar(300),
    guest_language  varchar(5)  default 'en'::character varying,
    status          varchar(15) default 'NOT_CONFIRMED'::character varying not null,
    created         timestamp(6)                                           not null,
    changed         timestamp(6)                                           not null,
    is_deleted      boolean     default false                              not null
);

comment on column reservations.pnr is 'personal_number_of_reservation';

comment on column reservations.local_date is 'dd-MM-yyyy';

comment on column reservations.local_time is 'hh-mm';

comment on column reservations.guest_note is 'guest''s special wishes';

alter table reservations
    owner to dev;

create index if not exists reservations_local_date_index
    on reservations (local_date);

create index if not exists reservations_local_date_local_time_index
    on reservations (local_date, local_time);

create index if not exists reservations_pnr_index
    on reservations (pnr);

create index if not exists reservations_guest_email_index
    on reservations (guest_email);

create index if not exists reservations_guest_full_name_index
    on reservations (guest_full_name);

create index if not exists reservations_guest_phone_index
    on reservations (guest_phone);

create index if not exists reservations_created_index
    on reservations (created);

create index if not exists reservations_status_index
    on reservations (status);

create index if not exists reservations_local_date_party_size_index
    on reservations (local_date, party_size);

create index if not exists reservations_party_size_index
    on reservations (party_size);

create index if not exists reservations_pnr_local_date_local_time_party_size_guest_full_na
    on reservations (pnr, local_date, local_time, party_size, guest_full_name);

create table if not exists calendar_days
(
    calendar_day_id bigint  default nextval('slots_slot_id_seq'::regclass) not null
        constraint slots_pkey
            primary key,
    local_date      date                                                   not null,
    restaurant_id   bigint                                                 not null
        constraint calendar_days_restaurants_restaurant_id_fk
            references restaurants,
    is_open         boolean default true                                   not null,
    created         timestamp(6)                                           not null,
    changed         timestamp(6)                                           not null,
    is_deleted      boolean default false                                  not null
);

alter table calendar_days
    owner to dev;

create table if not exists timeslots
(
    timeslot_id           bigint  default nextval('c_begin_times_begin_time_id_seq'::regclass) not null
        primary key,
    calendar_day_id       bigint                                                               not null
        constraint timeslots_calendar_days_calendar_day_id_fk
            references calendar_days,
    local_time            time                                                                 not null,
    is_available          boolean default true                                                 not null,
    current_slot_capacity integer,
    max_slot_capacity     integer                                                              not null,
    created               timestamp(6)                                                         not null,
    changed               timestamp(6)                                                         not null,
    is_deleted            boolean default false                                                not null
);

alter table timeslots
    owner to dev;

create index if not exists timeslots_calendar_day_id_index
    on timeslots (calendar_day_id);

create index if not exists timeslots_created_index
    on timeslots (created);

create index if not exists timeslots_is_available_index
    on timeslots (is_available);

create index if not exists timeslots_local_time_index
    on timeslots (local_time);

create index if not exists timeslots_local_time_is_available_index
    on timeslots (local_time, is_available);

create index if not exists timeslots_current_slot_capacity_index
    on timeslots (current_slot_capacity);

create index if not exists timeslots_max_slot_capacity_index
    on timeslots (max_slot_capacity);

create index if not exists calendar_days_calendar_day_id_index
    on calendar_days (calendar_day_id);

create index if not exists calendar_days_is_open_index
    on calendar_days (is_open);

create index if not exists calendar_days_local_date_index
    on calendar_days (local_date);

create index if not exists calendar_days_local_date_restaurant_id_is_open_index
    on calendar_days (local_date, restaurant_id, is_open);

create index if not exists calendar_days_restaurant_id_index
    on calendar_days (restaurant_id);

create table if not exists default_week_days
(
    default_week_day_id bigint  default nextval('default_open_timeslots_default_timeslot_id_seq'::regclass) not null
        primary key,
    restaurant_id       bigint                                                                              not null
        constraint default_week_days_restaurants_restaurant_id_fk
            references restaurants,
    day_of_week         integer                                                                             not null,
    is_open             boolean default true                                                                not null,
    created             timestamp(6)                                                                        not null,
    changed             timestamp(6)                                                                        not null,
    is_deleted          boolean default false                                                               not null
);

alter table default_week_days
    owner to dev;

create index if not exists default_week_days_is_available_index
    on default_week_days (is_open);

create index if not exists default_week_days_day_of_week_index
    on default_week_days (day_of_week);

create index if not exists default_week_days_restaurant_id_day_of_week_is_available_index
    on default_week_days (restaurant_id, day_of_week, is_open);

create table if not exists default_times
(
    default_time_id bigint default nextval('c_begin_times_begin_time_id_seq1'::regclass) not null
        constraint default_times_pkey1
            primary key,
    local_time      time                                                                 not null
);

alter table default_times
    owner to dev;

create index if not exists default_times_local_time_index
    on default_times (local_time);

create table if not exists l_default_week_days_times
(
    id                  bigint default nextval('l_default_schedules_c_begin_times_id_seq'::regclass) not null
        primary key,
    default_week_day_id bigint                                                                       not null
        constraint l_default_week_days_times_default_week_days_default_week_day_id
            references default_week_days,
    default_time_id     bigint                                                                       not null
        constraint l_default_week_days_times_default_times_default_time_id_fk
            references default_times
);

alter table l_default_week_days_times
    owner to dev;

create index if not exists l_default_week_days_times_default_week_day_id_default_time_id_i
    on l_default_week_days_times (default_week_day_id, default_time_id);