package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.api.util.multiblock.GT_MultiBlockUtility;
import gregtech.api.util.multiblock.GT_SimpleBlockChecker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

import static gregtech.api.enums.GT_Values.V;

public class GT_MetaTileEntity_HugeImplosionCompressor
        extends GT_MetaTileEntity_MultiBlockBase {
    public GT_MetaTileEntity_HugeImplosionCompressor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_HugeImplosionCompressor(String aName) {
        super(aName);
        mUtility = new GT_MultiBlockUtility(true,true, mStructure,new GT_SimpleBlockChecker[]{
                new GT_SimpleBlockChecker('C', GregTech_API.sBlockCasings2,(byte)0,0,1000, Arrays.asList(new Integer[]{1,2,5,7,8}),90)
        });
    }

    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_HugeImplosionCompressor(this.mName);
    }

    public String[] getDescription() {
        return new String[]{
                "Controller Block for the Implosion Compressor",
                "Use Holographic projector to render the structure",
                "1x Input Bus (Any casing)",
                "1x Output Bus (Any casing)",
                "1x Maintenance Hatch (Any casing)",
                "1x Muffler Hatch (Any casing)",
                "1x Energy Hatch (Any casing)",
                "Solid Steel Machine Casings for the rest (90 at least!)",
                "Same recipes as regular Implosion Compressor, but can overclock",
                "Causes " + 20 * getPollutionPerTick(null) + " Pollution per second"};
    }

    public static String[][] mStructure = new String[][]{
            {
                    "sssss",
                    "sCCCs",
                    "sCCCs",
                    "sCCCs",
                    "sssss"},
            {
                    "sCCCs",
                    "CCCCC",
                    "CCCCC",
                    "CCCCC",
                    "sCCCs"},
            {
                    "CCCCC",
                    "CCCCC",
                    "CCaCC",
                    "CCCCC",
                    "CCCCC"},
            {
                    "CCCCC",
                    "CCCCC",
                    "CCCCC",
                    "CCCCC",
                    "CCCCC"},
            {
                    "CCCCC",
                    "CCCCC",
                    "CCCCC",
                    "CCCCC",
                    "CCcCC"}


    };

    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[16], new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR)};
        }
        return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[16]};
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "ImplosionCompressor.png");
    }

    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return GT_Recipe.GT_Recipe_Map.sImplosionRecipes;
    }

    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    public boolean isFacingValid(byte aFacing) {
        return aFacing > 1;
    }

    public boolean checkRecipe(ItemStack aStack) {
        ArrayList<ItemStack> tInputList = getStoredInputs();
        int tInputList_sS=tInputList.size();
        for (int i = 0; i < tInputList_sS - 1; i++) {
            for (int j = i + 1; j < tInputList_sS; j++) {
                if (GT_Utility.areStacksEqual((ItemStack) tInputList.get(i), (ItemStack) tInputList.get(j))) {
                    if (((ItemStack) tInputList.get(i)).stackSize >= ((ItemStack) tInputList.get(j)).stackSize) {
                        tInputList.remove(j--); tInputList_sS=tInputList.size();
                    } else {
                        tInputList.remove(i--); tInputList_sS=tInputList.size();
                        break;
                    }
                }
            }
        }
        ItemStack[] tInputs = tInputList.toArray(new ItemStack[tInputList.size()]);
        if (tInputList.size() > 0) {
            GT_Recipe tRecipe = GT_Recipe.GT_Recipe_Map.sImplosionRecipes.findRecipe(getBaseMetaTileEntity(), false, 9223372036854775807L, null, tInputs);
            if ((tRecipe != null) && (tRecipe.isRecipeInputEqual(true, null, tInputs))) {
                this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
                this.mEfficiencyIncrease = 10000;
                long tVoltage = getMaxInputVoltage();
                byte mTier = (byte) Math.max(1, GT_Utility.getTier(tVoltage));
                int aEUt = tRecipe.mEUt;
                int aDuration = tRecipe.mDuration;
                if (aEUt <= 16) {
                    mEUt = aEUt * (1 << (mTier - 1)) * (1 << (mTier - 1));
                    mMaxProgresstime = aDuration / (1 << (mTier - 1));
                } else {
                    mEUt = aEUt;
                    mMaxProgresstime = aDuration;
                    while (mEUt <= V[mTier - 1]) {
                        mEUt *= 4;
                        mMaxProgresstime /= 2;
                    }
                }
                mMaxProgresstime = Math.max(mMaxProgresstime,1);
                this.mOutputItems = new ItemStack[]{tRecipe.getOutput(0), tRecipe.getOutput(1)};
                updateSlots();
                return true;
            }
        }
        return false;
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return mUtility.checkStructure(getBaseMetaTileEntity(),this, this.getBaseMetaTileEntity().getBackFacing());
    }

    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    public int getPollutionPerTick(ItemStack aStack) {
        return 1000;
    }

    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }


}