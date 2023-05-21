package com.zgurski.domain.hibernate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zgurski.domain.enums.SystemRoles;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@EqualsAndHashCode(exclude = {
        "restaurant"
})
@ToString(exclude = {
        "restaurant"
})
@Table(name = "roles")
//@Cacheable("roles")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@NamedQuery(name = "m_restaurant_multiple_ids_search", query = "select r from Restaurant where r.id = :restaurantIds)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private SystemRoles systemRole = SystemRoles.ROLE_USER;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp created;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp changed;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    private Restaurant restaurant;
}