package com.kamefrede.rpsideas.util.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.pipeline.LightUtil;
import vazkii.psi.client.core.handler.ClientTickHandler;
import vazkii.psi.common.Psi;

public class ClientHelpers {
    public static int pulseColor(int mergedColorIn) {
        return pulseColor(mergedColorIn, 1f);
    }

    public static int pulseColor(int mergedColorIn, float multiplier) {
        int add = (int) (Math.sin(ClientTickHandler.ticksInGame * 0.2) * 24);
        int red = (0xFF0000 & mergedColorIn) >> 16;
        int green = (0x00FF00 & mergedColorIn) >> 8;
        int blue = 0x0000FF & mergedColorIn;
        int addedRed = MathHelper.clamp((int) (multiplier * (red + add)), 0, 255);
        int addedGreen = MathHelper.clamp((int) (multiplier * (green + add)), 0, 255);
        int addedBlue = MathHelper.clamp((int) (multiplier * (blue + add)), 0, 255);
        return (addedRed << 16) | (addedGreen << 8) | addedBlue;
    }

    public static float getDiffuseLighting(EnumFacing side) {
        return LightUtil.diffuseLight(side);
    }

    public static int getFlowColor(ItemStack stack) {
        ItemStack colorizer = FlowColorsHelper.getColorizer(stack);
        if (colorizer.isEmpty()) return 0x000000;
        else return Psi.proxy.getColorizerColor(colorizer).getRGB();
    }
}
