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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.sch.camera.ui.dialog.ProductPreviewDialog;

import java.io.File;
import java.util.Collections;

/**
 * Created by StoneHui on 2018/8/1.
 * <p>
 * 拍照/视频录制的基类。
 */
public class OneProductCameraActivity extends BaseCameraActivity {

    /**
     * 图片/视频路径。
     */
    private String mProductFilePath;

    /**
     * 图片/视频预览弹窗。
     */
    private ProductPreviewDialog mPreviewDialog;

    @Override
    protected int getContentViewResId() {
        return R.layout.sch_activity_one_product_camera;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreviewDialog = new ProductPreviewDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 确认预览内容，将图片/视频回传给上一个界面。
                if (sCallback != null) {
                    sCallback.callback(Collections.singletonList(mProductFilePath));
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mPreviewDialog != null) {
            mPreviewDialog.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onPictureTaken(File file, File thumbFile) {
        // 预览拍摄的图片。
        mProductFilePath = file.getAbsolutePath();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPreviewDialog.showPicture(mProductFilePath);
            }
        });
    }

    @Override
    public void onVideoRecorded(File file, File thumbFile) {
        // 预览拍摄的视频。
        mProductFilePath = file.getAbsolutePath();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPreviewDialog.showVideo(mProductFilePath);
            }
        });
    }

}