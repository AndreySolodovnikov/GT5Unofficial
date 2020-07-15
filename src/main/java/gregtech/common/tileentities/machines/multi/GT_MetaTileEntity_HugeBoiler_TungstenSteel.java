package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.block.Block;

public class GT_MetaTileEntity_HugeBoiler_TungstenSteel
        extends GT_MetaTileEntity_HugeBoiler {
    public GT_MetaTileEntity_HugeBoiler_TungstenSteel(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_HugeBoiler_TungstenSteel(String aName) {
        super(aName);
    }

    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_HugeBoiler_TungstenSteel(this.mName);
    }

    public String getCasingMaterial(){
        return "TungstenSteel";
    }

    @Override
    public String getCasingBlockType() {
        return "Machine Casings";
    }

    public Block getCasingBlock() {
        return GregTech_API.sBlockCasings4;
    }

    public byte getCasingMeta() {
        return 0;
    }

    public int getCasingTextureIndex() {
        return 48;
    }

    public Block getPipeBlock() {
        return GregTech_API.sBlockCasings2;
    }

    public byte getPipeMeta() {
        return 15;
    }

    public Block getFireboxBlock() {
        return GregTech_API.sBlockCasings3;
    }

    public byte getFireboxMeta() {
        return 15;
    }

    public int getFireboxTextureIndex() {
        return 47;
    }

    public int getEUt() {
        return 1000;
    }

    public int getEfficiencyIncrease() {
        return 4;
    }

    @Override
    int runtimeBoost(int mTime) {
        return mTime * 120 / 100;
    }

}