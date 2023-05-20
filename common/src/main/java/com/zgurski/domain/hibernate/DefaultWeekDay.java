package com.zgurski.domain.hibernate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = {
        "restaurant", "defaultTimes"
})
@ToString(exclude = {
        "restaurant", "defaultTimes"
})
@Table(name = "default_week_days")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@NamedQuery(name = "m_restaurant_multiple_ids_search", query = "select r from Restaurant where r.id = :restaurantIds)
//@Cacheable
public class DefaultWeekDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "default_week_day_id")
    private Long defaultWeekDayId;

    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "is_open")
    private Boolean isOpen;

    @Column
    @JsonIgnore
    private Timestamp created;

    @Column
    @JsonIgnore
    private Timestamp changed;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    private Restaurant restaurant;

    @ManyToMany(mappedBy = "defaultWeekDays", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("defaultWeekDays")
    private Set<DefaultTime> defaultTimes = Collections.emptySet();
}