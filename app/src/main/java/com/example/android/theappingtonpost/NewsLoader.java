package com.example.android.theappingtonpost;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    public NewsLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        // TODO actual loader!
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<News> news = new ArrayList<>();
        News fakeNew = new News("Da news");
        fakeNew.setDate("2018-01-28T08:00:13Z", getContext().getString(R.string.guardianDateFormat));
        News fakeNew2 = new News("Da news in second");
        fakeNew2.setDate("2018-03-28T08:00:13Z", getContext().getString(R.string.guardianDateFormat));
        fakeNew2.setRating(3);
        news.add(fakeNew);
        news.add(fakeNew2);
        return news;
    }
}
