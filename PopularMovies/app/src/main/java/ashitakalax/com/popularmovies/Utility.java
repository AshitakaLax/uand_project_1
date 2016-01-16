package ashitakalax.com.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by balling on 1/15/2016.
 */
public class Utility {


    public static final String MOVIE_SHARE_PREF_FILE = "movie_shared_preferences";
    public static final String SORT_BY_POPULARITY = "sort_by_popularity";
    public static final String SORT_BY_RATING = "sort_by_rating";
    public static final String SORT_BY_FAVORITES = "sort_by_favorites";
    private static final String SORT_TYPE = "sort_type";

    public static String getPreferredSortingType(Context context) {

        SharedPreferences prefs = context.getSharedPreferences(MOVIE_SHARE_PREF_FILE, Context.MODE_PRIVATE);
         return prefs.getString(context.getString(R.string.pref_sort_type),
                context.getString(R.string.pref_sort_default_type));
    }


    public static void setPreferredSortingType(Context context, String sortType) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MOVIE_SHARE_PREF_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(SORT_TYPE, sortType);
        editor.apply();
    }

}
