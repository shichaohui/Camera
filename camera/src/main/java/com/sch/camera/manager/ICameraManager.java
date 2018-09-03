/*
 * Copyright (c) 2015-2018 Shi ChaoHui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sch.camera.manager;

import android.view.MotionEvent;

import com.sch.camera.annotation.Facing;
import com.sch.camera.annotation.Flash;
import com.sch.camera.listener.OnCameraListener;
import com.sch.camera.listener.OnPictureListener;
import com.sch.camera.listener.OnVideoListener;
import com.sch.camera.widget.AutoFitTextureView;

import java.io.IOException;

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
     *
     * @return 是否可以自动对焦。
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
