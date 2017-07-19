package edu.phystech.stethoscope.domain;

import org.joda.time.DateTime;

public class Person {
    private long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private int age;
    private DateTime date;
    private String comment;

    public Person(long id, String firstName, String lastName,
                  String middleName, int age, DateTime date, String comment) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.age = age;
        this.date = date;
        this.comment = comment;
    }


    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public int getAge() {
        return age;
    }

    public DateTime getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }
}
