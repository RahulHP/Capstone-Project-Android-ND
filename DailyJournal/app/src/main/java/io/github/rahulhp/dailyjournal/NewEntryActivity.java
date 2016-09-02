package io.github.rahulhp.dailyjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class NewEntryActivity extends FirebaseActivity{
    private static final String TAG=NewEntryActivity.class.getName();


    TextInputEditText newEntryText;
    TextView sharingText;
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
        sharingText = (TextView) findViewById(R.id.shared_boolean_text);
        mPrivacySwitch = (Switch) findViewById(R.id.privacy_switch);
        mPrivacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    sharingText.setText("Private");
                } else {
                    sharingText.setText("Public");
                }
            }
        });

        newEntryText = (TextInputEditText) findViewById(R.id.new_entry_textbox);

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
                            mPrivacySwitch.setChecked(jEntry.getmPrivate());
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
        JournalEntry jEntry = new JournalEntry(mDate,entry_text,mPrivacySwitch.isChecked());
        mFirebaseDatabase.getReference(data_string).setValue(jEntry);
        Toast.makeText(this, "Entry Saved", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,MainActivity.class));
    }

}
