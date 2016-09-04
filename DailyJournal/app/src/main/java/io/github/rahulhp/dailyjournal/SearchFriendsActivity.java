package io.github.rahulhp.dailyjournal;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SearchFriendsActivity extends FirebaseActivity {


    private ArrayAdapter<String> mUsersAdapter;

    ProgressBar mProgressBar;
    TextView emptyListView;
    String emptyListString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle(getString(R.string.activity_title_search));
        final String[] dummy = {
        };

        final List<String> dummyData = new ArrayList<>(Arrays.asList(dummy));
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyListView = (TextView) findViewById(R.id.empty_list_textview);
        emptyListString = getString(R.string.search_no_results_found);
        findViewById(R.id.start_search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText search_input_editText = (EditText) findViewById(R.id.search_input_edittext);
                mUsersAdapter.clear();
                new FetchUserTask().execute(search_input_editText.getText().toString());

                mProgressBar.setVisibility(View.VISIBLE);

            }
        });
        mUsersAdapter = new ArrayAdapter<String>(
                this,
                R.layout.search_entry,
                R.id.search_result,
                dummyData
        ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final String user = dummyData.get(position);
                if (convertView==null){
                    convertView= LayoutInflater.from(getContext()).inflate(R.layout.search_entry,parent,false);
                }
                TextView user_name = (TextView) convertView.findViewById(R.id.search_result);
                user_name.setText(user);

                ImageButton send_req = (ImageButton) convertView.findViewById(R.id.send_request);
                send_req.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String queryURL = getString(R.string.database_user_friends_base_url)+"/"
                                +user.toLowerCase()+"/"+getString(R.string.database_user_friends_pending_url)+"/"+
                                mUID.toLowerCase();
                        mFirebaseDatabase.getReference(queryURL).setValue(mUID);
                    }
                });

                return super.getView(position, convertView, parent);
            }
        };

        ListView searchResult = (ListView) findViewById(R.id.search_result_listView);
        searchResult.setAdapter(mUsersAdapter);

    }

    public class FetchUserTask extends AsyncTask<String,Void,String[]>{
        private String searchString;
        private final String TAG = FetchUserTask.class.getSimpleName();

        private String[] getUserDataFromJson(String userJsonstr)
                throws JSONException{
            JSONObject userJson = new JSONObject(userJsonstr);
            ArrayList<String> usersArrayList = new ArrayList<>();
            Iterator<String> i = userJson.keys();
            while (i.hasNext()){
                String username = i.next().toLowerCase();
                if (username.contains(searchString))
                    usersArrayList.add(username);
            }
            return usersArrayList.toArray(new String[usersArrayList.size()]);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            searchString = strings[0].toLowerCase();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            String forecastJsonStr = null;
            try {
                final String databaseurl = getString(R.string.database_user_list_public_url);
                Uri builtUri = Uri.parse(databaseurl);
                URL url = new URL(builtUri.toString());



                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getUserDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }


        @Override
        protected void onPostExecute(String[] result) {
            mProgressBar.setVisibility(View.GONE);
            if (result != null && result.length!=0) {
                emptyListView.setVisibility(View.GONE);
                mUsersAdapter.clear();
                for(String dayForecastStr : result) {
                    mUsersAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            } else {
                emptyListView.setText(emptyListString);
                emptyListView.setVisibility(View.VISIBLE);
            }
        }
    }
}
