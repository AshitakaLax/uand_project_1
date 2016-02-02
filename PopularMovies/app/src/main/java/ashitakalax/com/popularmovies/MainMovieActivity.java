package ashitakalax.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import ashitakalax.com.popularmovies.movie.MovieContract;

/**
 * Created by Levi Balling on 1/2/2016.
 */
public class MainMovieActivity extends AppCompatActivity implements MovieFragment.OnMovieSelected{

    @Override
    public void onMovieSelected(long movieId)
    {
        if (mTwoPane)
        {

            //load the fragment into the other container
            Bundle arguments = new Bundle();
            arguments.putLong(MOVIE_ID_BUNDLE, movieId);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {

            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MOVIE_ID_BUNDLE, movieId);
            startActivity(intent);

//
//            mDetailFragmentActive = true;
//            Bundle arguments = new Bundle();
//            arguments.putLong(MOVIE_ID_BUNDLE, movieId);
//            MovieDetailFragment fragment = new MovieDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.movie_grid_container, fragment)
//                    .commit();
        }
    }


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
    private static final String MOVIE_ID_BUNDLE = "movie_id";
    private MovieAdapter mMovieAdapter;
    private GridView mGridView;
    private boolean mTwoPane;
    private boolean mDetailFragmentActive;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        mTwoPane = false;
        mDetailFragmentActive = false;

        View containerView = findViewById(R.id.container);

        if(containerView.findViewById(R.id.movie_detail_container)!= null)
        {
            mTwoPane = true;
            //two pane

                long temp = Utility.getSelectedMovie(this);
                if(temp != -1) {

                    Bundle arguments = new Bundle();
                    arguments.putLong(MOVIE_ID_BUNDLE, temp);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                }
        }

        if (savedInstanceState == null) {
            //don't load into container
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_grid_container, new MovieFragment()).commit();
        }
    }



    @Override
    public void onBackPressed() {
        if(!mTwoPane) {

            if(mDetailFragmentActive) {

                //this is to prevent the infinite looping
                Utility.setSelectedMovie(this, -1);

                mDetailFragmentActive = false;
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_grid_container, new MovieFragment()).commit();
                return;
            }
        }
        super.onBackPressed();
    }
}
