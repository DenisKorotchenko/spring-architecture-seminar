package ru.dksu.entity;

import javax.persistence.*;

@Entity
@Table(name = "user2")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    public int age;

    public UserEntity(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public UserEntity(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public UserEntity() {}
}
