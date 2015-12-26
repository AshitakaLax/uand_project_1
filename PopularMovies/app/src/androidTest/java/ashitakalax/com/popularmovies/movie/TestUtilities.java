package ashitakalax.com.popularmovies.movie;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 12/10/2015
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_LOCATION = "99705";
    static final long TEST_MOVIE_ID = 99705;
    static final String TEST_TRAILER_ID = "AC523KB";
    static final String TEST_REVIEW_ID = "BBEC3KB";
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createAwesomeMovieValues() {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        weatherValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "AWESOME TO THE MAX");
        weatherValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "This is the best movie ever");
        weatherValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.86);
        weatherValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-07-17");
        weatherValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, "/kvXLZqY0Ngl1XSw7EaMQO0C1CCj");
        return weatherValues;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */
    static ContentValues createAwesomeTrailer() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, TEST_MOVIE_ID);
        testValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, TEST_TRAILER_ID);
        testValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_HOST, "youtube");
        testValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE, "TOTALLY AWESOME TRAILER");
        testValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_URL, "ZiS7akYy4yA");//this is the key

        return testValues;
    }

     /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */
    static ContentValues createAwesomeReviews() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, TEST_MOVIE_ID);
        testValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, TEST_REVIEW_ID);
        testValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "Ash");
        testValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "By far the best movie ever made, it was awesome");
        testValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, "http://j.mp/oteru");

        return testValues;
    }

    static long insertAwesomeMovieValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createAwesomeMovieValues();

        long locationRowId;
        locationRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    /**
     * inserts the reviews into the review table
     * @param context needed to insert the values into the table
     * @return the locationRowId
     */
    static long insertReviewsValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createAwesomeReviews();

        long locationRowId;
        locationRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert review Values", locationRowId != -1);

        return locationRowId;
    }

    /**
     * inserts the trailers into the trailer table
     * @param context needed to insert the values into the table
     * @return the locationRowId
     */
    static long insertTrailerValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createAwesomeTrailer();

        long locationRowId;
        locationRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Trailer Values", locationRowId != -1);

        return locationRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.
        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
//            new PollingCheck(5000) {
//                @Override
//                protected boolean check() {
//                    return mContentChanged;
//                }
//            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}