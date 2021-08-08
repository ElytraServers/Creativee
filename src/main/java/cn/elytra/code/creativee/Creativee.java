package cn.elytra.code.creativee;

import cn.elytra.code.api.locale.ILocale;
import cn.elytra.code.api.locale.LocaleService;
import cn.elytra.code.api.locale.LocaleSetupException;
import cn.elytra.code.api.locale.SuggestedLanguageChangedEvent;
import cn.elytra.code.creativee.event.SafeModeListener;
import cn.elytra.code.creativee.tempofly.TempoflyManager;
import com.google.common.collect.Lists;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public final class Creativee extends JavaPlugin implements Listener {

	public ILocale locale = ILocale.EMPTY_LOCALE;

	@Nullable
	public Economy economy;

	public TempoflyManager tempofly;
	public SafeModeListener safeMode;

	@Override
	public void onEnable() {
		setupLocale();
		setupEconomy();

		tempofly = new TempoflyManager(this);
		safeMode = new SafeModeListener(this);

		onReload();

		// Listen SuggestedLanguageChangeEvent for Localization change.
		getServer().getPluginManager().registerEvents(this, this);
		// Listen PlayerQuitEvent for Temporary Fly.
		getServer().getPluginManager().registerEvents(tempofly, this);
		// Listen EntityExplodeEvent, PlayerInteractEvent(on Farmland) and PlayerEnterBedEvent(in Nether, etc.) for SafeMode
		getServer().getPluginManager().registerEvents(safeMode, this);

		getCommand("creativee").setExecutor(this);
		getCommand("creativee").setTabCompleter(this);

		// Setup Temporary Fly ticking.
		tempofly.setupTicker();
	}

	@Override
	public void onDisable() {
	}

	private void onReload() {
		reloadConfig();
		safeMode.enable = getConfig().getBoolean("creativee.SafeMode", true);
	}

	private void setupLocale() {
		if(getServer().getPluginManager().getPlugin("ElytraApi") != null) {
			LocaleService ls = getServer().getServicesManager().getRegistration(LocaleService.class).getProvider();
			try {
				this.locale = ls.loadLocaleYaml(this,
						"locale/"+ls.getSuggestedLanguage()+".yml");
			} catch (LocaleSetupException setup) {
				if(LocaleSetupException.TYPE_FILE_MISSING == setup.getExceptionType()) {
					getLogger().warning("Locale file is missing! Report this to the Issues in Github repo.");
				}
				throw setup;
			}
		} else {
			throw new IllegalStateException("Creativee requires ElytraApi2.");
		}
	}

	private void setupEconomy() {
		if(getServer().getPluginManager().getPlugin("Vault") != null) {
			economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
			if(economy == null) {
				tempofly.setNoCost();
				getLogger().warning(locale.format("elytra.creativee.loading.error.missing-economy"));
			}
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(label.equalsIgnoreCase("creativee")) {
			switch (args.length) {
				case 1:
					String sub = args[0];
					if(sub.equalsIgnoreCase("tempofly")) {
						if(sender instanceof Player) {
							tempofly.buyTempofly(((Player) sender));
						} else {
							sender.sendMessage(locale.format("elytra.creativee.command.tempofly.player-only"));
						}
						return true;
					} else if(sub.equalsIgnoreCase("reload")) {
						onReload();
						sender.sendMessage(locale.format("elytra.creativee.command.reload"));
						return true;
					}
					break;
				case 2:
					String sub1 = args[0];
					String sub2 = args[1];
					if(sub1.equalsIgnoreCase("tempofly")) {
						tempofly.buyTempofly(sender, getServer().getPlayer(sub2));
						return true;
					}
					break;
				default:
					sender.sendMessage(locale.format("elytra.creativee.command.help.0"));
					sender.sendMessage(locale.format("elytra.creativee.command.help.1"));
					sender.sendMessage(locale.format("elytra.creativee.command.help.2"));
					sender.sendMessage(locale.format("elytra.creativee.command.help.3"));
					return true;
			}
		}
		return super.onCommand(sender, command, label, args);
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(alias.equalsIgnoreCase("creativee")) {
			if(args.length == 1) {
				String sub = args[0];
				if(sub.equalsIgnoreCase("")) {
					return Lists.newArrayList("tempofly", "reload");
				} else if(sub.equalsIgnoreCase("tempofly")) {
					return getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
				}
				return Lists.newArrayList();
			}
		}
		return super.onTabComplete(sender, command, alias, args);
	}

	@EventHandler
	public void onLangChange(SuggestedLanguageChangedEvent event) {
		setupLocale();
	}
}
