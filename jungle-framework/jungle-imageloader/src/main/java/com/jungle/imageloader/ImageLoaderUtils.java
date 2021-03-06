/**
 * Android Jungle framework project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.jungle.base.misc.JungleSize;
import com.jungle.base.utils.MiscUtils;

import java.io.File;

public class ImageLoaderUtils {

    public static final int ERROR_LOAD_FAILED = -1;
    public static final int ERROR_LOAD_EMPTY_IMAGE = -2;
    public static final int ERROR_HANDLE_IMAGE = -3;


    private static ImageLoaderEngine mImageLoaderEngine;

    public static void initImageLoader(Context context, String imgCachePath) {
        getEngine().initImageLoader(context, imgCachePath);
    }


    public interface ImageLoadListener {
        void onSuccess(Uri uri, Bitmap bitmap);

        void onFailed(int retCode);
    }

    public static ImageLoaderEngine getEngine() {
        if (mImageLoaderEngine == null) {
            mImageLoaderEngine = new FrescoImageLoaderEngine();
        }

        return mImageLoaderEngine;
    }

    public static void setImageLoaderEngine(FrescoImageLoaderEngine engine) {
        mImageLoaderEngine = engine;
    }

    public static String getImageResUri(int resId) {
        return getEngine().getImageResUri(resId);
    }

    public static String getImageAssetUri(String assetPath) {
        return getEngine().getImageAssetUri(assetPath);
    }

    public static String getImageFileUri(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        return Uri.fromFile(new File(filePath)).toString();
    }

    public static void displayImageByDrawableResId(
            ImageView imageView, int drawableResId) {

        getEngine().displayImageByDrawableResId(imageView, drawableResId);
    }

    public static void displayImageByBitmapResId(
            ImageView imageView, int bitmapResId) {

        getEngine().displayImageByBitmapResId(imageView, bitmapResId);
    }

    public static void displayImage(ImageView imageView, String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return;
        }

        try {
            displayImage(imageView, Uri.parse(imgUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayImage(ImageView imageView, Uri uri) {
        getEngine().displayImage(imageView, uri);
    }

    public static void loadImageByResId(int resId, ImageLoadListener listener) {
        if (resId == 0) {
            listener.onFailed(-1);
            return;
        }

        ImageLoaderUtils.loadImage(getImageResUri(resId), listener);
    }

    public static void loadImage(String url, ImageLoadListener listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onFailed(-1);
            return;
        }

        try {
            ImageLoaderUtils.loadImage(Uri.parse(url), listener);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailed(-1);
        }
    }

    public static void loadImage(Uri uri, ImageLoadListener listener) {
        JungleSize size = MiscUtils.getScreenSize();
        int maxSize = Math.max(size.mWidth, size.mHeight);
        getEngine().loadImageInternal(uri, maxSize, listener);
    }

    public static void loadOrigImage(Uri uri, ImageLoadListener listener) {
        getEngine().loadImageInternal(uri, 0, listener);
    }

    public static void prefetchToDiskCache(Uri uri) {
        getEngine().prefetchToDiskCache(uri);
    }

    public static void prefetchToMemoryCache(Uri uri) {
        getEngine().prefetchToMemoryCache(uri);
    }

    public static JungleSize getAppropriateSize(View view) {
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        if (width > 0 && height > 0) {
            return new JungleSize(width, height);
        }

        width = view.getWidth();
        height = view.getHeight();
        if (width > 0 && height > 0) {
            return new JungleSize(width, height);
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            width = params.width;
            height = params.height;

            if (width <= 0 || height <= 0) {
                JungleSize size = getEngine().getAppropriateSize(view, width, height);
                if (size != null) {
                    width = size.mWidth;
                    height = size.mHeight;
                }
            }
        }

        JungleSize size = MiscUtils.getScreenSize();
        if (width <= 0 || height <= 0) {
            width = size.mWidth;
            height = size.mHeight;
        } else {
            if (width > size.mWidth) {
                width = size.mWidth;
            }

            if (height > size.mHeight) {
                height = size.mHeight;
            }
        }

        return new JungleSize(width, height);
    }
}
