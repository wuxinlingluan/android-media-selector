package com.ice.picker.view;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ice.picker.R;
import com.ice.picker.entity.MediaDir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MediaDirsPopWindow extends PopupWindow {
    public HashMap<String, MediaDir> dirMap;
    public LayoutInflater inflater;
    public ListView lvDir;
    public Context context;
    public View.OnClickListener onItemClickListener;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MediaDirsPopWindow(Context context, int width, int height) {
        inflater = LayoutInflater.from(context);
        setContentView(initView());
        setHeight(height);
        setWidth(width);
        setFocusable(true);
        setAnimationStyle(R.style.pop_anim_style);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        this.context = context;
    }

    private View initView() {
        View view = inflater.inflate(R.layout.popwindow_list, null);
        lvDir = (ListView) view.findViewById(R.id.lv_img_dir);
        return view;
    }

    public void show(HashMap<String, MediaDir> dirMap, View view) {
        this.dirMap = dirMap;
        lvDir.setAdapter(new ImageDirAdapter(dirMap));
        showAsDropDown(view);
    }

    private class ImageDirAdapter extends BaseAdapter {
        private List<MediaDir> dirs;

        ImageDirAdapter(HashMap<String, MediaDir> dirMap) {
            dirs = new ArrayList<>();
            dirs.addAll(dirMap.values());
            Collections.sort(dirs, new Comparator<MediaDir>() {
                @Override
                public int compare(MediaDir o1, MediaDir o2) {
                    return o2.files.size() - o1.files.size();
                }
            });
        }

        @Override
        public int getCount() {
            return dirs.size();
        }

        @Override
        public MediaDir getItem(int position) {
            return dirs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MediaDir dir = getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.pop_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.imgHeader = (ImageView) convertView.findViewById(R.id.img_dir_header);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_dir_title);
                viewHolder.imgDot = (ImageView) convertView.findViewById(R.id.img_dir_dot);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvTitle.setText(String.format(Locale.CHINA, "%s (%d)", dir.dirName, dir.files.size()));

            //如果没有媒体文件时files的size为0
            if (dir.files.size() != 0) {
                Glide.with(context).load(dir.files.get(0)).apply(new RequestOptions().placeholder(R.mipmap.default_image)).into(viewHolder.imgHeader);
            }else {
                Glide.with(context).load(R.mipmap.default_image).into(viewHolder.imgHeader);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(dir);
                    onItemClickListener.onClick(v);
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tvTitle;
            ImageView imgHeader;
            ImageView imgDot;
        }
    }

}
