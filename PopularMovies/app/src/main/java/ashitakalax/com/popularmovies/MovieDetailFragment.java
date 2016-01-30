package ashitakalax.com.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ashitakalax.com.popularmovies.movie.MovieContract;
import ashitakalax.com.popularmovies.movie.MovieItem;


/**
 * A fragment representing a single movie detail screen.
 * This fragment is either contained in a {@link MovieGridActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MOVIE_BUNDLE_ID = "SelectedMovieItem";
    public static final String MOVIE_ID_BUNDLE = "movie_id";
    static final int COL_ID = 0;

    //todo add the sharing later
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_VOTE = 5;
    static final int COL_MOVIE_RELEASE_DATE = 6;


    static final int REVIEW_COL_ROW_ID = 0;
    static final int REVIEW_COL_AUTHOR = 1;
    static final int REVIEW_COL_CONTENT = 2;
    static final int REVIEW_COL_ID_STR = 3;

    static final int TRAILER_COL_ID = 0;
    static final int TRAILER_COL_TITLE = 1;
    static final int TRAILER_COL_URL = 2;
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;
    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };
    private static final String[] REVIEW_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
    };
    private static final String[] TRAILER_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE,
            MovieContract.TrailerEntry.COLUMN_TRAILER_URL,
    };
    /**
     * The dummy content this fragment is presenting.
     */
    private String mMovieId;
    private MovieItem mItem;
    private Button favoriteButton;
    private ListView mTrailerListView;
    private ListView mReviewListView;
    private LinearLayout mReviewLayout;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private ArrayList<String> reviewIds;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable("movies", mItem);
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(MOVIE_ID_BUNDLE))
        {
            this.mMovieId = getArguments().getLong(MOVIE_ID_BUNDLE) + "";
        }
        return;
    }

    @Override
    public void onResume() {

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        reviewIds = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        this.favoriteButton = (Button) rootView.findViewById(R.id.favoriteButton);
        mReviewLayout = (LinearLayout)rootView.findViewById(R.id.reviewsLayout);
        mTrailerListView = (ListView)rootView.findViewById(R.id.TrailersListView);

        this.favoriteButton = (Button)rootView.findViewById(R.id.favoriteButton);
        this.favoriteButton.setOnClickListener(this);

        this.mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        mTrailerListView.setAdapter(mTrailerAdapter);
        TextView reviewTextView = (TextView) this.mReviewLayout.findViewById(R.id.reviewLabelTextView);
        reviewTextView.setText("");

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
//        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
//        getLoaderManager().initLoader(TRAILER_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);


    }

    private void updateFavoriteButtonText(boolean isFavorite)
    {
        if(isFavorite)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    favoriteButton.setText("Remove from Favorites");
                }
            });
        }
        else
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    favoriteButton.setText("Save to Favorites");
                }
            });
        }
    }

    public boolean isMovieFavorite()
    {
        //check the string of the button
        return this.favoriteButton.getText().equals("Remove from Favorites");
    }




    @Override
    public void onClick(View view) {

        //check if the current movie is a favorite or not
        //add movie to the favories table in the db

        boolean updatedFavoriteValue = !isMovieFavorite();

        if(updatedFavoriteValue)
        {
            ContentValues favoriteValues = new ContentValues();

            //add movieId to the table
            favoriteValues.put(MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY, this.mMovieId);

            this.getContext().getContentResolver().insert(MovieContract.FavoritesEntry.CONTENT_URI, favoriteValues);
        }
        else
        {
            //remove the movieId from the table
            this.getContext().getContentResolver().delete(MovieContract.FavoritesEntry.CONTENT_URI,MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY + "=?", new String[]{this.mMovieId});
        }
        updateFavoriteButtonText(updatedFavoriteValue);


        return;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "In OnCreateLoader");

        long tempMovieId = Long.parseLong(mMovieId);

        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(tempMovieId);
        if(movieUri == null)
        {
            return null;
        }
        //we already have the mMovieId at this point
        //store the movie
        Utility.setSelectedMovie(getContext(), Long.parseLong(mMovieId));

        if (id == DETAIL_LOADER) {

            //just do a simple query to determine if it is a favorite
            Cursor favoriteCursor = this.getContext().getContentResolver().query(
                    MovieContract.FavoritesEntry.CONTENT_URI,
                    null,
                    MovieContract.FavoritesEntry.COLUMN_MOVIE_KEY + "=?",
                    new String[]{this.mMovieId},
                    null);


            if(favoriteCursor.getCount() == 0)
            {
                //it isn't a favorite
                this.updateFavoriteButtonText(false);
            }
            else
            {
                //it isn't a favorite
                this.updateFavoriteButtonText(true);
            }
            favoriteCursor.close();

            return new CursorLoader(getActivity(),
                    movieUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        } else if (id == REVIEW_LOADER) {
            Uri ReviewUri = MovieContract.ReviewEntry.buildMovieReview(this.mMovieId);
            return new CursorLoader(getActivity(),
                    ReviewUri,
                    REVIEW_COLUMNS,
                    null,
                    null,
                    null
            );
        } else if (id == TRAILER_LOADER) {
            Uri TrailerUri = MovieContract.TrailerEntry.buildMovieTrailer(this.mMovieId);
            return new CursorLoader(getActivity(),
                    TrailerUri,
                    TRAILER_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In OnLoadFinished");

        if (loader.getId() == DETAIL_LOADER) {
            loadMovieDetail(data);
        } else if (loader.getId() == REVIEW_LOADER) {
            loadReviewDetail(data);
        } else if (loader.getId() == TRAILER_LOADER) {
            loadTrailerDetail(data);
        }
        return;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadTrailerDetail(Cursor data) {
        this.mTrailerAdapter.swapCursor(data);
        return;

    }

    private void loadReviewDetail(Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        //int i = 0;
        TextView reviewTextView = (TextView) this.mReviewLayout.findViewById(R.id.reviewLabelTextView);
        do {

            String id = data.getString(MovieDetailFragment.REVIEW_COL_ID_STR);//todo update index
            if(reviewIds.contains(id))
            {
                continue;
            }
            else
            {
                reviewIds.add(id);
            }
            String author = data.getString(MovieDetailFragment.REVIEW_COL_AUTHOR);//todo update index
            String reviewStr = data.getString(MovieDetailFragment.REVIEW_COL_CONTENT);//todo update index
            reviewTextView.append( author +": " + reviewStr + "\n\n");
        }while(data.moveToNext());

        return;
    }

    private void loadMovieDetail(Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String movieTitle = data.getString(COL_MOVIE_TITLE);
        String movieReleaseDate = data.getString(COL_MOVIE_RELEASE_DATE);
        String movieOverview = data.getString(COL_MOVIE_OVERVIEW);

        String moviePoster = data.getString(COL_MOVIE_POSTER);

        double movieRating = data.getDouble(COL_MOVIE_VOTE);

        //get the data from the cursor
        TextView titleTextView = (TextView) getView().findViewById(R.id.titleTextView);
        TextView releaseDateTextView = (TextView) getView().findViewById(R.id.releaseDateTextView);
        TextView movieRatingTextView = (TextView) getView().findViewById(R.id.movieRatingTextView);
        TextView movieOverviewTextView = (TextView) getView().findViewById(R.id.movieOverviewTextView);
        ImageView posterImageView = (ImageView) getView().findViewById(R.id.moviePosterImageView);

        if (titleTextView != null) {
            titleTextView.setText(movieTitle);
        }

        if (releaseDateTextView != null) {
            releaseDateTextView.setText(movieReleaseDate);
        }

        if (movieRatingTextView != null) {
            movieRatingTextView.setText(movieRating + "/10");
        }

        if (movieOverviewTextView != null) {
            movieOverviewTextView.setText(movieOverview);
        }

        if (posterImageView != null) {

            String imageUrl = "http://image.tmdb.org/t/p/w185" + moviePoster;
            Picasso.with(getContext()).load(imageUrl).into(posterImageView);
        }
    }

}
