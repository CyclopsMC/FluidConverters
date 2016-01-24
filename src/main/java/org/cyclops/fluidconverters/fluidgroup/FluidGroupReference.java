package org.cyclops.fluidconverters.fluidgroup;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import org.cyclops.cyclopscore.persist.nbt.INBTSerializable;

/**
 * Holds a reference to a FluidGroup.
 * @author immortaleeb
 */
public class FluidGroupReference implements INBTSerializable {

    @Getter
    @Setter
    private FluidGroup fluidGroup;

    public FluidGroupReference() {
    }

    public FluidGroupReference(FluidGroup fluidGroup) {
        this.fluidGroup = fluidGroup;
    }

    // NBT key for the group id
    public static final String NBTKEY_GROUPID = "fluidGroupId";

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        if (fluidGroup != null)
            nbt.setString(NBTKEY_GROUPID, fluidGroup.getGroupId());
        return nbt;
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        String groupId = tag.getString(NBTKEY_GROUPID);
        setFluidGroup(FluidGroupRegistry.getFluidGroupById(groupId));
    }
}
