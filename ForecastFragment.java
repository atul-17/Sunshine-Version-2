package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import Util.Constants;
import model.WeatherData;
import Util.WeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by atul on 19/9/16.
 */

    public  class  ForecastFragment extends Fragment {

        public ListView foreCastList;
        public List<String> weatherData= new ArrayList<String>();
        public ForecastFragment() {
        }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to tell that fragment has options menu to be infalted
        setHasOptionsMenu(true);
    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            weatherData.add("Today - Sunny - 88/63 ");
            weatherData.add("Tommorrow - Foggy - 70/26");
            weatherData.add("weds - cloudy - 72/63");
            weatherData.add("Thurs - Rainy - 64/51");
            weatherData.add("Fri - Foggy - 70/46");
            weatherData.add("Sat - Sunny - 76/68");
        foreCastList = (ListView)rootView.findViewById(R.id.listview_forecast);
        ArrayAdapter<String> weatherAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,weatherData);
        foreCastList.setAdapter(weatherAdapter);
            return rootView;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                WeatherTask("bangalore");
                Log.d("atul","onOptinons item selected");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void WeatherTask(String city){
        Retrofit retrofit = new Retrofit.Builder()
                // .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASEURL)
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);
        Call<WeatherData> call = weatherService.getWheatherReport(city,"metrics",7,Constants.APIKEY);
        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {



                Time dayTime = new Time();
                dayTime.setToNow();
                String[] weatherString = new String[7];
                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(),dayTime.gmtoff);
                dayTime = new Time();
                for (int i = 0;i<response.body().getList().size();i++){
                    for (int j =0;j<response.body().getList().get(i).getWeather().size();j++){
                      //  Log.d("atul",response.body().getList().get(i).getWeather().get(j).getDescription());
                        String day;
                        String highLow;
                        String description;
                        long dateTime;
                        dateTime = dayTime.setJulianDay(julianStartDay + i);
                        day = getReadableString(dateTime);
                        double high = response.body().getList().get(i).getTemp().getMax();
                        double low = response.body().getList().get(i).getTemp().getMin();
                        highLow = formatHighLows(high,low);
                        description = response.body().getList().get(i).getWeather().get(j).getDescription();
                        weatherString[i]=day +" - "+description+ " - "+highLow;
                    }
                }
                   weatherData.clear();
                    ArrayAdapter<String> weatherAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,weatherString);
                    foreCastList.setAdapter(weatherAdapter);

            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.d("atul","failure"+t);
            }
        });

    }



    private String getReadableString(long time){
        //the api return a unix timestamp which must
        //converted to date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd");
        return simpleDateFormat.format(time);
    }

    /*
    prepare the weather of high/lows for presentation
     */

    private String formatHighLows(double high,double low){
        long roundedHigh = Math.round(high);
        long roundedLOw = Math.round(low);
        String highLowStr = roundedHigh +"/ "+roundedLOw;
        return highLowStr;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       getActivity().getMenuInflater().inflate(R.menu.forecastfragment,menu);
    }



}


