/**
    Copyright (C) 2017 by jabelar

    This file is part of jabelar's Minecraft Forge modding examples; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    For a copy of the GNU General Public License see <http://www.gnu.org/licenses/>.
*/

package stridden.enrich.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stridden.enrich.MainMod;
import stridden.enrich.containers.ContainerOreGen;
import stridden.enrich.tileentities.TileEntityOreGen;

;

// TODO: Auto-generated Javadoc

/**
 * @author jabelar
 *
 */
@SideOnly(Side.CLIENT)
public class GuiOreGen extends GuiContainer
{
    private static final ResourceLocation ORE_GEN_GUI_TEXTURES = new ResourceLocation(MainMod.MODID + ":textures/gui/container/ore_gen.png");
    private final InventoryPlayer inventoryPlayer;
    private final IInventory tileOreGen;

    /**
     * Instantiates a new gui compactor.
     *
     * @param parInventoryPlayer
     *            the par inventory player
     * @param parInventoryOreGen
     *            the par inventory compactor
     */
    public GuiOreGen(InventoryPlayer parInventoryPlayer, IInventory parInventoryOreGen)
    {
        super(new ContainerOreGen(parInventoryPlayer, parInventoryOreGen));
        inventoryPlayer = parInventoryPlayer;
        tileOreGen = parInventoryOreGen;
        this.ySize = 168;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     *
     * @param mouseX
     *            the mouse X
     * @param mouseY
     *            the mouse Y
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = tileOreGen.getDisplayName().getUnformattedText();
        fontRenderer.drawString(s, 8, 6, 4210752);
        fontRenderer.drawString(inventoryPlayer.getDisplayName().getUnformattedText(), 8, ySize - 92, 4210752);
        for(int i = 0; i < 3; i++)
        {
            fontRenderer.drawStringWithShadow(((TileEntityOreGen)tileOreGen).getChanceInSlot(9+i) + "%", 125, 26 + i * 18, 0xFFFFFF);
        }
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY.
     *
     * @param partialTicks
     *            the partial ticks
     * @param mouseX
     *            the mouse X
     * @param mouseY
     *            the mouse Y
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ORE_GEN_GUI_TEXTURES);
        int marginHorizontal = (width - xSize) / 2;
        int marginVertical = (height - ySize) / 2;
        drawTexturedModalRect(marginHorizontal, marginVertical, 0, 0, xSize, ySize);

        // Draw progress indicator
        int progressLevel = getProgressLevel(24);
        drawTexturedModalRect(marginHorizontal + 117 - progressLevel, marginVertical + 36, 199 - progressLevel, 0, progressLevel + 1, 16);
    }

    private int getProgressLevel(int progressIndicatorPixelWidth)
    {
        int currentOreGenTime = tileOreGen.getField(0);
        int ticksPerOre = tileOreGen.getField(2);
        return ticksPerOre != 0 && currentOreGenTime != 0 ? currentOreGenTime * progressIndicatorPixelWidth / ticksPerOre : 0;
    }

}