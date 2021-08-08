package cn.elytra.code.creativee.tempofly;

import cn.elytra.code.creativee.Creativee;
import com.google.common.collect.Maps;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public class TempoflyManager implements Listener {

	private final Creativee creativee;

	private double cost = 100;
	private long duration = 20 * 60;

	private boolean noCost = false;

	private BukkitTask ticker;

	private long currentTick = 0L;
	private final Map<UUID, Long> tempoflyMap = Maps.newHashMap();

	public TempoflyManager(Creativee creativee) {
		this.creativee = creativee;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getCurrentTick() {
		return currentTick;
	}

	public void setCurrentTick(long currentTick) {
		this.currentTick = currentTick;
	}

	/**
	 * 因为没有经济系统而设置为免费。
	 */
	public void setNoCost() {
		noCost = true;
	}

	public void setupTicker() {
		if(ticker != null) {
			throw new IllegalStateException("It've already get one ticker.");
		} else {
			ticker = Bukkit.getScheduler().runTaskTimer(creativee, this::tick, 0, 1);
		}
	}

	public void tick() {
		currentTick++;

		for(Map.Entry<UUID, Long> entry : tempoflyMap.entrySet()) {
			UUID uuid = entry.getKey();
			Long endTick = entry.getValue();

			Player player;
			if ((player = creativee.getServer().getPlayer(uuid)) != null) {
				if (endTick > currentTick) {
					player.setAllowFlight(true);
				} else {
					removeTempofly(player);
				}
			}
		}
	}

	public void buyTempofly(Player player) {
		this.buyTempofly(player, player);
	}

	public void buyTempofly(CommandSender coster, Player flyer) {
		if(flyer == null) {
			coster.sendMessage(creativee.locale.format("elytra.creativee.tempofly.gift.receiver-not-found"));
			return;
		}

		if(tempoflyMap.containsKey(flyer.getUniqueId())) {
			if(coster == flyer) {
				coster.sendMessage(creativee.locale.format("elytra.creativee.tempofly.remains",
						tempoflyMap.get(flyer.getUniqueId()) - currentTick));
			} else {
				coster.sendMessage(creativee.locale.format("elytra.creativee.tempofly.gift.remains"));
			}
			return;
		}

		EconomyResponse resp = null;
		if(!noCost) {
			if(coster instanceof Player) {
				resp = creativee.economy.withdrawPlayer((Player) coster, cost);
			} else if(coster instanceof ConsoleCommandSender) {
				resp = new EconomyResponse(cost, Double.MAX_VALUE, EconomyResponse.ResponseType.SUCCESS, null);
			} else {
				coster.sendMessage("Unsupported Command Sender");
				return;
			}
		}

		if(noCost || resp.transactionSuccess()) {
			long tickToDisable = getCurrentTick() + getDuration();
			tempoflyMap.put(flyer.getUniqueId(), tickToDisable);
			if(coster == flyer) { // 正常情况
				if (!noCost) {
					flyer.sendMessage(creativee.locale.format("elytra.creativee.tempofly.buy-success", getCost(), getDuration()));
				} else {
					flyer.sendMessage(creativee.locale.format("elytra.creativee.tempofly.no-cost-success", getDuration()));
				}
			} else { // 帮买情况
				if(!noCost) {
					coster.sendMessage(creativee.locale.format("elytra.creativee.tempofly.gift.sender",
							getCost(), flyer.getDisplayName(), getDuration()));
					flyer.sendMessage(creativee.locale.format("elytra.creativee.tempofly.gift.receiver",
							getDuration(), coster.getName(), getCost()));
				} else {
					coster.sendMessage(creativee.locale.format("elytra.creativee.tempofly.gift-no-cost.sender",
							flyer.getDisplayName(), getDuration()));
					flyer.sendMessage(creativee.locale.format("elytra.creativee.tempofly.gift-no-cost.receiver",
							getDuration(), coster.getName()));
				}
			}
		}
	}

	/**
	 * 移除玩家的临时飞行。用于玩家退出游戏，关服等情况。
	 */
	public void removeTempofly(Player player) {
		tempoflyMap.remove(player.getUniqueId());
		switch(player.getGameMode()) {
			case ADVENTURE:
			case SURVIVAL:
				player.setAllowFlight(false);
				break;
			case CREATIVE:
			case SPECTATOR:
				player.setAllowFlight(true);
				break;
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		removeTempofly(event.getPlayer());
	}

}
