package tw.nekomimi.nekogram.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.UndoView;

import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.cell.AbstractConfigCell;
import tw.nekomimi.nekogram.config.cell.ConfigCellDivider;
import tw.nekomimi.nekogram.config.cell.ConfigCellHeader;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextCheck;
import xyz.nextalone.nagram.NaConfig;

@SuppressLint("RtlHardcoded")
public class NekoMaterial3SettingsActivity extends BaseNekoXSettingsActivity {

    private final CellGroup a = cellGroup = new CellGroup(this);

    // --- Master toggle ---
    private final AbstractConfigCell headerMaster = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("M3ExpressiveAll", R.string.M3ExpressiveAll)));
    private final AbstractConfigCell m3ExpressiveAllRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3ExpressiveAll()));
    private final AbstractConfigCell dividerMaster = cellGroup.appendCell(new ConfigCellDivider());

    // --- Shapes & Surfaces ---
    private final AbstractConfigCell headerSurfaces = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("Appearance", R.string.Appearance)));
    private final AbstractConfigCell m3FloatingSearchBarRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3FloatingSearchBar()));
    private final AbstractConfigCell m3QuoteCardRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3QuoteCard()));
    private final AbstractConfigCell m3SectionCardsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3SectionCards()));
    private final AbstractConfigCell m3GlassMenuRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3GlassMenu()));
    private final AbstractConfigCell m3ExpressiveBottomSheetRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3ExpressiveBottomSheet()));
    private final AbstractConfigCell m3ExpressiveBubblesRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3ExpressiveBubbles()));
    private final AbstractConfigCell m3ExpressiveDialogsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3ExpressiveDialogs()));
    private final AbstractConfigCell dividerSurfaces = cellGroup.appendCell(new ConfigCellDivider());

    // --- Controls & Motion ---
    private final AbstractConfigCell headerControls = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("General", R.string.General)));
    private final AbstractConfigCell m3ExpressiveSwitchRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3ExpressiveSwitch()));
    private final AbstractConfigCell m3ExpressiveFabRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3ExpressiveFab()));
    private final AbstractConfigCell m3TabPillRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3TabPill()));
    private final AbstractConfigCell m3WavySliderRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3WavySlider()));
    private final AbstractConfigCell m3ExpressiveVoiceRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3ExpressiveVoice()));
    private final AbstractConfigCell m3ExpressivePillSlidersRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3ExpressivePillSliders()));
    private final AbstractConfigCell m3ExpressiveProgressRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3ExpressiveProgress()));
    private final AbstractConfigCell m3SpringPhysicsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3SpringPhysics()));
    private final AbstractConfigCell m3TactileHapticsRow = cellGroup.appendCell(new ConfigCellTextCheck(NaConfig.INSTANCE.getM3TactileHaptics()));
    private final AbstractConfigCell dividerControls = cellGroup.appendCell(new ConfigCellDivider());

    @Override
    public String getTitle() {
        return "Material 3 Expressive";
    }

    @Override
    public View createView(Context context) {
        View view = super.createView(context);
        cellGroup.callBackSettingsChanged = (key, newValue) -> {
            if (key.equals(NaConfig.INSTANCE.getM3ExpressiveAll().getKey())) {
                boolean all = (boolean) newValue;
                NaConfig.INSTANCE.getM3ExpressiveProgress().setConfigBool(all);
                NaConfig.INSTANCE.getM3ExpressiveDialogs().setConfigBool(all);
                NaConfig.INSTANCE.getM3SectionCards().setConfigBool(all);
                NaConfig.INSTANCE.getM3GlassMenu().setConfigBool(all);
                NaConfig.INSTANCE.getM3WavySlider().setConfigBool(all);
                NaConfig.INSTANCE.getM3TabPill().setConfigBool(all);
                NaConfig.INSTANCE.getM3SpringPhysics().setConfigBool(all);
                NaConfig.INSTANCE.getM3ExpressiveVoice().setConfigBool(all);
                NaConfig.INSTANCE.getM3ExpressiveSwitch().setConfigBool(all);
                NaConfig.INSTANCE.getM3ExpressiveFab().setConfigBool(all);
                NaConfig.INSTANCE.getM3ExpressiveBubbles().setConfigBool(all);
                NaConfig.INSTANCE.getM3ExpressiveBottomSheet().setConfigBool(all);
                NaConfig.INSTANCE.getM3ExpressivePillSliders().setConfigBool(all);
                NaConfig.INSTANCE.getM3QuoteCard().setConfigBool(all);
                NaConfig.INSTANCE.getM3TactileHaptics().setConfigBool(all);
                NaConfig.INSTANCE.getM3FloatingSearchBar().setConfigBool(all);
                if (listView != null) {
                    listView.post(() -> {
                        if (listAdapter != null) {
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
            if (tooltip != null) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESTART, null, null);
            }
        };
        return view;
    }
}
