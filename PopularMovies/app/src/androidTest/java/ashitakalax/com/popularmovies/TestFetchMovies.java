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
import ashitakalax.com.popularmovies.movie.MovieDbHelper;


/**
 * this tests fetching the data from the servers
 * @author Levi.Balling
 * @version 1
 * @since 12/16/2015
 */
public class TestFetchMovies extends AndroidTestCase{

    private final String LOG_TAG = "TEST_FETCH_MOVIE";


    private void deleteDataBases()
    {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Override
    public void setUp() throws Exception {
        deleteDataBases();
    }


    public void testMovieDatabaseMovieAdd() throws Exception {

        FetchMovieTask testTask = new FetchMovieTask(getContext());

        testTask.execute(FetchMovieTask.SORT_BY_POPULARITY);
        testTask.get(100000, TimeUnit.MILLISECONDS);

        //let's see if we can add one first
//        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.)
        Cursor movieCursor = null;
        try {
            movieCursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);//, new String[]{""},"1", new String[]{""}, null);

            Log.d(LOG_TAG, "testMovieDatabaseMovieAdd: count(" + movieCursor.getCount() + ")");

            //validate test
            if (movieCursor.moveToFirst()) {

                for (int i = 0; i < movieCursor.getColumnCount(); i++) {
                    Log.d(LOG_TAG, "testMovieDatabaseMovieAdd: count(" + movieCursor.getColumnName(i) + ")");

                }

                assertTrue(movieCursor.getInt(0) != 0);//AutoIncrement Id
                assertTrue(movieCursor.getInt(1) != 0);// Movie Id
                assertFalse(movieCursor.getString(2).isEmpty());// Movie Title
                assertFalse(movieCursor.getString(3).isEmpty());// Movie overview
                assertTrue(movieCursor.getFloat(4) != 0.0);// Movie vote average
                assertFalse(movieCursor.getString(5).isEmpty());// release date
                assertFalse(movieCursor.getString(6).isEmpty());// Poster URL


            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }
        }
        finally {
            if(movieCursor != null) {
                movieCursor.close();
            }
        }
        testTask.getSubTask().get(100000, TimeUnit.MILLISECONDS);

        Cursor trailerCursor = null;
        try {
            trailerCursor = getContext().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI, null, null, null, null);//, new String[]{""},"1", new String[]{""}, null);

            Log.d(LOG_TAG, "testMovieDatabaseMovieAdd: count(" + trailerCursor.getCount() + ")");

            trailerCursor.moveToFirst();
            String logStr = "testMovieDatabaseMovieAdd: Trailer Columns(";
            for (int i = 0; i < trailerCursor.getColumnCount(); i++) {
                logStr = " " + trailerCursor.getColumnName(i) + ",";
            }
            Log.d(LOG_TAG, logStr);

            //validate test
            do {
                logStr = "testMovieDatabaseMovieAdd_Trailers:";
                logStr += " MovieId(" + trailerCursor.getInt(0) + "),";
                logStr += " Id(" + trailerCursor.getString(1) + "),";
                logStr += " Name(" + trailerCursor.getString(2) + "),";
                logStr += " Key(" + trailerCursor.getString(3) + "),";
                logStr += " Site(" + trailerCursor.getString(4) + "),";
                Log.d(LOG_TAG, logStr);

                assertTrue(trailerCursor.getInt(0) != 0);
                assertFalse(trailerCursor.getString(1).isEmpty());
                assertFalse(trailerCursor.getString(2).isEmpty());
                assertFalse(trailerCursor.getString(3).isEmpty());
                assertFalse(trailerCursor.getString(4).isEmpty());

            }
            while (trailerCursor.moveToNext());

        }
        finally {
            if(trailerCursor != null) {
                trailerCursor.close();
            }
        }

        Cursor reviewCursor = null;
        try {
            reviewCursor = getContext().getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI, null, null, null, null);//, new String[]{""},"1", new String[]{""}, null);

            Log.d(LOG_TAG, "testMovieDatabaseMovieAdd_Reviews: count(" + reviewCursor.getCount() + ")");

            reviewCursor.moveToFirst();
            String logStr = "testMovieDatabaseMovieAdd: Review Columns(";
            for (int i = 0; i < reviewCursor.getColumnCount(); i++) {
                logStr = " " + reviewCursor.getColumnName(i) + ",";
            }
            Log.d(LOG_TAG, logStr);

            Log.d(LOG_TAG, "testMovieDatabaseMovieAdd: ReviewMovieId(" + reviewCursor.getInt(0) + ")");
            Log.d(LOG_TAG, "testMovieDatabaseMovieAdd: ReviewId(" + reviewCursor.getString(1) + ")");
            Log.d(LOG_TAG, "testMovieDatabaseMovieAdd: ReviewName(" + reviewCursor.getString(2) + ")");
            Log.d(LOG_TAG, "testMovieDatabaseMovieAdd: ReviewKey(" + reviewCursor.getString(3) + ")");
            Log.d(LOG_TAG, "testMovieDatabaseMovieAdd: ReviewSite(" + reviewCursor.getString(4) + ")");
            //validate test
            do {

                logStr = "testMovieDatabaseMovieAdd_Reviews:";
                logStr += " MovieId(" + reviewCursor.getInt(0) + "),";
                logStr += " Id(" + reviewCursor.getString(1) + "),";
                logStr += " Name(" + reviewCursor.getString(2) + "),";
                logStr += " Key(" + reviewCursor.getString(3) + "),";
                logStr += " Site(" + reviewCursor.getString(4) + "),";
                Log.d(LOG_TAG, logStr);

                assertTrue(reviewCursor.getInt(0) != 0);
                assertFalse(reviewCursor.getString(1).isEmpty());
                assertFalse(reviewCursor.getString(2).isEmpty());
                assertFalse(reviewCursor.getString(3).isEmpty());
                assertFalse(reviewCursor.getString(4).isEmpty());
                //validate test
            }
            while (reviewCursor.moveToNext());

        }
        finally {
            if(reviewCursor != null) {
                reviewCursor.close();
            }
        }

    }


}
