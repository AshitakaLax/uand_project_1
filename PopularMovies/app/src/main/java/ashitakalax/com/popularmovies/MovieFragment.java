package ashitakalax.com.popularmovies;

import android.net.Uri;
import android.support.v4.app.Fragment;
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
public class MovieFragment extends Fragment{

    public static final String LOG_TAG = MovieFragment.class.getSimpleName();


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
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        //todo change the last null to be the sort order
        Cursor cur = getActivity().getContentResolver().query(movieUri, null, null, null, null);


        this.mMovieAdapter = new MovieAdapter(getActivity(), cur, 0);
        View rootView = inflater.inflate(R.layout.movie_grid,container, false);

        this.mGridView = (GridView)rootView.findViewById(R.id.movie_grid);

        this.mGridView.setAdapter(this.mMovieAdapter);
//        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//
//                Cursor cursor = (Cursor)adapterView.getItemAtPosition(position);
//
//            }
//        });
        return rootView;
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
}
