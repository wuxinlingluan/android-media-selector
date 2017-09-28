package com.ice.picker.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ice.picker.R;
import com.ice.picker.adapter.MediaListAdapter;
import com.ice.picker.entity.MediaDir;
import com.ice.picker.entity.MediaType;
import com.ice.picker.util.PhoneInfoUtil;
import com.ice.picker.view.MediaDirsPopWindow;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MediaListActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PIC = 100;
    private static final int REQUEST_TAKE_VIDEO = 101;
    private static final int REQUEST_PREVIEW = 102;
    private GridView mediaListGV;
    private MediaType type;
    private HashMap<String, MediaDir> dirMap = new HashMap<>();
    private MediaDir currentDir;
    private MediaDirsPopWindow dirPopWindow;
    private TextView dirNameTV;
    private List<String> selectedPaths = new ArrayList<>();
    private Button nextBtn;
    private MediaListAdapter mediaListAdapter;
    private int maxCount;
    private static final int DEFAULT_MAX_COUNT = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        type = (MediaType) getIntent().getSerializableExtra("type");
        if (type == null) {
            throw new IllegalArgumentException("type不能为空");
        }

        initView();
        initData();
    }

    private void initData() {
        maxCount = getIntent().getIntExtra("maxCount", DEFAULT_MAX_COUNT);
        loadMediaList(type);
    }

    private void loadMediaList(MediaType type) {
        new Thread(new LoadMediaListRunnable(type)).start();
    }

    private void initView() {
        mediaListGV = (GridView) findViewById(R.id.media_list_gv);
        dirNameTV = (TextView) findViewById(R.id.dir_name_tv);
        nextBtn = (Button) findViewById(R.id.next_btn);
        TextView cancelBtn = (TextView) findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPaths.size() > 0) {
                    onNext();
                }
            }
        });
        dirPopWindow = new MediaDirsPopWindow(this, PhoneInfoUtil.getScreenWidth(this), PhoneInfoUtil.getScreenHeight(this) * 3 / 5);
        dirNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dirNameTV.setSelected(true);
                dirPopWindow.show(dirMap, v);
            }
        });
        dirPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dirNameTV.setSelected(false);
            }
        });
        dirPopWindow.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDir = (MediaDir) v.getTag();
                dirNameTV.setText(currentDir.dirName);
                showMediaList(currentDir);
                dirPopWindow.dismiss();
            }
        });
    }

    private void onCancel() {
        onBackPressed();
    }

    private void onNext() {
        Intent intent = new Intent();
        intent.putExtra("selectedPaths", (Serializable) selectedPaths);
        setResult(RESULT_OK, intent);
        finish();
    }

    public MediaDir getMediaDir(String path, MediaType type) {
        MediaDir mediaDir;
        if (dirMap.containsKey(path)) {
            mediaDir = dirMap.get(path);
        } else {
            mediaDir = new MediaDir(path, type);
            dirMap.put(path, mediaDir);
        }
        return mediaDir;
    }

    private File currentTakeMediaFile;

    private void takeMedia(MediaDir mediaDir, MediaType type) {
        switch (type) {
            case PIC:
                currentTakeMediaFile = new File(mediaDir.path, System.currentTimeMillis() + ".jpg");
                Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentTakeMediaFile));
                startActivityForResult(takePicIntent, REQUEST_TAKE_PIC);
                break;
            case VIDEO:
                currentTakeMediaFile = new File(mediaDir.path, System.currentTimeMillis() + ".mp4");
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentTakeMediaFile));
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
                break;
        }


    }

    private void showMediaList(final MediaDir mediaDir) {
        if (mediaListAdapter == null) {
            mediaListAdapter = new MediaListAdapter(this, mediaDir, selectedPaths);
            mediaListAdapter.setOnItemClickListener(new MediaListAdapter.OnItemClickListener() {
                @Override
                public void onPreview(String path) {
                    previewImage(path);
                }

                @Override
                public void onAdd() {
                    takeMedia(mediaDir, mediaDir.type);
                }

                @Override
                public void onCheckedChange(CompoundButton buttonView, boolean isChecked, String path) {
                    if (isChecked) {
                        if (selectedPaths.size() >= maxCount) {
                            Toast.makeText(MediaListActivity.this, String.format(Locale.CHINA, "文件数不能超过%d", maxCount), Toast.LENGTH_SHORT).show();
                            buttonView.setChecked(false);
                        } else {
                            selectedPaths.add(path);
                        }
                    } else {
                        selectedPaths.remove(path);
                    }
                    updateSelectCount(selectedPaths);
                }
            });
            mediaListGV.setAdapter(mediaListAdapter);
        } else {
            mediaListAdapter.setMediaDir(mediaDir);
            mediaListAdapter.notifyDataSetChanged();
        }
    }

    private void previewImage(String path) {
        Intent intent = new Intent(this, PreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("currentPath", path);
        bundle.putSerializable("paths", (Serializable) currentDir.files);
        bundle.putSerializable("type", currentDir.type);
        bundle.putSerializable("selectedPaths", (Serializable) selectedPaths);
        bundle.putInt("maxCount", maxCount);
        intent.putExtra("params", bundle);
        startActivityForResult(intent, REQUEST_PREVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_TAKE_PIC || requestCode == REQUEST_TAKE_VIDEO) {
            String path = currentTakeMediaFile.getPath();
            updateGallery(path);
            currentDir.files.add(0, path);
            if (selectedPaths.size() >= maxCount) {
                Toast.makeText(MediaListActivity.this, String.format(Locale.CHINA, "文件数不能超过%d", maxCount), Toast.LENGTH_SHORT).show();
            } else {
                selectedPaths.add(path);
            }

            showMediaList(currentDir);
            updateSelectCount(selectedPaths);
        }
        if (requestCode == REQUEST_PREVIEW) {
            //selectedPaths = (List<String>) data.getSerializableExtra("selectedPaths"); 这样会导致内存地址改变
            selectedPaths.clear();
            selectedPaths.addAll((List<String>) data.getSerializableExtra("selectedPaths"));

            showMediaList(currentDir);
            updateSelectCount(selectedPaths);
        }
    }

    public void updateGallery(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void updateSelectCount(List<String> selectedPaths) {
        if (selectedPaths.size() > 0) {
            nextBtn.setSelected(true);
            nextBtn.setText("下一步(" + selectedPaths.size() + ")");
            nextBtn.setTextColor(Color.WHITE);
        } else {
            nextBtn.setSelected(false);
            nextBtn.setText("下一步");
            nextBtn.setTextColor(getResources().getColor(R.color.textGreyColor));
        }
    }

    private class LoadMediaListRunnable implements Runnable {

        private MediaType type;

        LoadMediaListRunnable(MediaType type) {
            this.type = type;
        }

        @Override
        public void run() {
            Cursor cursor = null;
            MediaDir totalDir = null;
            switch (type) {
                case PIC:
                    cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE},
                            String.format(Locale.CHINA, "%s=? or %s=?", MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.MIME_TYPE),
                            new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
                    totalDir = getMediaDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), type);
                    break;
                case VIDEO:
                    cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media._ID}, null, null, MediaStore.Images.Media.DATE_MODIFIED);
                    totalDir = getMediaDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), type);
                    break;
            }

            if (cursor == null) {
                throw new NullPointerException(getResources().getString(R.string.cursor_is_null));
            }

            totalDir.dirName = "全部";
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                MediaDir mediaDir = getMediaDir(new File(filePath).getParent(), type);
                if (!mediaDir.files.contains(filePath)) {
                    mediaDir.files.add(filePath);
                }
                if (!totalDir.files.contains(filePath)) {
                    totalDir.files.add(filePath);
                }
            }
            currentDir = totalDir;
            cursor.close();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showMediaList(currentDir);
                }
            });
        }
    }
}
