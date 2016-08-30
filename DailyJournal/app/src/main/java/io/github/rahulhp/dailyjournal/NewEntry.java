package io.github.rahulhp.dailyjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class NewEntry extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG="NewEntry";
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private String mUID;

    EditText newEntryText;
    TextView mCharCount;
    Switch mPrivacySwitch;
    Calendar cal = Calendar.getInstance();
    int cYear = cal.get(Calendar.YEAR);
    int cMonth = cal.get(Calendar.MONTH);
    int cDay = cal.get(Calendar.DAY_OF_MONTH);

    String data_string;
    String entry_text;
    JournalEntry jEntry;
    private GoogleApiClient mGoogleApiClient;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_items,menu);
        return true;
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
            case R.id.settings:
                Toast.makeText(getApplication(), "Settings", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        mPrivacySwitch = (Switch) findViewById(R.id.privacy_switch);
        //mPrivacySwitch.setTextOn("Private");
        //mPrivacySwitch.setTextOff("Public");

        mCharCount = (TextView) findViewById(R.id.charCount);
        newEntryText = (EditText) findViewById(R.id.new_entry_textbox);
        newEntryText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        newEntryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mCharCount.setText(editable.toString().length()+"/20");
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUID = mFirebaseUser.getUid();
        data_string= "user-data"+"/"+mUID+"/"+cYear+"/"+cMonth+"/"+cDay;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        getCurrentSavedEntry();
    }

    void getCurrentSavedEntry(){
        Log.e(TAG, "getCurrentSavedEntry: In function");
        mFirebaseDatabase.getReference(data_string).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        jEntry = dataSnapshot.getValue(JournalEntry.class);
                        //entry_text = dataSnapshot.getValue(String.class);
                        if (jEntry!=null){
                            Log.e(TAG, "onDataChange: "+ jEntry.getmEntry());
                            newEntryText.setText(jEntry.getmEntry(), TextView.BufferType.EDITABLE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }

    void saveEntry(View view){

        Date mDate = new Date();
        entry_text = newEntryText.getText().toString();
        JournalEntry jEntry = new JournalEntry(mDate,entry_text,Boolean.TRUE);
        Log.e(TAG, "Saving Entry: DOING" );
        mFirebaseDatabase.getReference(data_string).setValue(jEntry);

        Toast.makeText(this, "Entry Saved", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,MainActivity.class));
        Log.e(TAG, "Saving Entry: DONE" );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}