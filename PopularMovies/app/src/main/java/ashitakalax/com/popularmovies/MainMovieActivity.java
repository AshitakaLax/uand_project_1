package ashitakalax.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import ashitakalax.com.popularmovies.movie.MovieContract;

/**
 * Created by Levi Balling on 1/2/2016.
 */
public class MainMovieActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainMovieActivity.class.getSimpleName();
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTER = 2;
    private static final int MOVIE_LOADER = 0;
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_URL
    };

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private MovieAdapter mMovieAdapter;
    private GridView mGridView;
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        mTwoPane = false;

        View containerView = findViewById(R.id.container);


        //check if this is 2 pane or 1
//by default the movieFragment should be setup on launch

        //load the movie adapter in it
        this.mMovieAdapter = new MovieAdapter(this, null, 0);

        this.mGridView = (GridView)findViewById(R.id.movie_grid);

        this.mGridView.setAdapter(this.mMovieAdapter);

        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {

                    Long movieId = (long) cursor.getInt(COL_MOVIE_ID);
                    if (mTwoPane) {
                        //load the fragment into the other container
//                        getActivity().getIntent().setData(MovieContract.MovieEntry.buildMovieUri(movieId));
                        MovieDetailFragment fragment = new MovieDetailFragment();

                    } else {

                        Intent intent = new Intent(getParent(), MovieDetailFragment.class);


                        //store the movie
                        intent.setData(MovieContract.MovieEntry.buildMovieUri(movieId));
                        MovieDetailFragment fragment = new MovieDetailFragment();
//                        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    }
                }
            }
        });


        if(containerView.findViewById(R.id.movie_detail_container)!= null)
        {
            mTwoPane = true;
            //two pane
            if (savedInstanceState == null)
            {
                long temp = Utility.getSelectedMovie(this);
                if(temp != -1) {

                    //load the fragment into the other container
                    this.getIntent().setData(MovieContract.MovieEntry.buildMovieUri(temp));
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    return;
                }
            }
        }

        if (savedInstanceState == null) {
            //don't load into container
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MovieFragment()).commit();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        //todo change the last null to be the sort order
//        Cursor cur = getActivity().getContentResolver().query(movieUri, null, null, null, null);

        String sortingType = Utility.getPreferredSortingType(this);
        String sortStr = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        String SelectionStr = null;
        String[] SelectionArgs = null;
        if(sortingType.equals(Utility.SORT_BY_RATING)) {

            sortStr = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }
        else if(sortingType.equals(Utility.SORT_BY_FAVORITES)) {

            movieUri = MovieContract.FavoritesEntry.buildFavoriteMovies();
            sortStr = null;

        }



        return new CursorLoader(this, movieUri, MOVIE_COLUMNS, SelectionStr, SelectionArgs, sortStr);    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);

    }


    //we want this to be the main activity that handles all of the fragments




}
