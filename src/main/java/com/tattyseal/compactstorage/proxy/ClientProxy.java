package com.tattyseal.compactstorage.proxy;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.client.render.ChestItemRenderer;
import com.tattyseal.compactstorage.client.render.TileEntityChestRenderer;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.util.ModelUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created by Toby on 06/11/2014.
 */
public class ClientProxy implements IProxy
{
    public void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChest.class, new TileEntityChestRenderer());
        //MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(CompactStorage.chest), new ChestItemRenderer());
    
        ModelUtil.registerItem(CompactStorage.backpack, "compactstorage:backpack");
        ModelUtil.registerChest();
    }
}
