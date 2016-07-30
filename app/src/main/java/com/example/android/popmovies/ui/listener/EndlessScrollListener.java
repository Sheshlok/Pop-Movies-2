package com.example.android.popmovies.ui.listener;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by sheshloksamal on 9/11/15.
 * @see  <a href="https://guides.codepath.com/android/Endless-Scrolling-With-AdapterViews">
 *     CodePath's Guide</a>
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    // The minimum number of items to have below your current scroll position before loading more
    // Can be 0-4 in this case. Let's keep 5 for smoother scrolling experience in case of
    // poor connectivity
    private int visibleThreshold = 5;

    // The current offset index of data you have loaded
    private int currentPage = 0;

    // The total number of items in the data set after the last load
    private int previousTotalItemCount = 0;

    // True if we are still waiting for the last set of data to load
    private boolean loading = true;

    // Sets the starting page Index
    private int startingPageIndex = 0;

    public EndlessScrollListener() {}

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    public interface OnLoadMoreCallback {
        void onLoadMore(int page, int totalItemsCount);
    }

    private OnLoadMoreCallback mOnLoadMoreCallback;

    /* Similar to onAttach in case of Fragment-Activity coupling */
    public EndlessScrollListener setOnLoadMoreCallback(OnLoadMoreCallback onLoadMoreCallback) {
        this.mOnLoadMoreCallback = onLoadMoreCallback;
        return this;
    }

    /*
     * This happens many times a second during a scroll, so be wary of the code you place here.
     * We are given a few useful properties to help us work out if we need to load more data, but
     * first we check if we are waiting for the previous load to finish.
     */

    @Override public abstract void onScrolled(RecyclerView recyclerView, int dx, int dy);

    public void onScrolled(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        // If the total item count is zero and the previous isn't, assume the list is invalidated
        // and should be reset back to its initial state.
        if (totalItemCount < this.previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // If it's still loading, we check to see if the data set count has changed, if so we
        // conclude it has finished loading and update the current page number and total item count
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn't currently loading, we check to see if we have breached the visibleThreshold
        // and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // FROM PARENT DOC: This is the callback method to be invoked, when the recyclerView has been
        // scrolled. This will be called after the scroll has completed. dx is the amount of horizontal
        // scroll, and dy the amount of vertical scroll
        // Note every-time you scroll, the first visible item index will change to the index of top
        // left view. So, if you are scrolling down a grid view (with a data set of 20 items and
        // a visible threshold of 5), the below conditional statement when you reach:
        // Last page on the screen, n: (20 - 4) <= (17 + 5): True
        // n - 1: (20 - 4) <= (13 + 5): True --- RELOAD MORE DATA on 2nd last page
        // n - 2: (20 - 4) <= (9 + 5): False and false henceforth till the 1st page
        if(!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            onLoadMore(currentPage + 1, totalItemCount);
            loading = true;
        }

    }

    /*
        Defines the process for actually loading more data based on page
     */
    public void onLoadMore(int page, int totalItemsCount) {
        if (mOnLoadMoreCallback != null) {
            mOnLoadMoreCallback.onLoadMore(page, totalItemsCount);
        }
    }

    public static EndlessScrollListener fromGridLayoutManager (
            @NonNull final GridLayoutManager layoutManager, int visibleThreshold, int startPage) {

        return new EndlessScrollListener(visibleThreshold, startPage) {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // If the amount of vertical scroll is zero or less than zero, i.e. scrolling up or
                // not at all, return, as this won't change the number of items. So, basically the
                // newly instantiated ESL says "If you are scrolling upwards, don't expect anything"
                // but "if you are going down, i will do the job for you"

                if (dy <= 0) return;

                final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                final int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                final int totalItemCount = layoutManager.getItemCount();

                onScrolled(firstVisibleItem, lastVisibleItem - firstVisibleItem + 1, totalItemCount);

            }
        };
    }
}
