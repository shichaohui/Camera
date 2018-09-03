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

package com.sch.camera;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by StoneHui on 2018/8/10.
 * <p>
 * 视频录制。
 */
public class VideoRecorder {

    private MediaRecorder mMediaRecorder = new MediaRecorder();

    private boolean isRecording = false;

    /**
     * 初始化。
     *
     * @param camera      相机对象。
     * @param orientation 摄像方向。
     * @param size        视频尺寸。
     * @param filePath    视频文件保存路径。
     */
    public VideoRecorder(Camera camera, int orientation, Size size, String filePath) {
        mMediaRecorder.setCamera(camera);
        init(orientation, size, filePath, MediaRecorder.VideoSource.CAMERA);
    }

    /**
     * 初始化。
     *
     * @param orientation 摄像方向。
     * @param size        视频尺寸。
     * @param filePath    视频文件保存路径。
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoRecorder(int orientation, Size size, String filePath) {
        init(orientation, size, filePath, MediaRecorder.VideoSource.SURFACE);
    }

    private void init(int orientation, Size size, String filePath, int videoSource) {
        mMediaRecorder.reset();
        mMediaRecorder.setOrientationHint(orientation);

        mMediaRecorder.setVideoSource(videoSource);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(filePath);

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoSize(size.getWidth(), size.getHeight());
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoEncodingBitRate(6 * 1024 * 1024);

        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }

    /**
     * 是否正在录制。
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * 开始录制。
     *
     * @throws IOException MediaRecorder.prepare() 可能抛出异常。
     */
    public void start() throws IOException {
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            release();
            throw e;
        }
    }

    /**
     * 结束录制。
     *
     * @throws Exception MediaRecorder.stop() 可能会抛出异常。
     */
    public void stop() throws Exception {
        try {
            // 录制时间太短，stop 会抛异常。
            mMediaRecorder.stop();
        } finally {
            release();
        }
    }

    /**
     * 释放资源。
     */
    private void release() {
        isRecording = false;
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Surface getSurface() {
        return mMediaRecorder == null ? null : mMediaRecorder.getSurface();
    }

}