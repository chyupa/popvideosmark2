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
    private final List<String> urls = new ArrayList<String>();
    private final LayoutInflater mInflater;

    public PicassoImageAdapter(Context context, String[] poster_paths){
        this.context = context;

        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(String s : poster_paths){
            String uri = "http://image.tmdb.org/t/p/w500" + s;
            urls.add(uri);
        }
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String url = getItem(position);
        Picasso picasso = Picasso.with(context);
        picasso.setDebugging(true);
        View view;

        if( convertView == null ){
            view = mInflater.inflate(R.layout.grid_item, null);
        }else{
            view = convertView;
        }
        final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        SquaredImageView squaredImageView = (SquaredImageView)view.findViewById(R.id.grid_item);
        picasso.load(url)
                .tag(context).
                into(squaredImageView, new Callback() {
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
