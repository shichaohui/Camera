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

package com.sch.camera.listener;

import java.io.File;

/**
 * Created by StoneHui on 2018/8/8.
 * <p>
 * 拍照监听。
 */
public interface OnPictureListener {

    /**
     * 拍照成功。
     *
     * @param file      照片文件。
     * @param thumbFile 缩略图文件。
     */
    void onPictureTaken(File file, File thumbFile);

}
