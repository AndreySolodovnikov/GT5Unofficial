package gregtech.common.tileentities.machines.multi;

import gregtech.GT_Mod;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_DataAccess;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.api.util.multiblock.GT_MultiBlockUtility;
import gregtech.api.util.multiblock.GT_SimpleBlockChecker;
import gregtech.api.util.multiblock.IGuideRenderer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GT_MetaTileEntity_HugeBoiler extends GT_MetaTileEntity_MultiBlockBase {
    private boolean firstRun = true;
    protected int mSuperEfficencyIncrease = 0;
    private int integratedCircuitConfig = 0; //Steam output is reduced by 1000L per config
    private int excessFuel = 0; //Eliminate rounding errors for fuels that burn half items
    private int excessProjectedEU = 0; //Eliminate rounding errors from throttling the boiler
    protected int processMultiplier = 5;

    public GT_MetaTileEntity_Hatch_DataAccess mDataAccessHatch = null;

    public boolean addDataAccessToMachineList(IGregTechTileEntity aTileEntity) {
        if(aTileEntity==null)
            return false;
        IMetaTileEntity aMetaTileEntity = ((IGregTechTileEntity)aTileEntity).getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_DataAccess) {
            if(mDataAccessHatch != null)
                return false;
            ((GT_MetaTileEntity_Hatch) aMetaTileEntity).updateTexture(getFireboxTextureIndex());
            mDataAccessHatch = (GT_MetaTileEntity_Hatch_DataAccess)aMetaTileEntity;
            return  true;
        }
        return false;
    }

    public static String[][] mStructure = new String[][]{
            {
                    "sNNNs",
                    "NNNNN",
                    "NNNNN",
                    "NNNNN",
                    "sNNNs"},
            {
                    "sNNNs",
                    "NMMMN",
                    "NMMMN",
                    "NMMMN",
                    "sNNNs"},
            {
                    "sNNNs",
                    "NMMMN",
                    "NMMMN",
                    "NMMMN",
                    "sNNNs"},
            {
                    "sNNNs",
                    "NMMMN",
                    "NMMMN",
                    "NMMMN",
                    "sNNNs"},
            {
                    "sNNNs",
                    "NMMMN",
                    "NMMMN",
                    "NMMMN",
                    "sNNNs"},
            {
                    "sNNNs",
                    "NMMMN",
                    "NMMMN",
                    "NMMMN",
                    "sNNNs"},
            {
                    "sFFFs",
                    "FFFFF",
                    "FFFFF",
                    "FFFFF",
                    "sFFFs"},
            {
                    "sFFFs",
                    "FFFFF",
                    "FFFFF",
                    "FFFFF",
                    "sFcFs"}


    };

    @Override
    public boolean onWrenchRightClick(byte aSide, byte aWrenchingSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        return checkMachine(getBaseMetaTileEntity(),getRealInventory()[1]);
    }

    public GT_MetaTileEntity_HugeBoiler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_HugeBoiler(String aName) {
        super(aName);
        mUtility = new GT_MultiBlockUtility(true,true, mStructure,new GT_SimpleBlockChecker[]{
                new GT_SimpleBlockChecker('N',getCasingBlock(),getCasingMeta(),70, 1000, Arrays.asList(new Integer[]{3}),getCasingTextureIndex()),
                new GT_SimpleBlockChecker('M',getPipeBlock(),getPipeMeta(),44,1000, Arrays.asList(new Integer[]{}),getCasingTextureIndex()),
                new GT_HugeBoilerBlockChecker('F',getFireboxBlock(),getFireboxMeta(),30,1000,Arrays.asList(new Integer[]{1,2,7,8,9}), getCasingTextureIndex())
        });
    }

    public String[] getDescription() {
        return new String[]{
                "Controller Block for the Large Boiler",
                "Produces " + (getEUt() * 40) * (runtimeBoost(20) / 20f) + "L of Steam with 1 Coal at " + getEUt() * 40 + "L/s",
                "A programmed circuit in the main block throttles the boiler (-1000L/s per config)",
                "Size(WxHxD): 3x5x3, Controller (Front middle in Fireboxes)",
                "3x1x3 of " +getCasingMaterial()+ " Firebox Casings (Bottom layer, Min 30)",
                "3x4x3 of " +getCasingMaterial()+ " " +getCasingBlockType()+ " (Above Fireboxes, hollow, Min 24!)",
                "1x3x1 of " +getCasingMaterial()+ " Pipe Casings (Inside the Hollow Machine Casings/Plated Bricks)",
                "1x Fuel Input Hatch/Bus (Any Firebox)",
                "1x Water Input Hatch (Any Firebox)",
                "1x Steam Output Hatch (Any Casing)",
                "1x Maintenance Hatch (Any Firebox)",
                "1x Muffler Hatch (Any Firebox)",
                "Diesel fuels have 1/4 efficiency",
                String.format("Takes %.2f seconds to heat up", 500.0 / getEfficiencyIncrease()),
                "Causes up to " + 20 * getPollutionPerTick(null) + " Pollution per second"};
    }

    public abstract String getCasingMaterial();

    public abstract Block getCasingBlock();

    public abstract String getCasingBlockType();

    public abstract byte getCasingMeta();

    public abstract int getCasingTextureIndex();

    public abstract Block getPipeBlock();

    public abstract byte getPipeMeta();

    public abstract Block getFireboxBlock();

    public abstract byte getFireboxMeta();

    public abstract int getFireboxTextureIndex();

    public abstract int getEUt();

    public abstract int getEfficiencyIncrease();

    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[getCasingTextureIndex()], new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER)};
        }
        return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[getCasingTextureIndex()]};
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "LargeBoiler.png");
    }

    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    public boolean isFacingValid(byte aFacing) {
        return aFacing > 1;
    }

    public boolean checkRecipe(ItemStack aStack) {
        //Do we have an integrated circuit with a valid configuration?
        if (mInventory[1] != null && mInventory[1].getUnlocalizedName().startsWith("gt.integrated_circuit")) {
            int circuit_config = mInventory[1].getItemDamage();
            if (circuit_config >= 1 && circuit_config <= 25) {
                // If so, overwrite the current config
                this.integratedCircuitConfig = circuit_config;
            }
        } else {
            //If not, set the config to zero
            this.integratedCircuitConfig = 0;
        }
        if(mDataAccessHatch != null) {
            ItemStack[] mData = mDataAccessHatch.mInventory;
            for (byte c = 0; c < mData.length; c++) {
                if (mData[c] != null && mData[c].getUnlocalizedName().startsWith("gt.integrated_circuit")) {
                    int circuit_config = mData[c].getItemDamage();
                    if (circuit_config >= 1 && circuit_config <= 25) {
                        // If so, overwrite the current config
                        this.integratedCircuitConfig += circuit_config;
                    }
                }
            }
        }

        this.mSuperEfficencyIncrease = 0;
        for (GT_Recipe tRecipe : GT_Recipe.GT_Recipe_Map.sDieselFuels.mRecipeList) {
            FluidStack tFluid = GT_Utility.getFluidForFilledItem(tRecipe.getRepresentativeInput(0), true);
            if (tFluid != null && tRecipe.mSpecialValue > 1) {
                tFluid.amount = 1000;
                if (depleteInput(tFluid)) {
                    this.mMaxProgresstime = adjustBurnTimeForConfig(runtimeBoost(tRecipe.mSpecialValue / 2))/processMultiplier;
                    this.mEUt = processMultiplier*adjustEUtForConfig(getEUt());
                    this.mEfficiencyIncrease = this.mMaxProgresstime * getEfficiencyIncrease() * 4;
                    return true;
                }
            }
        }
        for (GT_Recipe tRecipe : GT_Recipe.GT_Recipe_Map.sDenseLiquidFuels.mRecipeList) {
            FluidStack tFluid = GT_Utility.getFluidForFilledItem(tRecipe.getRepresentativeInput(0), true);
            if (tFluid != null) {
                tFluid.amount = 1000;
                if (depleteInput(tFluid)) {
                    this.mMaxProgresstime = adjustBurnTimeForConfig(Math.max(1, runtimeBoost(tRecipe.mSpecialValue * 2)))/processMultiplier;
                    this.mEUt = processMultiplier*adjustEUtForConfig(getEUt());
                    this.mEfficiencyIncrease = this.mMaxProgresstime * getEfficiencyIncrease();
                    return true;
                }
            }
        }
        ArrayList<ItemStack> tInputList = getStoredInputs();
        if (!tInputList.isEmpty()) {
            for (ItemStack tInput : tInputList) {
                if (tInput != GT_OreDictUnificator.get(OrePrefixes.bucket, Materials.Lava, 1)){
                    if (GT_Utility.getFluidForFilledItem(tInput, true) == null && (this.mMaxProgresstime = GT_ModHandler.getFuelValue(tInput) / 80) > 0) {
                        this.excessFuel += GT_ModHandler.getFuelValue(tInput) % 80;
                        this.mMaxProgresstime += this.excessFuel / 80;
                        this.excessFuel %= 80;
                        this.mMaxProgresstime = adjustBurnTimeForConfig(runtimeBoost(this.mMaxProgresstime))/processMultiplier;
                        this.mEUt = adjustEUtForConfig(getEUt())*processMultiplier;
                        this.mEfficiencyIncrease = this.mMaxProgresstime * getEfficiencyIncrease();
                        this.mOutputItems = new ItemStack[]{GT_Utility.getContainerItem(tInput, true)};
                        tInput.stackSize -= 1;
                        updateSlots();
                        if (this.mEfficiencyIncrease > 5000) {
                            this.mEfficiencyIncrease = 0;
                            this.mSuperEfficencyIncrease = 20;
                        }
                        return true;
                    }
                }
            }
        }
        this.mMaxProgresstime = 0;
        this.mEUt = 0;
        return false;
    }

    abstract int runtimeBoost(int mTime);

    public boolean onRunningTick(ItemStack aStack) {
        if (this.mEUt > 0) {
            if (this.mSuperEfficencyIncrease > 0)
                mEfficiency = Math.max(0, Math.min(mEfficiency + mSuperEfficencyIncrease, getMaxEfficiency(mInventory[1]) - ((getIdealStatus() - getRepairStatus()) * 1000)));
            int tGeneratedEU = (int) (this.mEUt * 2L * this.mEfficiency / 10000L);
            if (tGeneratedEU > 0) {
                long amount = (tGeneratedEU + 160) / 160;
                if (depleteInput(Materials.Water.getFluid(amount)) || depleteInput(GT_ModHandler.getDistilledWater(amount))) {
                    addOutput(GT_ModHandler.getSteam(tGeneratedEU));
                } else {
                    explodeMultiblock();
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (mProgresstime > 0 && firstRun) {
            firstRun = false;
            GT_Mod.achievements.issueAchievement(aBaseMetaTileEntity.getWorld().getPlayerEntityByName(aBaseMetaTileEntity.getOwnerName()), "extremepressure");
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mDataAccessHatch = null;
        return mUtility.checkStructure(getBaseMetaTileEntity(),this, this.getBaseMetaTileEntity().getBackFacing());
    }

    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    public int getPollutionPerTick(ItemStack aStack) {
        int adjustedEUOutput = Math.max(25, getEUt() - 25 * integratedCircuitConfig);
        return Math.max(1, 12 * adjustedEUOutput / getEUt());
    }

    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    private int adjustEUtForConfig(int rawEUt) {
        int adjustedSteamOutput = rawEUt - 25 * integratedCircuitConfig;
        return Math.max(adjustedSteamOutput, 25);
    }

    private int adjustBurnTimeForConfig(int rawBurnTime) {
        if (mEfficiency < getMaxEfficiency(mInventory[1]) - ((getIdealStatus() - getRepairStatus()) * 1000)) {
            return rawBurnTime;
        }
        int adjustedEUt = Math.max(25, getEUt() - 25 * integratedCircuitConfig);
        int adjustedBurnTime = rawBurnTime * getEUt() / adjustedEUt;
        this.excessProjectedEU += getEUt() * rawBurnTime - adjustedEUt * adjustedBurnTime;
        adjustedBurnTime += this.excessProjectedEU / adjustedEUt;
        this.excessProjectedEU %= adjustedEUt;
        return adjustedBurnTime;
    }

    public static class GT_HugeBoilerBlockChecker extends GT_SimpleBlockChecker{
        public GT_HugeBoilerBlockChecker(char c, Block aBlock, byte aMeta, int aMinCaseCount, int aMaxCaseCount, List<Integer> aAllowedHatches, int aCasingID){
            super(c,aBlock,aMeta,aMinCaseCount,aMaxCaseCount,aAllowedHatches,aCasingID);
        }

        @Override
        public boolean checkBlock(IGregTechTileEntity aBaseMetaTileEntity, GT_MetaTileEntity_MultiBlockBase aController, int aXoff, int aYoff, int aZoff) {
            if(! super.checkBlock(aBaseMetaTileEntity, aController, aXoff, aYoff, aZoff)){
                return ((GT_MetaTileEntity_HugeBoiler)aController).addDataAccessToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(aXoff,aYoff,aZoff));
            }
            return true;
        }
    }


}