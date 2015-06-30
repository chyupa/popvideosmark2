package ro.gdm.razvan.popularvideosappstage1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Razvan on 30-Jun-15.
 */
public class MyPopularMovies extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "my_popular_movies_db.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + MyPopluarMoviesTables.TABLE_MOVIES_NAME + "(" + MyPopluarMoviesTables._ID + " INTEGER PRIMARY KEY" + COMMA_SEP + MyPopluarMoviesTables.KEY_MOVIE_NAME + TEXT_TYPE + COMMA_SEP + MyPopluarMoviesTables.KEY_MOVIE_MOVIE_ID + " INTEGER" + COMMA_SEP + MyPopluarMoviesTables.KEY_MOVIE_POSTER + TEXT_TYPE +")";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + MyPopluarMoviesTables.TABLE_MOVIES_NAME;


    public MyPopularMovies(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static abstract class MyPopluarMoviesTables implements BaseColumns{
        public static final String TABLE_MOVIES_NAME = "movies";
        public static final String KEY_MOVIE_NAME = "name";
        public static final String KEY_MOVIE_MOVIE_ID = "tmdb_id";
        public static final String KEY_MOVIE_POSTER = "poster";
    }
}
