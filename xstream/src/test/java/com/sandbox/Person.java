package com.sandbox;

public class Person {

    private final String firstname;
    private final String lastname;

    public Person() {
        firstname = null;
        lastname = null;
    }

    public Person(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
