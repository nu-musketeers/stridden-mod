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

package stridden.enrich.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stridden.enrich.MainMod;
import stridden.enrich.client.gui.GuiHandler;
import stridden.enrich.init.ModCreativeTabs;
import stridden.enrich.tileentities.TileEntityOreGen;

// TODO: Auto-generated Javadoc

/**
 * @author Stridden
 *
 */
public class BlockOreGen extends BlockContainer
{
    /**
     * Instantiates a new block ore gen.
     */
    public BlockOreGen()
    {
        super(Material.WOOD);
        setDefaultState(blockState.getBaseState());
        setCreativeTab(ModCreativeTabs.CREATIVE_TAB);
        setSoundType(SoundType.WOOD);
        this.fullBlock = true;
        this.useNeighborBrightness = false;
        setTickRandomly(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.block.Block#onBlockActivated(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.state.IBlockState,
     * net.minecraft.entity.player.EntityPlayer, net.minecraft.util.EnumHand, net.minecraft.util.EnumFacing, float, float, float)
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
            EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            player.openGui(MainMod.instance, GuiHandler.GUI_ENUM.ORE_GEN.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     *
     * @param world
     *            the world in
     * @param meta
     *            the meta
     * @return the tile entity
     */
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityOreGen();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.block.BlockContainer#breakBlock(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.state.IBlockState)
     */
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityOreGen)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityOreGen) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraft.block.Block#hasComparatorInputOverride(net.minecraft.block.state.IBlockState)
     */
    @Override
    public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.block.Block#getComparatorInputOverride(net.minecraft.block.state.IBlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos)
     */
    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    // /**
    // * Possibly modify the given BlockState before rendering it on an Entity (Minecarts, Endermen, ...)
    // */
    // @Override
    // @SideOnly(Side.CLIENT)
    // public IBlockState getStateForEntityRender(IBlockState state)
    // {
    // return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    // }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraft.block.Block#createBlockState()
     */
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this);
    }
}