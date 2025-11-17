package com.blazedeveloper.chrono.dataflow.rlog

import com.blazedeveloper.chrono.dataflow.LogReceiver
import com.blazedeveloper.chrono.structure.LogTable
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RLOGWriter(private val filenameOverride: String? = null) : LogReceiver {
    private val encoder = RLOGEncoder()
    private var outputStream: FileOutputStream? = null

    private val logFolder = AppUtil.ROOT_FOLDER.resolve("logs")

    private val fileNameFormat = SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss", Locale.US)

    private fun String.ensureFileExtension(ext: String) =
        if (endsWith(".$ext")) this else "$this.$ext"

    override fun start() {
        if (!logFolder.exists()) logFolder.mkdirs()

        val fileName = filenameOverride ?: fileNameFormat.format(Date())
            .ensureFileExtension("rlog")

        val file = logFolder.resolve(fileName).apply {
            try {
                if (exists()) delete()
                createNewFile()
            } catch(e: Exception) {
                println("""
                    [RLOGWriter] Error opening file! Data WILL NOT be logged!
                    ${e.stackTraceToString()}
                """.trimIndent())
                return
            }
        }

        println("[RLOGWriter] Starting log writer on ${file.absolutePath}")

        outputStream = file.outputStream()
    }

    override fun receive(table: LogTable) {
        val stream = outputStream ?: return

        encoder.encodeTable(table, true)

        try {
            stream.write(encoder.output.array())
        } catch(e: Exception) {
            println("[RLOGWriter] Error writing table to log! Subsequent data will NOT be logged.")
            e.printStackTrace()
            outputStream = null
        }
    }

    override fun stop() {
        outputStream?.close()
        outputStream = null
    }
}