package xyz.colmmurphy.d2mc.listeners

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import xyz.colmmurphy.d2mc.D2MC
import java.awt.Color
import kotlin.math.roundToInt

class CommandListener : ListenerAdapter() {

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        if (e.channel.id != D2MC.channelId || e.isWebhookMessage || e.message.author.isBot) return
        val msg = e.message.contentRaw.split(" ")
        val command: Commands = Commands.findByAlias(msg[0]) ?: return;

        when (command) {
            Commands.HELP -> {
                e.channel.sendTyping().queue()
                e.channel.sendMessageEmbeds(
                    EmbedBuilder()
                        .setThumbnail(D2MC.jda.selfUser.avatarUrl)
                        .setColor(Color.blue)
                        .addField("commands", Commands.helpMenu(), false)
                        .build()
                ).queue()
            }

            Commands.IP -> {
                e.channel.sendMessage(D2MC.ipAddress).queue()
            }

            Commands.LINK -> {
                if (msg[1].isEmpty()) {
                    e.channel.sendMessage("You need to provide a Minecraft username")
                        .queue()
                    return
                }
                D2MC.avatarUrls[msg[1].toLowerCase()] = e.author.id
                e.channel.sendMessage("Linked your discord account with the minecraft username ${msg[1]}")
                    .queue()
            }

            Commands.PLAYERLIST -> {
                e.channel.sendTyping().queue()
                val onlinePlayers = D2MC.server.onlinePlayers
                e.channel.sendMessageEmbeds(
                    EmbedBuilder()
                        .setColor(Color.blue)
                        .setTitle("There are currently **${onlinePlayers.size}** players online")
                        .addField("", onlinePlayers.joinToString { "${it.name}\n" }, true)
                        .build()
                ).queue()
            }

            Commands.TPS -> {
                e.channel.sendTyping().queue()
                val tpsRounded = D2MC.server.tps.map {
                    (it * 100).roundToInt().div(100.0)
                }
                e.channel.sendMessageEmbeds(
                    EmbedBuilder()
                        .setTitle("TPS")
                        .setColor(when (tpsRounded[0]) {
                            in 17.5..20.0 -> Color.green
                            in 15.5..17.5 -> Color.orange
                            else -> Color.red
                        })
                        .addField("",
                            "1m - ${tpsRounded[0]}\n5m - ${tpsRounded[1]}\n15m - ${tpsRounded[2]}",
                            true)
                        .build()
                ).queue()
            }
        }
    }
}

enum class Commands (val aliases: List<String>, val description: String = ".") {
    HELP(
        listOf("help", "h", "commands"),
        "Displays this message"),
    IP(
        listOf("ip", "server"),
        "Shows the server's ip address"),
    LINK(
        listOf("link", "connect"),
        "Link your discord and minecraft accounts"
    ),
    PLAYERLIST(
        listOf("playerlist", "players", "online"),
        "Shows all players currently online"),
    TPS(
        listOf("tps"),
        "Shows the server's TPS (ticks per second)");

    companion object {

        fun findByAlias(s: String): Commands? {
            for (i in Commands.values()) {
                if (i.aliases.contains(s.toLowerCase())) return i
            }
            return null
        }

        fun cmdList(): String {
            var str: String = ""
            for (i in Commands.values()) {
                str += "**${i.name}** - ${i.description}"
            }
            return str
        }

        val helpMenu: () -> String = {
            var str: String = ""
            for (i in Commands.values()) {
                str += "**${i.name}** - ${i.description}\n"
            }
            str
        }
    }
}