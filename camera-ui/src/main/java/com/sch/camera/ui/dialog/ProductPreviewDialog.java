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

package com.sch.camera.ui.dialog;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.sch.camera.ui.R;

import java.io.File;

/**
 * Created by StoneHui on 2018/8/22.
 * <p>
 * 图片/视频预览弹窗。
 */
public class ProductPreviewDialog extends BaseFullScreenDialog implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener {

    private ImageView mIvPicture;
    private VideoView mVvVideo;

    private View.OnClickListener mOnConfirmListener;

    private Handler mHandler = new Handler();

    /**
     * 初始化
     *
     * @param context           Context。
     * @param onConfirmListener 确认预览内容的监听器。
     */
    public ProductPreviewDialog(@NonNull Context context, View.OnClickListener onConfirmListener) {
        super(context);
        mOnConfirmListener = onConfirmListener;

        findViewById(R.id.ibtn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 返回。
                onBackPressed();
            }
        });

        findViewById(R.id.ibtn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 确定预览内容。
                mOnConfirmListener.onClick(view);
                hide();
            }
        });

        mIvPicture = findViewById(R.id.iv_picture);

        mVvVideo = findViewById(R.id.vv_video);
        mVvVideo.setOnPreparedListener(this);
        mVvVideo.setOnCompletionListener(this);
    }

    @Override
    int getContentViewResId() {
        return R.layout.sch_dialog_product_preview;
    }

    @Override
    public void hide() {
        // 隐藏视图。
        mIvPicture.setVisibility(View.GONE);
        mVvVideo.stopPlayback();
        mVvVideo.setVisibility(View.GONE);
        super.hide();
    }

    @Override
    public void onBackPressed() {
        hide();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // 播放视频。
        mVvVideo.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // 防止黑屏
            mVvVideo.setOnInfoListener(this);
        } else {
            // 防止黑屏
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 隐藏占位图片。
                    mIvPicture.setVisibility(View.GONE);
                }
            }, 200L);
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            // 隐藏占位图片。
            mIvPicture.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // 循环播放。
        mVvVideo.start();
    }

    /**
     * 预览图片。
     *
     * @param path 图片地址。
     */
    public void showPicture(String path) {
        mIvPicture.setImageBitmap(BitmapFactory.decodeFile(path));
        mIvPicture.setVisibility(View.VISIBLE);
        show();
    }

    /**
     * 预览视频。
     *
     * @param path 视频地址。
     */
    public void showVideo(String path) {
        // 占位图防止黑屏。
        mIvPicture.setImageBitmap(ThumbnailUtils.createVideoThumbnail(path, Thumbnails.FULL_SCREEN_KIND));
        mIvPicture.setVisibility(View.VISIBLE);
        mVvVideo.setVideoURI(Uri.fromFile(new File(path)));
        // 视频视图延时显示。
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVvVideo.setVisibility(View.VISIBLE);
            }
        }, 50L);
        show();
    }

}