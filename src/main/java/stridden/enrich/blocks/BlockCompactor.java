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

import stridden.enrich.MainMod;
import stridden.enrich.client.gui.GuiHandler;
import stridden.enrich.init.ModCreativeTabs;
import stridden.enrich.tileentities.TileEntityCompactor;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TODO: Auto-generated Javadoc
/**
 * @author jabelar
 *
 */
public class BlockCompactor extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /**
     * Instantiates a new block compactor.
     */
    public BlockCompactor()
    {
        super(Material.ROCK);
        // DEBUG
        System.out.println("Constructing BlockCompactor instance");
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setCreativeTab(ModCreativeTabs.CREATIVE_TAB);
        setSoundType(SoundType.SNOW);
        blockParticleGravity = 1.0F;
        setDefaultSlipperiness(0.6F);
        lightOpacity = 20; // cast a light shadow
        setTickRandomly(false);
        useNeighborBrightness = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.block.Block#onBlockAdded(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.state.IBlockState)
     */
    @Override
    public void onBlockAdded(World parWorld, BlockPos parBlockPos, IBlockState parIBlockState)
    {
        if (!parWorld.isRemote)
        {
            // Rotate block if the front side is blocked
            IBlockState blockToNorth = parWorld.getBlockState(parBlockPos.north());
            IBlockState blockToSouth = parWorld.getBlockState(parBlockPos.south());
            IBlockState blockToWest = parWorld.getBlockState(parBlockPos.west());
            IBlockState blockToEast = parWorld.getBlockState(parBlockPos.east());
            EnumFacing enumfacing = parIBlockState.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && blockToNorth.isFullBlock() && !blockToSouth.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && blockToSouth.isFullBlock() && !blockToNorth.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            }
            else if (enumfacing == EnumFacing.WEST && blockToWest.isFullBlock() && !blockToEast.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (enumfacing == EnumFacing.EAST && blockToEast.isFullBlock() && !blockToWest.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            parWorld.setBlockState(parBlockPos, parIBlockState.withProperty(FACING, enumfacing), 2);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.block.Block#onBlockActivated(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.state.IBlockState,
     * net.minecraft.entity.player.EntityPlayer, net.minecraft.util.EnumHand, net.minecraft.util.EnumFacing, float, float, float)
     */
    @Override
    public boolean onBlockActivated(World parWorld, BlockPos parBlockPos, IBlockState parIBlockState, EntityPlayer parPlayer, EnumHand parHand,
            EnumFacing parSide, float hitX, float hitY, float hitZ)
    {
        if (!parWorld.isRemote)
        {
            // DEBUG
            System.out.println("BlockCompactor onBlockActivated() on server side");
            parPlayer.openGui(MainMod.instance, GuiHandler.GUI_ENUM.COMPACTOR.ordinal(), parWorld, parBlockPos.getX(), parBlockPos.getY(), parBlockPos.getZ());
        }

        return true;
    }

    /**
     * Change block based on compacting status.
     *
     * @param parIsCompacting
     *            the par is compacting
     * @param parWorld
     *            the par world
     * @param parBlockPos
     *            the par block pos
     */
    public static void changeBlockBasedOnCompactingStatus(boolean parIsCompacting, World parWorld, BlockPos parBlockPos)
    {
        // IBlockState iBlockState = parWorld.getBlockState(parBlockPos);
        // TileEntity tileentity = parWorld.getTileEntity(parBlockPos);
        // hasTileEntity = true;
        //
        // if (parIsCompacting)
        // {
        // parWorld.setBlockState(parBlockPos, BlockSmith.blockActiveCompactor.getDefaultState().withProperty(FACING, iBlockState.getValue(FACING)), 3);
        // parWorld.setBlockState(parBlockPos, BlockSmith.blockActiveCompactor.getDefaultState().withProperty(FACING, iBlockState.getValue(FACING)), 3);
        // }
        // else
        // {
        // parWorld.setBlockState(parBlockPos, BlockSmith.blockCompactor.getDefaultState().withProperty(FACING, iBlockState.getValue(FACING)), 3);
        // parWorld.setBlockState(parBlockPos, BlockSmith.blockCompactor.getDefaultState().withProperty(FACING, iBlockState.getValue(FACING)), 3);
        // }
        //
        // hasTileEntity = false;
        //
        // if (tileentity != null)
        // {
        // tileentity.validate();
        // parWorld.setTileEntity(parBlockPos, tileentity);
        // }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     *
     * @param worldIn
     *            the world in
     * @param meta
     *            the meta
     * @return the tile entity
     */
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        // DEBUG
        System.out.println("BlockCompactor createNewTileEntity()");
        return new TileEntityCompactor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.block.Block#getStateForPlacement(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.util.EnumFacing, float, float, float, int,
     * net.minecraft.entity.EntityLivingBase, net.minecraft.util.EnumHand)
     */
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer,
            EnumHand hand)
    {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.block.Block#onBlockPlacedBy(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.state.IBlockState,
     * net.minecraft.entity.EntityLivingBase, net.minecraft.item.ItemStack)
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if (stack.hasDisplayName())
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityCompactor)
            {
                ((TileEntityCompactor) tileentity).setCustomInventoryName(stack.getDisplayName());
            }
        }
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

        if (tileentity instanceof TileEntityCompactor)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityCompactor) tileentity);
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

    /**
     * The type of render function that is called for this block.
     *
     * @param parBlockState
     *            the par block state
     * @return the render type
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState parBlockState)
    {
        return EnumBlockRenderType.MODEL;
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

    /**
     * Convert the given metadata into a BlockState for this Block.
     *
     * @param meta
     *            the meta
     * @return the state from meta
     */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return getDefaultState().withProperty(FACING, enumfacing);
    }

    /**
     * Convert the BlockState into the correct metadata value.
     *
     * @param state
     *            the state
     * @return the meta from state
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.block.Block#createBlockState()
     */
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] { FACING });
    }

    @SideOnly(Side.CLIENT)
    static final class SwitchEnumFacing
    {
        static final int[] enumFacingArray = new int[EnumFacing.values().length];

        static
        {
            try
            {
                enumFacingArray[EnumFacing.WEST.ordinal()] = 1;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                enumFacingArray[EnumFacing.EAST.ordinal()] = 2;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                enumFacingArray[EnumFacing.NORTH.ordinal()] = 3;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                enumFacingArray[EnumFacing.SOUTH.ordinal()] = 4;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}