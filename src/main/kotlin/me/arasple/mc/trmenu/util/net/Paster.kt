package me.arasple.mc.trmenu.util.net

import com.google.gson.JsonParser
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.IO
import me.arasple.mc.trmenu.util.Tasks
import org.bukkit.command.CommandSender
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * @author Arasple
 * @date 2020/12/19 23:11
 */
object Paster {

    private const val URL = "https://paste.helpch.at/"

    fun paste(sender: CommandSender, content: String, extension: String = "") {
        TLocale.sendTo(sender, "Paster.Processing")
        paste(
            content,
            { TLocale.sendTo(sender, "Paster.Success", "$it.$extension") },
            { TLocale.sendTo(sender, "Paster.Failed") }
        )
    }

    private fun paste(content: String, url: (String) -> Unit, failed: () -> Unit) {
        Tasks.task(true) {
            try {
                val con = URL("${URL}documents").openConnection() as HttpURLConnection
                con.requestMethod = "POST"
                con.setRequestProperty("Charset", "UTF-8")
                con.doInput = true
                con.doOutput = true
                con.outputStream.also { it.write(content.toByteArray(StandardCharsets.UTF_8)) }
                val source = JsonParser().parse(IO.readFully(con.inputStream, StandardCharsets.UTF_8)).asJsonObject
                url(URL + source.get("key").asString)
            } catch (e: Throwable) {
                e.printStackTrace()
                failed.invoke()
            }
        }
    }

}