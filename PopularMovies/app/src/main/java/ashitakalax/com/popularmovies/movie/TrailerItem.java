package ashitakalax.com.popularmovies.movie;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 10/31/2015
 */
public class TrailerItem implements Parcelable{

    private String mId;//longer s
    private String mUrl;//url to stream movie
    private String mTitle;//first look, official trailer, etc...
    private String mSite;

    public TrailerItem(Parcel in)
    {
        this.mId = in.readString();
        this.mUrl = in.readString();
        this.mTitle = in.readString();
        this.mSite = in.readString();
    }

    public TrailerItem()
    {
        this.mId = "";
        this.mUrl = "";
        this.mTitle = "";
        this.mSite = "";

    }

    public static final Creator<TrailerItem> CREATOR = new Creator<TrailerItem>() {
        @Override
        public TrailerItem createFromParcel(Parcel in) {
            return new TrailerItem(in);
        }

        @Override
        public TrailerItem[] newArray(int size) {
            return new TrailerItem[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String site) {
        this.mSite = site;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
        public String toString() {
            return this.mTitle;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mId);
        parcel.writeString(this.mUrl);
        parcel.writeString(this.mTitle);
        parcel.writeString(this.mSite);
    }

//    public static ArrayList<TrailerItem> getTrailersFromJson(String movieJsonStr) throws JSONException
    public static String getTrailersFromJson(Context context, String movieJsonStr, int MovieId) throws JSONException
    {
        // list of
        final String VIDEOS_LIST = "videos";
        final String TRAILER_LIST = "results";
        final String TRAILER_ID = "id";
        final String TRAILER_TITLE = "name";
        final String TRAILER_URL_KEY = "key";
        final String TRAILER_HOST_SITE = "site";

        JSONObject movieQueryJson = new JSONObject(movieJsonStr);
        movieQueryJson = movieQueryJson.getJSONObject(VIDEOS_LIST);

        //get the movie id here

        JSONArray trailerArray = movieQueryJson.getJSONArray(TRAILER_LIST);

        Vector<ContentValues> trailerVector = new Vector<ContentValues>(trailerArray.length());
        ArrayList<TrailerItem> movieItemList = new ArrayList<TrailerItem>();
        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject movieJson = trailerArray.getJSONObject(i);
            TrailerItem item = new TrailerItem();

            item.setId(movieJson.getString(TRAILER_ID));
            item.setTitle(movieJson.getString(TRAILER_TITLE));
            item.setUrl(movieJson.getString(TRAILER_URL_KEY));
            item.setSite(movieJson.getString(TRAILER_HOST_SITE));

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, MovieId);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, item.getId());
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE, item.getTitle());
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_URL, item.getUrl());
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_HOST, item.getSite());
            trailerVector.add(trailerValues);


            movieItemList.add(item);
        }
        int insertResult = 0;
        if ( trailerVector.size() > 0 ) {
            ContentValues[] trailerCVArray = new ContentValues[trailerVector.size()];
            trailerVector.toArray(trailerCVArray);
            insertResult = context.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, trailerCVArray);
        }
        //return the trailerId for the movie to be added to the database
        return "";

    }
}

