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
package stridden.enrich.init;

import javax.annotation.Nullable;

import stridden.enrich.worldgen.WorldGenShrine;
import stridden.enrich.worldgen.WorldProviderCloud;
import stridden.enrich.worldgen.WorldTypeCloud;
import stridden.enrich.worldgen.structures.villages.VillageHouseCloudCreationHandler;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

// TODO: Auto-generated Javadoc
public class ModWorldGen
{  
    public static final String CLOUD_NAME = "cloud";
    public static final int CLOUD_DIM_ID = findFreeDimensionID();
    public static final DimensionType CLOUD_DIM_TYPE = DimensionType.register(CLOUD_NAME, "_"+CLOUD_NAME, CLOUD_DIM_ID, WorldProviderCloud.class, true);
    public static final WorldType CLOUD_WORLD_TYPE = new WorldTypeCloud(); // although instance isn't used, must create the instance to register the WorldType
    public static final IVillageCreationHandler CLOUD_VILLAGE_HANDLER = new VillageHouseCloudCreationHandler();
    
    /**
     * Register dimensions.
     */
    public static final void registerDimensions()
    {
        DimensionManager.registerDimension(CLOUD_DIM_ID, CLOUD_DIM_TYPE);
    }
    
    @Nullable
    private static Integer findFreeDimensionID()
    {
        for (int i=2; i<Integer.MAX_VALUE; i++)
        {
            if (!DimensionManager.isDimensionRegistered(i))
            {
                // DEBUG
                System.out.println("Found free dimension ID = "+i);
                return i;
            }
        }
        
        // DEBUG
        System.out.println("ERROR: Could not find free dimension ID");
        return null;
    }

    /**
     * Register world generators.
     */
    public static void registerWorldGenerators()
    {
        // DEBUG
        System.out.println("Registering world generators");
        GameRegistry.registerWorldGenerator(new WorldGenShrine(), 10);
    }
}
