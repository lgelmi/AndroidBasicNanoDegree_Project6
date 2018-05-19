package com.example.android.theappingtonpost;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<List<News>> {

    public static final String TAG = NewsActivity.class.getName();

    private NewsAdapter adapter;

    /**
     * Constant value for the news loader ID.
     */
    private static final int NEWS_LOADER_ID = 404;

    // View PARTS

    ListView newsListView;
    ProgressBar loadingView;
    TextView fallbackView;
    SwipeRefreshLayout swipeView;

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
                Log.i(TAG, "onRefresh: ");
            }
        });

}

    private void fetchData() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context
                .CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        fallbackView.setVisibility(View.VISIBLE);
        swipeView.setRefreshing(false);
        if (networkInfo != null && networkInfo.isConnected()) {
            // If there is a network connection, fetch data
            loadingView.setVisibility(View.VISIBLE);
            fallbackView.setText(getString(R.string.loadingText));
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            loadingView.setVisibility(View.GONE);
            fallbackView.setText(R.string.noConnection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this);
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
