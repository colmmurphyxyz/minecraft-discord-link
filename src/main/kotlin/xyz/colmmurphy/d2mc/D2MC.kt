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
import xyz.colmmurphy.d2mc.listeners.ChatListener
import xyz.colmmurphy.d2mc.listeners.CommandListener
import xyz.colmmurphy.d2mc.listeners.PlayerJoinLeaveListener
import java.io.InputStreamReader

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

        val playerAvatars = HashMap<String, String>()

        fun addAvatar(player: org.bukkit.entity.Player) = addAvatar(player.name)

        fun addAvatar(name: String) {
            playerAvatars[name] = getAvatarUrl(name)
        }

        fun removeAvatar(player: org.bukkit.entity.Player) = removeAvatar(player.name)

        fun removeAvatar(name: String) {
            playerAvatars.remove(name)
        }

        private fun getAvatarUrl(name: String): String {
            val reader1 = InputStreamReader(
                URL("https://api.mojang.com/users/profiles/minecraft/$name").openStream())
            val json1 = reader1.readText()
            reader1.close()
            val uuid = json1.substringAfter("\"id\":\"").substringBefore("\"}")

            val reader2 = InputStreamReader(
                URL("https://sessionserver.mojang.com/session/minecraft/profile/${uuid}?unsigned=false").openStream())
            val json2 = reader2.readText()
            reader2.close()
            val texture = json2.substringAfter("\"value\" : \"").substringBefore("\"")
            //val signature = json2.substringAfter("\"signature\" : \"").substringBefore("\"")
            return "https://crafatar.com/avatars/${uuid}.png?size=128&overlay#${texture}"
        }
    }

    override fun onEnable() {
        D2MC.server = this.server
        server.pluginManager.registerEvents(ChatListener(), this)
        server.pluginManager.registerEvents(PlayerJoinLeaveListener(), this)
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
            .addEventListeners(CommandListener())
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
            webhookClient.send(":green_circle: Server is online :green_circle:")
        }

    }

    override fun onDisable() {
        webhookClient.send(":octagonal_sign: Server is offline :octagonal_sign:")
        println("[D2MC] Shutting down D2MC")
        webhook.delete().queue { _ -> println("[D2MC] Deleted webhook") }
    }
}
