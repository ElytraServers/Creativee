package cn.elytra.code.creativee.event;

import cn.elytra.code.api.localeV1.PluginLocaleManagerV1;
import cn.elytra.code.creativee.Creativee;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SafeModeListener implements Listener {

	private final Creativee creativee;

	public boolean enable = true;

	public SafeModeListener(Creativee creativee) {
		this.creativee = creativee;
	}

	private static final EntityType[] PREVENTED_ENTITY_TYPES = new EntityType[] {
			EntityType.PRIMED_TNT,
			EntityType.ENDER_CRYSTAL,
			EntityType.CREEPER,
			EntityType.WITHER_SKULL,
			EntityType.SMALL_FIREBALL,
			EntityType.WITHER
	};

	@EventHandler
	public void onExplodeEvent(EntityExplodeEvent event) {
		if(!enable) return;
		if(ArrayUtils.contains(PREVENTED_ENTITY_TYPES, event.getEntityType())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBedEnterEvent(PlayerBedEnterEvent event) {
		if(!enable) return;
		if(event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_HERE) {
			event.setCancelled(true);
			PluginLocaleManagerV1.sendMessage(creativee, event.getPlayer(), "elytra.creativee.safemode.player-enter-bed");
		}
	}

	@EventHandler
	public void onSoilChange(PlayerInteractEvent event) {
		if(!enable) return;
		if(event.getAction() == Action.PHYSICAL &&
				event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FARMLAND) {
			event.setCancelled(true);
		}
	}

}
