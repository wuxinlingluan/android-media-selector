# android-media-selector

\[Android画廊效果\]图片,视频选择控件

## 功能
* 文件选择
* 单次操作文件最多选择数
* 文件预览
* 视频播放
* 文件夹分组
* 文件新建

## 功能预览
![](/example/example.jpg)
![视频](/example/video.gif)
![图片](/example/pic.gif)

## 导入项目
1.在项目的build.gradle文件中添加
```
allprojects {
    repositories {
          ...
          maven { url 'https://jitpack.io' }
    }
 }
 ```
2.在app模块下的build.gradle文件中添加依赖
```
dependencies {
      compile 'com.github.ice45571:android-media-selector:1.0.0'
 }
```

## 使用
1.图片选择
```
Intent i = new Intent(context, MediaListActivity.class);
i.putExtra("type", MediaType.VIDEO);
startActivityForResult(i, REQUEST_PIC);
```

2.视频选择
```
Intent i = new Intent(context, MediaListActivity.class);
i.putExtra("type", MediaType.PIC);
startActivityForResult(i, REQUEST_VIDEO);
```

3.回调
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
        List<String> selectedPaths = (List<String>) data.getSerializableExtra("selectedPaths");
    }
}
```
其中selectedPaths集合存放的就是你选择的文件的绝对地址

4.其他参数
* 最大选择数

    如果你想限制每次最多选择x张图片或视频,可以在跳转MediaListActivity时额外传入一个整形参数
    
    `i.putExtra("maxCount", x);`

