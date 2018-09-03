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

import android.app.Activity;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.sch.camera.DefOptions;
import com.sch.camera.FocusRegionHelper;
import com.sch.camera.R;
import com.sch.camera.Size;
import com.sch.camera.VideoRecorder;
import com.sch.camera.annotation.Facing;
import com.sch.camera.annotation.Flash;
import com.sch.camera.widget.AutoFitTextureView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.sch.camera.DefOptions.FLASH_AUTO;
import static com.sch.camera.DefOptions.FLASH_OFF;
import static com.sch.camera.DefOptions.FLASH_ON;
import static com.sch.camera.DefOptions.FLASH_TORCH;

/**
 * Created by StoneHui on 2018/8/10.
 * <p>
 * 相机管理。
 */
public class CameraManager extends BaseCameraManager {

    /**
     * 相机
     */
    private Camera mCamera;
    /**
     * 相机参数
     */
    private Camera.Parameters mCameraParameters;
    /**
     * 相机信息。
     */
    private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();

    /**
     * 当前相机 id。
     */
    private int mCameraId = 0;
    /**
     * 预览尺寸。
     */
    private Size mPictureSize;
    /**
     * 视频尺寸。
     */
    private Size mVideoSize;

    private VideoRecorder mVideoRecorder;

    /**
     * 初始化。
     *
     * @param activity           Activity。
     * @param autoFitTextureView 显示预览的 AutoFitTextureView。
     */
    public CameraManager(@NonNull Activity activity, @NonNull AutoFitTextureView autoFitTextureView) {
        super(activity, autoFitTextureView);
    }

    /**
     * 初始化。
     *
     * @param activity           Activity。
     * @param autoFitTextureView 显示预览的 AutoFitTextureView。
     * @param options            配置项。
     */
    public CameraManager(@NonNull Activity activity, @NonNull AutoFitTextureView autoFitTextureView, @NonNull DefOptions options) {
        super(activity, autoFitTextureView, options);
    }

    @Override
    public void openCamera(int viewWidth, int viewHeight) {
        try {

            // 根据相机方向查找对应的相机。
            Camera.CameraInfo info = new Camera.CameraInfo();
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == mFacing) {
                    mCameraId = i;
                    break;
                }
            }

            Camera.getCameraInfo(mCameraId, mCameraInfo);

            // 打开相机。
            mCamera = Camera.open(mCameraId);

            mCameraParameters = mCamera.getParameters();

            // 设置对焦模式
            List<String> supportedFocusModes = mCameraParameters.getSupportedFocusModes();
            if (isAutoFocus() && supportedFocusModes != null &&
                    supportedFocusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mCameraParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                setAfAvailable(true);
            } else {
                setAfAvailable(false);
            }
            mCamera.cancelAutoFocus();

            // 设置闪关灯模式
            List<String> supportedFlashModes = mCameraParameters.getSupportedFlashModes();
            if (supportedFlashModes != null && !supportedFlashModes.isEmpty()) {
                if (!(supportedFlashModes.size() == 1 && supportedFlashModes.contains(Parameters.FLASH_MODE_OFF))) {
                    setFlashSupport(true);
                    switchFlash(mFlash);
                } else {
                    setFlashSupport(false);
                }
            } else {
                setFlashSupport(false);
            }

            // 设置预览尺寸
            mPreviewSize = getOptimalSize(Size.convert(mCameraParameters.getSupportedPreviewSizes()),
                    viewWidth, viewHeight);
            mCameraParameters.setPreviewSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // 设置照片的尺寸
            mPictureSize = getOptimalSize(Size.convert(mCameraParameters.getSupportedPictureSizes()),
                    mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mCameraParameters.setPictureSize(mPictureSize.getWidth(), mPictureSize.getHeight());

            // 视频尺寸。
            List<Camera.Size> videoSizes = mCameraParameters.getSupportedVideoSizes();
            if (videoSizes == null) {
                videoSizes = mCameraParameters.getSupportedPreviewSizes();
            }
            mVideoSize = getOptimalSize(Size.convert(videoSizes), mPreviewSize.getWidth(), mPreviewSize.getHeight());

            mCamera.setParameters(mCameraParameters);
            mCamera.setDisplayOrientation(getOrientation());
            mCamera.setPreviewTexture(mAutoFitTextureView.getSurfaceTexture());

            // 开始预览。
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            mOnCameraListener.onError(new RuntimeException(mActivity.getString(R.string.sch_create_session_failed)));
        }
    }

    @Override
    public void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void switchCamera(@Facing int facing) {
        if (facing != mFacing) {
            mFacing = facing;
            closeCamera();
            openCamera(mAutoFitTextureView.getWidth(), mAutoFitTextureView.getHeight());
        }
    }

    @Override
    public void switchFlash(@Flash int flash) {
        if (isFlashSupport()) {
            mFlash = flash;
            switch (mFlash) {
                case FLASH_OFF:
                    mCameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                    break;
                case FLASH_ON:
                    mCameraParameters.setFlashMode(Parameters.FLASH_MODE_ON);
                    break;
                case FLASH_TORCH:
                    mCameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    break;
                case FLASH_AUTO:
                    mCameraParameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
                    break;
                default:
                    break;
            }
            mCamera.setParameters(mCameraParameters);
        }
    }

    @Override
    public void takePicture() {
        if (isCapturing()) {
            return;
        }
        setCapturing(true);
        if (isAfAvailable() && isAutoFocus()) {
            mCamera.cancelAutoFocus();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    // 自动对焦成功后拍照。
                    captureStillPicture();
                }
            });
        } else {
            // 直接拍照。
            captureStillPicture();
        }
    }

    @Override
    public void startVideoRecord() throws IOException {
        if (isCapturing()) {
            return;
        }
        setCapturing(true);
        mCamera.unlock();
        // 开始录像。
        mVideoRecorder = new VideoRecorder(mCamera, getRotation(), mVideoSize, mVideoFile.getPath());
        mVideoRecorder.start();
    }

    @Override
    public void stopVideoRecord() throws Exception {
        if (mVideoRecorder == null) {
            return;
        }
        mCamera.lock();
        // 录像结束。
        mVideoRecorder.stop();
        mOnVideoListener.onVideoRecorded(mVideoFile, thumbForVideo(mVideoFile));
        mVideoRecorder = null;
        setCapturing(false);
    }

    @Override
    public void focusOn(AutoFitTextureView view, MotionEvent event) {

        mCamera.cancelAutoFocus();

        // 保存当前的焦点模式。
        final String currentFocusMode = mCameraParameters.getFocusMode();

        // 计算聚焦区域。
        Rect rect = FocusRegionHelper.get(event.getX(), event.getY(), view.getWidth(), view.getHeight());
        List<Camera.Area> areaList = Collections.singletonList(new Camera.Area(rect, 800));

        // 设置聚焦区域。
        if (mCameraParameters.getMaxNumFocusAreas() > 0) {
            mCameraParameters.setFocusAreas(areaList);
            mCameraParameters.setFocusMode(Parameters.FOCUS_MODE_MACRO);
        }
        // 设置测光区域。
        if (mCameraParameters.getMaxNumMeteringAreas() > 0) {
            mCameraParameters.setMeteringAreas(areaList);
        }

        mCamera.setParameters(mCameraParameters);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                // 聚焦结束后恢复焦点模式。
                mCameraParameters.setFocusMode(currentFocusMode);
                camera.setParameters(mCameraParameters);
            }
        });
    }

    @Override
    public void zoomIn() {
        if (!mCameraParameters.isZoomSupported()) {
            return;
        }
        // 放大预览内容。
        int zoom = mCameraParameters.getZoom();
        if (zoom < mCameraParameters.getMaxZoom()) {
            mCameraParameters.setZoom(zoom + 1);
        }
        mCamera.setParameters(mCameraParameters);
    }

    @Override
    public void zoomOut() {
        if (!mCameraParameters.isZoomSupported()) {
            return;
        }
        // 缩小预览内容。
        int zoom = mCameraParameters.getZoom();
        if (zoom > 0) {
            mCameraParameters.setZoom(zoom - 1);
        }
        mCamera.setParameters(mCameraParameters);
    }

    /**
     * 拍照。
     */
    private void captureStillPicture() {
        mCameraParameters.setRotation(getRotation());
        mCamera.setParameters(mCameraParameters);
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // 保存照片。
                savePicture(data);
                // 回调。
                mOnPictureListener.onPictureTaken(mPictureFile, thumbForPicture(mPictureFile));
                camera.cancelAutoFocus();
                // 恢复预览。
                camera.startPreview();
                setCapturing(false);
            }
        });
    }

    /**
     * 获取拍摄方向。
     *
     * @return 方向。
     */
    private int getOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, cameraInfo);
        if (isBackCamera()) {
            return (cameraInfo.orientation - mSensorDegrees + 360) % 360;
        } else {
            return (cameraInfo.orientation + mSensorDegrees + 180) % 360;
        }
    }

    /**
     * 获取拍摄方向。
     *
     * @return 方向。
     */
    private int getRotation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, cameraInfo);
        if (isBackCamera()) {
            return (cameraInfo.orientation - mSensorDegrees + 360) % 360;
        } else {
            return (cameraInfo.orientation + mSensorDegrees + 360) % 360;
        }
    }

}
