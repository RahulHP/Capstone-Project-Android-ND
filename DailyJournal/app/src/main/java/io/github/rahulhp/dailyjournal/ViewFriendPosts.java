package io.github.rahulhp.dailyjournal;


import android.os.Bundle;
import android.view.View;


public class ViewFriendPosts extends EntryListActivity {


    @Override
    protected void getQueryUserDetails() {
        queryUID = getIntent().getExtras().getString(getString(R.string.intent_extra_user_id));
        queryName = getIntent().getExtras().getString(getString(R.string.intent_extra_user_name));
    }

    @Override
    protected void getDatabaseQuery() {
        mFirebaseQuery = mFirebaseDatabase.getReference(queryURL)
                .orderByChild(getString(R.string.database_user_post_privacy_boolean)).equalTo(true);
    }

    @Override
    protected void setEmptyListViewText() {
        emptyListString = getString(R.string.no_posts_user_friend);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle(queryName);
        findViewById(R.id.new_entry).setVisibility(View.GONE);
    }

}
