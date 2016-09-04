package io.github.rahulhp.dailyjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

/**
 * Created by root on 1/9/16.
 */
public class FirebaseActivity  extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    String TAG="FirebaseActivity";
    protected String mUsername;
    protected String mUID;
    public static final String ANONYMOUS = "anonymous";
    protected FirebaseAuth mFirebaseAuth;
    protected FirebaseUser mFirebaseUser;
    protected FirebaseDatabase mFirebaseDatabase;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpFirebase();
    }


    protected void setUpFirebase(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsername=ANONYMOUS;
        if (mFirebaseUser == null){
            startActivity(new Intent(this,SignInActivity.class));
            finish();
            return;
        } else {
            mUID = mFirebaseUser.getUid();
            mUsername = mFirebaseUser.getDisplayName();
            checkNewUser();
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this )
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    void checkNewUser(){
        final String user_string = "users/"+mUID;
        mFirebaseDatabase.getReference(user_string).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userEmail = dataSnapshot.getValue(String.class);
                        if (userEmail==null){
                            mFirebaseDatabase.getReference(user_string).setValue(mFirebaseUser.getEmail());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                startActivity(new Intent(this,SignInActivity.class));
                return true;
            case R.id.view_friends_activity:
                startActivity(new Intent(this,ViewFriendsActivity.class));
                return true;
            case R.id.view_entries_activity:
                startActivity(new Intent(this,MainActivity.class));
                return true;
            case R.id.new_entry_activity:
                startActivity(new Intent(this,NewEntryActivity.class));
                return true;
            case R.id.search_friends_activity:
                startActivity(new Intent(this,SearchFriendsActivity.class));
                return true;
            case R.id.friend_requests_activity:
                startActivity(new Intent(this,PendingFriendActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
