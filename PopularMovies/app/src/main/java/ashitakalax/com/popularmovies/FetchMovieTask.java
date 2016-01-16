package ashitakalax.com.popularmovies;

import android.content.Context;
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
    public static final String SORT_BY_POPULARITY = "sort_by_popularity";
    public static final String SORT_BY_RATING = "sort_by_rating";
    public static final String SORT_BY_FAVORITES = "sort_by_favorites";
    public static final String SORT_TYPE = "sort_type";
    public List<MovieItem> movieItemList = null;
    private Context mContext;

    private FetchMovieDetailsTask fetchMovieDetailsTask;


    public interface FetchComplete
    {
        void FetchComplete();
    }
    private FetchComplete completed;

    public FetchMovieTask(Context context)
    {
        this.mContext = context;

    }

    public void setFetchMovieCompleted(FetchComplete completeHandler)
    {
        this.completed = completeHandler;
    }

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
            final String MIN_VOTE_COUNT_PARAM = "vote_count.gte";
            //to make the results better we are filtering out any movies that don't have at least
            // 20 vote counts vote_count.gte=20&
            String minVoteCount = "20";

            String sortString = "popularity.desc";
            if(sortType[0].equals(SORT_BY_RATING))
            {
                sortString = "vote_average.desc";
            }

            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sortString)
                    .appendQueryParameter(MIN_VOTE_COUNT_PARAM, minVoteCount)
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
            this.movieItemList = MovieItem.getMoviesFromJson(this.mContext, movieJsonStr);
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
        this.fetchMovieDetailsTask = new FetchMovieDetailsTask(this.mContext);
        this.fetchMovieDetailsTask.setFetchMovieCompleted(this.completed);
        this.fetchMovieDetailsTask.execute(this.movieItemList);

    }

    public FetchMovieDetailsTask getSubTask()
    {
        return this.fetchMovieDetailsTask;
    }

}
