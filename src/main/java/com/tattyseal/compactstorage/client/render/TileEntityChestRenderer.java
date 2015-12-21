package com.tattyseal.compactstorage.client.render;

import org.lwjgl.opengl.GL11;

import com.tattyseal.compactstorage.tileentity.TileEntityChest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Toby on 06/11/2014.
 */
@SideOnly(Side.CLIENT)
public class TileEntityChestRenderer extends TileEntitySpecialRenderer
{
    public ModelChest model;
    public static final ResourceLocation texture = new ResourceLocation("compactstorage", "textures/models/chest.png");

    public TileEntityChestRenderer()
    {
        this.model = new ModelChest();
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float scale, int i)
    {
        GL11.glPushMatrix();

        GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);

        EnumFacing direction = ((TileEntityChest) tile).direction;

        switch (direction)
        {
            case NORTH: break;
            case SOUTH: GL11.glRotatef(180f, 0f, 1f, 0f); break;
            case WEST: GL11.glRotatef(-90f, 0f, 1f, 0f); break;
            case EAST: GL11.glRotatef(90f, 0f, 1f, 0f); break;
        }

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        int color;

        try
        {
            color = ((TileEntityChest) tile).color;
        }
        catch(Exception exception)
        {
            color = 0xFFFFFF;
        }

        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1F);

        model.chestBelow.render(0.0625f);
        model.chestLid.render(0.0625f);

        GL11.glColor4f(1f, 1f, 1f, 1f);
        model.chestKnob.render(0.0625f);

        GL11.glPopMatrix();
    }
}
