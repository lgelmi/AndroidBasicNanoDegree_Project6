package com.example.android.theappingtonpost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;


public class NewsActivity extends AppCompatActivity{

    public static final String LOG_TAG = NewsActivity.class.getName();

    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);
        // Create a new {@link ArrayAdapter} of news
        adapter = new NewsAdapter(this, new ArrayList<News>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(adapter);

        News fakeNew = new News("Da news");
        fakeNew.setDate("2018-01-28T08:00:13Z", getString(R.string.guardianDateFormat));
        News fakeNew2 = new News("Da news in second");
        fakeNew2.setDate("2018-03-28T08:00:13Z", getString(R.string.guardianDateFormat));
        fakeNew2.setRating(3);
        adapter.add(fakeNew);
        adapter.add(fakeNew2);

    }
}
// TODO remove this
//public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Earthquake>> {
//
//    public static final String LOG_TAG = EarthquakeActivity.class.getName();
//
//    private static final String USGS_REQUEST_URL =
//            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag" +
//                    "=5&limit=10";
//
//    /**
//     * Constant value for the earthquake loader ID. We can choose any integer.
//     * This really only comes into play if you're using multiple loaders.
//     */
//    private static final int EARTHQUAKE_LOADER_ID = 1;
//
//    private EarthquakeAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.earthquake_activity);
//        // Start the AsyncTask to fetch the earthquake data
//        // Get a reference to the LoaderManager, in order to interact with loaders.
//        LoaderManager loaderManager = getLoaderManager();
//
//        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
//        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
//        // because this activity implements the LoaderCallbacks interface).
//        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
//
//        // Find a reference to the {@link ListView} in the layout
//        ListView earthquakeListView = (ListView) findViewById(R.id.list);
//
//        // Create a new {@link ArrayAdapter} of earthquakes
//        adapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
//
//        // Set the adapter on the {@link ListView}
//        // so the list can be populated in the user interface
//        earthquakeListView.setAdapter(adapter);
//
//        // Set an item click listener on the ListView, which sends an intent to a web browser
//        // to open a website with more information about the selected earthquake.
//        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                // Find the current earthquake that was clicked on
//                Earthquake currentEarthquake = adapter.getItem(position);
//
//                // Convert the String URL into a URI object (to pass into the Intent constructor)
//                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
//
//                // Create a new intent to view the earthquake URI
//                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
//
//                // Send the intent to launch a new activity
//                startActivity(websiteIntent);
//            }
//        });
//    }
//
//    @Override
//    public Loader<ArrayList<Earthquake>> onCreateLoader(int i, Bundle bundle) {
//        // Create a new loader for the given URL
//        return new EarthquakeLoader(this, USGS_REQUEST_URL);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> earthquakes) {
//        // Clear the adapter of previous earthquake data
//        adapter.clear();
//
//        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
//        // data set. This will trigger the ListView to update.
//        if (earthquakes != null && !earthquakes.isEmpty()) {
//            adapter.addAll(earthquakes);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
//        adapter.clear();
//
//    }
//}
