package io.github.rahulhp.dailyjournal;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PendingFriendActivity extends FirebaseActivity{


    private DatabaseReference mFirebaseDatabaseReference;


    private String TAG="PEnding";
    RecyclerView mPendingView;
    private FirebaseRecyclerAdapter<String,PendingViewHolder > mFirebaseAdapter;



    public static class PendingEntry{
        String user_id;

        public PendingEntry(){

        }

        public PendingEntry(String user_id){
            this.user_id=user_id;
        }
    }

    public static class PendingViewHolder extends RecyclerView.ViewHolder{
        public TextView requestIdView;
        public Button acceptView;
        public PendingViewHolder (View itemView) {
            super(itemView);
            requestIdView = (TextView) itemView.findViewById(R.id.pending_friend_id);
            acceptView = (Button) itemView.findViewById(R.id.accept_request);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_friend);


        String data_string = "user-friend-requests"+"/"+mUID+"/Pending";
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(data_string);
        Log.e(TAG, "onCreate: "+data_string );

        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    Log.e(TAG, "onDataChange: Nope" );
                else{
                    Log.e(TAG, "onDataChange: Yup" );
                    createRecyclerView();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        createRecyclerView();
    }

    void createRecyclerView(){
        mPendingView = (RecyclerView) findViewById(R.id.pending_friend_list);
        mPendingView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mPendingView.setLayoutManager(mLinearLayoutManager);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<String, PendingViewHolder>(
                String.class,
                R.layout.pending_friend,
                PendingViewHolder.class,
                mFirebaseDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(PendingViewHolder viewHolder, final String model, int position) {
                Log.e(TAG, "populateViewHolder: "+model );
                viewHolder.requestIdView.setText(model);

                viewHolder.acceptView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplication(), model, Toast.LENGTH_SHORT).show();
                        ContentValues friend = new ContentValues();
                        friend.put(FriendContract.ID_COLUMN,model);
                        getContentResolver().insert(FriendContract.Content_Uri,friend);

                        Map<String,Object> childUpdates = new HashMap<String, Object>();
                        childUpdates.put("/user-friend-requests/"+mUID+"/Pending/"+model,null);
                        childUpdates.put("/user-friend-requests/"+model+"/Accepted/"+mUID,mUID);
                        Log.e(TAG, "onClick: Updating" );
                        mFirebaseDatabase.getReference().updateChildren(childUpdates);
                        mFirebaseAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        mPendingView.setAdapter(mFirebaseAdapter);
        Log.e(TAG, "createRecyclerView: Doing" );
    }

}
