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
public class ReviewItem implements Parcelable{

    private String mId;
    private String mUrl;
    private String mAuthor;
    private String mReview;

    public ReviewItem(Parcel in)
    {
        this.mId = in.readString();
        this.mUrl = in.readString();
        this.mAuthor = in.readString();
        this.mReview = in.readString();
    }

    public ReviewItem()
    {
        this.mId = "";
        this.mUrl = "";
        this.mAuthor = "";
        this.mReview = "";
    }

    public static final Creator<ReviewItem> CREATOR = new Creator<ReviewItem>() {
        @Override
        public ReviewItem createFromParcel(Parcel in) {
            return new ReviewItem(in);
        }

        @Override
        public ReviewItem[] newArray(int size) {
            return new ReviewItem[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }


    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getReview() {
        return mReview;
    }

    public void setReview(String review) {
        this.mReview = review;
    }

    @Override
        public String toString() {
            return this.mReview;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(this.mId);
        parcel.writeString(this.mUrl);
        parcel.writeString(this.mAuthor);
        parcel.writeString(this.mReview);
    }

    public static ArrayList<ReviewItem> getReviewsFromJson(String movieJsonStr) throws JSONException
    {
        // list of
        final String REVIEWS_RESULTS = "reviews";
        final String REVIEW_LIST = "results";
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        JSONObject movieQueryJson = new JSONObject(movieJsonStr);

        movieQueryJson = movieQueryJson.getJSONObject(REVIEWS_RESULTS);
        JSONArray movieArray = movieQueryJson.getJSONArray(REVIEW_LIST);

        ArrayList<ReviewItem> movieItemList = new ArrayList<ReviewItem>();
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieJson = movieArray.getJSONObject(i);
            ReviewItem item = new ReviewItem();

            item.setId(movieJson.getString(REVIEW_ID));
            item.setAuthor(movieJson.getString(REVIEW_AUTHOR));
            item.setReview(movieJson.getString(REVIEW_CONTENT));
            item.setUrl(movieJson.getString(REVIEW_URL));

            movieItemList.add(item);
        }

        return movieItemList;

    }

    public static String getReviewsFromJson(Context context, String movieJsonStr, int MovieId) throws JSONException
    {
        // list of

        final String REVIEWS_RESULTS = "reviews";
        final String REVIEW_LIST = "results";
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        JSONObject movieQueryJson = new JSONObject(movieJsonStr);
        movieQueryJson = movieQueryJson.getJSONObject(REVIEWS_RESULTS);

        //get the movie id here

        JSONArray reviewArray = movieQueryJson.getJSONArray(REVIEW_LIST);

        Vector<ContentValues> reviewVector = new Vector<ContentValues>(reviewArray.length());
        ArrayList<ReviewItem> reviewItemList = new ArrayList<ReviewItem>();
        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject movieJson = reviewArray.getJSONObject(i);
            ReviewItem item = new ReviewItem();

            item.setId(movieJson.getString(REVIEW_ID));
            item.setAuthor(movieJson.getString(REVIEW_AUTHOR));
            item.setReview(movieJson.getString(REVIEW_CONTENT));
            item.setUrl(movieJson.getString(REVIEW_URL));

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, MovieId);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, item.getId());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, item.getAuthor());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, item.getReview());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, item.getUrl());
            reviewVector.add(reviewValues);

            reviewItemList.add(item);
        }
        int insertResult = 0;
        if ( reviewVector.size() > 0 ) {
            ContentValues[] reviewCVArray = new ContentValues[reviewVector.size()];
            reviewVector.toArray(reviewCVArray);
            insertResult = context.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, reviewCVArray);
        }
        //return the trailerId for the movie to be added to the database
        return "";
    }
}

