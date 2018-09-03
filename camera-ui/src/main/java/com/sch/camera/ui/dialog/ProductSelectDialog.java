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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sch.camera.manager.ICameraManager;
import com.sch.camera.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by StoneHui on 2018/8/22.
 * <p>
 * 选择弹窗。
 */
public class ProductSelectDialog extends BaseFullScreenDialog implements CompoundButton.OnCheckedChangeListener {

    /**
     * 图片/视频选择监听器。
     */
    public interface OnSelectedListener {
        /**
         * 选择结束。
         *
         * @param selectedList 被选中的文件路径列表。
         */
        void onSelected(List<String> selectedList);
    }

    private ViewGroup layoutRoot;
    private RecyclerView rcvProduct;
    private ImageButton ibtnClose;
    private TextView tvComplete;

    /**
     * 最大选择数量。
     */
    private int maxSelectCount;
    /**
     * 选择监听器。
     */
    private OnSelectedListener mOnSelectedListener;

    /**
     * 数据适配器。
     */
    private ProductAdapter mProductAdapter;

    /**
     * 数据列表。
     */
    private List<Product> mProductList = new ArrayList<>();
    /**
     * 已被选中的数据列表。
     */
    private List<String> mSelectedProductList = new ArrayList<>();

    private int[] anchorXY = new int[2];
    private final long RIPPER_DURATION = 300L;

    public ProductSelectDialog(@NonNull Context context, int maxSelectCount, OnSelectedListener onCompleteListener) {
        super(context, false);
        this.maxSelectCount = maxSelectCount;
        this.mOnSelectedListener = onCompleteListener;

        layoutRoot = findViewById(R.id.layout_root);

        mProductAdapter = new ProductAdapter(getLayoutInflater(), mProductList, this);

        rcvProduct = findViewById(R.id.rcv_product);
        rcvProduct.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rcvProduct.setAdapter(mProductAdapter);

        ibtnClose = findViewById(R.id.ibtn_close);
        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvComplete = findViewById(R.id.tv_complete);
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 完成选择。
                mOnSelectedListener.onSelected(mSelectedProductList);
            }
        });
    }

    @Override
    int getContentViewResId() {
        return R.layout.sch_dialog_product_select;
    }

    @Override
    public void show() {
        super.show();
        layoutRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                layoutRoot.removeOnLayoutChangeListener(this);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ViewAnimationUtils.createCircularReveal(layoutRoot,
                            anchorXY[0], anchorXY[1],
                            0, Math.max(layoutRoot.getWidth(), layoutRoot.getHeight()))
                            .setDuration(RIPPER_DURATION)
                            .start();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(layoutRoot,
                    anchorXY[0], anchorXY[1],
                    Math.max(layoutRoot.getWidth(), layoutRoot.getHeight()), 0);
            animator.setDuration(RIPPER_DURATION);
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animator) {
                    ProductSelectDialog.super.onBackPressed();
                }

            });
            animator.start();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        /*  某图片/视频的选中状态改变。 */
        Product product = (Product) compoundButton.getTag();
        if (isChecked) {
            if (mSelectedProductList.size() >= maxSelectCount) {
                // 选中数据已经达到上限。
                Toast.makeText(getContext(), R.string.sch_max_product_count, Toast.LENGTH_SHORT).show();
                compoundButton.setChecked(false);
                return;
            } else if (!mSelectedProductList.contains(product.path)) {
                // 选中。
                product.isSelected = true;
                mSelectedProductList.add(product.path);
            }
        } else {
            // 解除选中。
            product.isSelected = false;
            mSelectedProductList.remove(product.path);
        }
        // 设置"完成"按钮状态。
        if (mSelectedProductList.isEmpty()) {
            tvComplete.setEnabled(false);
            tvComplete.setText(R.string.sch_complete);
        } else {
            tvComplete.setEnabled(true);
            tvComplete.setText(String.format(getContext().getString(R.string.sch_complete_format),
                    mSelectedProductList.size(), maxSelectCount));
        }
    }

    /**
     * 设置待选择的图片/视频数据列表。
     *
     * @param animAnchorView   动画锚点视图。
     * @param productList      图片/视频文件地址列表。
     * @param productThumbList 图片/视频缩略图文件地址列表。
     */
    public void setProductList(View animAnchorView, List<String> productList, List<String> productThumbList) {
        animAnchorView.getLocationOnScreen(anchorXY);
        anchorXY[0] += animAnchorView.getWidth() / 2;
        anchorXY[1] += animAnchorView.getHeight() / 2;

        for (int i = mProductList.size(); i < productList.size(); i++) {
            mProductList.add(0, new Product(productList.get(i), productThumbList.get(i)));
        }

        mProductAdapter.notifyDataSetChanged();
        show();
    }

    /**
     * 图片/视频数据实体。
     */
    private static class Product {

        String path;
        String thumbPath;
        boolean isSelected;

        private Bitmap bitmap;

        Product(String path, String thumbPath) {
            this.path = path;
            this.thumbPath = thumbPath;
        }

        boolean isVideo() {
            return path.endsWith(ICameraManager.VIDEO_TYPE);
        }

        Bitmap getBitmap() {
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeFile(thumbPath);
            }
            return bitmap;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Product product = (Product) o;
            return path.equals(product.path);
        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }
    }

    /**
     * 图片/视频数据适配器。
     */
    private static class ProductAdapter extends RecyclerView.Adapter {

        private LayoutInflater mLayoutInflater;
        private List<Product> mProductList;
        private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

        ProductAdapter(LayoutInflater layoutInflater, List<Product> productList,
                       CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
            mLayoutInflater = layoutInflater;
            mProductList = productList;
            mOnCheckedChangeListener = onCheckedChangeListener;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.sch_item_product_select, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            Product product = mProductList.get(viewHolder.getAdapterPosition());
            ((ViewHolder) viewHolder).cbSelect.setTag(product);
            ((ViewHolder) viewHolder).ivProduct.setImageBitmap(product.getBitmap());
            ((ViewHolder) viewHolder).cbSelect.setOnCheckedChangeListener(mOnCheckedChangeListener);
            ((ViewHolder) viewHolder).cbSelect.setChecked(product.isSelected);
            ((ViewHolder) viewHolder).ivVideoLogo.setVisibility(product.isVideo() ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return mProductList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivProduct;
            CheckBox cbSelect;
            ImageView ivVideoLogo;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProduct = itemView.findViewById(R.id.iv_product);
                cbSelect = itemView.findViewById(R.id.cb_select);
                ivVideoLogo = itemView.findViewById(R.id.iv_video_logo);
            }

        }

    }

}