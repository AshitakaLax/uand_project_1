package ashitakalax.com.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
    static final int COL_ID = 0;

    //todo add the sharing later
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_VOTE = 5;
    static final int COL_MOVIE_RELEASE_DATE = 6;
    static final int COL_MOVIE_IS_FAVORITE = 7;


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
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_IS_FAVORITE,
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
        return;
//        this.mItem  = this.getArguments().getParcelable(ARG_MOVIE_BUNDLE_ID);
//
//        if(this.mItem == null && savedInstanceState == null) {
//            mItem = new MovieItem();
//        }
//
//        Activity activity = this.getActivity();
//        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//        if (appBarLayout != null) {
//            appBarLayout.setTitle("Movie Detail");
//        }
    }

    @Override
    public void onResume() {

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reviewIds = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        mReviewLayout = (LinearLayout)rootView.findViewById(R.id.reviewsLayout);
        mTrailerListView = (ListView)rootView.findViewById(R.id.TrailersListView);

        this.favoriteButton = (Button)rootView.findViewById(R.id.favoriteButton);
        this.favoriteButton.setOnClickListener(this);
        //mReviewListView = (ListView)rootView.findViewById(R.id.ReviewListView);

        this.mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        mTrailerListView.setAdapter(mTrailerAdapter);
        TextView reviewTextView = (TextView) this.mReviewLayout.findViewById(R.id.reviewLabelTextView);
        reviewTextView.setText("");
        //this.mReviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        //mReviewListView.setAdapter(this.mReviewAdapter);


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

        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, this.mMovieId);
        boolean updatedFavoriteValue = !isMovieFavorite();

        movieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, updatedFavoriteValue);
        this.getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, movieValues, "movId="+this.mMovieId, null);

        return;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "In OnCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        Uri movieUri = intent.getData();
        this.mMovieId = MovieContract.MovieEntry.getMovieIdFromUri(movieUri);


        if (id == DETAIL_LOADER) {
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
//        if (!data.moveToFirst()) {
//            return;
//        }
//
//        LinearLayout reviewLayout = (LinearLayout) getView().findViewById(R.id.reviewsLayout);
//                    int i = 0;
//        do {
//            View  custom = LayoutInflater.from(getContext()).inflate(R.layout.review_detail,reviewLayout, false);
//            TextView reviewTextView = (TextView) custom.findViewById(R.id.reviewLabelTextView);
//
//            String reviewStr = data.getString(0);//todo update index
//            reviewTextView.setText("Review: " + reviewStr );
////            custom.setTag(review);
//            reviewLayout.addView(custom);
//        }while(data.moveToNext());

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

        int rowId = data.getInt(COL_ID);
        int boolValue = data.getInt(COL_MOVIE_IS_FAVORITE);
        boolean movieIsFavorite = boolValue>0;

        //get the data from the cursor
        TextView titleTextView = (TextView) getView().findViewById(R.id.titleTextView);
        TextView releaseDateTextView = (TextView) getView().findViewById(R.id.releaseDateTextView);
        TextView movieTimeLengthTextView = (TextView) getView().findViewById(R.id.movieTimeLengthTextView);
        TextView movieRatingTextView = (TextView) getView().findViewById(R.id.movieRatingTextView);
        TextView movieOverviewTextView = (TextView) getView().findViewById(R.id.movieOverviewTextView);
        ImageView posterImageView = (ImageView) getView().findViewById(R.id.moviePosterImageView);
        this.favoriteButton = (Button) getView().findViewById(R.id.favoriteButton);
        LinearLayout trailerLayout = (LinearLayout) getView().findViewById(R.id.TrailersLayout);
        LinearLayout reviewLayout = (LinearLayout) getView().findViewById(R.id.reviewsLayout);

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

        if(this.favoriteButton != null)
        {
            this.updateFavoriteButtonText(movieIsFavorite);

        }

    }

}
