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

package com.sch.camera.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.sch.camera.DefOptions;
import com.sch.camera.manager.ICameraManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Created by StoneHui on 2018/8/13.
 * <p>
 * 相机。
 */
public class Camera {

    /**
     * 以单拍模式启动相机。
     *
     * @param activity Activity。
     * @param callback 回调。
     */
    public static void singleShot(@NonNull Activity activity, @NonNull Camera.Callback callback) {
        singleShot(activity, new Options(), callback);
    }

    /**
     * 以单拍模式启动相机。
     *
     * @param activity Activity。
     * @param options  配置选项。
     * @param callback 回调。
     */
    public static void singleShot(@NonNull Activity activity, @NonNull Options options, @NonNull Camera.Callback callback) {
        OneProductCameraActivity.sCallback = callback;
        Intent intent = new Intent(activity, OneProductCameraActivity.class);
        intent.putExtra(BaseCameraActivity.INTENT_KEY_OPTIONS, options);
        activity.startActivity(intent);
    }

    /**
     * 以多拍模式启动相机。
     *
     * @param activity Activity。
     * @param count    可拍摄数量。
     * @param callback 回调。
     */
    public static void multiShot(@NonNull Activity activity, int count, @NonNull Camera.Callback callback) {
        Options options = new Options();
        options.setMaxProductCount(count);
        multiShot(activity, options, callback);
    }

    /**
     * 以多拍模式启动相机。
     *
     * @param activity Activity。
     * @param options  配置选项。
     * @param callback 回调。
     */
    public static void multiShot(@NonNull Activity activity, @NonNull Options options, @NonNull Camera.Callback callback) {
        ManyProductCameraActivity.sCallback = callback;
        Intent intent = new Intent(activity, ManyProductCameraActivity.class);
        intent.putExtra(BaseCameraActivity.INTENT_KEY_OPTIONS, options);
        activity.startActivity(intent);
    }

    /**
     * 回调
     */
    public interface Callback {

        /**
         * 拍摄结束并已选择文件时的回调函数。
         *
         * @param fileList 已选择的文件列表。
         *                 图片文件后缀：{@link ICameraManager#PICTURE_TYPE}，
         *                 视频文件后缀：{@link ICameraManager#VIDEO_TYPE}。
         */
        void callback(@NonNull List<String> fileList);

    }

    /**
     * Created by StoneHui on 2018/8/20.
     * <p>
     * 相机配置。
     */
    public static class Options extends DefOptions {

        /**
         * 相机模式，既能拍摄图片，也能拍摄视频。
         */
        public static final int CAMERA_MODE_BOTH = 0;
        /**
         * 相机模式，只能拍摄图片。
         */
        public static final int CAMERA_MODE_PICTURE = 1;
        /**
         * 相机模式，只能拍摄视频。
         */
        public static final int CAMERA_MODE_VIDEO = 2;

        /**
         * 是否仅使用旧版 API 。
         */
        private boolean isOnlyOldApi = false;
        /**
         * 最大的视频录制时长。
         */
        private long maxVideoRecordTime = 10 * 1000L;
        /**
         * 最大的拍摄照片/视频数量。
         */
        private int maxProductCount = 1;
        /**
         * 相机模式。
         */
        @CameraMode
        private int cameraMode = CAMERA_MODE_BOTH;

        /**
         * 是否仅使用旧版 API。
         */
        public boolean isOnlyOldApi() {
            return isOnlyOldApi;
        }

        /**
         * 设置是否仅使用旧版 API。
         *
         * @param onlyOldApi 是否仅使用旧版 API。
         */
        public void setOnlyOldApi(boolean onlyOldApi) {
            isOnlyOldApi = onlyOldApi;
        }

        /**
         * 获取最大的视频录制时长。
         */
        public long getMaxVideoRecordTime() {
            return maxVideoRecordTime;
        }

        /**
         * 设置最大的视频录制时长。
         *
         * @param maxVideoRecordTime 最大的视频录制时长，单位 ms。
         */
        public void setMaxVideoRecordTime(@IntRange(from = 0) long maxVideoRecordTime) {
            this.maxVideoRecordTime = maxVideoRecordTime;
        }

        /**
         * 获取最大的拍摄照片/视频数量。
         */
        public int getMaxProductCount() {
            return maxProductCount;
        }

        /**
         * 设置最大的拍摄照片/视频数量。
         *
         * @param maxProductCount 最大的拍摄照片/视频数量。
         */
        public void setMaxProductCount(@IntRange(from = 1) int maxProductCount) {
            this.maxProductCount = maxProductCount;
        }

        /**
         * 获取相机模式。
         */
        @CameraMode
        public int getCameraMode() {
            return cameraMode;
        }

        /**
         * 设置相机模式。
         *
         * @param cameraMode 相机模式。
         */
        public void setCameraMode(@CameraMode int cameraMode) {
            this.cameraMode = cameraMode;
        }

        @Retention(RetentionPolicy.SOURCE)
        @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
        @IntDef({CAMERA_MODE_BOTH, CAMERA_MODE_PICTURE, CAMERA_MODE_VIDEO})
        @interface CameraMode {
        }

    }

}