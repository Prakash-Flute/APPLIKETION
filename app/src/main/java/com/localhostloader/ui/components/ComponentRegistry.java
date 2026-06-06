package com.localhostloader.ui.components;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import com.localhostloader.ui.theme.DesignTokens;

public final class ComponentRegistry {
    public static View createPrimaryButton(Context c, String text, View.OnClickListener listener) {
        Button btn = new Button(c);
        btn.setText(text);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, DesignTokens.Typography.BODY_SP);
        btn.setTextColor(DesignTokens.Color.ON_PRIMARY);
        btn.setBackgroundColor(DesignTokens.Color.PRIMARY);
        int pad = (int) (DesignTokens.Spacing.MD * c.getResources().getDisplayMetrics().density + 0.5f);
        btn.setPadding(pad, pad/2, pad, pad/2);
        btn.setOnClickListener(listener);
        return btn;
    }
    private ComponentRegistry() {}
}
