package org.example.lb.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_connection", schema = "lb")
public class ServiceConnection {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "address", nullable = false, unique = true)
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(name="name",nullable = false)
    private Type type;
}
