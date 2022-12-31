package com.capstone.itshere.membership;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.itshere.R;
import com.google.api.Distribution;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class MBSitemAdapter extends RecyclerView.Adapter<MBSitemAdapter.MBSItemHolder> {
    private ArrayList<MBSitem> arrayList;
    private Context context;
    private MBSitem myitem;
    private ImageView barcode_image;

    public MBSitemAdapter(ArrayList<MBSitem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MBSItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.membership_item, parent, false);
        //MBSItemHolder holder = new MBSItemHolder(view);
        MBSItemHolder holder = null;
        try {
            holder = new MBSItemHolder(view);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MBSitemAdapter.MBSItemHolder holder, int position) {
        myitem = arrayList.get(position);
        holder.tv_mbs_contents.setText(myitem.getContents()+" > ");
        holder.ly_color.setBackgroundColor(myitem.getColor());
        holder.barcode_text.setText(myitem.getBarcode());

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            String contents = myitem.getBarcode().replace(" ","");
            Bitmap bitmap = barcodeEncoder.encodeBitmap(contents, BarcodeFormat.CODE_128, 300, 50);
            barcode_image.setImageBitmap(bitmap);
        } catch(Exception e) {
            barcode_image.setImageResource(R.drawable.logo);
        }

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0 );
    }

    public class MBSItemHolder extends RecyclerView.ViewHolder{
        TextView tv_mbs_contents, barcode_text;
        ImageButton btn_delete, btn_pencil;
        LinearLayout ly_color;

        public MBSItemHolder(@NonNull View itemview) throws WriterException {
            super(itemview);
            this.tv_mbs_contents = itemview.findViewById(R.id.mbsitem_name);
            this.ly_color = itemview.findViewById(R.id.mbsitem_ly_color);
            this.barcode_text = itemview.findViewById(R.id.barcode_text);
            barcode_image = itemview.findViewById(R.id.barcode_image);

            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    String id = arrayList.get(pos).getID();
                    if(pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(context, MBSDetailActivity.class);
                        intent.putExtra("id", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }else{
                        Log.e("MBS어댑터", "잘못된 position");
                    }
                }
            });
        }
    }//MBSItemHolder
}
