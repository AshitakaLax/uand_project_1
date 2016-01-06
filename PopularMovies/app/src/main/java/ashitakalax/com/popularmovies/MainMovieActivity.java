package ashitakalax.com.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Levi Balling on 1/2/2016.
 */
public class MainMovieActivity extends AppCompatActivity {

    private final String LOG_TAG = MainMovieActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new MovieFragment()).commit();
        }
    }




}
