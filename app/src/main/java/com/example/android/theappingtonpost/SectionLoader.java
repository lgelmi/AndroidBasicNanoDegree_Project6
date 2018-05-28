package com.example.android.theappingtonpost;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SectionLoader extends AsyncTaskLoader<ArrayList<Map.Entry<String, String>>> {

    /**
     * Tag for log messages
     */
    private static final String TAG = SectionLoader.class.getName();

    private String requestUrl;


    SectionLoader(Context context, String url) {
        super(context);
        requestUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Map.Entry<String, String>> loadInBackground() {
        if (requestUrl == null)
            return null;
        return fetchSections();
    }

    private ArrayList<Map.Entry<String, String>> fetchSections() {
        // Store the response in a News List and return it
        return extractFeatureFromJson(QueryUtils.fetchUrl(requestUrl));
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private ArrayList<Map.Entry<String, String>> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Map.Entry<String, String>> news = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON).getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of features.
            JSONArray newsArray = baseJsonResponse.getJSONArray("results");

            // Add each section
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list
                JSONObject currentSection = newsArray.getJSONObject(i);
                String id = currentSection.getString("id");
                String webTitle = currentSection.getString("webTitle");
                news.add(new AbstractMap.SimpleEntry<>(id, webTitle));
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
