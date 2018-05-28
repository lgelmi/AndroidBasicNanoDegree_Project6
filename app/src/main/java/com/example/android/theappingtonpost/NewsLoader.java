package com.example.android.theappingtonpost;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /** Tag for log messages */
    private static final String TAG = NewsLoader.class.getName();

    private final String requestUrl;


    NewsLoader(Context context, String url) {
        super(context);
        requestUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (requestUrl == null)
            return null;
        return fetchNews();
    }

    private List<News> fetchNews(){
        // Store the response in a News List and return it
        return extractFeatureFromJson(QueryUtils.fetchUrl(requestUrl));
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<News> news = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON).getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of features.
            JSONArray newsArray = baseJsonResponse.getJSONArray("results");

            // For each news, create a News object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list
                JSONObject currentNews = newsArray.getJSONObject(i);

                // For a given news, extract the JSONObject associated with the
                // key called "fields", collecting several news data.
                JSONObject fields = currentNews.getJSONObject("fields");

                // Extract the value for the key called "headline" and initialize the News
                News newNews = new News(fields.getString("headline"));
                newNews.setSection(currentNews.getString("sectionName"));
                newNews.setTrailText(fields.getString("trailText"));
                if (fields.has("thumbnail"))
                    newNews.setThumbnail(fields.getString("thumbnail"));
                newNews.setDate(currentNews.getString("webPublicationDate"), getContext().getResources().getString(R.string.guardianDateFormat));

                List<String> authors = new ArrayList<>();
                JSONArray tags = currentNews.getJSONArray("tags");
                for (int j = 0; j < tags.length(); j++){
                    JSONObject tag = tags.getJSONObject(j);
                    if (tag.getString("type").equals("contributor"))
                        authors.add(tag.getString("webTitle"));
                }
                newNews.setAuthor(TextUtils.join(", ", authors));
                newNews.setUrl(currentNews.getString("webUrl"));
                news.add(newNews);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(TAG, "Problem parsing the news JSON results", e);
        }
        // Return the list of earthquakes
        return news;
    }
}
