package ashitakalax.com.popularmovies;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ashitakalax.com.popularmovies.movie.MovieContract;

/**
 * this tests fetching the data from the servers
 * @author Levi.Balling
 * @version 1
 * @since 12/16/2015
 */
public class TestFetchMovies extends AndroidTestCase{

    private final String LOG_TAG = "TEST_FETCH_MOVIE";


    public void testMovieUriStringBuilder() throws Exception
    {
        //setup querying the movie database

        FetchMovieTask testTask = new FetchMovieTask(getContext());

        testTask.execute(FetchMovieTask.SORT_BY_POPULARITY);
        testTask.get(10000, TimeUnit.MILLISECONDS);

        assertFalse(testTask.movieItemList.isEmpty());
    }

    public void testMovieDatabaseMovieAdd() throws Exception
    {

        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getContext().getPackageManager();
        //  get a list of installed apps.
        packages = pm.getInstalledApplications(0);
        ActivityManager mActivityManager = (ActivityManager) getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ApplicationInfo packageInfo : packages) {
            //packageInfo.packageName contains package name
            Log.d(LOG_TAG, "packageInfo: " + packageInfo.packageName);
        }

        FetchMovieTask testTask = new FetchMovieTask(getContext());

        testTask.execute(FetchMovieTask.SORT_BY_POPULARITY);
        testTask.get(100000, TimeUnit.MILLISECONDS);
        //let's see if we can add one first
//        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.)
        Cursor movieCursor =  getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null,null);//, new String[]{""},"1", new String[]{""}, null);

        Log.d(LOG_TAG, "testMovieDatabaseMovieAdd: count(" + movieCursor.getCount() + ")");

        //validate test
        if (movieCursor.moveToFirst()) {
                assertEquals("Error: the queried value of locationId does not match the returned value" +
                        "from addLocation", movieCursor.getInt(0), 5);
                assertEquals("Error: the queried value of location setting is incorrect",
                        movieCursor.getInt(1), 6);
                assertEquals("Error: the queried value of location city is incorrect",
                        movieCursor.getInt(2), 0);
                assertEquals("Error: the queried value of latitude is incorrect",
                        movieCursor.getString(3), "hello");
                assertEquals("Error: the queried value of longitude is incorrect",
                        movieCursor.getString(4), "World");
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }


    }



}
