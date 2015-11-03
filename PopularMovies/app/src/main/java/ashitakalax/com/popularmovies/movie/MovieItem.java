package ashitakalax.com.popularmovies.movie;

/**
 * todo add file Description
 *
 * @author Levi.Balling
 * @version 1
 * @since 10/31/2015
 */
public class MovieItem {
    private   int mId;

    private String mImageUrl;
    private String mOriginalTitle;
    private String mPlotSynopsis;
    private double mUserRating;//0-10
    private String mReleaseDate;//format "2014-10-23"




    public MovieItem(int id, String overview, String url)
    {
        this.mId = id;
        this.mPlotSynopsis = overview;
        this.mImageUrl = url;
        this.mOriginalTitle = "";
        this.mUserRating = 0.0;
        this.mReleaseDate  = "1900-1-1";

    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public void setmOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    public String getmPlotSynopsis() {
        return mPlotSynopsis;
    }

    public void setmPlotSynopsis(String mPlotSynopsis) {
        this.mPlotSynopsis = mPlotSynopsis;
    }

    public double getmUserRating() {
        return mUserRating;
    }

    public void setmUserRating(double mUserRating) {
        this.mUserRating = mUserRating;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    @Override
        public String toString() {
            return this.mPlotSynopsis;
        }
}

