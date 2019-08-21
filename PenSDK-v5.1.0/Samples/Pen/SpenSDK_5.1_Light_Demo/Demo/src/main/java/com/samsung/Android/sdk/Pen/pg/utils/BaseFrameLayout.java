package com.samsung.android.sdk.pen.pg.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by gr218 on 16. 12. 19.
 */

public class BaseFrameLayout extends FrameLayout {

    private int[] mBaseLocation = new int[2];
    private int[] mTargetLocation = new int[2];

    public BaseFrameLayout(Context context) {
        super(context);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        PointerIcon pointerIcon = super.onResolvePointerIcon(event, pointerIndex);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && pointerIcon == null) {
            getLocationOnScreen(mBaseLocation);
            final int x = (int) event.getX(pointerIndex) + mBaseLocation[0];
            final int y = (int) event.getY(pointerIndex) + mBaseLocation[1];
            View targetView = resolveTargetView(this, x, y);
            if (targetView != null) {
                targetView.getLocationOnScreen(mTargetLocation);
                event.offsetLocation(mBaseLocation[0] - mTargetLocation[0], mBaseLocation[1] - mTargetLocation[1]);
                return targetView.onResolvePointerIcon(event, pointerIndex);
            }
        }

        return pointerIcon;
    }

    /**
     * Resolve view containing the specified point recursively
     *
     * @return view if (x,y) are contained by the view or null
     */
    private View resolveTargetView(View view, int x, int y) {
        if (view instanceof ViewGroup) {
            if (contains(view, x, y) && view.getVisibility() == VISIBLE) {
                ViewGroup viewGroup = ((ViewGroup) view);
                final int childrenCount = viewGroup.getChildCount();
                for (int i = childrenCount - 1; i >= 0; i--) {
                    View targetView = resolveTargetView(((ViewGroup) view).getChildAt(i), x, y);
                    if (targetView != null && targetView.getVisibility() == VISIBLE) {
                        return targetView;
                    }
                }

                Drawable background = viewGroup.getBackground();
                if (viewGroup.getAlpha() != 0 && (background != null && background.getAlpha() != 0) && viewGroup.getVisibility() == VISIBLE) {
                    return view;
                }
            }
        } else if (view != null && view.getVisibility() == VISIBLE) {
            if (contains(view, x, y)) {
                return view;
            }
        }

        return null;
    }

    /**
     * Returns true if (x,y) is inside the view
     *
     * @return true if (x,y) are contained by the view
     */
    private boolean contains(View view, int x, int y) {
        view.getLocationOnScreen(mTargetLocation);
        if (mTargetLocation[0] <= x && x <= mTargetLocation[0] + view.getWidth()
                && mTargetLocation[1] <= y && y <= mTargetLocation[1] + view.getHeight()) {
            return true;
        }
        return false;
    }
}