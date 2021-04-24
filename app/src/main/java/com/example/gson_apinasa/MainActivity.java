package com.example.gson_apinasa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    TextView describText;
    ImageView spaceImage;
    Button speechButton;
    String token = "";

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
        speechButton = findViewById(R.id.speechButton);

        SpaceTask spaceTask = new SpaceTask();
        spaceTask.execute();
    }

    public void getVoices(View view) {
        String text = describText.getText().toString();
        Retrofit tokenRetrofit = new Retrofit.Builder().baseUrl(AzureTokenAPI.tokenURI)
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        AzureTokenAPI azureTokenAPI = tokenRetrofit.create(AzureTokenAPI.class);
        Call<String> callToken = azureTokenAPI.getToken();
        callToken.enqueue(new TokenCallback());

        //следующий объект retrofit
        //смотри в TokenCallback

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
            //перенд показом текста запросим перевод
            // 1 настроить класс - тело и записать в него английский текст
            BodyTranslate [] bodyTranslates = new BodyTranslate[1];
            bodyTranslates[0] = new BodyTranslate();
            bodyTranslates[0].Text = spaceResponse.explanation;
            Toast.makeText(getApplicationContext(), bodyTranslates[0].Text, Toast.LENGTH_SHORT).show();

            //построить объект Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(AzureTranslateAPI.address)
                    .build();
            //создать объект интерфейса
            AzureTranslateAPI api = retrofit.create(AzureTranslateAPI.class);
            //отправить запрос
            Call<ResponseTranslate[]> call = api.requestTranslate(bodyTranslates);
            call.enqueue(new ResponseCallback());
            //describText.setText(spaceResponse.explanation);
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

    private class ResponseCallback implements retrofit2.Callback<ResponseTranslate[]> {

        @Override
        public void onResponse(Call<ResponseTranslate[]> call, retrofit2.Response<ResponseTranslate[]> response) {
            if (response.isSuccessful()){
                String s = response.body()[0].toString();
                describText.append("\n" + s);
            }else {
                Toast.makeText(getApplicationContext(), Integer.toString(response.code()), Toast.LENGTH_SHORT).show();
            }
            //if (response.headers().)
        }

        @Override
        public void onFailure(Call<ResponseTranslate[]> call, Throwable t) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private class TokenCallback implements retrofit2.Callback<String> {

        @Override
        public void onResponse(Call<String> call, retrofit2.Response<String> response) {
            if (response.isSuccessful()){
                token = response.body();
                //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                Retrofit voicesRetrofit = new Retrofit.Builder()
                        .baseUrl(AzureTokenAPI.tokenURI)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                AzureVoicesAPI azureVoicesAPI = voicesRetrofit.create(AzureVoicesAPI.class);
                Call<ArrayList<Dictor>> callDictors = azureVoicesAPI.getDictorsList(token);
                callDictors.enqueue(new DictorsCallback());
            }else
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(Call<String> call, Throwable t) {
            Toast.makeText(getApplicationContext(), "Error in onFailure", Toast.LENGTH_SHORT).show();

        }
    }

    private class DictorsCallback implements retrofit2.Callback<ArrayList<Dictor>> {
        @Override
        public void onResponse(Call<ArrayList<Dictor>> call, retrofit2.Response<ArrayList<Dictor>> response) {
            ArrayList<Dictor> dictors = new ArrayList<>();
            if (response.isSuccessful()){
            dictors = response.body();
            Toast.makeText(getApplicationContext(), dictors.get(0).ShortName,
                    Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), Integer.toString(response.code()),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ArrayList<Dictor>> call, Throwable t) {

        }
    }
}
