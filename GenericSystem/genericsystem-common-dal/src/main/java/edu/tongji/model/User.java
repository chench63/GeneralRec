/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.model;

import java.io.Serializable;

/**
 * 
 * @author chench
 * @version $Id: User.java, v 0.1 2013-9-6 下午3:41:20 chench Exp $
 */
public class User implements Serializable{

    /**  serialVersionUID */
    private static final long serialVersionUID = -7370087848218013886L;

    /** 用户id **/
    private String id;

    /** 性别    **/
    private String gender;

    /** 年龄 **/
    private String age;

    /** 职业 **/
    private String occupation;

    /** 邮编地址 **/
    private String zipCode;

    /**
     * Getter method for property <tt>id</tt>.
     * 
     * @return property value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter method for property <tt>id</tt>.
     * 
     * @param id value to be assigned to property id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter method for property <tt>gender</tt>.
     * 
     * @return property value of gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Setter method for property <tt>gender</tt>.
     * 
     * @param gender value to be assigned to property gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Getter method for property <tt>age</tt>.
     * 
     * @return property value of age
     */
    public String getAge() {
        return age;
    }

    /**
     * Setter method for property <tt>age</tt>.
     * 
     * @param age value to be assigned to property age
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * Getter method for property <tt>occupation</tt>.
     * 
     * @return property value of occupation
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * Setter method for property <tt>occupation</tt>.
     * 
     * @param occupation value to be assigned to property occupation
     */
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    /**
     * Getter method for property <tt>zipCode</tt>.
     * 
     * @return property value of zipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Setter method for property <tt>zipCode</tt>.
     * 
     * @param zipCode value to be assigned to property zipCode
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[User] id: " + id + "  gender: " + gender + "  age: " + age + "  occupation: "
               + occupation + "  zipCode" + zipCode;
    }

}
