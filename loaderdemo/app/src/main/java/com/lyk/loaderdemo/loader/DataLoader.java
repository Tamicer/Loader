package com.lyk.loaderdemo.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;

import com.lyk.loaderdemo.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

import static com.lyk.loaderdemo.dummy.DummyContent.*;

/**
 * Created by LIUYONGKUI on 2016-05-30.
 */
public class DataLoader extends AsyncTaskLoader<List<DummyItem>> {

    CancellationSignal mCancellationSignal;

    private List<DummyItem> mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    public List<DummyItem> loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }
        if (mData == null) {
            mData = new ArrayList<DummyItem>();
        }
        try {
            //模拟网络
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            // 模拟读数据
            for (int i = 1; i <= COUNT; i++) {
                addItem(new DummyItem(String.valueOf(i), "Item " + i, "this is Item" + i));

            }
        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
        mData = ITEMS;

        return mData;
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

   /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<DummyContent.DummyItem> data) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (data != null) {
                onReleaseResources(data);
            }
        }
        List<DummyItem> olds = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }

        if (olds != null) {
            onReleaseResources(olds);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {


        if (mData != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mData);
        }
        if (takeContentChanged() || mData == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();

    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<DummyItem> data) {
        super.onCanceled(data);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(data);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }

    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<DummyItem> data) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
        mData = null;
    }
}
