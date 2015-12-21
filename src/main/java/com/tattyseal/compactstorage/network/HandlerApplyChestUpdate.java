package com.tattyseal.compactstorage.network;

import com.tattyseal.compactstorage.inventory.InventoryBackpack;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.util.ChestUtil;
import com.tattyseal.compactstorage.util.ChestUtil.Type;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerApplyChestUpdate implements IMessageHandler<PacketApplyChestUpdate, IMessage>
{
	@Override
	public IMessage onMessage(PacketApplyChestUpdate message, MessageContext ctx) {
		if(message.x == 0 && message.y == 0 && message.z == 0 && message.invX == 0 && message.invY == 0) return null;
		
		if(message.type == Type.CHEST)
		{
			TileEntityChest chest = (TileEntityChest) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
			
			chest.setInvX(ChestUtil.clamp(message.invX, 1, 24));
			chest.setInvY(ChestUtil.clamp(message.invY, 1, 12));
			
			chest.markDirty();
			chest.updateBlock();
		}
		else if(message.type == Type.BACKPACK)
		{
			InventoryBackpack backpack = new InventoryBackpack(ctx.getServerHandler().playerEntity.getHeldItem());

			backpack.setInvX(ChestUtil.clamp(message.invX, 1, 24));
			backpack.setInvY(ChestUtil.clamp(message.invY, 1, 12));
			
			backpack.markDirty();
			
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			World world = player.worldObj;
	        
	        if(backpack.items.length != backpack.getSizeInventory())
	        {
	        	for(int i = backpack.getSizeInventory() - 1; i < backpack.items.length; i++)
	        	{
	        		if(backpack.items[i] != null) world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY + 1f, player.posZ, backpack.items[i].copy()));
	        		backpack.items[i] = null;
	        	}
	        }
		}
		
		return null;
	}
}