package ashitakalax.com.popularmovies.movie;

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

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public void setPlotSynopsis(String mPlotSynopsis) {
        this.mPlotSynopsis = mPlotSynopsis;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public void setUserRating(double mUserRating) {
        this.mUserRating = mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public ArrayList<TrailerItem> getTrailers()
    {
        return  this.mTrailers;
    }

    public void setTrailers(ArrayList<TrailerItem> trailers) {
        this.mTrailers = trailers;
    }

    public ArrayList<ReviewItem> getReviews() {
        return mReviews;
    }

    public void setReviews(ArrayList<ReviewItem> reviews) {
        this.mReviews = reviews;
    }

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

    public static List<MovieItem> getMoviesFromJson(String movieJsonStr) throws JSONException
    {
        // list of
        final String MOVIE_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_POSTER_URL = "poster_path";

        JSONObject movieQueryJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieQueryJson.getJSONArray(MOVIE_LIST);

        List<MovieItem> movieItemList = new ArrayList<MovieItem>();
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieJson = movieArray.getJSONObject(i);
            MovieItem item = new MovieItem();

            item.setId(movieJson.getInt(MOVIE_ID));
            item.setPlotSynopsis(movieJson.getString(MOVIE_OVERVIEW));
            item.setImageUrl(movieJson.getString(MOVIE_POSTER_URL));
            item.setOriginalTitle(movieJson.getString(MOVIE_TITLE));
            item.setUserRating(movieJson.getDouble(MOVIE_RATING));
            item.setReleaseDate(movieJson.getString(MOVIE_RELEASE_DATE));
            item.setOriginalTitle(movieJson.getString(MOVIE_TITLE));
            //this is already running on the background thread we technically skip on another
            //background task from running

            //foreach of these fetch the trailers and reviews for each movie

//            item.setReviews(MovieItem.queryReviewList(item.getId()));
//            MovieItem.queryMovieDetails(item);
            movieItemList.add(item);
        }

        return movieItemList;

    }

    public static boolean queryMovieDetails(MovieItem movieItem)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieDetailJsonStr = null;
        ArrayList<TrailerItem> trailerItemList = null;
        try
        {

            final String MOVIE_DB_BASE_URL ="http://api.themoviedb.org/3/movie/" + movieItem.getId() + "?";
            final String APPID_PARAM = "api_key";
            final String MOVIE_DATA_PARAM = "append_to_response";
            final String MOVIE_DATA_VALUE = "videos,reviews";


            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)//.OPEN_WEATHER_MAP_API_KEY)
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
                return false;
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
                return false;
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
            movieItem.setTrailers(TrailerItem.getTrailersFromJson(movieDetailJsonStr));
            movieItem.setReviews(ReviewItem.getReviewsFromJson(movieDetailJsonStr));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static ArrayList<TrailerItem> queryTrailerList(int movieId)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailerJsonStr = null;
        ArrayList<TrailerItem> trailerItemList = null;
        try
        {

            final String MOVIE_DB_BASE_URL ="http://api.themoviedb.org/3/movie/" + movieId + "?";
            final String APPID_PARAM = "api_key";
            final String MOVIE_DATA_PARAM = "append_to_response";
            final String MOVIE_DATA_VALUE = "videos,reviews";


            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)//.OPEN_WEATHER_MAP_API_KEY)
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
            trailerJsonStr = buffer.toString();

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
            trailerItemList = TrailerItem.getTrailersFromJson(trailerJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailerItemList;
    }

    private static ArrayList<ReviewItem> queryReviewList(int movieId) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewJsonStr = null;
        ArrayList<ReviewItem> reviewItemList = null;
        try
        {

            final String MOVIE_DB_BASE_URL ="http://api.themoviedb.org/3/movie/" + movieId + "/reviews?";
            final String APPID_PARAM = "api_key";


            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
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
            reviewJsonStr = buffer.toString();

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
            reviewItemList = ReviewItem.getReviewsFromJson(reviewJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewItemList;
    }

}

