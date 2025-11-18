package com.blazedeveloper.chrono.output

import com.blazedeveloper.chrono.Logger
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintStream

/** Captures and allows the console to be logged. */
internal object ConsoleLogger {
    private val systemOut = System.out
    private val systemErr = System.err
    private val capturedOut = ByteArrayOutputStream()
    private val capturedErr = ByteArrayOutputStream()
    private var outBytesLogged: Int = 0
    private var errBytesLogged: Int = 0

    /** Starts the capture of System.out and System.err */
    fun start() {
        // Make output streams also print to our stream.
        System.setOut(PrintStream(ParallelOutputStream(systemOut, capturedOut), true))
        System.setErr(PrintStream(ParallelOutputStream(systemErr, capturedErr), true))
    }

    private fun getUnloggedLines(stream: ByteArrayOutputStream, loggedBytes: Int): String {
        val allBytes = stream.toByteArray()
        var unloggedText = String(allBytes, loggedBytes, allBytes.size - loggedBytes)

        val unloggedLines = StringBuilder()

        while (unloggedText.contains('\n')) {
            val line = unloggedText.slice(0..unloggedText.indexOf('\n'))
            unloggedText = unloggedText.drop(line.length)
            unloggedLines.append(line)
        }

        return unloggedLines.toString()
    }

    /** Logs every new captured byte from System.out and System.err. */
    fun log() {
        val unloggedOutput = getUnloggedLines(capturedOut, outBytesLogged)
        outBytesLogged += unloggedOutput.toByteArray().size

        val unloggedErr = getUnloggedLines(capturedErr, errBytesLogged)
        errBytesLogged += unloggedErr.toByteArray().size

        if (unloggedOutput.isBlank() && unloggedErr.isBlank()) return
        Logger.output("Console", unloggedOutput + unloggedErr)
    }

    /** Stops the capture of System.out and System.err */
    fun stop() {
        System.setOut(systemOut)
        System.setErr(systemErr)
    }

    private class ParallelOutputStream(private vararg val streams: OutputStream) : OutputStream() {
        @Synchronized
        override fun write(byte: Int) {
            streams.forEach { it.write(byte) }
        }

        @Synchronized
        override fun flush() {
            streams.forEach { it.flush() }
        }
    }
}