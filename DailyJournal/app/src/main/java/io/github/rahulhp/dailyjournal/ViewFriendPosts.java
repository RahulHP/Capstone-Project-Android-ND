package io.github.rahulhp.dailyjournal;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewFriendPosts extends FirebaseActivity {
    String friendid;
    String friendName;
    public static class EntryViewHolder extends RecyclerView.ViewHolder{
        public TextView entryView;
        public TextView dateView;
        public EntryViewHolder(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.journal_entry_date);
            entryView = (TextView) itemView.findViewById(R.id.journal_entry_text);
        }
    }
    private RecyclerView mEntryRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<JournalEntry,EntryViewHolder> mFirebaseAdapter;

    Calendar cal = Calendar.getInstance();
    int cYear = cal.get(Calendar.YEAR);
    int cMonth = cal.get(Calendar.MONTH);
    int cDay = cal.get(Calendar.DAY_OF_MONTH);
    Query mFirebaseQuery;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend_posts);
        friendid = getIntent().getExtras().getString("FRIEND_ID");
        friendName = getIntent().getExtras().getString("FRIEND_NAME");
        getSupportActionBar().setTitle(friendName);
        Log.e(TAG, "onCreate: New Activity"+friendid);

        String data_string = "user-data"+"/"+friendid.toLowerCase()+"/"+cYear+"/"+"7";
        Log.e(TAG, "onCreate: "+data_string);
        mFirebaseQuery = FirebaseDatabase.getInstance().getReference(data_string)
                .orderByChild("mPrivate").equalTo(true);
        mFirebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                            Log.e(TAG, "onDataChange: Nope");
                        else
                            createRecyclerView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
    void createRecyclerView(){
        mEntryRecyclerView = (RecyclerView) findViewById(R.id.friend_entryRecyclerView);
        mEntryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mEntryRecyclerView.setLayoutManager(mLinearLayoutManager);

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
                String t = new SimpleDateFormat("dd-MMM-yy").format(mDate.getTime());
                viewHolder.dateView.setText(t);
                viewHolder.entryView.setText(model.getmEntry());
            }

        };

        mEntryRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
