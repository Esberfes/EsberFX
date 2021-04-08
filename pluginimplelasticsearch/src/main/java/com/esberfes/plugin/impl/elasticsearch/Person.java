package com.esberfes.plugin.impl.elasticsearch;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String name;
    private String firstName;
    private String lastName;
    private String streetAddress;
    private List<Person> child;

    public Person(String name, String firstName, String lastName, String streetAddress) {
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.streetAddress = streetAddress;
        this.child = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public List<Person> getChild() {
        return child;
    }

    public void setChild(List<Person> child) {
        this.child = child;
    }

    public static Person fake(int depth) {
        Faker faker = new Faker();
        String name = faker.name().fullName(); // Miss Samanta Schmidt
        String firstName = faker.name().firstName(); // Emory
        String lastName = faker.name().lastName(); // Barton
        String streetAddress = faker.address().streetAddress(); // 60018 Sawayn Brooks Suite 449

        Person person = new Person(name, firstName, lastName, streetAddress);

        int max = faker.number().numberBetween(0, 6);
        if(depth < 3) {
            for(int i = 0; i < max; i ++) {
                Person child = fake(depth + 1);
                person.getChild().add(child);
            }
        }

        return person;
    }


}
