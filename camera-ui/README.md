# camera-ui

基于 [camera](../camera/) 实现的相机 UI 组件。

## 特性

* 切换摄像头。
* 切换闪光灯状态。
* 拍照(.jpg)。
* 拍视频(.mp4)。
* 点击对焦。
* 双指缩放预览内容。

## 内置 UI 预览

![单拍](./Screenshots/single_shot.gif)
&nbsp;&nbsp;
![多拍](./Screenshots/multi_shot.gif)

## 使用

### Gradle 依赖

```
implementation 'com.sch.camera:camera:1.0.0'
implementation 'com.sch.camera:camera-ui:1.0.0'
```

### 相机配置

```
Camera.Options options = new Camera.Options();

// 设置相机模式，默认 CAMERA_MODE_BOTH。
options.setCameraMode(Camera.Options.CAMERA_MODE_BOTH);

// 设置最大的拍摄照片/视频数量，默认 1。
options.setMaxProductCount(1);

// 设置最大的视频录制时长，默认 10 * 1000L。
options.setMaxVideoRecordTime(10 * 1000L);

// 设置是否仅使用旧版 API ，默认 false。
options.setOnlyOldApi(false);

// 设置是否自动对焦, 默认 true。
options.setAutoFocus(true);

// 设置相机方向, 默认 FACING_BACK。
options.setFacing(DefOptions.FACING_BACK);

// 设置闪光灯状态，默认 FLASH_OFF。
options.setFlash(DefOptions.FLASH_OFF);
```

### 启动相机

使用 [Camera.java](./src/main/java/com/sch/camera/ui/Camera.java) 类启动相机。

```
// 只拍摄一个照片/视频。
Camera.singleShot(...);

// 拍摄一组照片/视频。
Camera.multiShot(...);
```

### 获取拍摄结果

启动相机时传入 [Camera.Callback](./src/main/java/com/sch/camera/ui/Camera.java) 接口的实例接收拍摄结果。

```
Camera.Callback callback = new Camera.Callback() {

    @Override
    public void callback(List<String> fileList) {
        // 拍摄结束，并已选择文件，fileList 为已选择的文件列表。
    }
}
```

# License

```
Copyright (c) 2015-2018 Shi ChaoHui

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```