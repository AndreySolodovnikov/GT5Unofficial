package gregtech.common.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.gui.GT_ContainerMetaTile_Machine;
import gregtech.api.gui.GT_Slot_Holo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_HolographicProjector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.Iterator;

public class GT_Container_HolographicProjector extends GT_ContainerMetaTile_Machine {
    public int param1 = 0;
    public int param2 = 0;
    public int param3 = 0;
    public GT_Container_HolographicProjector(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity);
    }

    @Override
    public void addSlots(InventoryPlayer aInventoryPlayer) {
        addSlotToContainer(new Slot(mTileEntity, 0, 80, 63));

        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 1, 8, 5, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 8, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 3, 8, 41, false, false, 1));

        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 4, 26, 5, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 5, 26, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 6, 26, 41, false, false, 1));

        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 7, 152, 5, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 8, 152, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 9, 152, 41, false, false, 1));

        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 10, 134, 5, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 11, 134, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 12, 134, 41, false, false, 1));


    }

    @Override
    public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer aPlayer) {
        if (aSlotIndex < 0) {
            return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
        }
        Slot tSlot = (Slot) this.inventorySlots.get(aSlotIndex);
        if ((tSlot != null) && (this.mTileEntity.getMetaTileEntity() != null)) {
            aSlotIndex--;
            switch (aSlotIndex) {
                case 0:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param1 -= 1;
                    return null;
                case 1:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param2 -= 1;
                    return null;
                case 2:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param3 -= 1;
                    return null;
                case 3:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param1 -= 1;
                    return null;
                case 4:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param2 -= 1;
                    return null;
                case 5:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param3 -= 1;
                    return null;
                case 6:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param1 += 1;
                    return null;
                case 7:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param2 += 1;
                    return null;
                case 8:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param3 += 1;
                    return null;
                case 9:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param1 += 1;
                    return null;
                case 10:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param2 += 1;
                    return null;
                case 11:
                    ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param3 += 1;
                    return null;
            }
            aSlotIndex++;
        }
        return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if ((this.mTileEntity.isClientSide()) || (this.mTileEntity.getMetaTileEntity() == null)) {
            return;
        }
        this.param1 = ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param1;
        this.param2 = ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param2;
        this.param3 = ((GT_MetaTileEntity_HolographicProjector) this.mTileEntity.getMetaTileEntity()).param3;

        Iterator var2 = this.crafters.iterator();
        while (var2.hasNext()) {
            ICrafting var1 = (ICrafting) var2.next();
            var1.sendProgressBarUpdate(this, 100, this.param1& 0xFFFF);
            var1.sendProgressBarUpdate(this, 101, this.param1 >>> 16);
            var1.sendProgressBarUpdate(this, 102, this.param2 & 0xFFFF);
            var1.sendProgressBarUpdate(this, 103, this.param2 >>> 16);
            var1.sendProgressBarUpdate(this, 104, this.param3 & 0xFFFF);
            var1.sendProgressBarUpdate(this, 105, this.param3 >>> 16);
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        super.updateProgressBar(par1, par2);
        switch (par1) {
            case 100:
                this.param1 = (this.param1 & 0xFFFF0000 | par2);
                break;
            case 101:
                this.param1 = (this.param1 & 0xFFFF | par2 << 16);
                break;
            case 102:
                this.param2 = (this.param2 & 0xFFFF0000 | par2);
                break;
            case 103:
                this.param2 = (this.param2 & 0xFFFF | par2 << 16);
                break;
            case 104:
                this.param3 = (this.param3 & 0xFFFF0000 | par2);
                break;
            case 105:
                this.param3 = (this.param3 & 0xFFFF | par2 << 16);
                break;
        }
    }
}
