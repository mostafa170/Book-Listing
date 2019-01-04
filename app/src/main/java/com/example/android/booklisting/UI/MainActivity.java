package com.example.android.booklisting.UI;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.booklisting.JP.JSONParsing;
import com.example.android.booklisting.List.ListBook;
import com.example.android.booklisting.Adapter.ListBookAdapter;
import com.example.android.booklisting.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    ImageButton imageButton;
    ListBookAdapter adapter;
    ListView listView;
    TextView textNoDataFound;
    static final String SEARCH_RESULTS = "booksSearchResults";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        imageButton = (ImageButton) findViewById(R.id.searchButton);
        textNoDataFound = (TextView) findViewById(R.id.not_found_books);

        adapter = new ListBookAdapter(this, -1);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        imageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetConnectionAvailable();
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute();
            }
        });

        if (savedInstanceState != null) {
            ListBook[] books = (ListBook[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            adapter.addAll(books);
        }
    }

    private boolean isInternetConnectionAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork !=null)
        {return activeNetwork.isConnectedOrConnecting();}
        else {
            Toast.makeText(MainActivity.this, "No internet connection available",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void updateUi(List<ListBook> books){
        if (books.isEmpty()){
            // if no books found, show a message
            textNoDataFound.setVisibility(View.VISIBLE);
        } else {
            textNoDataFound.setVisibility(View.GONE);
        }
        adapter.clear();
        adapter.addAll(books);
    }

    private String getUserInput() {
        return editText.getText().toString();
    }

    private String getUrlForHttpRequest() {
        final String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=search+";
        String formatUserInput = getUserInput().trim().replaceAll("\\s+","+");
        String url = baseUrl + formatUserInput;
        return url;
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, List<ListBook>> {

        @Override
        protected List<ListBook> doInBackground(URL... urls) {
            URL url = createURL(getUrlForHttpRequest());
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<ListBook> books = parseJson(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(List<ListBook> books) {
            if (books == null) {
                return;
            }
            updateUi(books);
        }

        private URL createURL(String stringUrl) {
            try {
                return new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null){
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200){
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("mainActivity", "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<ListBook> parseJson(String json) {

            if (json == null) {
                return null;
            }

            List<ListBook> books =  JSONParsing.extractBooks(json);
            return books;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ListBook[] books = new ListBook[adapter.getCount()];
        for (int i = 0; i < books.length; i++) {
            books[i] = adapter.getItem(i);
        }
        outState.putParcelableArray(SEARCH_RESULTS, (Parcelable[]) books);
    }
}
