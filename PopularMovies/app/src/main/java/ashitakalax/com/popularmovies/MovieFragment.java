package ashitakalax.com.popularmovies;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String LOG_TAG = MovieFragment.class.getSimpleName();


    private static final int MOVIE_LOADER = 0;
    private MovieAdapter mMovieAdapter;
    private GridView mGridView;

    public MovieFragment()
    {

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
        if (id == R.id.refresh_menu_option) {

            this.updateMovieDb();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        //get the cursor
//        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
//        //todo change the last null to be the sort order
//        Cursor cur = getActivity().getContentResolver().query(movieUri, null, null, null, null);


        this.mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.movie_grid,container, false);

        this.mGridView = (GridView)rootView.findViewById(R.id.movie_grid);

        this.mGridView.setAdapter(this.mMovieAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieDb();
    }

    private void updateMovieDb()
    {
        new FetchMovieTask(this.getContext()).execute(FetchMovieTask.SORT_BY_POPULARITY);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        //todo change the last null to be the sort order
//        Cursor cur = getActivity().getContentResolver().query(movieUri, null, null, null, null);

        return new CursorLoader(getActivity(), movieUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }
}
