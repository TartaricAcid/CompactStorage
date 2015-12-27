package com.tattyseal.compactstorage.client.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.tattyseal.compactstorage.api.IChest;
import com.tattyseal.compactstorage.client.gui.tab.ChestInventoryTab;
import com.tattyseal.compactstorage.client.gui.tab.ChestSettingsTab;
import com.tattyseal.compactstorage.client.gui.tab.ITab;
import com.tattyseal.compactstorage.inventory.InventoryBackpack;
import com.tattyseal.compactstorage.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Toby on 09/11/2014.
 */
public class GuiChest extends GuiContainer
{
    public World world;
    public EntityPlayer player;
    public BlockPos pos;

    public int invX;
    public int invY;

    public IChest chest;
    
    public ITab[] tabs;
    public ITab activeTab;
    
    public KeyBinding[] HOTBAR;
    public int backpackSlot;
    
    public GuiChest(Container container, IChest chest, World world, EntityPlayer player, BlockPos pos)
    {
        super(container);

        this.world = world;
        this.player = player;
        this.pos = pos;

        this.chest = chest;

        this.invX = chest.getInvX();
        this.invY = chest.getInvY();

        this.xSize = 7 + (Math.max(9, invX) * 18) + 7;
        this.ySize = 15 + (invY * 18) + 13 + 54 + 4 + 18 + 7;
        this.HOTBAR = Minecraft.getMinecraft().gameSettings.keyBindsHotbar;
        
        backpackSlot = -1;
        
        if(chest instanceof InventoryBackpack)
        {
        	backpackSlot = player.inventory.currentItem;
        }
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        
        tabs = new ITab[2];
        tabs[0] = new ChestInventoryTab(this, new ItemStack(Blocks.chest), "tab.chest", guiLeft + 1, guiTop - 28, true, invX, invY);
        tabs[1] = new ChestSettingsTab(this, new ItemStack(Items.redstone), "tab.settings", guiLeft + 28, guiTop - 28, true, invX, invY);
   
        activeTab = tabs[0];
        tabs[0].selected = true;
        tabs[0].selected();
    }
    
    @Override
    public void drawGuiContainerForegroundLayer(int arg0, int arg1) 
    {
    	super.drawGuiContainerForegroundLayer(arg0, arg1);
    	activeTab.drawForeground(guiLeft, guiTop);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float i, int j, int k)
    {    	
    	GL11.glPushMatrix();
    	
    	GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor3f(1, 1, 1);

        if(activeTab.shouldChestRenderBackground())
        {
            RenderUtil.renderChestBackground(this, guiLeft, guiTop, invX, invY);
        }

        activeTab.drawBackground(guiLeft, guiTop);
        
        GL11.glColor3f(1, 1, 1);
        
        for(ITab tab : tabs)
        {
        	tab.draw();
        }
        
        for(ITab tab : tabs)
        {
        	if(tab.clickIntersects(j, k))
        	{
        		drawHoveringText(Arrays.asList(I18n.format(tab.name)), j, k, fontRendererObj);
        	}
        }
        
        GL11.glPopMatrix();
    }
    
    @Override
    protected void mouseClicked(int x, int y, int b) 
    {
    	ITab iTab = null;
    	
    	for(ITab tab : tabs)
    	{
    		if(tab.clickIntersects(x, y))
    		{
    			iTab = tab;
    			break;
    		}
    	}
    	
    	if(iTab != null)
    	{
        	for(ITab tab : tabs)
        	{
        		tab.selected = false;
        		tab.deselected();
        	}
        	
    		iTab.selected = true;
        	iTab.selected();
        	this.activeTab = iTab;
    	}
    	
    	activeTab.mouseClicked(x, y, b);
    	
    	try 
    	{
			super.mouseClicked(x, y, b);
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
    }
    
    @Override
    protected void keyTyped(char c, int id)
    {
    	if (backpackSlot != -1 && HOTBAR[backpackSlot].getKeyCode() == id) 
        {
            return;
        }
    	
    	if(!activeTab.areTextboxesInFocus())
		{
    		try
    		{
        		super.keyTyped(c, id);
    		}
    		catch(IOException ex)
    		{
    			ex.printStackTrace();
    		}
		}
    	activeTab.keyTyped(c, id);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) 
    {
    	try 
    	{
			super.actionPerformed(button);
		} 
    	catch (IOException e) 
    	{
			e.printStackTrace();
		}
    	
    	activeTab.buttonClicked(button);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }
    
    public List getButtonList()
    {
    	return buttonList;
    }
    
    public int getGuiLeft()
    {
    	return guiLeft;
    }
    
    public int getGuiTop()
    {
    	return guiTop;
    }
}
