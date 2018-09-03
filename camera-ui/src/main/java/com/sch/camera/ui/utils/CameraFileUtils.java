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

package com.sch.camera.ui.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by StoneHui on 2018/8/21.
 * <p>
 * 文件工具类。
 */
public class CameraFileUtils {

    /**
     * 保存图片。
     *
     * @param targetPath 保存地址。
     * @param bitmap     图片。
     */
    public static void save(String targetPath, Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(targetPath));
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
    }

    /**
     * 复制文件。
     *
     * @param targetFile 目标地址。
     * @param originPath 源文件。
     */
    public static void copyFile(File targetFile, File originPath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(originPath);
            fos = new FileOutputStream(targetFile);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) > 0) {
                fos.write(bytes, 0, length);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件。
     *
     * @param file 文件。
     * @return 删除是否成功。
     */
    public static boolean deleteFile(File file) {
        return !file.exists() || file.isFile() && file.delete();
    }

    /**
     * 清理目录内容，但不删除目录。
     *
     * @param file 目录。
     * @return 是否清理成功。
     */
    public static boolean clearDir(File file) {
        if (!file.exists() || !file.isDirectory()) {
            return true;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return true;
        }
        for (File f : files) {
            if (f.isFile()) {
                deleteFile(f);
            } else if (f.isDirectory()) {
                clearDir(f);
                f.delete();
            }
        }
        return true;
    }

}