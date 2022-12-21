package com.capstone.itshere.accountBook;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.itshere.R;

import java.util.ArrayList;

public class DailyNoteAdapter extends RecyclerView.Adapter<DailyNoteAdapter.DailyNoteHolder> {

    private ArrayList<DailyNote> arrayList;
    private Context context;

    public DailyNoteAdapter(ArrayList<DailyNote> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public DailyNoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailynote_item, parent,false);
        DailyNoteHolder holder = new DailyNoteHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DailyNoteHolder holder, int position) {
        DailyNote myitem = arrayList.get(position);
        holder.tv_dn_date.setText(myitem.getDate());
        holder.tv_dn_category.setText(myitem.getCategory());
        holder.tv_dn_note.setText(myitem.getNote());
        holder.tv_dn_amount.setText(String.valueOf(myitem.getAmount()));
        String colorpos = myitem.getBigcate();
        if (colorpos.equals("수입")){
            holder.tv_dn_bigcate.setTextColor(ContextCompat.getColor(context,R.color.myBlue));
        }else{
            holder.tv_dn_bigcate.setTextColor(ContextCompat.getColor(context,R.color.myRed));
        }

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class DailyNoteHolder extends RecyclerView.ViewHolder {
        TextView tv_dn_bigcate;
        TextView tv_dn_date;
        TextView tv_dn_category;
        TextView tv_dn_note;
        TextView tv_dn_amount;
        public DailyNoteHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_dn_date = itemView.findViewById(R.id.tv_dn_date);
            this.tv_dn_category = itemView.findViewById(R.id.tv_dn_category);
            this.tv_dn_note = itemView.findViewById(R.id.tv_dn_note);
            this.tv_dn_amount = itemView.findViewById(R.id.tv_dn_amount);
            this.tv_dn_bigcate = itemView.findViewById(R.id.tv_dn_bigcate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    String idNum = arrayList.get(pos).getIdNum();
                    String MONTH = arrayList.get(pos).getMONTH();
                    if (pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(context, DailyDetailActivity.class);
                        intent.putExtra("idNum", idNum);
                        intent.putExtra("MONTH", MONTH);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.e("in adpater", idNum);

                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context,"잘못된 position",Toast.LENGTH_SHORT).show();
                    }
                }
            });//-itemView클릭--*

        }
    }
}
