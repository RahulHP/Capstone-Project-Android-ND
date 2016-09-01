package io.github.rahulhp.dailyjournal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

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

public class ViewFriendsActivity extends FirebaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private DatabaseReference mFirebaseDatabaseReference;

    private SimpleCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);


        mFirebaseDatabaseReference = mFirebaseDatabase.getReference("/user-friend-requests/"+mUID+"/Accepted/");
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null){
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        String newfriend = (String) child.getValue(Object.class);
                        Log.e("TAG", "onDataChange: You have a new friend "+newfriend );
                        ContentValues friend = new ContentValues();
                        friend.put(FriendContract.ID_COLUMN,newfriend);
                        getContentResolver().insert(FriendContract.Content_Uri,friend);

                    }
                }
                mFirebaseDatabaseReference.setValue(null);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String[] columns = new String[]{
                FriendContract.ID_COLUMN
        };

        int[] ids = {
                R.id.friend_id
        };

        adapter = new SimpleCursorAdapter(this,R.layout.friend_entry,null,columns,ids,0);

        ((ListView) findViewById(R.id.friend_list)).setAdapter(adapter);

        getSupportLoaderManager().initLoader(0,null,this);
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
