package com.tattyseal.compactstorage.block;

import java.util.Random;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.exception.InvalidSizeException;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.util.EntityUtil;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Created by Toby on 06/11/2014.
 */
public class BlockChest extends Block implements ITileEntityProvider
{
    public BlockChest()
    {
        super(Material.wood);
        setUnlocalizedName("compactchest");
        setCreativeTab(CompactStorage.tabCS);

        setHardness(2F);
        setResistance(2F);
        setHarvestLevel("axe", 1);
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }
    
    @Override
    public int getRenderType() 
    {
    	return -1;
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, entity, stack);

        TileEntityChest chest = ((TileEntityChest) world.getTileEntity(pos));

        chest.direction = EntityUtil.get2dOrientation(entity);
        chest.invX = 9;
        chest.invY = 3;
        chest.color = 0xffffff;
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!player.isSneaking())
        {
            if(!world.isRemote)
            {
                world.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "random.chestopen", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
                player.openGui(CompactStorage.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
            }

            return true;
        }

        return false;
    }

    public TileEntity createNewTileEntity(World world, int dim)
    {
        return new TileEntityChest();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState block)
    {
        TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);

        if(chest != null)
        {
            ItemStack stack = new ItemStack(CompactStorage.chest, 1);
            Random rand = new Random();

            world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY() + 0.5f, pos.getZ(), stack));

            for(int slot = 0; slot < chest.items.length; slot++)
            {
                float randX = rand.nextFloat();
                float randZ = rand.nextFloat();

                if(chest.items != null && chest.items[slot] != null) world.spawnEntityInWorld(new EntityItem(world, pos.getX() + randX, pos.getY() + 0.5f, pos.getZ() + randZ, chest.items[slot]));
            }
        }

        super.breakBlock(world, pos, block);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) 
    {
    	return null;
    }
}
