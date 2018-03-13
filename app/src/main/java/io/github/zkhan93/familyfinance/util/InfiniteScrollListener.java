package io.github.zkhan93.familyfinance.util;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AbsListView;

/**
 * Created by zeeshan on 1/29/18.
 */

public abstract class InfiniteScrollListener extends RecyclerView.OnScrollListener {
    public static final String TAG = InfiniteScrollListener.class.getSimpleName();
    // The minimum number of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 40;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private int firstVisibleItem = 0;
    private int previousTotal = -1;

    public InfiniteScrollListener() {
    }

    public InfiniteScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = recyclerView.getLayoutManager().getItemCount();
        firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
        if (previousTotal == -1)
            previousTotal = totalItemCount;
        if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            Util.Log.d(TAG, "end called visibleItemCount:%d, totalItemCount:%d, " +
                    "firstVisibleItem:%d", visibleItemCount, totalItemCount, firstVisibleItem);
            if (onLoadMore(totalItemCount)) {
                //started loading
                previousTotal = totalItemCount;
            } else {
                //still loading
            }
        }
    }

    // Defines the process for actually loading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    public abstract boolean onLoadMore(int totalItemsCount);

}
