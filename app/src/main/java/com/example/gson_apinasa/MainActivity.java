package com.example.gson_apinasa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView describText;
    ImageView spaceImage;

    private final String ADDRESS = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY";
    OkHttpClient spaceClient;
    Request spaceRequest;

    SpaceResponse spaceResponse = new SpaceResponse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        describText = findViewById(R.id.describ);
        spaceImage  = findViewById(R.id.space);


    }
    private class SpaceTask extends AsyncTask<String, Void, Response> {

        @Override
        protected Response doInBackground(String... strings) {
            //подготовка запроса
            spaceClient = new OkHttpClient();
            //подготовка запроса с параментрами
            HttpUrl.Builder hub = HttpUrl.parse(ADDRESS).newBuilder();
            String url = hub.toString();
            spaceRequest = new Request.Builder().url(url).build();

            //отправка запроса
            try {
                Response spaceResponse = spaceClient.newCall(spaceRequest).execute();
                return spaceResponse;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if (response != null) {
                Gson gson = new Gson();
                try {
                    spaceResponse = gson.fromJson(response.body().string(), SpaceResponse.class);
                    describText.setText(spaceResponse.explanation);
                    //Picasso picasso = new Picasso.Builder(getApplicationContext()).dow;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
