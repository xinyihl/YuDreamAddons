package com.yudream.yudreamaddons.mixins.mmce;

import hellfirepvp.modularmachinery.common.block.prop.ParallelControllerData;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = ParallelControllerData.class, remap = false)
public abstract class ParallelControllerDataMixin {
    @Shadow
    @Final
    @Mutable
    private static ParallelControllerData[] $VALUES;

    @Unique
    private static ParallelControllerData YUDREAM_A;

    @Unique
    private static ParallelControllerData YUDREAM_B;

    @Invoker(value = "<init>", remap = false)
    private static ParallelControllerData invokeNew(String name, int ordinal, int defaultMaxParallelism) {
        return null;
    }

    @Inject(
            method = "<clinit>",
            at = @At(value = "TAIL"),
            remap = false
    )
    private static void injectNewEnum(CallbackInfo ci) {
        int nextOrdinal = $VALUES.length;

        YUDREAM_A = invokeNew("YUDREAM_A", nextOrdinal, 1024);
        YUDREAM_B = invokeNew("YUDREAM_B", ++nextOrdinal, 2048);

        List<ParallelControllerData> newValues = new ArrayList<>(Arrays.asList($VALUES));

        newValues.add(YUDREAM_A);
        newValues.add(YUDREAM_B);

        $VALUES = newValues.toArray(new ParallelControllerData[0]);
    }
}
