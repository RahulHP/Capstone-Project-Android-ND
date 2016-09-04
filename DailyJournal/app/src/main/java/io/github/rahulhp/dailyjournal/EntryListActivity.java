package io.github.rahulhp.dailyjournal;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class EntryListActivity extends FirebaseActivity {
    String queryUID = null;
    String queryName = null;
    String queryURL;
    Query mFirebaseQuery;

    Calendar cal = Calendar.getInstance();
    int cYear = cal.get(Calendar.YEAR);
    int cMonth = cal.get(Calendar.MONTH);

    RecyclerView mEntryRecyclerView;
    FirebaseRecyclerAdapter<JournalEntry,EntryViewHolder> mFirebaseAdapter;

    ProgressBar mProgressBar;
    TextView emptyListView;
    String emptyListString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        getQueryUserDetails();
        queryURL = getString(R.string.database_user_posts_url)+"/"+queryUID+"/"+cYear+"/"+cMonth;
        getDatabaseQuery();

        mFirebaseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (!dataSnapshot.exists()){
                    emptyListView = (TextView) findViewById(R.id.empty_list_textview);
                    setEmptyListViewText();
                    emptyListView.setText(emptyListString);
                    emptyListView.setVisibility(TextView.VISIBLE);
                } else {
                    createAdapter();
                    createRecyclerView();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    void createAdapter(){
        mFirebaseAdapter = new FirebaseRecyclerAdapter<JournalEntry, EntryViewHolder>(
                JournalEntry.class,
                R.layout.journal_entry,
                EntryViewHolder.class,
                mFirebaseQuery
        ) {

            @Override
            protected void populateViewHolder(EntryViewHolder viewHolder, JournalEntry model, int position) {
                Calendar mDate = Calendar.getInstance();
                mDate.setTime(model.getmDate());
                String t = new SimpleDateFormat(getString(R.string.journal_entry_date_format), Locale.ENGLISH).format(mDate.getTime());
                viewHolder.dateView.setText(t);
                viewHolder.entryView.setText(model.getmEntry());
            }

        };
    }

    void createRecyclerView(){
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        mEntryRecyclerView = (RecyclerView) findViewById(R.id.entryRecyclerView);
        mEntryRecyclerView.setHasFixedSize(true);
        mEntryRecyclerView.setLayoutManager(mLinearLayoutManager);
        mEntryRecyclerView.setAdapter(mFirebaseAdapter);
    }


    protected void setEmptyListViewText(){
    }

    protected void getQueryUserDetails(){
    }

    protected void getDatabaseQuery(){
    }
}
