package ashitakalax.com.popularmovies.movie;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 12/26/2015
 */
public class TestUriMatcher  extends AndroidTestCase {
    private static final String MOVIE_QUERY = "1234";

    // content://ashitakalax.com.popularmovies/movie"
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_REVIEWS_DIR = MovieContract.ReviewEntry.buildMovieReview(MOVIE_QUERY);
    private static final Uri TEST_MOVIE_WITH_TRAILERS_DIR = MovieContract.TrailerEntry.buildMovieTrailer(MOVIE_QUERY);
    // content://ashitakalax.com.popularmovies/review"
    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.CONTENT_URI;
    // content://ashitakalax.com.popularmovies/trailer"
    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The Movie URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIES);
        assertEquals("Error: The Movie with Reviews URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_REVIEWS_DIR), MovieProvider.MOVIES_WITH_REVIEWS);
        assertEquals("Error: The Movie with Trailers was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_TRAILERS_DIR), MovieProvider.MOVIES_WITH_TRAILERS);
        assertEquals("Error: The Review URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEWS);
        assertEquals("Error: The Trailer URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_DIR), MovieProvider.TRAILERS);
    }
}
