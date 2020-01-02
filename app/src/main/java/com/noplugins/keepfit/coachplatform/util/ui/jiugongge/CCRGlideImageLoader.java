/**
 * Copyright 2016 bingoogolapple
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noplugins.keepfit.coachplatform.util.ui.jiugongge;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;


/**
 * 在此写用途
 *
 * @Author: Acheng
 * @Email: 345887272@qq.com
 * @Date: 2017-09-22 16:47
 * @Version: V1.0 <描述当前版本功能>
 */
public class CCRGlideImageLoader extends CCRImageLoader {

    @Override
    public void display(final ImageView imageView, Uri uri, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final DisplayDelegate delegate) {
//        final String finalPath = getPath(path);
        Activity activity = getActivity(imageView);
        Glide.with(activity).load(uri).placeholder(loadingResId).error(failResId).override(width, height).dontAnimate().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (delegate != null) {
                    delegate.onSuccess(imageView, uri);
                }
                return false;
            }
        }).into(imageView);
    }

    @Override
    public void download(Uri uri, final DownloadDelegate delegate) {
//        final String finalPath = getPath(path);
//        Glide.with(CCRPhotoPickerUtil.sApp).asBitmap().load(finalPath).into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                if (delegate != null) {
//                    delegate.onSuccess(finalPath, resource);
//                }
//            }
//
//            @Override
//            public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                if (delegate != null) {
//                    delegate.onFailed(finalPath);
//                }
//            }
//        });
        Glide.with(CCRPhotoPickerUtil.sApp).asBitmap().load(uri).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (delegate != null) {
                    delegate.onSuccess(uri, resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (delegate != null) {
                    delegate.onFailed(uri);
                }
            }
        });
    }

    @Override
    public void pause(Activity activity) {
        Glide.with(activity).pauseRequests();
    }

    @Override
    public void resume(Activity activity) {
        Glide.with(activity).resumeRequestsRecursive();
    }
}