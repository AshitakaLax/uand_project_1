package ashitakalax.com.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ashitakalax.com.popularmovies.movie.MovieArrayAdapter;
import ashitakalax.com.popularmovies.movie.MovieItem;
import ashitakalax.com.popularmovies.movie.ReviewItem;


/**
 * An activity representing a list of movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieGridActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String FAVORITE_MOVIES_SHARE_PREF_FILE = "favorite_movie_shared_preferences";

    private final String SORT_BY_POPULARITY = "sort_by_popularity";
    private final String SORT_BY_RATING = "sort_by_rating";
    private final String SORT_BY_FAVORITES = "sort_by_favorites";
    private final String SORT_TYPE = "sort_type";
    public static final String MOVIE_SHARE_PREF_FILE = "movie_shared_preferences";
    private final String MOVIE_ITEM_LIST = "movie_item_list";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String mSortTypeStr;
    private  GridView mGridView;
    private TextView mNoInternetTextView;
    private List<MovieItem> mMovieItemList;
    private List<MovieItem> mFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        mGridView = (GridView)findViewById(R.id.movie_grid);
        mNoInternetTextView = (TextView)findViewById(R.id.noInternetTextView);
        assert mGridView != null;

        //check the shared preferences whether
        SharedPreferences prefs = getSharedPreferences(MOVIE_SHARE_PREF_FILE, MODE_PRIVATE);
        this.mSortTypeStr = prefs.getString(SORT_TYPE, SORT_BY_POPULARITY);

//        if(savedInstanceState == null || !savedInstanceState.containsKey("SortType")) {
//
//            mSortTypeStr = SORT_BY_POPULARITY;
//        }
//        else {
//
//            mSortTypeStr = savedInstanceState.getString("SortType", SORT_BY_POPULARITY);
//        }
        //todo change the number of columns and the column width based on the size of the display

        mGridView.setNumColumns(3);
        //todo change from TwoPane
        mTwoPane = false;
        mGridView.setOnItemClickListener(this);

        //here we want to do a json query to get the list of movies
        if(isNetworkAvailable()) {
            mNoInternetTextView.setVisibility(View.INVISIBLE);
            new downloadMovieList().execute(mSortTypeStr);
        }
        else
            mNoInternetTextView.setVisibility(View.VISIBLE);

        mTwoPane = findViewById(R.id.movie_detail_container) != null;
    }

    /**
     * This check is to optimize whether we should even attempt to request the json data
     * Note: this code snippet was taken from
     * http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
     * answer by Alexandre Jasmin
     * @return true if network connection is setup(not direct internet connection)
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_grid, menu);
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_TYPE, this.mSortTypeStr);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mSortTypeStr = savedInstanceState.getString(SORT_TYPE,  SORT_BY_POPULARITY);
        if(isNetworkAvailable()) {
            mNoInternetTextView.setVisibility(View.INVISIBLE);
            new downloadMovieList().execute(mSortTypeStr);
        }
        else
            mNoInternetTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_popularity:
                //send request for the
                this.mSortTypeStr = SORT_BY_POPULARITY;
                break;
            case R.id.sort_highest_rated:
                this.mSortTypeStr = SORT_BY_RATING;
                break;
            case R.id.sort_favories:
                this.mSortTypeStr = SORT_BY_FAVORITES;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        SharedPreferences.Editor editor = getSharedPreferences(MOVIE_SHARE_PREF_FILE, MODE_PRIVATE).edit();
        editor.putString(SORT_TYPE, this.mSortTypeStr);
        editor.apply();

        if(isNetworkAvailable()) {
            mNoInternetTextView.setVisibility(View.INVISIBLE);
            if(this.mSortTypeStr != SORT_BY_FAVORITES) {
                new downloadMovieList().execute(this.mSortTypeStr);
            }
            else
            {
                SharedPreferences prefs = getSharedPreferences(MovieGridActivity.MOVIE_SHARE_PREF_FILE, Activity.MODE_PRIVATE);
                //this is a string set of movie id's only not of anything else
                Set<String> favoriteMovies = prefs.getStringSet(MovieGridActivity.FAVORITE_MOVIES_SHARE_PREF_FILE, new TreeSet<String>());

                for(String str : favoriteMovies)
                {
                    MovieItem tempItem = new MovieItem();
                    tempItem.setId(Integer.parseInt(str));
                    new downloadMovieDetails().execute(tempItem);
                }
            }
        }
        else
            mNoInternetTextView.setVisibility(View.VISIBLE);

        return true;
    }

    private void setupArrayAdapter(List<MovieItem> movieItemList) {
        //thread wise it is ok to write to this class's movie list
        if(movieItemList == null) {

            mNoInternetTextView.setVisibility(View.VISIBLE);
            return;
        }
        this.mMovieItemList = movieItemList;

        mGridView.setAdapter(new MovieArrayAdapter(getApplicationContext(), R.id.movie_grid, this.mMovieItemList));

        //load the first on the list to make sure that there is something on the display
        if (mTwoPane && this.mMovieItemList.size() != 0) {
            new downloadMovieDetails().execute(this.mMovieItemList.get(0));

            //we will want to begin the query for the movie details, then go to the fragment
//            Bundle arguments = new Bundle();
//
//            arguments.putParcelable(MovieDetailFragment.ARG_MOVIE_BUNDLE_ID, this.mMovieItemList.get(0));//go with the first as default
//
//            MovieDetailFragment fragment = new MovieDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.movie_detail_container, fragment)
//                    .commit();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MovieItem item = (MovieItem)view.getTag();

        new downloadMovieDetails().execute(item);

//        Bundle arguments = new Bundle();
//        arguments.putParcelable(MovieDetailFragment.ARG_MOVIE_BUNDLE_ID, item);//go with the first as default
//
//        if (mTwoPane) {
//            MovieDetailFragment fragment = new MovieDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.movie_detail_container, fragment)
//                    .commit();
//        } else {
//            Context context = view.getContext();
//            Intent intent = new Intent(context, MovieDetailActivity.class);
//            intent.putExtras(arguments);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        }
    }

    @Override
    public void onClick(View view) {
        //new downloadMovieDetails().execute(this.mMovieItemList.get(0));
//
//        if (mTwoPane) {
//            //new downloadMovieDetails().execute(this.mMovieItemList.get(0));
//
//            Bundle arguments = new Bundle();
//            //arguments.putString(MovieDetailFragment.ARG_MOVIE_ID, "1");
//            MovieDetailFragment fragment = new MovieDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.movie_detail_container, fragment)
//                    .commit();
//        } else {
//            Context context = view.getContext();
//            Intent intent = new Intent(context, MovieDetailActivity.class);
//            //intent.putExtra(MovieDetailFragment.ARG_MOVIE_ID, "0");//holder.mItem.id);
//
//            context.startActivity(intent);
//        }
    }

    private class downloadMovieList extends AsyncTask<String, Void, String>
    {
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
                mNoInternetTextView.setVisibility(View.VISIBLE);
                return;//can't setup array adapter without any data
            }
            mNoInternetTextView.setVisibility(View.INVISIBLE);

            setupArrayAdapter(this.movieItemList);
        }
    }
    private class downloadMovieDetails extends AsyncTask<MovieItem, Void, MovieItem>
    {
        @Override
        protected MovieItem doInBackground(MovieItem... movie) {

            //this will query the movie info
            MovieItem.queryMovieDetails(movie[0].getId()+"");


            return movie[0];
        }
        protected void onPostExecute(MovieItem movie) {

            Bundle arguments = new Bundle();

            arguments.putParcelable(MovieDetailFragment.ARG_MOVIE_BUNDLE_ID, movie);//this.mMovieItemList.get(0));//go with the first as default

            if (mTwoPane) {
                //arguments.putString(MovieDetailFragment.ARG_MOVIE_ID, "1");
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            } else {
                Context context = getApplicationContext();//.getContext();

                Intent intent = new Intent(context, MovieDetailActivity.class);
                //intent.putExtra(MovieDetailFragment.ARG_MOVIE_ID, "0");//holder.mItem.id);
                intent.putExtras(arguments);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
//            //maybe update the data
//            //we will want to begin the query for the movie details, then go to the fragment
//            Bundle arguments = new Bundle();
//
//            arguments.putParcelable(MovieDetailFragment.ARG_MOVIE_BUNDLE_ID, movie);//this.mMovieItemList.get(0));//go with the first as default
//
//            MovieDetailFragment fragment = new MovieDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.movie_detail_container, fragment)
//                    .commit();

        }
    }



}
