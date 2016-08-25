package io.github.rahulhp.dailyjournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class NewEntry extends AppCompatActivity {
    private static final String TAG="NewEntry";

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
}
