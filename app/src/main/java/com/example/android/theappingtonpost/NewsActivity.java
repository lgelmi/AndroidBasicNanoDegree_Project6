package com.example.android.theappingtonpost;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends AppCompatActivity implements SharedPreferences
        .OnSharedPreferenceChangeListener {

    @SuppressWarnings("unused")
    public static final String TAG = NewsActivity.class.getName();

    private static final String KEY = "d612ada5-8086-4a49-b724-cb851b978c2b";

    private NewsAdapter adapter;

    /**
     * Constant value for the news loader ID.
     */
    private static final int NEWS_LOADER_ID = 404;

    // View PARTS
    private ListView newsListView;
    private ProgressBar loadingView;
    private TextView fallbackView;
    private SwipeRefreshLayout swipeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        // Find a reference to the views in the layout
        newsListView = findViewById(R.id.list);
        loadingView = findViewById(R.id.loading);
        fallbackView = findViewById(R.id.fallbackText);
        swipeView = findViewById(R.id.swipe_container);
        // Create a new {@link ArrayAdapter} of news
        adapter = new NewsAdapter(this, new ArrayList<News>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(adapter);
        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

        fetchData();

        newsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            /* Taken from http://nlopez.io/swiperefreshlayout-with-listview-done-right/ to
             * improve the refresh behaviour.
             */
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int
                    visibleItemCount, int alItemCount) {
                int topRowVerticalPosition =
                        (newsListView == null || newsListView.getChildCount() == 0) ?
                                0 : newsListView.getChildAt(0).getTop();
                swipeView.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                News currentNews = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                if (currentNews != null) {
                    Uri newsUri = Uri.parse(currentNews.getUrl());

                    // Create a new intent to view the earthquake URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            }
        });
    }

    private void fetchData() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        fallbackView.setVisibility(View.VISIBLE);
        swipeView.setRefreshing(false);
        if (networkInfo != null && networkInfo.isConnected()) {
            // If there is a network connection, fetch data
            loadingView.setVisibility(View.VISIBLE);
            fallbackView.setText(getString(R.string.loadingText));
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.restartLoader(NEWS_LOADER_ID, null, new NewsCallback(this));
        } else {
            // Otherwise, display error
            loadingView.setVisibility(View.GONE);
            fallbackView.setText(R.string.noConnection);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        fetchData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class NewsCallback implements LoaderManager.LoaderCallbacks<List<News>> {

        final Context mContext;

        NewsCallback(Context context) {
            super();
            mContext = context;
        }

        @Override
        public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            // URL PARTS
            String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                    getString(R.string.settings_order_by_default));
            String pageSize = sharedPrefs.getString(getString(R.string.settings_page_size_key),
                    getString(R.string.settings_page_size_default));
            String section = sharedPrefs.getString(getString(R.string.settings_section_key),
                    getString(R.string.settings_section_none_value));


            // URL for news data from the guardian api
            String GUARDIAN_REQUEST_URL = "https://content.guardianapis" +
                    ".com/search?show-tags=contributor&show-fields=headline," +
                    "thumbnail,trailText";

            Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("api-key", KEY);
            uriBuilder.appendQueryParameter(getString(R.string.settings_page_size_key), pageSize);
            uriBuilder.appendQueryParameter(getString(R.string.settings_order_by_key), orderBy);
            if (!section.equals(getString(R.string.settings_section_none_value))) {
                uriBuilder.appendQueryParameter(getString(R.string.settings_section_key), section);
            }
            // Create a new loader for the given URL
            return new NewsLoader(mContext, uriBuilder.toString());
        }

        @Override
        public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
            adapter.clear();
            // Sets the visibility according to the new state
            loadingView.setVisibility(View.GONE);
            if (news == null || news.size() == 0) {
                // No news found!
                fallbackView.setVisibility(View.VISIBLE);
                fallbackView.setText(getString(R.string.emptyText));
            } else {
                // News found!
                newsListView.setVisibility(View.VISIBLE);
                fallbackView.setVisibility(View.GONE);
                adapter.addAll(news);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<News>> loader) {
            // Loader reset, so we can clear out our existing data.
            adapter.clear();
        }

    }

}
