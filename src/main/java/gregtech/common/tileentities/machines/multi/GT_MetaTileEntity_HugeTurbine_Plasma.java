package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;

public class GT_MetaTileEntity_HugeTurbine_Plasma extends GT_MetaTileEntity_HugeTurbine {

    private float water;
    private boolean achievement = false;

    public GT_MetaTileEntity_HugeTurbine_Plasma(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }


    public GT_MetaTileEntity_HugeTurbine_Plasma(String aName) {
        super(aName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        return new ITexture[]{Textures.BlockIcons.MACHINE_CASINGS[1][aColorIndex + 1], aFacing == aSide ? aActive ? new GT_RenderedTexture(Textures.BlockIcons.LARGETURBINE_TU_ACTIVE5) : new GT_RenderedTexture(Textures.BlockIcons.LARGETURBINE_TU5) : Textures.BlockIcons.CASING_BLOCKS[60]};
    }

    public String[] getDescription() {
        return new String[]{
                "Controller Block for the Large Steam Turbine",
                "Use Holographic projector to render the structure",
                "1x Steam Input Hatch (Side centered)",
                "1x Maintenance Hatch (Side centered)",
                "1x Dynamo Hatch (Back centered)",
                "Turbine Casings for the rest (24 at least!)",
                "Needs a Turbine Item (Inside controller GUI)",
                "Output depending on Rotor: 60-3360EU/t"};
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_HugeTurbine_Plasma(mName);
    }

    @Override
    public Block getCasingBlock() {
        return GregTech_API.sBlockCasings4;
    }

    @Override
    public byte getCasingMeta() {
        return 12;
    }

    @Override
    public byte getCasingTextureIndex() {
        return 60;
    }

    @Override
    public int getPollutionPerTick(ItemStack aStack) {
        return 0;
    }


    private int useWater(float input) {
        water = water + input;
        int usage = (int) water;
        water = water - (int) usage;
        return usage;
    }

    public int getFuelValue(FluidStack aLiquid) {
        if (aLiquid == null || GT_Recipe.GT_Recipe_Map.sTurbineFuels == null) return 0;
        FluidStack tLiquid;
        Collection<GT_Recipe> tRecipeList = GT_Recipe.GT_Recipe_Map.sPlasmaFuels.mRecipeList;
        if (tRecipeList != null) for (GT_Recipe tFuel : tRecipeList)
            if ((tLiquid = GT_Utility.getFluidForFilledItem(tFuel.getRepresentativeInput(0), true)) != null)
                if (aLiquid.isFluidEqual(tLiquid)) return tFuel.mSpecialValue;
        return 0;
    }

    @Override
    int fluidIntoPower(ArrayList<FluidStack> aFluids, int aOptFlow, int aBaseEff) {
        System.out.println("processing plasma");
        aOptFlow *= 40;
        int tEU = 0;

        int actualOptimalFlow = 0;

        if (aFluids.size() >= 1) {
            FluidStack firstFuelType = new FluidStack(aFluids.get(0), 0); // Identify a SINGLE type of fluid to process.  Doesn't matter which one. Ignore the rest!
            int fuelValue = getFuelValue(firstFuelType);
            actualOptimalFlow = (int) ((aOptFlow + fuelValue - 1) / fuelValue);
            this.realOptFlow = actualOptimalFlow; // For scanner info

            int remainingFlow = (int) (actualOptimalFlow * 1.25f); // Allowed to use up to 125% of optimal flow.  Variable required outside of loop for multi-hatch scenarios.
            int flow = 0;
            int totalFlow = 0;

            int aFluids_sS=aFluids.size();
            for (int i = 0; i < aFluids_sS; i++) {
                if (aFluids.get(i).isFluidEqual(firstFuelType)) {
                    flow = aFluids.get(i).amount; // Get all (steam) in hatch
                    flow = Math.min(flow, Math.min(remainingFlow, (int) (actualOptimalFlow * 1.25f))); // try to use up to 125% of optimal flow w/o exceeding remainingFlow
                    depleteInput(new FluidStack(aFluids.get(i), flow)); // deplete that amount
                    this.storedFluid = aFluids.get(i).amount;
                    remainingFlow -= flow; // track amount we're allowed to continue depleting from hatches
                    totalFlow += flow; // track total input used
                }
            }
            String fn = FluidRegistry.getFluidName(firstFuelType);
            String[] nameSegments = fn.split("\\.",2);
            if (nameSegments.length==2){
                String outputName=nameSegments[1];
                FluidStack output = FluidRegistry.getFluidStack(outputName, totalFlow);
                if (output==null){
                    output = FluidRegistry.getFluidStack("molten."+outputName, totalFlow);
                }
                if (output!=null) {
                    addOutput(output);
                }
            }

            tEU = (int) (Math.min((float) actualOptimalFlow, totalFlow) * fuelValue);

            if (totalFlow != actualOptimalFlow) {
                float efficiency = 1.0f - Math.abs(((totalFlow - (float) actualOptimalFlow) / actualOptimalFlow));
                if(totalFlow>actualOptimalFlow){efficiency = 1.0f;}
                if (efficiency < 0)
                    efficiency = 0; // Can happen with really ludicrously poor inefficiency.
                tEU *= efficiency;
                tEU = Math.max(1, (int)((long)tEU * (long)aBaseEff / 10000L));
            } else {
                tEU = (int)((long)tEU * (long)aBaseEff / 10000L);
            }
            System.out.println("getting eut "+tEU);
            return tEU;

        }
        return 0;
    }
}
