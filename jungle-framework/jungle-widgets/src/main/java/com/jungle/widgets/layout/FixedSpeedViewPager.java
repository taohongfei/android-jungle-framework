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

package com.jungle.widgets.layout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class FixedSpeedViewPager extends ViewPager {

    public interface OnSetItemListener {
        void onSetNewItem();
    }


    private FixedSpeedScroller mScroller;
    private OnSetItemListener mSetItemListener;


    public FixedSpeedViewPager(Context context) {
        super(context);

        initFixedSpeed();
    }

    public FixedSpeedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        initFixedSpeed();
    }

    public void setSetItemListener(OnSetItemListener listener) {
        mSetItemListener = listener;
    }

    /**
     * 设置滚动时长因子. factor 越大,滚动所花时间越长.
     */
    public void setScrollDurationFactor(float factor) {
        if (mScroller != null) {
            mScroller.setScrollDurationFactor(factor);
        }
    }

    private void initFixedSpeed() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            if (scroller != null && interpolator != null) {
                scroller.setAccessible(true);
                interpolator.setAccessible(true);

                mScroller = new FixedSpeedScroller(getContext(),
                        (Interpolator) interpolator.get(null));
                scroller.set(this, mScroller);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentItemManual(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);

        if (mSetItemListener != null) {
            mSetItemListener.onSetNewItem();
        }
    }

    private static class FixedSpeedScroller extends Scroller {

        private float mScrollFactor = 1;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context,
                Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        public void setScrollDurationFactor(float factor) {
            mScrollFactor = factor;
        }

        @Override
        public void startScroll(
                int startX, int startY, int dx, int dy, int duration) {

            super.startScroll(
                    startX, startY, dx, dy, (int) (duration * mScrollFactor));
        }
    }
}
