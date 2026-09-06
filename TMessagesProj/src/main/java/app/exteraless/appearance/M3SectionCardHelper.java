package app.exteraless.appearance;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

import xyz.nextalone.nagram.NaConfig;

/**
 * Standalone M3 Expressive section-card helper extracted from exteraless.
 *
 * <p>Original: {@code SectionCardRecyclerView} forced elevation shadows locally
 * and used {@code AppearanceConfig.sectionRadius()} (16-20dp). This port removes
 * the {@code AppearanceConfig} dependency and gates via {@link NaConfig#m3SectionCards}.
 *
 * <p>Radius: 18dp when enabled (inside 16-20dp M3 Expressive range), 16dp fallback via
 * {@link org.telegram.ui.Components.RecyclerListView#setSections()} default.
 * Shadows are forced only when M3 cards are enabled, matching exteraless
 * {@code forceShadows = true} behaviour but without touching global
 * {@code SharedConfig.shadowsInSections}.
 *
 * <p>Skipped (documented):
 * <ul>
 *   <li>{@code MainTabsUiHelper} (M3 nav bar shape) - requires MainTabsHelper compact mode
 *       and blur infrastructure; lightweight alternative is not needed for settings cards.
 *   </li>
 *   <li>{@code ChatHeaderUiHelper} (M3 avatar/header) - requires ProfileTransitionState and
 *       ChatAvatarContainer layout changes; too much exteraless infra. Left as future work;
 *       settings cards alone satisfy the acceptance criteria.
 *   </li>
 * </ul>
 */
public final class M3SectionCardHelper {

    private M3SectionCardHelper() {}

    public static boolean isEnabled() {
        return NaConfig.INSTANCE.getM3ExpressiveAll().Bool() || NaConfig.INSTANCE.getM3SectionCards().Bool();
    }

    /** M3 Expressive card corner radius: 18dp when enabled, otherwise 0 (stock delegates). */
    public static float sectionRadiusPx() {
        // Check directly; do not cache to react to toggle without restart (after recreation).
        return isEnabled() ? AndroidUtilities.dp(18) : 0;
    }

    public static int sectionPaddingPx() {
        return AndroidUtilities.dp(12);
    }

    // Lightweight shadow-forced draw, ported from SectionCardRecyclerView.drawBackgroundRect.
    // Kept standalone so callers can delegate without subclassing.
    private static final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Path path = new Path();
    private static final float[] radii = new float[8];

    public static void drawM3BackgroundRect(Canvas canvas, RectF rect, float topRadius, float bottomRadius, float alpha, Theme.ResourcesProvider resourcesProvider) {
        if (!isEnabled() || sectionRadiusPx() <= 0) {
            // Stock path: respect SharedConfig.shadowsInSections via RecyclerListView static helper.
            org.telegram.ui.Components.RecyclerListView.drawBackgroundRect(canvas, rect, topRadius, bottomRadius, alpha, resourcesProvider);
            return;
        }
        // Forced shadows - same values as exteraless SectionCardRecyclerView:
        // stroke shadow 0.33dp @ 0x0C000000, fill shadow 2dp + 0.33dp @ 0x0A000000
        strokePaint.setColor(0);
        strokePaint.setShadowLayer(AndroidUtilities.dpf2(0.33f), 0, 0, Theme.multAlpha(0x0C000000, alpha));
        fillPaint.setShadowLayer(AndroidUtilities.dpf2(2f), 0, AndroidUtilities.dpf2(0.33f), Theme.multAlpha(0x0A000000, alpha));
        fillPaint.setColor(Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider), alpha));

        if (topRadius == bottomRadius) {
            canvas.drawRoundRect(rect, topRadius, topRadius, strokePaint);
            canvas.drawRoundRect(rect, topRadius, topRadius, fillPaint);
        } else {
            path.rewind();
            radii[0] = radii[1] = radii[2] = radii[3] = topRadius;
            radii[4] = radii[5] = radii[6] = radii[7] = bottomRadius;
            path.addRoundRect(rect, radii, Path.Direction.CW);
            canvas.drawPath(path, strokePaint);
            canvas.drawPath(path, fillPaint);
        }
    }

    /**
     * Apply M3 section styling to a RecyclerListView.
     * When enabled: padding 12dp, radius 18dp, M3 shadow draw callback.
     * When disabled: stock behaviour (RecyclerListView default 16dp handled by caller,
     * or disableSections if caller wants flat).
     */
    public static void applyToRecyclerListView(org.telegram.ui.Components.RecyclerListView listView) {
        if (isEnabled()) {
            listView.setSections(
                view -> !(view instanceof org.telegram.ui.Cells.TextInfoPrivacyCell
                    || view instanceof org.telegram.ui.Cells.ShadowSectionCell
                    || view instanceof org.telegram.ui.Cells.GraySectionCell
                    || view instanceof org.telegram.ui.Cells.CollapseTextCell)
                    && !java.util.Objects.equals(view.getTag(), org.telegram.ui.Components.RecyclerListView.TAG_NOT_SECTION),
                sectionPaddingPx(),
                sectionRadiusPx(),
                (c, rect, topR, bottomR, alpha) -> drawM3BackgroundRect(c, rect, topR, bottomR, alpha, null),
                false
            );
        } else {
            // Stock: 12dp padding, 16dp radius, default drawBackgroundRect (no forced shadow)
            listView.setSections();
        }
    }

    public static void applyToRecyclerListView(org.telegram.ui.Components.RecyclerListView listView, Theme.ResourcesProvider resourcesProvider) {
        if (isEnabled()) {
            listView.setSections(
                view -> !(view instanceof org.telegram.ui.Cells.TextInfoPrivacyCell
                    || view instanceof org.telegram.ui.Cells.ShadowSectionCell
                    || view instanceof org.telegram.ui.Cells.GraySectionCell
                    || view instanceof org.telegram.ui.Cells.CollapseTextCell)
                    && !java.util.Objects.equals(view.getTag(), org.telegram.ui.Components.RecyclerListView.TAG_NOT_SECTION),
                sectionPaddingPx(),
                sectionRadiusPx(),
                (c, rect, topR, bottomR, alpha) -> drawM3BackgroundRect(c, rect, topR, bottomR, alpha, resourcesProvider),
                false
            );
        } else {
            listView.setSections();
        }
    }
}
