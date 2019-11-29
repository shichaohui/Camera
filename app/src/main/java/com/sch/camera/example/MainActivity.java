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

package com.sch.camera.example;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sch.camera.DefOptions;
import com.sch.camera.ui.Camera;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Camera.Callback {

    private Camera.Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options = new Camera.Options();
        // // 设置相机模式，默认 CAMERA_MODE_BOTH。
        // options.setCameraMode(Options.CAMERA_MODE_BOTH);
        // // 设置最大的拍摄照片/视频数量，默认 1。
        // options.setMaxProductCount(1);
        // 设置最大的视频录制时长，默认 10 * 1000L。
        options.setMaxVideoRecordTime(10 * 1000L);
        // 设置是否仅使用旧版 API ，默认 false。
        options.setOnlyOldApi(true);
        // 设置是否自动对焦, 默认 true。
        options.setAutoFocus(true);
        // 设置相机方向, 默认 FACING_BACK。
        options.setFacing(DefOptions.FACING_BACK);
        // 设置闪光灯状态，默认 FLASH_OFF。
        options.setFlash(DefOptions.FLASH_OFF);

        findViewById(R.id.btn_shot_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 拍摄一个照片/视频。
                options.setCameraMode(Camera.Options.CAMERA_MODE_BOTH);
                options.setMaxProductCount(1);
                Camera.singleShot(MainActivity.this, options, MainActivity.this);
            }
        });
        findViewById(R.id.btn_shot_one_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 拍摄一个照片。
                options.setCameraMode(Camera.Options.CAMERA_MODE_PICTURE);
                options.setMaxProductCount(1);
                Camera.singleShot(MainActivity.this, options, MainActivity.this);
            }
        });
        findViewById(R.id.btn_shot_one_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 拍摄一个视频。
                options.setCameraMode(Camera.Options.CAMERA_MODE_VIDEO);
                options.setMaxProductCount(1);
                Camera.singleShot(MainActivity.this, options, MainActivity.this);
            }
        });

        findViewById(R.id.btn_shot_one_video_infinite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 拍摄一个长视频。
                options.setCameraMode(Camera.Options.CAMERA_MODE_VIDEO_INFINITE);
                options.setMaxProductCount(1);
                Camera.singleShot(MainActivity.this, options, MainActivity.this);
            }
        });
        findViewById(R.id.btn_shot_many).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 拍摄多个照片/视频。
                options.setMaxProductCount(9);
                options.setCameraMode(Camera.Options.CAMERA_MODE_BOTH);
                Camera.multiShot(MainActivity.this, options, MainActivity.this);
            }
        });
        findViewById(R.id.btn_shot_many_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 拍摄多个照片。
                options.setMaxProductCount(9);
                options.setCameraMode(Camera.Options.CAMERA_MODE_PICTURE);
                Camera.multiShot(MainActivity.this, options, MainActivity.this);
            }
        });
        findViewById(R.id.btn_shot_many_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 拍摄多个视频。
                options.setMaxProductCount(9);
                options.setCameraMode(Camera.Options.CAMERA_MODE_VIDEO);
                Camera.multiShot(MainActivity.this, options, MainActivity.this);
            }
        });

    }

    @Override
    public void callback(@NonNull List<String> fileList) {
        // 获取拍摄结果。
        Toast.makeText(MainActivity.this, "拍摄结束", Toast.LENGTH_SHORT).show();
        Log.i("CameraResult", "-------------------------------");
        for (String filePath : fileList) {
            Log.i("CameraResult", filePath);
        }
        Log.i("CameraResult", "-------------------------------");
    }

}