package ashitakalax.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ashitakalax.com.popularmovies.movie.MovieArrayAdapter;
import ashitakalax.com.popularmovies.movie.MovieItem;


/**
 * An activity representing a list of movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieGridActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final String SORT_BY_POPULARITY = "sort_by_popularity";
    private final String SORT_BY_RATING = "sort_by_rating";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private  GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        mGridView = (GridView)findViewById(R.id.movie_grid);
        assert mGridView != null;
        //todo change the number of columns and the column width based on the size of the display

        mGridView.setNumColumns(3);
        //todo change from TwoPane
        mTwoPane = false;

        //here we want to do a json query to get the list of movies
        new downloadMovieList().execute(SORT_BY_POPULARITY);
        //setupArrayAdapter(gridView);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_grid, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_popularity:
                //send request for the
                new downloadMovieList().execute(SORT_BY_POPULARITY);
                return true;
            case R.id.sort_highest_rated:
                new downloadMovieList().execute(SORT_BY_RATING);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupArrayAdapter(String jsonReply) {
        //todo reuse list, and adapter instead of overwriting it for speed.
        List<MovieItem> movieItemList = null;
        try {
            movieItemList = MovieItem.getMoviesFromJson(jsonReply);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mGridView.setOnItemClickListener(this);
        mGridView.setAdapter(new MovieArrayAdapter(getApplicationContext(), R.id.movie_grid, movieItemList));
        //load the first on the list to make sure that there is something on the display
        if (mTwoPane && movieItemList.size() != 0) {
            Bundle arguments = new Bundle();
            arguments.putInt(MovieDetailFragment.ARG_MOVIE_ID, movieItemList.get(0).getId());
            arguments.putString(MovieDetailFragment.ARG_MOVIE_TITLE, movieItemList.get(0).getOriginalTitle());
            arguments.putString(MovieDetailFragment.ARG_MOVIE_OVERVIEW, movieItemList.get(0).getPlotSynopsis());
            arguments.putString(MovieDetailFragment.ARG_MOVIE_RELEASE_DATE, movieItemList.get(0).getReleaseDate());
            arguments.putString(MovieDetailFragment.ARG_MOVIE_POSTER_URL, movieItemList.get(0).getImageUrl());
            arguments.putDouble(MovieDetailFragment.ARG_MOVIE_RATING, movieItemList.get(0).getUserRating());

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        }else {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MovieItem item = (MovieItem)view.getTag();
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(MovieDetailFragment.ARG_MOVIE_ID, item.getId());
            arguments.putString(MovieDetailFragment.ARG_MOVIE_TITLE, item.getOriginalTitle());
            arguments.putString(MovieDetailFragment.ARG_MOVIE_OVERVIEW, item.getPlotSynopsis());
            arguments.putString(MovieDetailFragment.ARG_MOVIE_RELEASE_DATE, item.getReleaseDate());
            arguments.putString(MovieDetailFragment.ARG_MOVIE_POSTER_URL, item.getImageUrl());
            arguments.putDouble(MovieDetailFragment.ARG_MOVIE_RATING, item.getUserRating());

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {



//            Bundle arguments = new Bundle();
//
//            arguments.putInt(MovieDetailActivity.ARG_MOVIE_ID, item.getId());
//            arguments.putString(MovieDetailActivity.ARG_MOVIE_TITLE, item.getOriginalTitle());
//            arguments.putString(MovieDetailActivity.ARG_MOVIE_OVERVIEW, item.getPlotSynopsis());
//            arguments.putString(MovieDetailActivity.ARG_MOVIE_RELEASE_DATE, item.getReleaseDate());
//            arguments.putString(MovieDetailActivity.ARG_MOVIE_POSTER_URL, item.getImageUrl());
//            arguments.putDouble(MovieDetailActivity.ARG_MOVIE_RATING, item.getUserRating());

            Context context = view.getContext();
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.ARG_MOVIE_ID, item.getId());
            intent.putExtra(MovieDetailActivity.ARG_MOVIE_ID, item.getId());
            intent.putExtra(MovieDetailActivity.ARG_MOVIE_TITLE, item.getOriginalTitle());
            intent.putExtra(MovieDetailActivity.ARG_MOVIE_OVERVIEW, item.getPlotSynopsis());
            intent.putExtra(MovieDetailActivity.ARG_MOVIE_RELEASE_DATE, item.getReleaseDate());
            intent.putExtra(MovieDetailActivity.ARG_MOVIE_POSTER_URL, item.getImageUrl());
            intent.putExtra(MovieDetailActivity.ARG_MOVIE_RATING, item.getUserRating());
//            intent.putExtra(MovieDetailActivity.ARG_BUNDLE_ID, arguments);//holder.mItem.id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private class downloadMovieList extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... sortType) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String movieJsonStr = null;
            try
            {

                final String MOVIE_DB_BASE_URL ="http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                String sortString = "popularity.desc";
                if(sortType[0] == SORT_BY_RATING)
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
                    buffer.append(line + "\n");
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

            return movieJsonStr;
        }
        protected void onPostExecute(String result) {
            setupArrayAdapter(result);
        }
    }

    @Override
    public void onClick(View view) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(MovieDetailFragment.ARG_MOVIE_ID, "1");
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Context context = view.getContext();
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra(MovieDetailFragment.ARG_MOVIE_ID, "0");//holder.mItem.id);

            context.startActivity(intent);
        }
    }

}
