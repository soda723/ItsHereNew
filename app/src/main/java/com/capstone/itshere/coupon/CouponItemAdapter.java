package com.capstone.itshere.coupon;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.itshere.R;
import com.capstone.itshere.accountBook.DailyDetailActivity;
import com.capstone.itshere.accountBook.DailyNote;
import com.capstone.itshere.accountBook.DailyNoteAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class CouponItemAdapter extends RecyclerView.Adapter<CouponItemAdapter.CouponItemHolder>{
    private ArrayList<CouponItem> arrayList;
    private Context context;

    public CouponItemAdapter(ArrayList<CouponItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CouponItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item, parent,false);
        CouponItemHolder holder = new CouponItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CouponItemAdapter.CouponItemHolder holder, int position) {
        CouponItem myitem = arrayList.get(position);
        holder.tv_cp_contents.setText(myitem.getContents());
        holder.tv_cp_date.setText(myitem.getDate());
        holder.tv_cp_dday.setText(myitem.getDday());

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CouponItemHolder extends RecyclerView.ViewHolder {
        TextView tv_cp_contents;
        TextView tv_cp_date;
        TextView tv_cp_dday;

        public CouponItemHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_cp_contents = itemView.findViewById(R.id.tv_cp_contents);
            this.tv_cp_date = itemView.findViewById(R.id.tv_cp_date);
            this.tv_cp_dday = itemView.findViewById(R.id.tv_cp_dday);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    String idNum = arrayList.get(pos).getIdNum();
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, CouponDetailActivity.class);
                        intent.putExtra("idNum", idNum);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.e("in adpater", idNum);

                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "잘못된 position", Toast.LENGTH_SHORT).show();
                    }
                }
            });//-itemView클릭--*

        }
    }
}
