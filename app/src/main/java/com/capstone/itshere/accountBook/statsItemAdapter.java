package com.capstone.itshere.accountBook;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.itshere.R;

import java.util.ArrayList;

public class statsItemAdapter extends RecyclerView.Adapter<statsItemAdapter.statsItemHolder> {

    private ArrayList<StatsItem> arrayList;
    private Context mcontext;

    public statsItemAdapter(ArrayList<StatsItem> arrayList, Context mcontext) {
        this.arrayList = arrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public statsItemAdapter.statsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_item,parent,false);
        statsItemHolder holder = new statsItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull statsItemAdapter.statsItemHolder holder, int position) {
        holder.tv_si_percentage.setText(arrayList.get(position).getPercentage() + "%");
        holder.tv_si_category.setText(arrayList.get(position).getCategory());
        holder.tv_si_amount.setText(arrayList.get(position).getAmount() + "원");
    }

    @Override
    public int getItemCount() { return (arrayList != null ? arrayList.size() : 0); }

    public class statsItemHolder extends RecyclerView.ViewHolder{
        TextView tv_si_percentage;
        TextView tv_si_category;
        TextView tv_si_amount;
        public statsItemHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_si_percentage = itemView.findViewById(R.id.statitem_percentage);
            this.tv_si_category = itemView.findViewById(R.id.statitem_category);
            this.tv_si_amount = itemView.findViewById(R.id.statitem_amount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    String category = arrayList.get(pos).getCategory();
                    if (pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(mcontext, StatsDetailyActivity.class);
                        intent.putExtra("category", category);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mcontext.startActivity(intent);
                    }else{
                        Toast.makeText(mcontext,"잘못된 position",Toast.LENGTH_SHORT).show();
                    }
                }
            });//-itemView클릭--*
        }
    }

}
