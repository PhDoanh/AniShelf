package com.library.anishelf.model;

import com.library.anishelf.model.enums.Gender;

/**
 * The type Person.
 */
public class Person {
    private int id;
    private String firstName;
    private String lastName;
    private String imagePath;
    private Gender gender;
    private String birthdate;
    private String email;
    private String phone;

    /**
     * The constant DEFAULT_IMAGE_PATH.
     */
    public static final String DEFAULT_IMAGE_PATH = "/image/default/avatar.png";

    /**
     * Instantiates a new Person.
     */
    public Person() {

    }

    /**
     * Instantiates a new Person.
     *
     * @param firstName   the first name
     * @param lastName    the last name
     * @param imagePath   the image path
     * @param gender      the gender
     * @param dateOfBirth the date of birth
     * @param email       the email
     * @param phone       the phone
     */
    public Person(String firstName, String lastName, String imagePath, Gender gender, String dateOfBirth, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.imagePath = imagePath;
        this.gender = gender;
        this.birthdate = dateOfBirth;
        this.email = email;
        this.phone = phone;
    }


    /**
     * Instantiates a new Person.
     *
     * @param id          the id
     * @param firstName   the first name
     * @param lastName    the last name
     * @param imagePath   the image path
     * @param gender      the gender
     * @param dateOfBirth the date of birth
     * @param email       the email
     * @param phone       the phone
     */
    public Person(int id, String firstName, String lastName, String imagePath, Gender gender, String dateOfBirth, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imagePath = imagePath;
        this.gender = gender;
        this.birthdate = dateOfBirth;
        this.email = email;
        this.phone = phone;
    }


    /**
     * Instantiates a new Person.
     *
     * @param firstName   the first name
     * @param lastName    the last name
     * @param gender      the gender
     * @param dateOfBirth the date of birth
     * @param email       the email
     * @param phone       the phone
     */
    public Person(String firstName, String lastName, Gender gender, String dateOfBirth, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.imagePath = DEFAULT_IMAGE_PATH;
        this.gender = gender;
        this.birthdate = dateOfBirth;
        this.email = email;
        this.phone = phone;
    }


    /**
     * Instantiates a new Person.
     *
     * @param id          the id
     * @param firstName   the first name
     * @param lastName    the last name
     * @param gender      the gender
     * @param dateOfBirth the date of birth
     * @param email       the email
     * @param phone       the phone
     */
    public Person(int id, String firstName, String lastName, Gender gender, String dateOfBirth, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imagePath = DEFAULT_IMAGE_PATH;
        this.gender = gender;
        this.birthdate = dateOfBirth;
        this.email = email;
        this.phone = phone;
    }


    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Gets image path.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }


    /**
     * Gets gender.
     *
     * @return the gender
     */
    public Gender getGender() {
        return gender;
    }


    /**
     * Gets birthdate.
     *
     * @return the birthdate
     */
    public String getBirthdate() {
        return birthdate;
    }


    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }


    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Sets image path.
     *
     * @param imagePath the image path
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Sets gender.
     *
     * @param gender the gender
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Sets birthdate.
     *
     * @param birthdate the birthdate
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }
}
