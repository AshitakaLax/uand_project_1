package ashitakalax.com.popularmovies.movie;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public MovieItem(Parcel in)
    {
        this.mOriginalTitle = in.readString();
        this.mImageUrl = in.readString();
        this.mPlotSynopsis = in.readString();
        this.mReleaseDate = in.readString();
        this.mId = in.readInt();
        this.mUserRating = in.readDouble();
    }

    public MovieItem()
    {
        this.mId = 0;
        this.mPlotSynopsis = "";
        this.mImageUrl = "";
        this.mOriginalTitle = "";
        this.mUserRating = 0.0;
        this.mReleaseDate  = "1900-1-1";

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

            movieItemList.add(item);
        }

        return movieItemList;

    }
}

