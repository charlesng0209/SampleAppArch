package com.cn29.aac.ui.masterdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cn29.aac.R;
import com.cn29.aac.repo.bean.DummyContent;
import com.cn29.aac.repo.bean.DummyContent.DummyItem;
import com.cn29.aac.ui.masterdetail.vm.SimpleMasterDetailShareViewModel;
import dagger.android.support.DaggerAppCompatActivity;
import java.util.List;
import javax.inject.Inject;


/**
 * An activity representing a list of FeedEntrys. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SimpleDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * https://developer.android.com/topic/libraries/architecture/viewmodel.html
 */
public class SimpleListActivity extends DaggerAppCompatActivity {

  @Inject
  SimpleMasterDetailShareViewModel masterDetailShareViewModel;
  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private boolean mTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_list);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle(getTitle());
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(view -> {

    });
    View recyclerView = findViewById(R.id.feedentry_list);
    assert recyclerView != null;
    setupRecyclerView((RecyclerView) recyclerView);

    if (findViewById(R.id.feedentry_detail_container) != null) {
      // The detail container view will be present only in the
      // large-screen layouts (res/values-w900dp).
      // If this view is present, then the
      // activity should be in two-pane mode.
      mTwoPane = true;
    }

  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
  }

  public class SimpleItemRecyclerViewAdapter
      extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;

    public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
      mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.simple_list_content, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
      holder.mItem = mValues.get(position);
      holder.mIdView.setText(mValues.get(position).id);
      holder.mContentView.setText(mValues.get(position).content);
      holder.mView.setOnClickListener(v -> {
        masterDetailShareViewModel.selectFeedEntry(holder.mItem.id);
        if (mTwoPane) {
          Bundle arguments = new Bundle();
          arguments.putString(SimpleDetailFragment.ARG_ITEM_ID, holder.mItem.id);
          SimpleDetailFragment fragment = new SimpleDetailFragment();
          fragment.setArguments(arguments);
          getSupportFragmentManager().beginTransaction()
              .replace(R.id.feedentry_detail_container, fragment)
              .commit();
        } else {

          Context context = v.getContext();
          Intent intent = new Intent(context, SimpleDetailActivity.class);
          intent.putExtra(SimpleDetailFragment.ARG_ITEM_ID, holder.mItem.id);
          context.startActivity(intent);
        }
      });
    }

    @Override
    public int getItemCount() {
      return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

      public final View mView;
      public final TextView mIdView;
      public final TextView mContentView;
      public DummyContent.DummyItem mItem;

      public ViewHolder(View view) {
        super(view);
        mView = view;
        mIdView = view.findViewById(R.id.id);
        mContentView = view.findViewById(R.id.content);
      }

      @Override
      public String toString() {
        return super.toString() + " '" + mContentView.getText() + "'";
      }
    }
  }
}
