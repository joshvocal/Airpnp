package joshvocal.me.client.util;

import android.content.Context;
import android.util.TypedValue;

public class ViewUtil {

    private ViewUtil() {
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;

        int defaultStatusBarHeightDp = 24;

        if (context != null) {
            int resourceId = context.getResources()
                    .getIdentifier("status_bar_height", "dimen", "android");

            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            } else {
                result = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        defaultStatusBarHeightDp, context.getResources().getDisplayMetrics()));
            }
        }

        return result;
    }
}
