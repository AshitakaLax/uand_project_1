package ashitakalax.com.popularmovies.movie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ashitakalax.com.popularmovies.movie.MovieContract.MovieEntry;
import ashitakalax.com.popularmovies.movie.MovieContract.ReviewEntry;
import ashitakalax.com.popularmovies.movie.MovieContract.TrailerEntry;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 12/8/2015
 */
public class MovieDbHelper extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION = 11;

    public static final String DATABASE_NAME = "movie.db";


    public MovieDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL  NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_IS_FAVORITE + " BOOLEAN, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " + // Slot for favorites
                "UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE" +
                " );";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_ID + " TEXT," +//Keys are alpha numeric
                TrailerEntry.COLUMN_TRAILER_TITLE + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_URL + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_HOST + " TEXT NOT NULL," +
                " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE," +

                // the ID of the location entry associated with this weather data
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +//these are integers
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +//these are integers
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT," +//keys are alpha numeric
                ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT," +
                ReviewEntry.COLUMN_REVIEW_URL + " TEXT," +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT," +
                " UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE," +
                // the ID of the location entry associated with this weather data
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + MovieContract.FavoritesEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                MovieContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +//these are integers
                MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +//these are integers
                " FOREIGN KEY (" + MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";




        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
