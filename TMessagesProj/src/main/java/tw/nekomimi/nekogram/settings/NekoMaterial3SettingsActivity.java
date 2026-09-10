package tw.nekomimi.nekogram.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Cells.TextCheckCell;
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
    public int getBaseGuid() {
        return 14000;
    }

    @Override
    public int getDrawable() {
        return R.drawable.msg_theme;
    }

    @Override
    public View createView(Context context) {
        var superView = super.createView(context);

        listAdapter = new ListAdapter(context);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((view, position, x, y) -> {
            AbstractConfigCell cell = cellGroup.rows.get(position);
            if (cell instanceof ConfigCellTextCheck) {
                ((ConfigCellTextCheck) cell).onClick((TextCheckCell) view);
            }
        });
        listView.setOnItemLongClickListener((view, position, x, y) -> {
            var holder = listView.findViewHolderForAdapterPosition(position);
            if (holder != null && listAdapter.isEnabled(holder)) {
                createLongClickDialog(context, NekoMaterial3SettingsActivity.this, "m3", position);
                return true;
            }
            return false;
        });

        cellGroup.callBackSettingsChanged = (key, newValue) -> {
            if (key.equals(NaConfig.INSTANCE.getM3ExpressiveAll().getKey())) {
                boolean all = (boolean) newValue;
                if (NaConfig.INSTANCE.getM3ExpressiveProgress().Bool() != all) NaConfig.INSTANCE.getM3ExpressiveProgress().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3ExpressiveDialogs().Bool() != all) NaConfig.INSTANCE.getM3ExpressiveDialogs().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3SectionCards().Bool() != all) NaConfig.INSTANCE.getM3SectionCards().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3GlassMenu().Bool() != all) NaConfig.INSTANCE.getM3GlassMenu().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3WavySlider().Bool() != all) NaConfig.INSTANCE.getM3WavySlider().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3TabPill().Bool() != all) NaConfig.INSTANCE.getM3TabPill().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3SpringPhysics().Bool() != all) NaConfig.INSTANCE.getM3SpringPhysics().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3ExpressiveVoice().Bool() != all) NaConfig.INSTANCE.getM3ExpressiveVoice().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3ExpressiveSwitch().Bool() != all) NaConfig.INSTANCE.getM3ExpressiveSwitch().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3ExpressiveFab().Bool() != all) NaConfig.INSTANCE.getM3ExpressiveFab().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3ExpressiveBubbles().Bool() != all) NaConfig.INSTANCE.getM3ExpressiveBubbles().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3ExpressiveBottomSheet().Bool() != all) NaConfig.INSTANCE.getM3ExpressiveBottomSheet().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3ExpressivePillSliders().Bool() != all) NaConfig.INSTANCE.getM3ExpressivePillSliders().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3QuoteCard().Bool() != all) NaConfig.INSTANCE.getM3QuoteCard().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3TactileHaptics().Bool() != all) NaConfig.INSTANCE.getM3TactileHaptics().setConfigBool(all);
                if (NaConfig.INSTANCE.getM3FloatingSearchBar().Bool() != all) NaConfig.INSTANCE.getM3FloatingSearchBar().setConfigBool(all);
                refreshM3List();
            } else {
                // Runtime checks use master || child, so keep the master toggle
                // consistent with the individual switches: turning any child off
                // clears the master, and turning the last child on restores it.
                boolean childValue = (boolean) newValue;
                if (!childValue && NaConfig.INSTANCE.getM3ExpressiveAll().Bool()) {
                    NaConfig.INSTANCE.getM3ExpressiveAll().setConfigBool(false);
                    refreshM3List();
                } else if (childValue && !NaConfig.INSTANCE.getM3ExpressiveAll().Bool() && allM3ChildrenOn()) {
                    NaConfig.INSTANCE.getM3ExpressiveAll().setConfigBool(true);
                    refreshM3List();
                }
            }
            if (tooltip != null) {
                tooltip.showWithAction(0, UndoView.ACTION_NEED_RESTART, null, null);
            }
        };

        addRowsToMap();
        cellGroup.setListAdapter(listView, listAdapter);

        return superView;
    }

    private void refreshM3List() {
        if (listView != null) {
            listView.post(() -> {
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private static boolean allM3ChildrenOn() {
        return NaConfig.INSTANCE.getM3ExpressiveProgress().Bool()
                && NaConfig.INSTANCE.getM3ExpressiveDialogs().Bool()
                && NaConfig.INSTANCE.getM3SectionCards().Bool()
                && NaConfig.INSTANCE.getM3GlassMenu().Bool()
                && NaConfig.INSTANCE.getM3WavySlider().Bool()
                && NaConfig.INSTANCE.getM3TabPill().Bool()
                && NaConfig.INSTANCE.getM3SpringPhysics().Bool()
                && NaConfig.INSTANCE.getM3ExpressiveVoice().Bool()
                && NaConfig.INSTANCE.getM3ExpressiveSwitch().Bool()
                && NaConfig.INSTANCE.getM3ExpressiveFab().Bool()
                && NaConfig.INSTANCE.getM3ExpressiveBubbles().Bool()
                && NaConfig.INSTANCE.getM3ExpressiveBottomSheet().Bool()
                && NaConfig.INSTANCE.getM3ExpressivePillSliders().Bool()
                && NaConfig.INSTANCE.getM3QuoteCard().Bool()
                && NaConfig.INSTANCE.getM3TactileHaptics().Bool()
                && NaConfig.INSTANCE.getM3FloatingSearchBar().Bool();
    }

    private class ListAdapter extends BaseListAdapter {
        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean partial, boolean divider) {
            AbstractConfigCell cell = cellGroup.rows.get(position);
            if (cell != null) {
                cell.onBindViewHolder(holder);
            }
        }
    }
}
