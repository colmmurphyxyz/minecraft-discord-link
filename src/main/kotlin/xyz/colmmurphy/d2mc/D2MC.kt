package xyz.colmmurphy.d2mc

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.kyori.adventure.text.Component
import okhttp3.OkHttpClient
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import xyz.colmmurphy.d2mc.listeners.GuildMessageListener
import java.io.InputStream
import java.lang.NullPointerException
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel

class D2MC : JavaPlugin() {

    companion object {
        lateinit var server: org.bukkit.Server

        lateinit var ipAddress: String

        lateinit var guildId: String
        lateinit var channelId: String
        lateinit var gld: Guild
        lateinit var chnl: TextChannel

        lateinit var jda: JDA
        lateinit var webhook: Webhook
        lateinit var webhookClient: WebhookClient
    }

    override fun onEnable() {
        D2MC.server = this.server
        // config stuff
        this.saveDefaultConfig()
        val config = this.config

        val token: String
        try {
            guildId = config.getString("guild-id")!!
            channelId = config.getString("channel-id")!!
            token = config.getString("bot-token")!!
        } catch (e: NullPointerException) {
            println("[D2MC] Your config.yml file isn't configured correctly")
            kotlin.system.exitProcess(1)
        }
        ipAddress = config.getString("ip-address") ?: "Minecraft Server"

        //login into Discord and send notice
        jda = JDABuilder.create(
            token,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_WEBHOOKS,
            GatewayIntent.GUILD_MEMBERS,
        )
            .setActivity(Activity.playing(ipAddress))
            .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS,
                CacheFlag.ONLINE_STATUS)
            .addEventListeners(GuildMessageListener())
            .build()
            .awaitReady()
        println("[D2MC] successfully logged into discord as ${jda.selfUser.name}#${jda.selfUser.discriminator}")

        try {
            gld = jda.getGuildById(guildId)!!
            chnl = gld.getTextChannelById(channelId)!!
        } catch (e: NullPointerException) {
            println("[D2MC] Couldn't find a Discord server or channel with the ID's provided in config.yml\n" +
                "server ID: ${guildId}\nchannel ID: $channelId")
            kotlin.system.exitProcess(1)
        }

        // Create a webhook and send a message with it
        chnl.createWebhook("mc.colmmurphy.xyz")
            .setAvatar(Icon.from(URL(jda.selfUser.avatarUrl).openStream()))
            .queue { createdWebhook ->
            webhook = createdWebhook
            webhookClient = WebhookClient.withUrl(createdWebhook.url)
            webhookClient.send("Server is online")
            webhookClient.send(
                WebhookMessageBuilder()
                    .setUsername("test")
                    .setAvatarUrl(jda.selfUser.avatarUrl)
                    .build()
            )
        }

    }

    override fun onDisable() {
        println("[D2MC] Shutting down D2MC")
        webhook.delete().queue { _ -> println("[D2MC] Deleted webhook") }
    }
}