package io.github.rahulhp.dailyjournal;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.github.rahulhp.dailyjournal.BaseActivities.FirebaseActivity;
import io.github.rahulhp.dailyjournal.data.FriendContract;
import io.github.rahulhp.dailyjournal.ui.PendingViewHolder;

public class PendingFriendActivity extends FirebaseActivity {


    private DatabaseReference mFirebaseDatabaseReference;

    RecyclerView mPendingView;
    private FirebaseRecyclerAdapter<String,PendingViewHolder> mFirebaseAdapter;

    TextView emptyListView;
    String emptyListString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_friend);

        emptyListView = (TextView) findViewById(R.id.empty_list_textview);
        emptyListString = getString(R.string.no_pending_requests);
        emptyListView.setText(emptyListString);

        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.activity_title_pending_requests));

        String queryString = getString(R.string.database_user_friends_base_url)+"/"+mUID+"/"+
                getString(R.string.database_user_friends_pending_url);
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference(queryString);
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    emptyListView.setVisibility(View.VISIBLE);
                else{
                    emptyListView.setVisibility(View.GONE);
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
        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, PendingViewHolder>(
                String.class,
                R.layout.pending_friend,
                PendingViewHolder.class,
                mFirebaseDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(PendingViewHolder viewHolder, final String model, int position) {
                viewHolder.requestIdView.setText(model);
                viewHolder.requestIdView.setContentDescription(model);

                viewHolder.acceptView.setContentDescription(getString(R.string.con_d_accept_request));
                viewHolder.acceptView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplication(), model, Toast.LENGTH_SHORT).show();
                        ContentValues friend = new ContentValues();
                        friend.put(FriendContract.ID_COLUMN,model);
                        getContentResolver().insert(FriendContract.Content_Uri,friend);

                        Map<String,Object> childUpdates = new HashMap<>();
                        childUpdates.put("/"+getString(R.string.database_user_friends_base_url)+"/"+mUID+
                                "/"+getString(R.string.database_user_friends_pending_url)+"/"+model,null);
                        childUpdates.put("/"+getString(R.string.database_user_friends_base_url)+"/"+model+
                                "/"+getString(R.string.database_user_friends_accepted_url)+"/"+mUID,mUID);
                        mFirebaseDatabase.getReference().updateChildren(childUpdates);
                        mFirebaseAdapter.notifyDataSetChanged();
                    }
                });

                viewHolder.rejectView.setContentDescription(getString(R.string.con_d_reject_request));
                viewHolder.rejectView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String,Object> childUpdates = new HashMap<>();
                        childUpdates.put("/"+getString(R.string.database_user_friends_base_url)+"/"+mUID+
                                "/"+getString(R.string.database_user_friends_pending_url)+"/"+model,null);
                        mFirebaseDatabase.getReference().updateChildren(childUpdates);
                        mFirebaseAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
    }
    void createRecyclerView(){
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        mPendingView = (RecyclerView) findViewById(R.id.pending_friend_list);
        mPendingView.setHasFixedSize(true);
        mPendingView.setLayoutManager(mLinearLayoutManager);
        mPendingView.setAdapter(mFirebaseAdapter);

    }

}
