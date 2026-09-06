package app.exteraless.appearance;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundProvider;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundProviderBuilder;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceBitmap;
import org.telegram.ui.Components.blur3.utils.Blur3Utils;
import org.telegram.ui.Components.ScrimOptions;

import xyz.nextalone.nagram.NaConfig;

/**
 * Minimal standalone port of exteraless GlassMenuHelper.
 *
 * <p>Original key: AppearanceConfig.glassMessageMenu() && BlurredBackgroundProviderImpl.checkBlurEnabled().
 * This port gates via NaConfig.m3GlassMenu (default false) plus the same blur-enabled check,
 * without importing AppearanceConfig.
 *
 * <p>If blur is unavailable or the toggle is off, callers must leave the normal popup background.
 * This keeps the change compile-safe and reversible. When enabled, behaviour matches exteraless:
 * - scrimMenuBackground: 0.85 dark / 0.76 light alpha, no stroke, shadow 0x26000000 @ 4dp
 * - scrimMenuBackgroundFill: no shadow
 * - panel radius 12dp, padding 8dp
 *
 * <p>Skipped: swipe-back foreground drawable (requires PopupSwipeBackLayout.setForegroundDrawable
 * which does not exist in this tree) - documented as in exteraless.
 */
public abstract class M3GlassMenuHelper {

    private M3GlassMenuHelper() {}

    public static boolean isEnabled(int currentAccount, Theme.ResourcesProvider resourcesProvider) {
        return (NaConfig.INSTANCE.getM3ExpressiveAll().Bool() || NaConfig.INSTANCE.getM3GlassMenu().Bool())
            && BlurredBackgroundProviderImpl.checkBlurEnabled(currentAccount, resourcesProvider);
    }

    public static boolean isEnabled() {
        // Fallback when account/provider not available - just check toggle.
        return NaConfig.INSTANCE.getM3ExpressiveAll().Bool() || NaConfig.INSTANCE.getM3GlassMenu().Bool();
    }

    private static BlurredBackgroundProviderBuilder scrimMenuGlass(Theme.ResourcesProvider resourcesProvider) {
        return new BlurredBackgroundProviderBuilder(resourcesProvider)
            .setBackgroundColor((r, isDark) -> Theme.multAlpha(
                Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), isDark ? 0.85f : 0.76f))
            .setStrokeColorTop(0, 0)
            .setStrokeColorBottom(0, 0)
            .setStrokeWidth(0.0f, 0.0f);
    }

    public static BlurredBackgroundProvider scrimMenuBackground(Theme.ResourcesProvider resourcesProvider) {
        return scrimMenuGlass(resourcesProvider)
            .setShadowColor(0x26000000, 0)
            .setShadowLayer(AndroidUtilities.dpf2(4.0f), 0.0f, 0.0f)
            .build();
    }

    public static BlurredBackgroundProvider scrimMenuBackgroundFill(Theme.ResourcesProvider resourcesProvider) {
        return scrimMenuGlass(resourcesProvider)
            .setShadowColor(0, 0)
            .build();
    }

    public static BlurredBackgroundDrawable createPanelBackground(BlurredBackgroundDrawableViewFactory factory,
                                                                  Theme.ResourcesProvider resourcesProvider,
                                                                  View view) {
        return factory.create(view, true)
            .setColorProvider(scrimMenuBackground(resourcesProvider))
            .setRadius(AndroidUtilities.dp(12.0f))
            .setPadding(AndroidUtilities.dp(8.0f))
            .setHasPadding(true);
    }

    public static BlurredBackgroundDrawable createFill(BlurredBackgroundDrawableViewFactory factory,
                                                       Theme.ResourcesProvider resourcesProvider,
                                                       View view) {
        return factory.create(view, true).setColorProvider(scrimMenuBackgroundFill(resourcesProvider));
    }

    public static void draw(BlurredBackgroundDrawable drawable, Canvas canvas, RectF rect, float radius, int alpha) {
        draw(drawable, canvas, rect, radius, alpha, 0.0f, 0.0f);
    }

    public static void draw(BlurredBackgroundDrawable drawable, Canvas canvas, RectF rect,
                            float radius, int alpha, float offsetX, float offsetY) {
        final boolean translate = offsetX != 0.0f || offsetY != 0.0f;
        if (translate) {
            canvas.save();
            canvas.translate(-offsetX, -offsetY);
        }
        drawable.setRadius(radius);
        drawable.setAlpha(alpha);
        drawable.setBounds(
            Math.round(rect.left + offsetX),
            Math.round(rect.top + offsetY),
            Math.round(rect.right + offsetX),
            Math.round(rect.bottom + offsetY));
        drawable.draw(canvas);
        if (translate) {
            canvas.restore();
        }
    }

    public static void applyToGaps(View view, int color) {
        applyToGaps(view, color, false);
    }

    public static void applyToGaps(View view, int color, boolean dividerVisible) {
        if (view instanceof ActionBarPopupWindow.GapView) {
            final ActionBarPopupWindow.GapView gapView = (ActionBarPopupWindow.GapView) view;
            gapView.setColor(color);
            gapView.setDividerVisible(dividerVisible);
        } else if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                applyToGaps(viewGroup.getChildAt(i), color, dividerVisible);
            }
        }
    }

    public static void applyToPopup(BlurredBackgroundDrawableViewFactory factory,
                                    Theme.ResourcesProvider resourcesProvider,
                                    ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout) {
        popupLayout.setBackground(createPanelBackground(factory, resourcesProvider, popupLayout));
    }

    public static void applyToReusedMenu(BlurredBackgroundDrawableViewFactory factory,
                                         BlurredBackgroundSourceBitmap sourceBitmap,
                                         Theme.ResourcesProvider resourcesProvider,
                                         ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout,
                                         View rootView,
                                         boolean glass) {
        if (popupLayout == null) {
            return;
        }
        if (glass) {
            applyToPopup(factory, resourcesProvider, popupLayout);
            applyToGaps(popupLayout, separatorColor(true, resourcesProvider));
            captureBlur(sourceBitmap, factory, rootView);
        } else {
            final Drawable background = popupLayout.getResources().getDrawable(org.telegram.messenger.R.drawable.popup_fixed_alert4).mutate();
            popupLayout.setBackgroundDrawable(background);
            popupLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, resourcesProvider));
            applyToGaps(popupLayout, separatorColor(false, resourcesProvider), true);
        }
    }

    public static void captureBlur(final BlurredBackgroundSourceBitmap sourceBitmap,
                                   final BlurredBackgroundDrawableViewFactory factory,
                                   final View view) {
        ScrimOptions.makeGlobalBlurBitmaps((bitmapBg, bitmapOptions) -> {
            sourceBitmap.setBitmap(bitmapOptions);
            Blur3Utils.checkBitmapSourceMatrixScale(sourceBitmap, view);
            factory.invalidateAllLinkedViews();
        });
    }

    public static int separatorColor(boolean glass, Theme.ResourcesProvider resourcesProvider) {
        if (!glass) {
            return Theme.getColor(Theme.key_actionBarDefaultSubmenuSeparator, resourcesProvider);
        }
        final boolean isDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
        return Theme.multAlpha(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider),
            isDark ? 0.03f : 0.06f);
    }
}
