package com.localhostloader.ui.components;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.localhostloader.ui.theme.DesignTokens;

public final class ComponentExtensions {
    private static int dp(Context c, int dp) {
        return (int) (dp * c.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static View createAppCard(Context c, String name, String version,
                                     View.OnClickListener click, View.OnLongClickListener longClick) {
        LinearLayout card = new LinearLayout(c);
        card.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(c, DesignTokens.Spacing.MD);
        int padV = dp(c, DesignTokens.Spacing.SM);
        card.setPadding(pad, padV, pad, padV);
        TextView nameView = new TextView(c);
        nameView.setText(name);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DesignTokens.Typography.H2_SP);
        nameView.setTextColor(DesignTokens.Color.ON_SURFACE);
        card.addView(nameView);
        if (version != null && !version.isEmpty()) {
            TextView verView = new TextView(c);
            verView.setText(version);
            verView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DesignTokens.Typography.CAPTION_SP);
            card.addView(verView);
        }
        card.setOnClickListener(click);
        card.setOnLongClickListener(longClick);
        return card;
    }

    private ComponentExtensions() {}
}
