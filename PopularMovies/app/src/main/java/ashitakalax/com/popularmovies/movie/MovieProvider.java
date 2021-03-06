package ashitakalax.com.popularmovies.movie;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MovieProvider extends ContentProvider {


    static final int MOVIES = 100;
    static final int MOVIES_WITH_REVIEWS = 101;
    static final int MOVIES_WITH_TRAILERS = 102;
    static final int MOVIES_WITH_ID = 103;
    static final int TRAILERS = 300;
    static final int REVIEWS = 400;//do these mean anything?
    static final int FAVORITES = 500;
    static final int FAVORITES_MOVIES = 501;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sReviewsByMovieIdQueryBuilder;
    private static final SQLiteQueryBuilder sTrailersByMovieIdQueryBuilder;
    private static final SQLiteQueryBuilder sMovieByIdQueryBuilder;
    private static final SQLiteQueryBuilder sFavoriteMoviesQueryBuilder;


    //location.location_setting = ?
    //reviews.movieId = ?
    private static final String sMovieReviewSelection =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ";
    private static final String sMovieTrailerSelection =
            MovieContract.TrailerEntry.TABLE_NAME +
                    "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ";
    private static final String sMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";


    static{
        sReviewsByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //reviews INNER JOIN movie ON reviews.review_id = movie.review_id
        sReviewsByMovieIdQueryBuilder.setTables(
                MovieContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    static {
        sTrailersByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //reviews INNER JOIN movie ON reviews.review_id = movie.review_id
        sTrailersByMovieIdQueryBuilder.setTables(
                MovieContract.TrailerEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.TrailerEntry.TABLE_NAME +
                        "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    static {
        sMovieByIdQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //reviews INNER JOIN movie ON reviews.review_id = movie.review_id
        sMovieByIdQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " WHERE " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    static
    {
        sFavoriteMoviesQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //reviews INNER JOIN movie ON reviews.review_id = movie.review_id
        sFavoriteMoviesQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavoritesEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.FavoritesEntry.TABLE_NAME +
                        "." + MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY);

    }
    private MovieDbHelper mOpenHelper;

    public MovieProvider() {
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIES_WITH_ID);//querying a specific movie
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/*", MOVIES_WITH_REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS + "/*", MOVIES_WITH_TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES + "/*", FAVORITES_MOVIES);//querying a specific movie
        return matcher;
    }

    private Cursor getReviewsByMovieId(Uri uri, String[] projection, String sortOrder) {
        String movieIdStr = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieReviewSelection;
        selectionArgs = new String[]{movieIdStr};

        return sReviewsByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailersByMovieId(Uri uri, String[] projection, String sortOrder) {
        String movieIdStr = MovieContract.MovieEntry.getMovieIdFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sMovieTrailerSelection;
        selectionArgs = new String[]{movieIdStr};

        return sTrailersByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoritesByMovieId(Uri uri, String[] projection, String sortOrder) {
        String movieIdStr = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return sFavoriteMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        String movieIdStr = MovieContract.MovieEntry.getMovieIdFromUri(uri);
        //don't think this is needed
//        long startDate = MovieContract.MovieEntry.getStartDateFromUri(uri);
//        long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieIdSelection;
        selectionArgs = new String[]{movieIdStr};

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_WITH_ID:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:
                rowsDeleted = db.delete(
                        MovieContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            //todo
            case MOVIES_WITH_REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case MOVIES_WITH_TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIES_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case FAVORITES:
                return MovieContract.FavoritesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        if(db.isReadOnly())
        {
            throw new android.database.SQLException("Can't insert in a ReadOnly Database");
        }
        switch (match) {
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILERS: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITES: {
                long _id = db.insert(MovieContract.FavoritesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavoritesEntry.buildFavoriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        this.mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            //todo First query movies then jump to querying the combination of them.
            case MOVIES_WITH_REVIEWS:
            {
                retCursor = getReviewsByMovieId(uri, projection, sortOrder);
                break;
            }
            case MOVIES_WITH_TRAILERS: {
                retCursor = getTrailersByMovieId(uri, projection, sortOrder);
                break;
            }
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME, projection,selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MOVIES_WITH_ID: {
                retCursor = getMovieById(uri, projection, sortOrder);
//                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME, projection,selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case FAVORITES_MOVIES: {
                retCursor = getFavoritesByMovieId(uri, projection, sortOrder);
                break;
            }
            case TRAILERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.TrailerEntry.TABLE_NAME, projection,selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME, projection,selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case FAVORITES: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.FavoritesEntry.TABLE_NAME, projection,selection, selectionArgs, null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILERS:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITES:
                rowsUpdated = db.update(MovieContract.FavoritesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    //bulk insert when we get closer

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int returnCount = 0;
        switch (match)
        {
            case MOVIES:
                db.beginTransaction();
                try{
                    for(ContentValues value : values)
                    {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if(_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRAILERS:
                db.beginTransaction();
                try{
                    for(ContentValues value : values)
                    {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if(_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEWS:
                db.beginTransaction();
                try{
                    for(ContentValues value : values)
                    {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if(_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case FAVORITES:
                db.beginTransaction();
                try{
                    for(ContentValues value : values)
                    {
                        long _id = db.insert(MovieContract.FavoritesEntry.TABLE_NAME, null, value);
                        if(_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
