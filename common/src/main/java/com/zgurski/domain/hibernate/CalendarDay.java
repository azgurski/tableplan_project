package com.zgurski.domain.hibernate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = {
        "restaurant", "timeslots"
})
@ToString(exclude = {
        "restaurant", "timeslots"
})
@Table(name = "calendar_days")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@NamedQuery(name = "m_restaurant_multiple_ids_search", query = "select r from Restaurant where r.id = :restaurantIds)
//@Cacheable
public class CalendarDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_day_id")
    private Long calendarDayId;

    @Column(name = "local_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;

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

    @OneToMany(mappedBy = "calendarDay", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<Timeslot> timeslots = Collections.emptySet();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}