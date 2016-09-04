package io.github.rahulhp.dailyjournal;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;



public class MainActivity extends EntryListActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.new_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),NewEntryActivity.class));
            }
        });
    }

    @Override
    protected void getQueryUserDetails() {
        queryUID = mUID;
        queryName =mUsername;
    }

    @Override
    protected void getDatabaseQuery() {
        mFirebaseQuery = mFirebaseDatabase.getReference(queryURL);
    }


    @Override
    protected void setEmptyListViewText() {
        emptyListString = getString(R.string.no_posts_user_self);
    }

}
