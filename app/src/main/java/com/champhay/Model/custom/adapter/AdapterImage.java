package com.champhay.Model.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.champhay.Model.custom.component.SettingHandle;
import com.champhay.Model.handler.social.Content;
import com.champhay.mcomics.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by HoangTP
 */
public class AdapterImage extends RecyclerView.Adapter<AdapterImage.ViewHolder> {
    private ArrayList<Content> arrImage;
    private Activity activity;

    public AdapterImage(Activity activity, ArrayList<Content> arrImage) {
        this.arrImage = arrImage;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_page, parent, false);
        return new ViewHolder((CardView) view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Context context = holder.imageView.getContext();
        SettingHandle settingHandle = new SettingHandle(activity);
        if (settingHandle.getOrientation() == SettingHandle.VERTICAL) {
            holder.imageView.setPadding(0, 0, 0, 10);
            Picasso.with(context).load(arrImage.get(position).getLink()).resize(getScreenWidth(), (getScreenHeight() - 40)).into(holder.imageView);
        } else {
            holder.imageView.setPadding(0, 0, 0, 0);
            Picasso.with(context).load(arrImage.get(position).getLink()).resize(getScreenWidth(), getScreenHeight()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return arrImage.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(CardView v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.coverImageView);
        }
    }

    private int getScreenWidth() {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.x;
    }

    private int getScreenHeight() {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point.y;
    }
}