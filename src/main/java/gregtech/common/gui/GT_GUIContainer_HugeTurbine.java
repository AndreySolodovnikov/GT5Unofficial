package gregtech.common.gui;

import gregtech.api.gui.GT_Container_MultiMachine;
import gregtech.api.gui.GT_GUIContainerMetaTile_Machine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.GT_MetaGenerated_Tool;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GT_GUIContainer_HugeTurbine
        extends GT_GUIContainerMetaTile_Machine {
    public GT_GUIContainer_HugeTurbine(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(new GT_Container_HugeTurbine(aInventoryPlayer, aTileEntity), "gregtech:textures/gui/multimachines/HugeTurbine.png");
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString("Huge Turbine", 10, 8, 16448255);

        if (mContainer != null) {
            if ((((GT_Container_MultiMachine) mContainer).mDisplayErrorCode & 1) != 0)
                fontRendererObj.drawString(trans("132", "Pipe is loose."), 10, 16, 16448255);
            if ((((GT_Container_MultiMachine) mContainer).mDisplayErrorCode & 2) != 0)
                fontRendererObj.drawString(trans("133", "Screws are missing."), 10, 24, 16448255);
            if ((((GT_Container_MultiMachine) mContainer).mDisplayErrorCode & 4) != 0)
                fontRendererObj.drawString(trans("134", "Something is stuck."), 10, 32, 16448255);
            if ((((GT_Container_MultiMachine) mContainer).mDisplayErrorCode & 8) != 0)
                fontRendererObj.drawString(trans("135", "Platings are dented."), 10, 40, 16448255);
            if ((((GT_Container_MultiMachine) mContainer).mDisplayErrorCode & 16) != 0)
                fontRendererObj.drawString(trans("136", "Circuitry burned out."), 10, 48, 16448255);
            if ((((GT_Container_MultiMachine) mContainer).mDisplayErrorCode & 32) != 0)
                fontRendererObj.drawString(trans("137", "That doesn't belong there."), 10, 56, 16448255);
            if ((((GT_Container_MultiMachine) mContainer).mDisplayErrorCode & 64) != 0)
                fontRendererObj.drawString(trans("138", "Incomplete Structure."), 10, 64, 16448255);

            if (((GT_Container_MultiMachine) mContainer).mDisplayErrorCode == 0) {
                if (((GT_Container_MultiMachine) mContainer).mActive == 0) {
                    fontRendererObj.drawString(trans("139", "Hit with Soft Hammer"), 10, 16, 16448255);
                    fontRendererObj.drawString(trans("140", "to (re-)start the Machine"), 10, 24, 16448255);
                    fontRendererObj.drawString(trans("141", "if it doesn't start."), 10, 32, 16448255);
                } else {
                    fontRendererObj.drawString(trans("142", "Running perfectly."), 10, 16, 16448255);
                }
                if(!checkRotors()){
                    fontRendererObj.drawString(trans("144", "Missing Turbine Rotor"), 10, ((GT_Container_MultiMachine) mContainer).mActive == 0 ? 40 : 24, 16448255);
                }

            }
        }
    }

    boolean checkRotors(){
        ItemStack aStack = mContainer.mTileEntity.getStackInSlot(1);
        for(int j = 1;j<6;j++){
            ItemStack tStack = mContainer.mTileEntity.getStackInSlot(j);
            if(tStack==null || !(tStack.getItem() instanceof GT_MetaGenerated_Tool)  || tStack.getItemDamage() < 170 || tStack.getItemDamage() >178)return false;
            if(!GT_Utility.areStacksEqual(aStack, tStack,true)) return false;
        }
        return true;
    }

    public String trans(String aKey, String aEnglish) {
        return GT_LanguageManager.addStringLocalization("Interaction_DESCRIPTION_Index_" + aKey, aEnglish, false);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
