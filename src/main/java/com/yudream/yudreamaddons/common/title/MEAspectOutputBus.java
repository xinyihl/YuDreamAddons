package com.yudream.yudreamaddons.common.title;

import com.warmthdawn.mod.gugu_utils.modularmachenary.MMCompoments;
import com.warmthdawn.mod.gugu_utils.modularmachenary.components.GenericMachineCompoment;
import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.RequirementAspect;
import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.basic.ICraftNotifier;
import com.warmthdawn.mod.gugu_utils.modularmachenary.requirements.basic.IGeneratable;
import com.yudream.yudreamaddons.common.title.base.MEAspectBus;
import hellfirepvp.modularmachinery.common.crafting.ComponentType;

import javax.annotation.Nullable;

public class MEAspectOutputBus extends MEAspectBus implements IGeneratable<RequirementAspect.RT>, ICraftNotifier<RequirementAspect.RT> {

    @Nullable
    @Override
    public GenericMachineCompoment<RequirementAspect.RT> provideComponent() {
        return new GenericMachineCompoment<>(this, (ComponentType) MMCompoments.COMPONENT_ASPECT);
    }

    @Override
    public boolean generate(RequirementAspect.RT rt, boolean b) {
        if (!isPowered() && !isActive()) {
            rt.setError("ME机械源质输出总线未连接ME网络");
            return false;
        }
        int i = addToAspectME(rt.getAspect(), rt.getAmount(), b);
        rt.setAmount(rt.getAmount() - i);
        return true;
    }
}
