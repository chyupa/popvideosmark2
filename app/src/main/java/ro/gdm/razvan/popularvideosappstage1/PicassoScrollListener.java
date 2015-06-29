package ro.gdm.razvan.popularvideosappstage1;

import android.content.Context;
import android.widget.AbsListView;

import com.squareup.picasso.Picasso;

/**
 * Created by Razvan on 15-Jun-15.
 */
public class PicassoScrollListener implements AbsListView.OnScrollListener {

    private final Context context;

    public PicassoScrollListener(Context context){
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        final Picasso picasso = Picasso.with(context);
        if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL){
            picasso.resumeTag(context);
        }else{
            picasso.pauseTag(context);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
