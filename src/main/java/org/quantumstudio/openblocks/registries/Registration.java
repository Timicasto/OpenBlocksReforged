package org.quantumstudio.openblocks.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Registration {
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e) {
		e.getRegistry().registerAll(

		);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e) {
		e.getRegistry().registerAll(

		);
	}
}
