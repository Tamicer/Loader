package com.lyk.loaderdemo;



import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.lyk.loaderdemo.loader.DataLoader;
import com.lyk.loaderdemo.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private SimpleRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        if (findViewById(R.id.item_detail_container) != null) {

            mTwoPane = true;
        }
        getLoaderManager().initLoader(0, null, new DataLoaderCallback());
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
    }

    public class SimpleRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder> {

        private List<DummyItem> mLists = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            public DummyItem mItem;

            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }

        public SimpleRecyclerViewAdapter(List<DummyItem> items) {
            mLists = items;
        }

        public void setLists(List<DummyItem> mLists) {
            this.mLists = mLists;
        }

        public SimpleRecyclerViewAdapter() {

        }

        public List<DummyItem> getLists() {
            return mLists;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mLists.get(position);
            holder.mIdView.setText(mLists.get(position).id);
            holder.mContentView.setText(mLists.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    }
                    else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mLists.size();
        }
    }

    // LoaderCallbacks
    class DataLoaderCallback  implements android.app.LoaderManager.LoaderCallbacks<List<DummyItem>> {


        @Override
        public Loader<List<DummyItem>> onCreateLoader(int id, Bundle args) {
            Toast.makeText(ItemListActivity.this, "Loading... ", Toast.LENGTH_SHORT).show();
            return new DataLoader(ItemListActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<List<DummyItem>> loader, List<DummyItem> data) {
            Toast.makeText(ItemListActivity.this,"LoadFinish! ",Toast.LENGTH_SHORT).show();
            adapter.setLists(data);
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onLoaderReset(Loader<List<DummyItem>> loader) {
            adapter.setLists(null);
        }

    }

}
