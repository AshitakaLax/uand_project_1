package ashitakalax.com.popularmovies.movie;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 12/7/2015
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.

    public static final String CONTENT_AUTHORITY ="ashitakalax.com.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_FAVORITES = "favorites";

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movId";
        public static final String COLUMN_MOVIE_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_URL = "poster_path";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_POPULARITY = "popularity";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //todo we need more items here in order to support more interaction


        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class TrailerEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_TRAILER_ID = "strId";
        public static final String COLUMN_TRAILER_TITLE = "name";
        public static final String COLUMN_TRAILER_URL = "key";
        public static final String COLUMN_TRAILER_HOST = "site";
        public static final String COLUMN_MOVIE_KEY = "movies";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * This is the uri for querying a specific list of reviews for a movie
         * @param movieId of the trailers to be returns
         * @return uri for the query
         */
        public static Uri buildMovieTrailer(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

    }

    public static final class ReviewEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_REVIEW_ID = "strId";
        public static final String COLUMN_REVIEW_AUTHOR = "key";
        public static final String COLUMN_REVIEW_URL = "url";
        public static final String COLUMN_REVIEW_CONTENT = "content";
        public static final String COLUMN_MOVIE_KEY = "movies";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        /**
         * This is the uri for querying a specific list of reviews for a movie
         * @param movieId of the reviwes to be returns
         * @return uri for the query
         */
        public static Uri buildMovieReview(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

    }
    public static final class FavoritesEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_KEY = "movieId";

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        /**
         * This is the uri for querying a specific list of reviews for a movie
         * @param movieId of the reviwes to be returns
         * @return uri for the query
         */
        public static Uri buildFavoriteMovies() {
            return CONTENT_URI.buildUpon().appendPath("*").build();
        }
    }

}
