package ro.gdm.razvan.popularvideosappstage1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MyPopularMovies movies_db = new MyPopularMovies(this);
        SQLiteDatabase db = movies_db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyPopularMovies.MyPopluarMoviesTables.KEY_MOVIE_MOVIE_ID, 123);
        values.put(MyPopularMovies.MyPopluarMoviesTables.KEY_MOVIE_NAME, "test");
        values.put(MyPopularMovies.MyPopluarMoviesTables.KEY_MOVIE_POSTER, "123");

        long new_row_id;
        new_row_id = db.insert(MyPopularMovies.MyPopluarMoviesTables.TABLE_MOVIES_NAME, null, values);
        Log.d("SQLITE3", String.valueOf(new_row_id));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /**
         * start settings activity
         */
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
