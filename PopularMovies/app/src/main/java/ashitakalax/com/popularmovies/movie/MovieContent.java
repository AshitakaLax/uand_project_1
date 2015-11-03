package ashitakalax.com.popularmovies.movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this file will hold the movie Content, as well as the MovieItem.
 * The movie Item will contain the info that we want to keep track of for the displaying to the user
 * the layout of this was taken from the DummyContent android studio example.
 * @author Levi.Balling
 * @version 1
 * @since 10/31/2015
 */
public class MovieContent {


    //this is going to change
    //we want an obj

    public static final List<MovieItem> ITEMS = new ArrayList<MovieItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Integer, MovieItem> MOVIE_MAP = new HashMap<Integer, MovieItem>();

    private static final int COUNT = 25;


    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addMovie(createMovieItem(i));
        }
    }
    private static void addMovie(MovieItem item) {
        ITEMS.add(item);
        MOVIE_MAP.put(item.getmId(), item);
    }

    private static MovieItem createMovieItem(int position) {
        return new MovieItem(position, "Movie " + position, makeDetails(position));
    }

    public static List<MovieItem> getMoviesFromJson(String movieJsonStr) throws JSONException
    {
        // list of
        final String MOVIE_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_POSTER_URL = "poster_path";

        JSONObject movieQueryJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieQueryJson.getJSONArray(MOVIE_LIST);

        List<MovieItem> movieItemList = new ArrayList<MovieItem>();
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieJson = movieArray.getJSONObject(i);
            int movieId = movieJson.getInt(MOVIE_ID);
            String  movieOverview = movieJson.getString(MOVIE_OVERVIEW);
            String  moviePoster = movieJson.getString(MOVIE_POSTER_URL);

            MovieItem item = new MovieItem(movieId, movieOverview, moviePoster);

            item.setmOriginalTitle(movieJson.getString(MOVIE_TITLE));
            item.setmUserRating(movieJson.getDouble(MOVIE_RATING));
            item.setmReleaseDate(movieJson.getString(MOVIE_RELEASE_DATE));
            item.setmOriginalTitle(movieJson.getString(MOVIE_TITLE));

            movieItemList.add(item);
        }

        return movieItemList;

    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about movie: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
}
