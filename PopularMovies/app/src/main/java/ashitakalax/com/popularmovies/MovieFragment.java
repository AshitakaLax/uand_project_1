package ashitakalax.com.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ashitakalax.com.popularmovies.movie.MovieContract;

/**
 * Created by levi Balling on 1/1/2016.
 * This class is taking a lot of what is in the MovieGridActivity and putting it in here
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,FetchMovieTask.FetchComplete {

    public static final String LOG_TAG = MovieFragment.class.getSimpleName();
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
    private MovieAdapter mMovieAdapter;
    private GridView mGridView;
    private Activity mMainActivity;

    private boolean mTwoPane;

    public interface OnMovieSelected
    {
        public void onMovieSelected(long movieId);
    }

    public MovieFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof Activity) {
            mMainActivity = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //the menuMain grid will reference other class to handle the selection of menu options
        inflater.inflate(R.menu.menu_main_grid, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_popularity) {
            //todo change the location of the SORT_BY_POPULARITY to be a more suitable location
            Utility.setPreferredSortingType(getContext(), Utility.SORT_BY_POPULARITY);
            this.updateMovieDb();
            return true;
        }
        else if (id == R.id.sort_highest_rated) {
            //todo change the location of the SORT_BY_POPULARITY to be a more suitable location
            Utility.setPreferredSortingType(getContext(), Utility.SORT_BY_RATING);
            this.updateMovieDb();
            return true;
        }
        else if (id == R.id.sort_favories) {
            //todo change the location of the SORT_BY_POPULARITY to be a more suitable location
            Utility.setPreferredSortingType(getContext(), Utility.SORT_BY_FAVORITES);
            this.updateMovieDb();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //todo before we replace the viewgroup we need to know if the user
        //is Two Pane. if so then we need to load a different container,

        this.mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.movie_grid, container, false);

        this.mGridView = (GridView)rootView.findViewById(R.id.movie_grid);

        //todo pass the number of panes in as an arguement
        this.mTwoPane = true;

        if(this.mTwoPane) {
            this.mGridView.setNumColumns(3);
        }
        else
        {

            this.mGridView.setNumColumns(2);
        }
        this.mGridView.setAdapter(this.mMovieAdapter);

        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {

                    Long movieId = (long) cursor.getInt(COL_MOVIE_ID);

                    try{
                        ((OnMovieSelected) mMainActivity).onMovieSelected(movieId);
                    }catch (ClassCastException cce){

                    }
//
//                    if (mTwoPane) {
//                        //load the fragment into the other container
//                        getActivity().getIntent().setData(MovieContract.MovieEntry.buildMovieUri(movieId));
//                        MovieDetailFragment fragment = new MovieDetailFragment();
//
//                    } else {
//                        Intent intent = new Intent(getActivity(), MovieDetailFragment.class);
//
//                        //store the movie
//                        intent.setData(MovieContract.MovieEntry.buildMovieUri(movieId));
//                        MovieDetailFragment fragment = new MovieDetailFragment();
//                        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
//                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        //check if there is a movie already selected before
        Long movieId = Utility.getSelectedMovie(getContext());

        if(movieId != -1)
        {
            if(this.mTwoPane)
            {
                //load up the other page
                //arguments.putString(MovieDetailFragment.ARG_MOVIE_ID, "1");
                this.getActivity().getIntent().setData(MovieContract.MovieEntry.buildMovieUri(movieId));
                MovieDetailFragment fragment = new MovieDetailFragment();
//                this.getActivity().getFragmentManager().beginTransaction()
//                        .replace(R.id.movie_detail_container, fragment)
//                        .commit();
            }
            else {
                //we already have a selected movie load it up
                Intent intent = new Intent(getActivity(), MovieDetailFragment.class);
                intent.setData(MovieContract.MovieEntry.buildMovieUri(movieId));
                startActivity(intent);
            }
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieDb();
    }

    @Override
    public void onResume() {
        super.onResume();
        //clear the data we return to this screan
        Utility.setSelectedMovie(getContext(), -1);
    }

    private void updateMovieDb()
    {
        String sortingType = Utility.getPreferredSortingType(getContext());
        FetchMovieTask fetchMovieTask = new FetchMovieTask(this.getContext());
        fetchMovieTask.setFetchMovieCompleted(this);

        fetchMovieTask.execute(sortingType);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        //todo change the last null to be the sort order
//        Cursor cur = getActivity().getContentResolver().query(movieUri, null, null, null, null);

        String sortingType = Utility.getPreferredSortingType(getContext());
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



        return new CursorLoader(getActivity(), movieUri, MOVIE_COLUMNS, SelectionStr, SelectionArgs, sortStr);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void FetchComplete() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

}
