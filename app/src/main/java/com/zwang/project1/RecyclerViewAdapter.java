package com.zwang.project1;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    ArrayList<String> imageNames = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    ArrayList<Stock> stocks = new ArrayList<>();
    Context context;

    public RecyclerViewAdapter(ArrayList<Stock> stocks, Context context) {
        this.stocks = stocks;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Glide.with(context).asBitmap().load(stocks.get(position).getPictureUrl_()).into(holder.image);
        holder.companyName.setText(stocks.get(position).getName_());
        holder.price.setText(stocks.get(position).getPrice_());
        StringBuilder change = new StringBuilder(stocks.get(position).getChange_());
        if(change.charAt(0) == '-'){
            holder.change.setBackgroundColor(Color.RED);
            holder.change.setTextColor(Color.WHITE);
        }
        else if(!change.toString().equals("0.00%")){
            holder.change.setBackgroundResource(R.color.solidGreen);
            change.insert(0, "+");
            holder.change.setTextColor(Color.WHITE);
        }
        holder.change.setText(change.toString());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,stocks.get(position).getName_(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView image;
        public TextView companyName;
        public TextView price;
        public TextView change;
        public RelativeLayout mainLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            companyName = itemView.findViewById(R.id.company_name);
            price = itemView.findViewById(R.id.price);
            change = itemView.findViewById(R.id.change);
            mainLayout = itemView.findViewById(R.id.main_layout);
        }
    }
}
