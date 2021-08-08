# Creativee

<image src="https://img.shields.io/github/license/ElytraServers/Creativee?label=License&style=flat-square"></image>
<image src="https://img.shields.io/github/stars/ElytraServers/Creativee?label=Stars&style=flat-square"></image>
<image src="https://img.shields.io/badge/author-Taskeren-red?style=flat-square"></image>

## 插件介绍

Creativee 是一个 Bukkit 插件，用于创造服、生存建筑服。

## 使用

### 对服主

1. 在 [这里](https://github.com/ElytraServers/Creativee/release/latest) 中下载最新版本的 Creativee。
2. 在 [这里](https://github.com/ElytraServers/ElytraApi2/release/latest) 中下载最新版本的 ElytraApi2。
3. 将上面的两个拖入支持 Bukkit 插件的服务器的 `./plugins` 文件夹中。
4. 启动。

#### 配置插件

在 `./plugins/Creativee/config.yml` 中有以下配置：

| Yaml 键 | 值类型 | 说明 |
| :-: | :-: | :-: |
| `creativee.SafeMode` | Boolean | 配置是否启用 SafeMode，即防爆功能 |
| `creativee.TempoFly.cost` | Double | 配置每一份 TempoFly 的金额 |
| `creativee.TempoFly.duration` | Long | 配置每一份 TempoFly 的时长，单位：游戏刻（20tick = 1s）|

### 对玩家

插件默认启用 SafeMode 防爆功能，无需额外操作。如果没有生效是服务器配置问题。

此外 Creativee 还提供 Temporary Fly 功能，即你可以使用一定的金额换取一定时间的飞行权限。以下是相关指令和说明：

| 指令 | 说明 |
| :-: | :-: |
| `/creativee tempofly` | 若目前没有使用 TempoFly，则使用配置的金额获得配置的时间（游戏刻）的飞行权限；若正在使用 TempoFly，则返回剩余的飞行时间长度（刻）。 |
| `/creativee tempofly <player>` | 给 `<player>` 购买一个 TempoFly 时长，与自己购买获得的时间相同。本指令可以在控制台执行，且不会花费任何金额。 |

## FAQ

### Temporary Fly 不扣钱怎么办？

首先先检查配置是否正确。若配置正确无误，请确认有安装 Vault 和任意 Vault 支持的经济插件。

### Temporary Fly 莫名其妙消失？

在 TempoFly 有效时退出服务器，则被视为放弃剩余时间。
