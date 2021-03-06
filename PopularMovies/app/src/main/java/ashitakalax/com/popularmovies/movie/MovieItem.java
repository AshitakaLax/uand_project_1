package ashitakalax.com.popularmovies.movie;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ashitakalax.com.popularmovies.BuildConfig;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 10/31/2015
 */
public class MovieItem implements Parcelable{
    private   int mId;

    private String mImageUrl;
    private String mOriginalTitle;
    private String mPlotSynopsis;
    private double mUserRating;//0-10
    private String mReleaseDate;//format "2014-10-23"

    private ArrayList<TrailerItem> mTrailers;
    private ArrayList<ReviewItem> mReviews;



    public MovieItem(Parcel in)
    {
        this.mOriginalTitle = in.readString();
        this.mImageUrl = in.readString();
        this.mPlotSynopsis = in.readString();
        this.mReleaseDate = in.readString();
        this.mId = in.readInt();
        this.mUserRating = in.readDouble();

        this.mTrailers = new ArrayList<TrailerItem>();
        this.mReviews = new ArrayList<ReviewItem>();
        in.readTypedList(this.mTrailers, TrailerItem.CREATOR);
        in.readTypedList(this.mReviews, ReviewItem.CREATOR);

//        in.readTypedList(this.mTrailers, TrailerItem.;TrailerItem.getClass().CREATOR);
//        in.readTypedList(this.mReviews, ReviewItem.CREATOR);
    }

    public MovieItem()
    {
        this.mId = 0;
        this.mPlotSynopsis = "";
        this.mImageUrl = "";
        this.mOriginalTitle = "";
        this.mUserRating = 0.0;
        this.mReleaseDate  = "1900-1-1";
        this.mTrailers = new ArrayList<TrailerItem>();
        this.mReviews = new ArrayList<ReviewItem>();

    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    @Override
        public String toString() {
            return this.mPlotSynopsis;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mOriginalTitle);
        parcel.writeString(this.mImageUrl);
        parcel.writeString(this.mPlotSynopsis);
        parcel.writeString(this.mReleaseDate);
        parcel.writeInt(this.mId);
        parcel.writeDouble(this.mUserRating);
        parcel.writeTypedList(this.mTrailers);
        parcel.writeTypedList(this.mReviews);
        //todo add write to array of trailer and review objects

    }

    public static List<Integer> getMoviesFromJson(Context context, String movieJsonStr) throws JSONException
    {
        // list of
        final String MOVIE_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_POSTER_URL = "poster_path";
        final String MOVIE_IS_FAVORITE = "is_favorite";
        final String MOVIE_POPULARITY= "popularity";

        JSONObject movieQueryJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieQueryJson.getJSONArray(MOVIE_LIST);

        List<Integer> movieItemList = new ArrayList<Integer>();

        //it would be best to make the reviews and the trailers nullable
        //then to populate them after
        Vector<ContentValues> movieVector = new Vector<ContentValues>(movieArray.length());


        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieJson = movieArray.getJSONObject(i);
           // MovieItem item = new MovieItem();
            //here we don't want to add it to the database yet, we want to wait till we get both the reviews
            //and the trailers for the movie.
            //but we do want to do a check here to see if we have the reviews and the trailers
            //in the database

            ContentValues movieValues = new ContentValues();

            movieItemList.add(movieJson.getInt(MOVIE_ID));

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieItemList.get(movieItemList.size()-1));//item.getId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieJson.getString(MOVIE_TITLE));//item.getOriginalTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieJson.getString(MOVIE_OVERVIEW));//item.getPlotSynopsis());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieJson.getDouble(MOVIE_RATING));//item.getUserRating());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieJson.getString(MOVIE_RELEASE_DATE));//item.getReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, movieJson.getString(MOVIE_POSTER_URL));//item.getImageUrl());
            movieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, false);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movieJson.getString(MOVIE_POPULARITY));


            movieVector.add(movieValues);
            //this is already running on the background thread we technically skip on another
            //background task from running

            //foreach of these fetch the trailers and reviews for each movie

//            item.setReviews(MovieItem.queryReviewList(item.getId()));
//            MovieItem.queryMovieDetails(item);
           // movieItemList.add(item);
        }
        int insertResult = 0;
        if ( movieVector.size() > 0 ) {
            ContentValues[] movieCVArray = new ContentValues[movieVector.size()];
            movieVector.toArray(movieCVArray);
            insertResult = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieCVArray);
        }

        return movieItemList;

    }


    public static MovieItem queryMovieDetails(Context context, int movieId)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieDetailJsonStr = null;

        //we will want 2 vectors one for trailers and one for reviews

        try
        {

            final String MOVIE_DB_BASE_URL ="http://api.themoviedb.org/3/movie/" + movieId + "?";
            final String APPID_PARAM = "api_key";
            final String MOVIE_DATA_PARAM = "append_to_response";
            final String MOVIE_DATA_VALUE = "videos,reviews";


            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .appendQueryParameter(MOVIE_DATA_PARAM, MOVIE_DATA_VALUE)
                    .build();

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
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
                return null;
            }
            movieDetailJsonStr = buffer.toString();

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

            String resultStr = TrailerItem.getTrailersFromJson(context, movieDetailJsonStr, movieId);
            resultStr = ReviewItem.getReviewsFromJson(context, movieDetailJsonStr, movieId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

