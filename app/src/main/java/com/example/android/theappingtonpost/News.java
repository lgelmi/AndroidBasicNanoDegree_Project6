package com.example.android.theappingtonpost;

import android.annotation.SuppressLint;
import android.util.Log;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class to collect any useful data related to a news.
 */
public class News {
    /**
     * The tag used in case of log messages.
     */
    private String TAG = News.class.getName();
    /**
     * The article author.
     */
    private String author = "";
    /**
     * The article headline/title.
     */
    private String headline = "";
    /**
     * The article section name.
     */
    private String section = "";
    /**
     * The article trail text.
     */
    private String trailText = "";
    /**
     * The article Web Publication date.
     */
    private Date date;
    /**
     * An Url to a useful image to be displayed with the news.
     */
    private String thumbnail;
    /**
     * The url to the news page.
     */
    private String url;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTrailText() {
        return trailText;
    }

    public void setTrailText(String trailText) {
        this.trailText = trailText;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String date, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            this.date = formatter.parse(date);
        } catch (ParseException e) {
            Log.w(TAG, "setDate: ", e);
            e.printStackTrace();
            this.date = null;
        }
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail){
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    News(String headline) {
        setHeadline(headline);
    }
}
