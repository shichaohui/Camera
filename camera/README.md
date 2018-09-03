# camera

基于 Camera 和 Camera2 实现的相机 API 组件。

## 特性

* 切换摄像头。
* 切换闪光灯状态。
* 拍照(.jpg)。
* 拍视频(.mp4)。
* 点击对焦。
* 双指缩放预览内容。
* 基于 Camera 和 Camera2 实现。

## 使用

### Gradle 依赖

```
implementation project(':camera')
```

### API

#### 初始化 [ICameraManager](./src/main/java/com/sch/camera/manager/ICameraManager.java)

```
Options options = new Options();
options.setAutoFocus(true); // 设置是否自动对焦, 默认 true。
options.setFacing(DefOptions.FACING_BACK); // 设置相机方向, 默认 FACING_BACK。
options.setFlash(DefOptions.FLASH_OFF); // 设置闪光灯状态，默认 FLASH_OFF。

ICameraManager manager;
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
        Camera2Manager.isSupported(this, mOptions.getFacing())) {
    manager = new Camera2Manager(this, autoFitTextureView, options);
} else {
    manager = new CameraManager(this, autoFitTextureView, options);
}
```

#### 设置监听

```
// 设置相机监听
mCameraManager.setOnCameraListener(new OnCameraListener() {

    @Override
    public void onFlashSupport(boolean isSupport) {
        // 是否支持闪光灯
        ibtnFlash.setVisibility(isSupport ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onError(Exception e) {
        toast(e.getMessage());
        finish();
    }
});

// 设置拍照监听
mCameraManager.setOnPictureListener(new OnPictureListener() {
    @Override
    public void onPictureTaken(File file, File thumbFile) {
       // 拍照成功
    }
});

// 设置拍视频监听
mCameraManager.setOnVideoListener(new OnVideoListener() {
    @Override
    public void onVideoRecorded(File file, File thumbFile) {
       // 拍视频成功
    }
});
```

#### 暂停和恢复

```
public class CameraActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraManager != null) {
            mCameraManager.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraManager != null) {
            mCameraManager.onPause();
        }
    }

}
```

#### 其他 API

详见 [ICameraManager](./src/main/java/com/sch/camera/manager/ICameraManager.java) 接口。

```
/**
 * Created by StoneHui on 2018/8/7.
 * <p>
 * 相机管理接口。
 */
public interface ICameraManager {

    String PICTURE_TYPE = ".jpg";
    String VIDEO_TYPE = ".mp4";

    /**
     * 恢复相机状态，在 Activity.onResume() 中调用。
     */
    void onResume();

    /**
     * 暂停相机，在 Activity.onPause() 中调用。
     */
    void onPause();

    /**
     * 打开相机。
     *
     * @param viewWidth  预览视图的宽度。
     * @param viewHeight 预览视图的高度。
     */
    void openCamera(int viewWidth, int viewHeight);

    /**
     * 关闭相机。
     */
    void closeCamera();

    /**
     * 切换相机。
     *
     * @param facing 指定相机的方向。
     */
    void switchCamera(@Facing int facing);

    /**
     * 切换闪光灯状态。
     *
     * @param flash 指定闪光灯状态。
     */
    void switchFlash(@Flash int flash);

    /**
     * 拍照。
     */
    void takePicture();

    /**
     * 开始摄像。
     *
     * @throws IOException 初始化 MediaRecorder 时可能抛出异常。
     */
    void startVideoRecord() throws IOException;

    /**
     * 结束录像。
     *
     * @throws Exception MediaRecorder.stop() 可能抛出异常。
     */
    void stopVideoRecord() throws Exception;

    /**
     * 设置相机监听器。
     *
     * @param listener 相机监听器。
     */
    void setOnCameraListener(OnCameraListener listener);

    /**
     * 设置拍照监听器。
     *
     * @param listener 拍照监听器。
     */
    void setOnPictureListener(OnPictureListener listener);

    /**
     * 设置录像监听器。
     *
     * @param listener 录像监听器。
     */
    void setOnVideoListener(OnVideoListener listener);

    /**
     * 设置是否可以自动对焦。
     *
     * @param autoFocus 是否可自动对焦。
     */
    void setAutoFocus(boolean autoFocus);

    /**
     * 是否可以自动对焦。
     */
    boolean isAutoFocus();

    /**
     * 对焦。
     *
     * @param view  预览视图。
     * @param event 预览视图的动作事件。
     */
    void focusOn(AutoFitTextureView view, MotionEvent event);

    /**
     * 放大预览。
     */
    void zoomIn();

    /**
     * 缩小预览。
     */
    void zoomOut();

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