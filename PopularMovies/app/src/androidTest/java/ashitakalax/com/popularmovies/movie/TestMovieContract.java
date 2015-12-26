package ashitakalax.com.popularmovies.movie;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 12/26/2015
 */
public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_MOVIE_ID = "1234";

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildMovieReview() {
        Uri movieReviewUri = MovieContract.ReviewEntry.buildMovieReview(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.", movieReviewUri);
        assertEquals("Error: movieId not properly appended to the end of the Uri",
                TEST_MOVIE_ID, movieReviewUri.getLastPathSegment());
        assertEquals("Error: movie review Uri doesn't match our expected result",
                movieReviewUri.toString(),
                "content://ashitakalax.com.popularmovies/reviews/1234");
    }
}