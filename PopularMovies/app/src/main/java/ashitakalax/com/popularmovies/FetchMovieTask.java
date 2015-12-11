package ashitakalax.com.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ashitakalax.com.popularmovies.movie.MovieItem;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 12/10/2015
 */
public class FetchMovieTask  extends AsyncTask<String, Void, String>
{
    private final String SORT_BY_POPULARITY = "sort_by_popularity";
    private final String SORT_BY_RATING = "sort_by_rating";
    private final String SORT_BY_FAVORITES = "sort_by_favorites";
    private final String SORT_TYPE = "sort_type";
    List<MovieItem> movieItemList = null;
    @Override
    protected String doInBackground(String... sortType) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;
        movieItemList = null;
        try
        {

            final String MOVIE_DB_BASE_URL ="http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String APPID_PARAM = "api_key";

            String sortString = "popularity.desc";
            if(sortType[0].equals(SORT_BY_RATING))
            {
                sortString = "vote_average.desc";
            }

            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sortString)
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)//.OPEN_WEATHER_MAP_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return "";
            }
            movieJsonStr = buffer.toString();

        }
        catch (IOException ioex)
        {

            Log.e("PlaceholderFragment", "Error ", ioex);
        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        //parse the json string into a movieItem list
        try {
            this.movieItemList = MovieItem.getMoviesFromJson(movieJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieJsonStr;
    }
    protected void onPostExecute(String result) {
        if(result == null || result.isEmpty())
        {
        //    mNoInternetTextView.setVisibility(View.VISIBLE);
            return;//can't setup array adapter without any data
        }

        //it is unlikely that we want to pause till this is finished, so we will update the ui
        //first then do these things in the background

//        mNoInternetTextView.setVisibility(View.INVISIBLE);
//
//        setupArrayAdapter(this.movieItemList);
        for(MovieItem item :this.movieItemList)
        {
            //this will do the other query to get the rest of the data
            new FetchMovieDetailsTask().execute(item);
        }
    }
}
