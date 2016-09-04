package io.github.rahulhp.dailyjournal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ViewFriendsActivity extends FirebaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    DatabaseReference mFirebaseDatabaseReference;

    private SimpleCursorAdapter adapter;
    ListView mFriendList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.activity_title_my_friends));

        updateFriendsFromFireBase();


        String[] columns = new String[]{
                FriendContract.ID_COLUMN
        };

        int[] ids = {
                R.id.friend_id
        };

        adapter = new SimpleCursorAdapter(this,R.layout.friend_entry,null,columns,ids,0);
        mFriendList =(ListView) findViewById(R.id.friend_list);
        mFriendList.setAdapter(adapter);
        mFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = (Cursor) adapterView.getAdapter().getItem(i);
                String friendid = c.getString(0);
                Intent intent = new Intent(getBaseContext(),ViewFriendPosts.class);
                intent.putExtra(getString(R.string.intent_extra_user_id),friendid);
                intent.putExtra(getString(R.string.intent_extra_user_name),friendid);
                startActivity(intent);

            }
        });

        getSupportLoaderManager().initLoader(0,null,this);
    }


    void updateFriendsFromFireBase(){
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference("/"
                +getString(R.string.database_user_friends_base_url)+"/"+mUID+"/"+
                getString(R.string.database_user_friends_accepted_url)+"/");
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null){
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        String newFriend = (String) child.getValue(Object.class);
                        ContentValues friend = new ContentValues();
                        friend.put(FriendContract.ID_COLUMN,newFriend);
                        getContentResolver().insert(FriendContract.Content_Uri,friend);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,FriendContract.Content_Uri,null,"",null,"");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}
