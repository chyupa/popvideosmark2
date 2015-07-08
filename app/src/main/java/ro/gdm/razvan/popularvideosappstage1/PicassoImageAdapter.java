package ro.gdm.razvan.popularvideosappstage1;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Razvan on 15-Jun-15.
 */
final class PicassoImageAdapter extends BaseAdapter {
    private final Context context;
    private final String[][] results;
    private final LayoutInflater mInflater;

    public PicassoImageAdapter(Context context, String[][] results){
        this.context = context;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.results = results;

    }

    @Override
    public int getCount() {
        return results.length;
    }

    @Override
    public String[] getItem(int position) {
        return results[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String[] movie = getItem(position);
        Picasso picasso = Picasso.with(context);
        View view;

        if( convertView == null ){
            view = mInflater.inflate(R.layout.grid_item, null);
        }else{
            view = convertView;
        }
        final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        view.setTag(movie[1]);
        SquaredImageView squaredImageView = (SquaredImageView)view.findViewById(R.id.grid_item);

        String url = "http://image.tmdb.org/t/p/w500" + movie[0];
        picasso.load(url)
                .tag(context)
                .into(squaredImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        return view;
    }
}
