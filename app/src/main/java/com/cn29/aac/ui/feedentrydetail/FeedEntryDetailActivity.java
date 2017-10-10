package com.cn29.aac.ui.feedentrydetail;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import com.cn29.aac.R;
import com.cn29.aac.databinding.ActivityFeedEntryDetailBinding;
import com.cn29.aac.databinding.DialogFeedentryBinding;
import com.cn29.aac.repo.feedentry.FeedEntry;
import com.cn29.aac.ui.feedentrydetail.vm.FeedEntryDetailViewModel;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;


public class FeedEntryDetailActivity extends DaggerAppCompatActivity {

  public static final String EXTRA_POSITION = "position";
  @Inject
  FeedEntryDetailViewModel viewModel;

  @Inject
  int uid;

  @Inject
  FeedEntry feedEntry;

  private ActivityFeedEntryDetailBinding binding;


  @SuppressLint("StaticFieldLeak")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    viewModel.getFeedEntry(uid).observe(this, feedEntry -> {
      this.feedEntry = feedEntry;
      binding.setFeedEntry(this.feedEntry);
    });
    binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_entry_detail);
    setSupportActionBar(binding.toolbar);
    binding.setFeedEntry(new FeedEntry("Testing Feed Title", "Testing Feed Sub Title"));
    binding.setImageUrl("http://i.imgur.com/DvpvklR.png");
    binding.collapsingToolbar.setTitle("Feed Entry Title");
    new ProgressBar(FeedEntryDetailActivity.this).setVisibility(View.GONE);
    binding.fab.setOnClickListener(view -> {
      //insert sample data by button click
      final DialogFeedentryBinding dialogFeedEntryBinding = DataBindingUtil
          .inflate(LayoutInflater.from(this), R.layout.dialog_feedentry, null, false);
      dialogFeedEntryBinding.setFeedEntry(this.feedEntry);
      new Builder(FeedEntryDetailActivity.this)
          .setTitle("Create a new Feed Entry")
          .setView(dialogFeedEntryBinding.getRoot())
          .setPositiveButton("Submit", (dialogInterface, i) -> {
            FeedEntry inputFeedEntry = dialogFeedEntryBinding.getFeedEntry();
            Boolean[] validations = new Boolean[]{
                dialogFeedEntryBinding.imageUrlValidation.isErrorEnabled(),
                dialogFeedEntryBinding.titleValidation.isErrorEnabled(),
                dialogFeedEntryBinding.subTitleValidation.isErrorEnabled()
            };
            boolean isValid = true;
            for (Boolean validation : validations) {
              if (validation) {
                isValid = false;
              }
            }
            if (isValid) {
              Single.create(subscriber -> viewModel.update(inputFeedEntry))
                  .subscribeOn(Schedulers.newThread())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(result -> binding.setFeedEntry(inputFeedEntry));
              dialogInterface.dismiss();
            }
          }).create().show();
    });
  }

}
