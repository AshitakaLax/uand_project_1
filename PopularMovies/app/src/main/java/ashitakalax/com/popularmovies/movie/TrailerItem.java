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

    public static ArrayList<TrailerItem> getTrailersFromJson(String movieJsonStr) throws JSONException
    {
        // list of
        final String TRAILER_LIST = "results";
        final String TRAILER_ID = "id";
        final String TRAILER_TITLE = "name";
        final String TRAILER_URL_KEY = "key";
        final String TRAILER_HOST_SITE = "site";

        JSONObject movieQueryJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieQueryJson.getJSONArray(TRAILER_LIST);

        ArrayList<TrailerItem> movieItemList = new ArrayList<TrailerItem>();
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieJson = movieArray.getJSONObject(i);
            TrailerItem item = new TrailerItem();

            item.setId(movieJson.getString(TRAILER_ID));
            item.setTitle(movieJson.getString(TRAILER_TITLE));
            item.setUrl(movieJson.getString(TRAILER_URL_KEY));
            item.setSite(movieJson.getString(TRAILER_HOST_SITE));
            movieItemList.add(item);
        }

        return movieItemList;

    }
}

