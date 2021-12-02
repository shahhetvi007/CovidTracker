package com.example.covidtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    int n=1;
    List<ModelClass> countryList;

    public Adapter(Context context, List<ModelClass> countryList) {
        this.context = context;
        this.countryList = countryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelClass modelClass = countryList.get(position);
        if (n==1){
            holder.countryCase.setText(NumberFormat.getInstance().format(Integer.parseInt(modelClass.getCases())));
        }
        else if (n==2){
            holder.countryCase.setText(NumberFormat.getInstance().format(Integer.parseInt(modelClass.getRecovered())));
        }
        else if (n==3){
            holder.countryCase.setText(NumberFormat.getInstance().format(Integer.parseInt(modelClass.getDeaths())));
        }
        else {
            holder.countryCase.setText(NumberFormat.getInstance().format(Integer.parseInt(modelClass.getActive())));
        }
        holder.countryName.setText(modelClass.getCountry());
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView countryCase, countryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countryCase = itemView.findViewById(R.id.countryCase);
            countryName = itemView.findViewById(R.id.countryName);
        }
    }

    public void filter(String charText){
        if (charText.equals("cases")){
            n=1;
        }
        else if (charText.equals("recovered")){
            n=2;
        }
        else if (charText.equals("deaths")){
            n=3;
        }
        else {
            n=4;
        }
        notifyDataSetChanged();
    }
}
