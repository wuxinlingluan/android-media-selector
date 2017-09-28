package com.ice.picker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ice.picker.R;
import com.ice.picker.entity.MediaDir;

import java.util.List;

public class MediaListAdapter extends BaseAdapter {

    private MediaDir mediaDir;
    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private List<String> selectedPaths;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MediaListAdapter(Context context, MediaDir mediaDir, List<String> selectedPaths) {
        this.mediaDir = mediaDir;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.selectedPaths = selectedPaths;
    }

    public void setMediaDir(MediaDir mediaDir) {
        this.mediaDir = mediaDir;
    }

    @Override
    public int getCount() {
        return mediaDir.files.size() + 1;
    }

    @Override
    public String getItem(int position) {
        return mediaDir.files.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item_photo, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.chSelect = (CheckBox) convertView.findViewById(R.id.ch_photo_select);
            viewHolder.photoView = (ImageView) convertView.findViewById(R.id.img_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            switch (mediaDir.type) {
                case PIC:
                    viewHolder.photoView.setImageResource(R.mipmap.photo_camera);
                    break;
                case VIDEO:
                    viewHolder.photoView.setImageResource(R.mipmap.vedio_camera);
                    break;
            }
            viewHolder.photoView.setBackgroundColor(Color.WHITE);
            viewHolder.photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewHolder.chSelect.setVisibility(View.GONE);
        } else {

            viewHolder.photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.chSelect.setVisibility(View.VISIBLE);

            viewHolder.chSelect.setOnCheckedChangeListener(null);
            if (selectedPaths.contains(getItem(position))) {
                viewHolder.chSelect.setChecked(true);
            }else {
                viewHolder.chSelect.setChecked(false);
            }
            Glide.with(context).load(getItem(position)).into(viewHolder.photoView);

        }


        viewHolder.chSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onItemClickListener != null) {
                    onItemClickListener.onCheckedChange(buttonView, isChecked, getItem(position));
                }
            }
        });
        viewHolder.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    if (position == 0) {
                        onItemClickListener.onAdd();
                    } else {
                        onItemClickListener.onPreview(getItem(position));
                    }
                }
            }
        });
        return convertView;
    }

    public interface OnItemClickListener {
        void onPreview(String path);

        void onAdd();

        void onCheckedChange(CompoundButton buttonView, boolean isChecked, String path);
    }

    private static class ViewHolder {
        ImageView photoView;
        CheckBox chSelect;
    }
}
