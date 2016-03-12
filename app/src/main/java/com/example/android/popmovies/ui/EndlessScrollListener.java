package com.example.android.popmovies.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * Created by sheshloksamal on 9/11/15.
 * @link https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    // The minimum number of items to have below your current scroll position before loading more
    // Can be 0-4 in this case. Let's keep 4 for smoother scrolling experience in case of
    // poor connectivity
    private int visibleThreshold = 4;

    // The current offset index of data you have loaded
    private int currentPage = 0;

    // The total number of items in the data set (which is bound to adapter) after the last load
    private int previousTotalItemCount = 0;

    // True if we are still waiting for the last set of data to load
    private boolean loading = true;

    // Sets the starting page Index
    private int startingPageIndex = 0;

    public EndlessScrollListener() {

    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    /**
     * In short, this gives me a complete context of my position while scrolling.
     * From parent class documentation.
     * Callback method to be invoked when the list/grid has been scrolled. This will be called after
     * the scroll has completed.
     * @param view              The view whose scroll state is being reported
     * @param firstVisibleItem  The index of the first visible cell (ignore if visibleItemCount == 0)
     * @param visibleItemCount  The number of visible cells
     * @param totalItemCount    The number of items in the adapter
     */
    @Override
    public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        // If the total item count is zero and the previous isn't, assume the list is invalidated
        // and should be reset back to its initial state.
        if (totalItemCount < this.previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { // Can we do away with this if bloack
                this.loading = true;
            }
        }

        // If it's still loading, we check to see if the dataset count has changed, if so we
        // conclude it has finished loading and update the current page number and total item count
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn't currently loading, we check to see if we have breached the visibleThreshold
        // and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data
        // Note every-time you scroll, the first visible item index will change to the index of top
        // left view. So, if you are scrolling down a grid view (with a data set of 20 items and
        // a visible threshold of 4), the below conditional statement when you reach the last page
        // would evaluate to following:
        // (20 - 4) <= (16 + 0-4) == True // Time for pagination, Yayyy !!!
        if(!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            loading = onLoadMore(currentPage + 1, totalItemCount);
        }

    }

    /**
     * Note: Abstract method cannot be defined in a non-abstract class. Hence we have to make
     * the above class abstract
     * Defines the process for actually loading more data based on page
     * @param page              Page number for say, API call
     * @param totalItemsCount   Total items to be requested
     * @return
     */
    public abstract boolean onLoadMore(int page, int totalItemsCount);

    /**
     * From parent class documentation.
     * Callback method to be invoked while the list view or grid view is being scrolled. If the
     * view is being scrolled, this method will be called before the next frame of the scroll is
     * rendered. In particular it will be called before any calls to
     * {@link  android.widget.Adapter#getView(int, View, ViewGroup)}.
     * @param view          The view whose scroll state is being reported
     * @param scrollState   The current scroll state. One of
     *                      {@link #SCROLL_STATE_TOUCH_SCROLL} or {@link #SCROLL_STATE_IDLE}
     */
    @Override
    public void onScrollStateChanged (AbsListView view, int scrollState) {
        // Don't take any action on changed
    }



}
