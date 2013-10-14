/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.model;

import java.io.Serializable;

/**
 * 
 * @author chenkh
 * @version $Id: Movie.java, v 0.1 2013-9-6 下午3:42:42 chenkh Exp $
 */
public class Movie implements Serializable{

    /** serialVersionUID */
    private static final long serialVersionUID = 1376363341025295709L;

    /** id **/
    private String id;

    /** 电影标题 **/
    private String title;

    /** 电影类别 **/
    private String genres;

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
     * Getter method for property <tt>title</tt>.
     * 
     * @return property value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter method for property <tt>title</tt>.
     * 
     * @param title value to be assigned to property title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter method for property <tt>genres</tt>.
     * 
     * @return property value of genres
     */
    public String getGenres() {
        return genres;
    }

    /**
     * Setter method for property <tt>genres</tt>.
     * 
     * @param genres value to be assigned to property genres
     */
    public void setGenres(String genres) {
        this.genres = genres;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[Movie]  id: " + id + "  title:" + title + "  genres: " + genres;
    }

}
