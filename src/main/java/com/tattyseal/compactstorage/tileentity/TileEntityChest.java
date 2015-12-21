package com.tattyseal.compactstorage.tileentity;

import java.util.Arrays;

import com.tattyseal.compactstorage.api.IChest;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants;

/**
 * Created by Toby on 06/11/2014.
 */
public class TileEntityChest extends TileEntity implements IInventory, IChest
{
    public EnumFacing direction;

    public int color;
    public int invX;
    public int invY;

    public boolean init;

    public ItemStack[] items;

    public TileEntityChest()
    {
        super();

        this.direction = EnumFacing.NORTH;
        this.items = new ItemStack[getSizeInventory()];
    }

    /* INVENTORY START */
    @Override
    public int getSizeInventory()
    {
        return invX * invY + 3;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
    	if(slot < items.length && items[slot] != null)
        {
        	return items[slot];
        }

        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
    	ItemStack stack = getStackInSlot(slot);

        if(stack != null)
        {
            if(stack.stackSize <= amount)
            {
                setInventorySlotContents(slot, null);
                markDirty();

                return stack.copy();
            }
            else
            {
                ItemStack stack2 = stack.splitStack(amount);
                markDirty();

                return stack2.copy();
            }
        }

        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return getStackInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
    	if(items != null && slot < items.length)
        {
            items[slot] = stack;
            markDirty();
        }
    }

    @Override
    public String getName()
    {
        return "compactChest.inv";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public void markDirty()
    {
        super.markDirty();

        if(items.length != getSizeInventory())
        {
        	for(int i = getSizeInventory() - 3; i < items.length; i++)
        	{
        		if(items[i] != null) worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX(), pos.getY() + 1f, pos.getZ(), items[i].copy()));
        		items[i] = null;
        	}
        }

        worldObj.markBlockForUpdate(pos);
    }

    /* CUSTOM START */

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if(tag.hasKey("facing")) this.direction = EnumFacing.getFront(tag.getInteger("facing"));

        this.color = tag.getInteger("color");
        this.invX = tag.getInteger("invX");
        this.invY = tag.getInteger("invY");

        NBTTagList nbtTagList = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        items = new ItemStack[getSizeInventory()];

        for(int slot = 0; slot < nbtTagList.tagCount(); slot++)
        {
            NBTTagCompound item = nbtTagList.getCompoundTagAt(slot);

            int i = item.getInteger("Slot");

            if(i >= 0 && i < getSizeInventory())
            {
                items[i] = ItemStack.loadItemStackFromNBT(item);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        if(direction != null) tag.setInteger("facing", direction.ordinal());
        tag.setInteger("color", color);
        tag.setInteger("invX", invX);
        tag.setInteger("invY", invY);

        NBTTagList nbtTagList = new NBTTagList();
        for(int slot = 0; slot < getSizeInventory(); slot++)
        {
            if(slot < items.length && items[slot] != null)
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setInteger("Slot", slot);
                items[slot].writeToNBT(item);
                nbtTagList.appendTag(item);
            }
        }

        tag.setTag("Items", nbtTagList);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return new S35PacketUpdateTileEntity(pos, getBlockMetadata(), tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.getNbtCompound());
    }

    public void setDirection(EnumFacing direction)
    {
        this.direction = direction;
        markDirty();
    }

    public void setDirection(int direction)
    {
        this.direction = EnumFacing.getFront(direction);
        markDirty();
    }

    public void updateBlock()
    {
    	items = Arrays.copyOf(items, getSizeInventory());
    	worldObj.markBlockForUpdate(pos);
    }

    @Override
    public int getInvX()
    {
        return invX;
    }

    @Override
    public int getInvY()
    {
        return invY;
    }

    @Override
    public int getColor()
    {
        return color;
    }

	@Override
	public void setInvX(int invX)
	{
		this.invX = invX;
	}

	@Override
	public void setInvY(int invY)
	{
		this.invY = invY;
	}

	@Override
	public void setColor(int color)
	{
		this.color = color;
	}

    @Override
    public int getStartUpgradeSlots()
    {
        return getSizeInventory() - getAmountUpgradeSlots();
    }

    @Override
    public int getAmountUpgradeSlots()
    {
        return 3;
    }



    @Override
    public ItemStack[] getRequiredUpgrades(int invX, int invY)
    {
        int all = invX * invY;
        int oldAll = getInvX() * getInvY();

        ItemStack main = new ItemStack(Blocks.chest, oldAll > all ? 0 : (all - oldAll) / 10);
        return new ItemStack[] {main.stackSize == 0 ? null : main, null, null};
    }

	@Override
	public IChatComponent getDisplayName() {
		// TODO Auto-generated method stub
		return new ChatComponentText("Compact Chest");
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
