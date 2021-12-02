package com.example.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mTodayTotal, mTotal, mActive, mTodayActive, mRecovered, mTodayRecovered, mDeaths, mTodayDeaths;
    TextView mFilter;
    Spinner spinner;
    PieChart mPieChart;
    private RecyclerView recyclerView;
    Adapter adapter;

    String country;
    String[] types = {"cases", "deaths", "recovered", "active"};
    List<ModelClass> modelClassList;
    List<ModelClass> modelClassListRV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        countryCodePicker = findViewById(R.id.ccp);
        mTodayActive = findViewById(R.id.todayActive);
        mActive = findViewById(R.id.activeCases);
        mDeaths = findViewById(R.id.totalDeath);
        mTodayDeaths = findViewById(R.id.todayDeath);
        mRecovered = findViewById(R.id.recoveredCase);
        mTodayRecovered = findViewById(R.id.todayrecovered);
        mTotal = findViewById(R.id.totalCase);
        mTodayTotal = findViewById(R.id.todayTotal);
        mPieChart = findViewById(R.id.pieChart);
        spinner = findViewById(R.id.spinner);
        mFilter = findViewById(R.id.filter);
        recyclerView = findViewById(R.id.recyclerView);

        modelClassListRV = new ArrayList<>();
        modelClassList = new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        ApiUtils.getAPiInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassListRV.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplicationContext(), modelClassListRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country = countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryCodePicker.getSelectedCountryName();
                fetchData();
            }
        });
        fetchData();
    }

    private void fetchData() {
        ApiUtils.getAPiInterface().getCountryData().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
                for (int i=0; i<modelClassList.size(); i++){
                    if (modelClassList.get(i).getCountry().equals(country)){
                        mActive.setText((modelClassList.get(i).getActive()));
                        mTodayDeaths.setText((modelClassList.get(i).getTodayDeaths()));
                        mTodayTotal.setText((modelClassList.get(i).getTodayCases()));
                        mTodayRecovered.setText((modelClassList.get(i).getTodayRecovered()));
                        mTotal.setText((modelClassList.get(i).getCases()));
                        mDeaths.setText((modelClassList.get(i).getDeaths()));
                        mRecovered.setText((modelClassList.get(i).getRecovered()));

                        int active, total, recovered, deaths;

                        active = Integer.parseInt(modelClassList.get(i).getActive());
                        total = Integer.parseInt(modelClassList.get(i).getCases());
                        recovered = Integer.parseInt(modelClassList.get(i).getRecovered());
                        deaths = Integer.parseInt(modelClassList.get(i).getDeaths());

                        updateGraph(active, total, recovered, deaths);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });
    }

    private void updateGraph(int active, int total, int recovered, int deaths) {
        mPieChart.clearChart();
        mPieChart.addPieSlice(new PieModel("Confirm", total, Color.parseColor("#FFB701")));
        mPieChart.addPieSlice(new PieModel("Active", active, Color.parseColor("#FF4caf50")));
        mPieChart.addPieSlice(new PieModel("Recovered", recovered, Color.parseColor("#38ACCD")));
        mPieChart.addPieSlice(new PieModel("Deaths", deaths, Color.parseColor("#F55c46")));
        mPieChart.startAnimation();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = types[position];
        mFilter.setText(item);
        adapter.filter(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}