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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sch.camera.ui.dialog.ProductSelectDialog;
import com.sch.camera.ui.utils.CameraFileUtils;
import com.sch.camera.ui.widget.RoundImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by StoneHui on 2018/8/1.
 * <p>
 * 拍照/视频录制的基类。
 */
public class ManyProductCameraActivity extends BaseCameraActivity implements ProductSelectDialog.OnSelectedListener {

    /**
     * 照片/视频文件地址列表。
     */
    private List<String> mProductFileList = new ArrayList<>();
    /**
     * 照片/视频缩略图文件地址列表。
     */
    private List<String> mProductThumbFileList = new ArrayList<>();

    /**
     * 视图
     */
    private RoundImageView ivProduct;
    private TextView tvCount;
    private ImageView ivVideoLogo;

    /**
     * 文件目录。/storage/emulated/0/Android/data/[packageName]/files/camera/
     */
    private File cameraDir;
    /**
     * 照片/视频文件选择弹窗。
     */
    private ProductSelectDialog mProductSelectDialog;

    @Override
    protected int getContentViewResId() {
        return R.layout.sch_activity_many_product_camera;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProductSelectDialog = new ProductSelectDialog(this, mOptions.getMaxProductCount(), this);

        tvCount = findViewById(R.id.tv_count);

        ivVideoLogo = findViewById(R.id.iv_video_logo);

        ivProduct = findViewById(R.id.iv_product);
        ivProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 显示照片/视频列表。
                mProductSelectDialog.setProductList(view, mProductFileList, mProductThumbFileList);
            }
        });

        // 创建文件保存目录。
        cameraDir = new File(getExternalFilesDir(null), "camera");
        if (cameraDir.exists()) {
            CameraFileUtils.clearDir(cameraDir);
        } else {
            cameraDir.mkdir();
        }
    }

    @Override
    protected void onDestroy() {
        if (mProductSelectDialog != null) {
            mProductSelectDialog.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onPictureTaken(File file, File thumbFile) {
        // 保存并预览拍摄的图片。
        saveProduct(file, mProductFileList);
        saveProduct(thumbFile, mProductThumbFileList);
        showProduct(BitmapFactory.decodeFile(thumbFile.getAbsolutePath()), false);
    }

    @Override
    public void onVideoRecorded(File file, File thumbFile) {
        // 保存并预览拍摄的视频。
        saveProduct(file, mProductFileList);
        saveProduct(thumbFile, mProductThumbFileList);
        showProduct(BitmapFactory.decodeFile(thumbFile.getAbsolutePath()), true);
    }

    @Override
    public void onSelected(List<String> selectedList) {
        // 选择完成，回传结果给上一个界面。
        if (sCallback != null) {
            sCallback.callback(selectedList);
        }
        finish();
    }

    /**
     * 保存图片/视频。
     *
     * @param file     待保存文件。
     * @param saveList 保存到此列表。
     */
    private void saveProduct(File file, List<String> saveList) {
        String fileName = file.getName();
        final String fileNameSuffix = fileName.substring(fileName.indexOf("."));
        final File targetFile = new File(cameraDir, System.currentTimeMillis() + fileNameSuffix);

        saveList.add(targetFile.getAbsolutePath());

        // 保存文件
        CameraFileUtils.copyFile(targetFile, file);
    }

    /**
     * 显示图片/视频。
     *
     * @param thumb   缩略图。
     * @param isVideo 是否是视频。
     */
    private void showProduct(final Bitmap thumb, final boolean isVideo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCount.setText(String.valueOf(mProductFileList.size()));
                ivVideoLogo.setVisibility(isVideo ? View.VISIBLE : View.GONE);
                ivProduct.setImageBitmap(thumb);
            }
        });
    }

}