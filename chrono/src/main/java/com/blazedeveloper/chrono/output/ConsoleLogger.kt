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
    private var outBytesRead: Int = 0
    private var errBytesRead: Int = 0

    /** Starts the capture of System.out and System.err */
    fun start() {
        // Make output streams also print to our stream.
        System.setOut(PrintStream(ParallelOutputStream(systemOut, capturedOut)))
        System.setErr(PrintStream(ParallelOutputStream(systemErr, capturedErr)))
    }

    /** Logs every new captured byte from System.out and System.err. */
    fun log() {
        val outBytes = capturedOut.toByteArray()
        val newOutput = String(outBytes, outBytesRead, outBytes.size - outBytesRead)
        outBytesRead = outBytes.size

        val errBytes = capturedErr.toByteArray()
        val newErr = String(errBytes, errBytesRead, errBytes.size - errBytesRead)
        errBytesRead = errBytes.size

        Logger.output("Console", newOutput + newErr)
    }

    /** Stops the capture of System.out and System.err */
    fun stop() {
        System.setOut(systemOut)
        System.setErr(systemErr)
    }

    private class ParallelOutputStream(private vararg val streams: OutputStream) : OutputStream() {
        @Synchronized
        override fun write(byte: Int) {
            streams.forEach { it.write(byte); it.flush() }
        }
    }
}