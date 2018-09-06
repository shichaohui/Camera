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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Surface;
import android.view.TextureView;

import com.sch.camera.DefOptions;
import com.sch.camera.Size;
import com.sch.camera.annotation.Facing;
import com.sch.camera.annotation.Flash;
import com.sch.camera.annotation.SensorDegrees;
import com.sch.camera.listener.OnCameraListener;
import com.sch.camera.listener.OnPictureListener;
import com.sch.camera.listener.OnVideoListener;
import com.sch.camera.widget.AutoFitTextureView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by StoneHui on 2018/8/8.
 * <p>
 * 相机管理基类。
 */
abstract class BaseCameraManager implements ICameraManager, SensorEventListener, TextureView.SurfaceTextureListener {

    Activity mActivity;
    AutoFitTextureView mAutoFitTextureView;

    /**
     * 当前相机是否支持调焦。
     */
    private boolean isAfAvailable = false;
    /**
     * 当前相机是否支持闪光灯。
     */
    private boolean isFlashSupport = false;
    /**
     * 是否支持自动对焦。
     */
    private boolean isAutoFocus = true;

    /**
     * 是否正在拍摄。
     */
    private boolean isCapturing = false;

    /**
     * 相机预览尺寸。
     */
    Size mPreviewSize;

    /**
     * 相机方向。
     */
    @Facing
    int mFacing;
    /**
     * 摄像机模式。
     */
    @Flash
    int mFlash;

    /**
     * 拍摄图片的保存路径。
     * getExternalFilesDir(null)/camera_picture.mp4
     * /storage/emulated/0/Android/data/[packageName]/files/camera_picture.jpg
     */
    File mPictureFile;
    /**
     * 拍摄视频的保存路径。
     * getExternalFilesDir(null)/camera_video.mp4
     * /storage/emulated/0/Android/data/[packageName]/files/camera_video.mp4
     */
    File mVideoFile;

    /**
     * 相机监听器。
     */
    OnCameraListener mOnCameraListener;
    /**
     * 拍照监听器。
     */
    OnPictureListener mOnPictureListener;
    /**
     * 拍视频的监听器。
     */
    OnVideoListener mOnVideoListener;

    /**
     * 传感器管理器。
     */
    private SensorManager sensorManager;
    /**
     * 传感器角度。
     */
    @SensorDegrees
    int mSensorDegrees = SENSOR_UP;

    /**
     * 初始化。
     *
     * @param activity           Activity。
     * @param autoFitTextureView 显示预览的 AutoFitTextureView。
     */
    BaseCameraManager(@NonNull Activity activity, @NonNull AutoFitTextureView autoFitTextureView) {
        this(activity, autoFitTextureView, new DefOptions());
    }

    /**
     * 初始化。
     *
     * @param activity           Activity。
     * @param autoFitTextureView 显示预览的 AutoFitTextureView。
     * @param options            配置项。
     */
    BaseCameraManager(@NonNull Activity activity, @NonNull AutoFitTextureView autoFitTextureView, @NonNull DefOptions options) {
        mActivity = activity;
        mAutoFitTextureView = autoFitTextureView;
        isAutoFocus = options.isAutoFocus();
        mFacing = options.getFacing();
        mFlash = options.getFlash();

        mVideoFile = new File(activity.getExternalFilesDir(null), String.format("camera_video%s", VIDEO_TYPE));
        mPictureFile = new File(activity.getExternalFilesDir(null), String.format("camera_picture%s", PICTURE_TYPE));

        sensorManager = (SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);
    }

    @Override
    public void onResume() {
        // 注册传感器监听器。
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        if (mAutoFitTextureView.isAvailable() && mAutoFitTextureView.getSurfaceTexture() != null) {
            // 打开相机。
            openCamera(mAutoFitTextureView.getWidth(), mAutoFitTextureView.getHeight());
        } else {
            // 设置预览视图监听。
            mAutoFitTextureView.setSurfaceTextureListener(this);
        }
    }

    @Override
    public void onPause() {
        // 关闭相机。
        closeCamera();
        sensorManager.unregisterListener(this);
        mAutoFitTextureView.setSurfaceTextureListener(null);
    }

    @Override
    public void setOnCameraListener(OnCameraListener listener) {
        mOnCameraListener = listener;
    }

    @Override
    public void setOnPictureListener(OnPictureListener listener) {
        this.mOnPictureListener = listener;
    }

    @Override
    public void setOnVideoListener(OnVideoListener listener) {
        this.mOnVideoListener = listener;
    }

    @Override
    public void setAutoFocus(boolean autoFocus) {
        this.isAutoFocus = autoFocus;
    }

    @Override
    public boolean isAutoFocus() {
        return isAutoFocus;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // 传感器方向发生改变。
        synchronized (this) {
            if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                return;
            }
            final int oldDegrees = mSensorDegrees;
            float minXY = -1.5F, maxXY = 1.5F;
            float x = sensorEvent.values[0], y = sensorEvent.values[1];
            if (x < maxXY && x > minXY) {
                if (y > maxXY) {
                    mSensorDegrees = SENSOR_UP;
                } else if (y < minXY) {
                    mSensorDegrees = SENSOR_DOWN;
                }
            } else if (y < maxXY && y > minXY) {
                if (x > maxXY) {
                    mSensorDegrees = SENSOR_LEFT;
                } else if (x < minXY) {
                    mSensorDegrees = SENSOR_RIGHT;
                }
            }
            if (mSensorDegrees != oldDegrees) {
                mOnCameraListener.onSensorChanged(oldDegrees, mSensorDegrees);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
        // 打开相机。
        openCamera(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
        // 给 TextureView 配置 Matrix 转换。在相机预览尺寸和 TextureView 尺寸确定之后调用该函数。
        if (mAutoFitTextureView == null || mPreviewSize == null) {
            return;
        }
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, width, height);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) height / mPreviewSize.getHeight(),
                    (float) width / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mAutoFitTextureView.setTransform(matrix);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
    }

    /**
     * 当前是否正在使用后置摄像头。
     */
    boolean isBackCamera() {
        return mFacing == DefOptions.FACING_BACK;
    }

    /**
     * 是否支持调焦。
     */
    boolean isAfAvailable() {
        return isAfAvailable;
    }

    /**
     * 设置是否支持调焦。
     *
     * @param afAvailable 是否支持调焦。
     */
    void setAfAvailable(boolean afAvailable) {
        isAfAvailable = afAvailable;
    }

    /**
     * 是否支持闪光灯。
     */
    boolean isFlashSupport() {
        return isFlashSupport;
    }

    /**
     * 设置是否支持闪光灯。
     *
     * @param flashSupport 是否支持闪光灯。
     */
    void setFlashSupport(boolean flashSupport) {
        isFlashSupport = flashSupport;
        mOnCameraListener.onFlashSupport(isFlashSupport);
    }

    /**
     * 是否正在拍摄。
     */
    boolean isCapturing() {
        return isCapturing;
    }

    /**
     * 设置是否正在拍摄。
     *
     * @param capturing 是否正在拍摄。
     */
    void setCapturing(boolean capturing) {
        isCapturing = capturing;
    }

    /**
     * 获取合适的预览尺寸。
     *
     * @param sizeList   支持的尺寸列表。
     * @param viewWidth  view的宽度。
     * @param viewHeight view的高度。
     */
    Size getOptimalSize(List<Size> sizeList, int viewWidth, int viewHeight) {

        if (sizeList == null) {
            return new Size(viewWidth, viewHeight);
        }

        final double aspectTolerance = 0.1;
        double targetRatio = (double) viewHeight / viewWidth;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Size size : sizeList) {
            double ratio = (double) size.getWidth() / size.getHeight();
            if (Math.abs(ratio - targetRatio) > aspectTolerance) {
                continue;
            }
            if (Math.abs(size.getHeight() - viewHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.getHeight() - viewHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizeList) {
                if (Math.abs(size.getHeight() - viewHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.getHeight() - viewHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * 保存图片。
     *
     * @param imageBytes 图片字节数组。
     */
    void savePicture(byte[] imageBytes) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mPictureFile);
            if (isBackCamera()) {
                output.write(imageBytes);
            } else {
                // 水平翻转前置摄像头的图片。
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                Matrix matrix = new Matrix();
                matrix.setScale(-1, 1);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 为图片保存缩略图。
     *
     * @param file 原文件。
     * @return 缩略图文件。
     */
    File thumbForPicture(File file) {
        // 生成缩略图。
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap thumb = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return saveBitmap(file.getAbsolutePath().replace(PICTURE_TYPE, String.format("_thumb%s", PICTURE_TYPE)), thumb);
    }

    /**
     * 为视频保存缩略图。
     *
     * @param file 原文件。
     * @return 缩略图文件。
     */
    File thumbForVideo(File file) {
        // 生成缩略图。
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
        return saveBitmap(file.getAbsolutePath().replace(VIDEO_TYPE, String.format("_thumb%s", PICTURE_TYPE)), thumb);
    }

    /**
     * 保存图片。
     */
    private File saveBitmap(String targetPath, Bitmap bitmap) {
        File file = new File(targetPath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
