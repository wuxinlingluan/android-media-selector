package com.ice.picker.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ice.picker.R;
import com.ice.picker.entity.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImagePreviewFragment extends Fragment {
    private ViewPager viewPager;

    private List<String> paths;
    private int currentPosition;
    private List<String> selectedPaths = new ArrayList<>();
    private onImageSelectChangedListener imageSelectedListener;
    private Bundle bundle;
    private int maxCount;
    private MediaType type;
    public static ImagePreviewFragment newInstance(Bundle args) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface onImageSelectChangedListener {
        void onImageSelectChanged(List<String> paths);

        void onPageChanged(int index, int total);
    }

    public onImageSelectChangedListener getImageSelectedListener() {
        return imageSelectedListener;
    }

    public void setImageSelectedListener(onImageSelectChangedListener imageSelectedListener) {
        this.imageSelectedListener = imageSelectedListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView(view);
    }

    private void initData() {
        bundle = getArguments();
        paths = bundle.getStringArrayList("paths");
        maxCount = bundle.getInt("maxCount");
        type = (MediaType) bundle.getSerializable("type");
        selectedPaths = ((List<String>) bundle.getSerializable("selectedPaths"));
    }

    private void initView(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager_image_switcher);
        initViewPager();
        viewPager.setCurrentItem(paths.indexOf(bundle.getString("currentPath")));
    }

    private void initViewPager() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            @Override
            public int getCount() {
                return paths.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final String path = paths.get(position);
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.item_viewpager, container, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_isSelected);
                checkBox.setChecked(selectedPaths.contains(paths.get(position)));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (selectedPaths.size() >= maxCount) {
                                Toast.makeText(getActivity(), String.format(Locale.CHINA, "文件数不能超过%d", maxCount), Toast.LENGTH_SHORT).show();
                                buttonView.setChecked(false);
                            } else {
                                selectedPaths.add(path);
                            }
                        } else {
                            selectedPaths.remove(path);
                        }
                        imageSelectedListener.onImageSelectChanged(selectedPaths);
                    }
                });
                ImageView playImg = (ImageView) view.findViewById(R.id.play_video);
                if (type == MediaType.VIDEO) {
                    playImg.setVisibility(View.VISIBLE);
                    playImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse(path);
                            it.setDataAndType(uri, "video/mp4");
                            startActivity(it);
                        }
                    });
                } else {
                    playImg.setVisibility(View.GONE);
                }


                Glide.with(getActivity()).load(paths.get(position)).into(imageView);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                if (imageSelectedListener != null) {
                    imageSelectedListener.onPageChanged(pos, paths.size());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }
}
