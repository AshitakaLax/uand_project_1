package ashitakalax.com.popularmovies.movie;

import android.provider.BaseColumns;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 12/7/2015
 */
public class MovieContract {


    public static final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_URL = "poster_path";
        //these are the keys into other remove tables these can be null
        public static final String COLUMN_REVIEW_KEY = "reviews";
        public static final String COLUMN_VIDEOS_KEY = "videos";

    }

    public static final class TrailerEntry implements BaseColumns{
        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_TRAILER_ID = "id";
        public static final String COLUMN_TRAILER_TITLE = "name";
        public static final String COLUMN_TRAILER_URL = "key";
        public static final String COLUMN_TRAILER_HOST = "site";
        public static final String COLUMN_MOVIE_KEY = "movies";
    }

    public static final class ReviewEntry implements BaseColumns{
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_REVIEW_ID = "id";
        public static final String COLUMN_REVIEW_AUTHOR = "key";
        public static final String COLUMN_REVIEW_URL = "url";
        public static final String COLUMN_REVIEW_CONTENT = "content";
        public static final String COLUMN_MOVIE_KEY = "movies";
    }

}
