package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.block.Block;

public class GT_MetaTileEntity_HugeBoiler_Bronze
        extends GT_MetaTileEntity_HugeBoiler {
    public GT_MetaTileEntity_HugeBoiler_Bronze(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_HugeBoiler_Bronze(String aName) {
        super(aName);
    }

    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_HugeBoiler_Bronze(this.mName);
    }

    public String getCasingMaterial(){
        return "Bronze";
    }

    @Override
    public String getCasingBlockType() {
        return "Plated Bricks";
    }

    public Block getCasingBlock() {
        return GregTech_API.sBlockCasings1;
    }

    public byte getCasingMeta() {
        return 10;
    }

    public int getCasingTextureIndex() {
        return 10;
    }

    public Block getPipeBlock() {
        return GregTech_API.sBlockCasings2;
    }

    public byte getPipeMeta() {
        return 12;
    }

    public Block getFireboxBlock() {
        return GregTech_API.sBlockCasings3;
    }

    public byte getFireboxMeta() {
        return 13;
    }

    public int getFireboxTextureIndex() {
        return 45;
    }

    public int getEUt() {
        return 400;
    }

    public int getEfficiencyIncrease() {
        return 16;
    }

    @Override
    int runtimeBoost(int mTime) {
        return mTime * 2;
    }
}
