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

        SpaceTask spaceTask = new SpaceTask();
        spaceTask.execute();
    }
    private class SpaceTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //подготовка запроса
            spaceClient = new OkHttpClient();
            //подготовка запроса с параментрами
            HttpUrl.Builder hub = HttpUrl.parse(ADDRESS).newBuilder();
            String url = hub.toString();
            spaceRequest = new Request.Builder().url(url).build();

            //отправка запроса
            try {
                Response spaceResp = spaceClient.newCall(spaceRequest).execute();
                Gson gson = new Gson();
                spaceResponse = gson.fromJson(spaceResp.body().string(), SpaceResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            describText.setText(spaceResponse.explanation);
            //Picasso picasso = new Picasso.Builder(getApplicationContext()).dow;\
            if (spaceResponse.media_type.equals("image")){
            Picasso.get().load(spaceResponse.url)
                    .placeholder(R.drawable.spaceplug)
                    .into(spaceImage);
            }else {
                spaceImage.setImageResource(R.drawable.spaceplug);
                describText.append("\n" + spaceResponse.url);
                Toast.makeText(getApplicationContext(), "Сегодня доступно видео", Toast.LENGTH_SHORT).show();
            }

        }


    }
}
