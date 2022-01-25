package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private EditText location;
    private JSONObject weather;
    private JSONObject forecast;
    private ArrayList<DayBox> array;
    private ArrayList<Bitmap> bits;
    private ImageView image;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText location = findViewById(R.id.a_location);
        image = findViewById(R.id.imageView);
        DayBox d1 = findViewById(R.id.a_d1);
        DayBox d2 = findViewById(R.id.a_d2);
        DayBox d3 = findViewById(R.id.a_d3);
        DayBox d4 = findViewById(R.id.a_d4);
        DayBox d5 = findViewById(R.id.a_d5);
        location.setHint("Enter Zip/City");
        location.setText("12804");
        array = new ArrayList<>();
        bits = new ArrayList<>();
        array.add(d1);
        array.add(d2);
        array.add(d3);
        array.add(d4);
        array.add(d5);
        WeatherThread weather = new WeatherThread();
        weather.execute(location.getText()+"");
    }
    public class WeatherThread extends AsyncTask<String,Void,JSONObject>{
        private URL qwe;
        private Bitmap bit;
        private Calendar cal;
        protected JSONObject doInBackground(String... strings) {
            publishProgress();
            String loc = strings[0];
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?zip="+loc+"&units=imperial&APPID=API_KEY");
                URLConnection urlc = url.openConnection();
                InputStream input = urlc.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                forecast = new JSONObject(br.readLine());

                cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(forecast.getJSONArray("list").getJSONObject(0).getString("dt"))*1000);
                TimeZone t = TimeZone.getDefault();
                t.setRawOffset(Integer.parseInt(forecast.getJSONObject("city").getString("timezone"))*1000);
                cal.setTimeZone(t);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                if(hour>12){
                    String icon = forecast.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("icon");
                    qwe = new URL("https://openweathermap.org/img/wn/"+icon+"@2x.png");
                    bit = BitmapFactory.decodeStream(qwe.openConnection().getInputStream());
                    bits.add(bit);
                }
                for(int i = 8*(hour/13)+(13-hour)/3; i<40; i+=8){
                    String icon = forecast.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");
                    qwe = new URL("https://openweathermap.org/img/wn/"+icon+"@2x.png");
                    bit = BitmapFactory.decodeStream(qwe.openConnection().getInputStream());
                    bits.add(bit);
                }

                URL url2 = new URL("http://api.openweathermap.org/data/2.5/weather?zip="+loc+"&units=imperial&APPID=15613e40b5cbe28884bf03b1259c34db");
                URLConnection urlc2 = url2.openConnection();
                InputStream input2 = urlc2.getInputStream();
                BufferedReader br2 = new BufferedReader(new InputStreamReader(input2));
                weather = new JSONObject(br2.readLine());
                br.close();
                return forecast;

            }
            catch(IOException | JSONException e){
                Log.d("error","1"+e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            image.setImageBitmap(bit);
            try{
                int hour = cal.get(Calendar.HOUR_OF_DAY);

                for(int i = 0; i<5; i++){
                    double high = Double.MIN_VALUE;
                    double low = Double.MAX_VALUE;
                    for(int j = (8-(hour+1)/3)+i*8-8; j<(8-(hour+1)/3)+i*8; j++) {
                        if(j<0)
                            j = 0;
                        Double temp = forecast.getJSONArray("list").getJSONObject(j).getJSONObject("main").getDouble("temp");
                        if(temp>high)
                            high = temp;
                        if(temp<low)
                            low = temp;
                    }
                    array.get(i).set(cal.get(Calendar.DAY_OF_MONTH)+i+"", high+"", low+"", bits.get(i));
                }
            }
            catch(JSONException e){
                Log.d("error","2"+e);
            }
        }
    }
}
