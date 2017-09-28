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
    
    
## 注意事项
1. `java.lang.RuntimeException: Unable to resume activity {com.ice.mediapicker/com.ice.picker.activity.MediaListActivity}: java.lang.IllegalStateException: RelativeLayout`

    解决方法：将AppTheme改为NoActionBar。`<style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">`
    
    (如果朋友有更好的解决方法请发邮件至ice45571@163.com)
    
## 日志
1. 2017年09月28日 修复主题非NoActionBar崩溃问题



