package com.payphi.customersdk;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UpiAdapter extends RecyclerView.Adapter<UpiAdapter.CustomViewHolder> {

    private List<UpiModel> imageModelsList;
    private Context mContext;
    private Activity act;
    SharedPreferences sharedPreferences;
    private AdapterCallback adapterCallback;
    private QRCodeFragment fragment;
    public UpiAdapter(Context context, ArrayList<UpiModel> imageList, Activity activity,QRCodeFragment qrCodeFragment)  {
        this.imageModelsList = imageList;
        this.sharedPreferences = context.getSharedPreferences("AppSdk",0);
        this.mContext = context;
        this.act = activity;
        this.fragment = qrCodeFragment;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_layout, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        final UpiModel get = imageModelsList.get(position);
        holder.photographer.setText(get.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((QRCodeFragment)fragment).doesUpiAppExists(get.getAction())){
                    Log.d("shraedData",sharedPreferences.getString("upiString",null));
                    String qrString = sharedPreferences.getString("upiString",null);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(qrString));
                    intent.setPackage(get.getAction());
                    ((QRCodeFragment)fragment).onClickApp();
                    ((Activity) mContext).startActivityForResult(intent, 100);
                }else {
                    Toast.makeText(mContext,"Application is not installed in your mobile",Toast.LENGTH_LONG).show();
                }
            }
        });
        final String image = get.getImageUrl();
        Glide
                .with(this.mContext)
                .load(image)
                .into(holder.image);
    }
    @Override
    public int getItemCount() {
        return (null != imageModelsList ? imageModelsList.size() : 0);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static interface AdapterCallback {
        void onMethodCallback();
    }
    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView photographer;
        CardView cardView;
        public CustomViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            cardView = (CardView) itemView.findViewById(R.id.card);
            photographer = (TextView) itemView.findViewById(R.id.photographer);

        }
    }
}
