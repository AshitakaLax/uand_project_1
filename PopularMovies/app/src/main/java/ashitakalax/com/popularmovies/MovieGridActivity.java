package ashitakalax.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import ashitakalax.com.popularmovies.movie.MovieArrayAdapter;
import ashitakalax.com.popularmovies.movie.MovieContent;
import ashitakalax.com.popularmovies.movie.MovieItem;


/**
 * An activity representing a list of movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieGridActivity extends AppCompatActivity implements View.OnClickListener {

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
        mTwoPane = true;

        //here we want to do a json query to get the list of movies
        new downloadMovieList().execute("");
        //setupArrayAdapter(gridView);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupArrayAdapter(String jsonReply) {
        List<MovieItem> movieItemList = null;
        try {
            movieItemList = MovieContent.getMoviesFromJson(jsonReply);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mGridView.setAdapter(new MovieArrayAdapter(getApplicationContext(), R.id.movie_grid, movieItemList, this));


        //gridView.setAdapter(new SimpleItemRecyclerViewAdapter(MovieContent.ITEMS));
    }

    private class downloadMovieList extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String movieJsonStr = null;
            try
            {

                final String MOVIE_DB_BASE_URL ="http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                //todo change this string to sort by different values
                String popularDescending = "popularity.desc";

                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, popularDescending)
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

//    public class SimpleItemRecyclerViewAdapter
//            extends ArrayAdapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
//
//
//        public SimpleItemRecyclerViewAdapter(Context context, int resource, List<ViewHolder> objects) {
//            super(context, resource, objects);
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.movie_list_content, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            holder.mItem = mValues.get(position);
//            holder.mIdView.setText(mValues.get(position).id);
//            holder.mContentView.setText(mValues.get(position).content);
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putString(MovieDetailFragment.ARG_MOVIE_ID, holder.mItem.id);
//                        MovieDetailFragment fragment = new MovieDetailFragment();
//                        fragment.setArguments(arguments);
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.movie_detail_container, fragment)
//                                .commit();
//                    } else {
//                        Context context = v.getContext();
//                        Intent intent = new Intent(context, MovieDetailActivity.class);
//                        intent.putExtra(MovieDetailFragment.ARG_MOVIE_ID, holder.mItem.id);
//
//                        context.startActivity(intent);
//                    }
//                }
//            });
//        }
//
//
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            public final View mView;
//            public final TextView mIdView;
//            public final TextView mContentView;
//            public MovieContent.MovieItem mItem;
//
//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                mIdView = (TextView) view.findViewById(R.id.id);
//                mContentView = (TextView) view.findViewById(R.id.content);
//            }
//
//            @Override
//            public String toString() {
//                return super.toString() + " '" + mContentView.getText() + "'";
//            }
//        }
//    }
}
