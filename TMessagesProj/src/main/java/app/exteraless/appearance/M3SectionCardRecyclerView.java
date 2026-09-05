package app.exteraless.appearance;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

import org.telegram.ui.Components.BlurredRecyclerView;

/**
 * quiNa M3 section-card RecyclerView.
 * Port of exteraless SectionCardRecyclerView but gated by NaConfig.m3SectionCards
 * instead of AppearanceConfig.sectionRadius(). Forces shadows only when the toggle is on.
 */
public class M3SectionCardRecyclerView extends BlurredRecyclerView {

    public M3SectionCardRecyclerView(Context context) {
        super(context);
    }

    @Override
    public void drawBackgroundRect(Canvas canvas, RectF rect, float topRadius, float bottomRadius, float alpha) {
        // Delegate to helper which checks NaConfig and either draws M3 shadows or stock.
        M3SectionCardHelper.drawM3BackgroundRect(canvas, rect, topRadius, bottomRadius, alpha, resourcesProvider);
    }
}
