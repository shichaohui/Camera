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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sch.camera.DefOptions;
import com.sch.camera.annotation.Facing;
import com.sch.camera.listener.OnCameraListener;
import com.sch.camera.listener.OnPictureListener;
import com.sch.camera.listener.OnVideoListener;
import com.sch.camera.manager.Camera2Manager;
import com.sch.camera.manager.CameraManager;
import com.sch.camera.manager.ICameraManager;
import com.sch.camera.ui.addition.Flash;
import com.sch.camera.ui.widget.FocusView;
import com.sch.camera.ui.widget.MagicButton;
import com.sch.camera.widget.AutoFitTextureView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by StoneHui on 2018/8/1.
 * <p>
 * 拍照/视频录制的基类。
 */
public abstract class BaseCameraActivity extends Activity implements OnPictureListener, OnVideoListener {

    /**
     * Intent 传递配置选项的 key。
     */
    protected static final String INTENT_KEY_OPTIONS = "options";

    protected static Camera.Callback sCallback;

    /**
     * 权限
     */
    private final int permissionRequestCode = 103;
    private final String[] permissionArray = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 闪光灯组
     */
    private final List<Flash> videoFlashList = Arrays.asList(Flash.OFF, Flash.TORCH);
    private final List<Flash> flashList = Arrays.asList(Flash.OFF, Flash.ON, Flash.AUTO, Flash.TORCH);

    /**
     * 视图
     */
    protected AutoFitTextureView autoFitTextureView;
    protected FocusView focusView;
    private MagicButton mbCapture;
    private ImageButton ibtnFlash;
    private ImageButton ibtnSwitchCamera;

    /**
     * 配置项
     */
    protected Camera.Options mOptions;

    /**
     * 相机管理器
     */
    protected ICameraManager mCameraManager;

    /**
     * 相机方向
     */
    @Facing
    private int mFacing;
    /**
     * 默认闪光灯
     */
    private Flash mFlash;
    /**
     * 录像闪光灯
     */
    private Flash mVideoFlash;

    /**
     * 是否正在录像
     */
    private boolean isVideoRecorder = false;

    /**
     * 获取布局资源 id。
     *
     * @return 布局资源 id
     */
    @LayoutRes
    protected abstract int getContentViewResId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置 4.4 及以上版本导航栏透明。
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);

        initOptions();

        initView();

        if (requestCameraPermission()) {
            initCameraManager();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        setContentView(getContentViewResId());

        findViewById(R.id.ibtn_go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 预览
        autoFitTextureView = findViewById(R.id.aftv_preview);
        autoFitTextureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // 对焦及缩放处理。
                int pointerCount = event.getPointerCount();
                if (event.getAction() == MotionEvent.ACTION_DOWN && pointerCount == 1) {
                    focusOn((AutoFitTextureView) view, event);
                } else if (pointerCount == 2) {
                    zoom(event);
                }
                return true;
            }
        });

        // 焦点
        focusView = findViewById(R.id.focus_view);
        focusView.bringToFront();

        // 闪光灯
        ibtnFlash = findViewById(R.id.ibtn_flash);
        ibtnFlash.setImageResource(mOptions.getCameraMode() == Camera.Options.CAMERA_MODE_VIDEO ?
                mVideoFlash.getIcon() : mFlash.getIcon());
        ibtnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFlash();
            }
        });

        // 切换相机
        ibtnSwitchCamera = findViewById(R.id.ibtn_switch_camera);
        ibtnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFacing = mFacing == DefOptions.FACING_BACK ? DefOptions.FACING_FRONT : DefOptions.FACING_BACK;
                mCameraManager.switchCamera(mFacing);
            }
        });

        // 拍照及录像
        mbCapture = findViewById(R.id.mb_record);
        mbCapture.setMaxLongClickTime(mOptions.getMaxVideoRecordTime());
        mbCapture.setOnMagicClickedListener(new MagicButton.OnMagicClickedListener() {
            @Override
            public void onClicked() {
                mCameraManager.takePicture();
            }

            @Override
            public void onLongClickStart() {
                try {
                    isVideoRecorder = true;
                    switchFlash(mVideoFlash);
                    mCameraManager.startVideoRecord();
                } catch (IOException e) {
                    e.printStackTrace();
                    mbCapture.cancel();
                    toast(getString(R.string.sch_start_video_record_failed));
                }
            }

            @Override
            public void onLongClickStop() {
                try {
                    isVideoRecorder = false;
                    switchFlash(mFlash);
                    mCameraManager.stopVideoRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mOptions.getCameraMode() == Camera.Options.CAMERA_MODE_BOTH) {
                        mCameraManager.takePicture();
                    }
                }
            }
        });

        // 按钮操作提示。
        switch (mOptions.getCameraMode()) {
            case Camera.Options.CAMERA_MODE_BOTH:
                mbCapture.setText(String.format("%s %s", getString(R.string.sch_click_4_picture),
                        getString(R.string.sch_long_click_4_video_record)));
                break;
            case Camera.Options.CAMERA_MODE_PICTURE:
                mbCapture.setText(getString(R.string.sch_click_4_picture));
                mbCapture.setLongClickable(false);
                break;
            case Camera.Options.CAMERA_MODE_VIDEO:
                mbCapture.setText(getString(R.string.sch_long_click_4_video_record));
                mbCapture.setClickable(false);
                break;
            default:
                break;
        }
    }

    /**
     * 获取配置项。
     */
    private void initOptions() {
        mOptions = (Camera.Options) getIntent().getSerializableExtra(INTENT_KEY_OPTIONS);

        mFacing = mOptions.getFacing();

        Flash flash = Flash.get(mOptions.getFlash());
        mFlash = flashList.contains(flash) ? flash : flashList.get(0);
        mVideoFlash = videoFlashList.contains(flash) ? flash : videoFlashList.get(0);
    }

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

    @Override
    public void finish() {
        sCallback = null;
        super.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != permissionRequestCode) {
            return;
        }
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, R.string.sch_missing_permission, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        initCameraManager();
    }

    /**
     * 申请权限。
     */
    protected boolean requestCameraPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissionArray) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissionArray, permissionRequestCode);
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化相机。
     */
    private void initCameraManager() {
        if (mOptions.isOnlyOldApi()) {
            mCameraManager = new CameraManager(this, autoFitTextureView, mOptions);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                Camera2Manager.isSupported(this, mOptions.getFacing())) {
            mCameraManager = new Camera2Manager(this, autoFitTextureView, mOptions);
        } else {
            mCameraManager = new CameraManager(this, autoFitTextureView, mOptions);
        }
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
        mCameraManager.setOnPictureListener(this);
        mCameraManager.setOnVideoListener(this);
    }

    /**
     * 切换闪光灯。
     */
    private void switchFlash() {
        if (isVideoRecorder) {
            switchFlash(mVideoFlash = videoFlashList.get((videoFlashList.indexOf(mVideoFlash) + 1) % videoFlashList.size()));
        } else {
            switchFlash(mFlash = flashList.get((flashList.indexOf(mFlash) + 1) % flashList.size()));
        }
    }

    /**
     * 切换闪光灯。
     */
    private void switchFlash(Flash flash) {
        ibtnFlash.setImageResource(flash.getIcon());
        mCameraManager.switchFlash(flash.getValue());
    }

    /**
     * 聚焦。
     */
    private void focusOn(AutoFitTextureView view, MotionEvent event) {
        mCameraManager.focusOn(view, event);
        focusView.focusOn(event.getX(), event.getY());
    }

    private float prevFingerSpacing = -1;

    /**
     * 缩放。
     *
     * @param event 触摸事件。
     */
    private void zoom(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            prevFingerSpacing = -1;
            return;
        }
        float fingerSpacing = getFingerSpacing(event);
        if (prevFingerSpacing < 0) {
            prevFingerSpacing = fingerSpacing;
            return;
        }
        int fingerSpacingValid = 15;
        if (Math.abs(fingerSpacing - prevFingerSpacing) < fingerSpacingValid) {
            return;
        }
        if (fingerSpacing > prevFingerSpacing) {
            mCameraManager.zoomIn();
        } else {
            mCameraManager.zoomOut();
        }
        prevFingerSpacing = fingerSpacing;
    }

    /**
     * 获取手指间的距离。
     *
     * @param event 触摸事件。
     */
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Toast。
     */
    protected void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseCameraActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}