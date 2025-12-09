//    Copyright (c) 2021-2025 Littleton Robotics. All rights reserved.
//
//    Redistribution and use in source and binary forms, with or without
//    modification, are permitted provided that the following conditions are met:
//
//    - Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
//    - Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
//    - Neither the name of Littleton Robotics, FRC 6328 ("Mechanical Advantage"),
//    AdvantageKit, nor the names of other AdvantageKit contributors may be
//    used to endorse or promote products derived from this software without
//    specific prior written permission.
//
//    THIS SOFTWARE IS PROVIDED BY LITTLETON ROBOTICS AND OTHER ADVANTAGEKIT
//    CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
//    NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY NONINFRINGEMENT
//    AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
//    LITTLETON ROBOTICS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
//    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
//    OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
//    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
//    EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.blazedeveloper.chrono.dataflow.rlog;

import com.blazedeveloper.chrono.dataflow.LogReceiver;
import com.blazedeveloper.chrono.structure.LogTable;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class RLOGServer implements LogReceiver {
    private final int port;
    private ServerThread thread;
    private RLOGEncoder encoder = new RLOGEncoder();

    private boolean running = false;

    private static Object encoderLock = new Object();
    private static Object socketsLock = new Object();

    /** Creates a new RLOGServer on the default port (5800). */
    public RLOGServer() {
        this(5800);
    }

    /**
     * Creates a new RLOGServer.
     *
     * @param port The port number.
     */
    public RLOGServer(int port) {
        this.port = port;
    }

    public void start() {
        running = true;
        thread = new ServerThread(port);
        thread.start();
        System.out.println("[Chrono] RLOG server started on port " + port);
    }

    public void stop() {
        System.out.println("[RLOGServer] Stopping...");
        running = false;
        if (thread != null) {
            thread.close();
            thread = null;
        }
    }

    public void receive(LogTable table) throws InterruptedException {
        if (thread != null && thread.broadcastQueue.remainingCapacity() > 0) {
            // If broadcast is behind, drop this cycle and encode changes in the next cycle
            byte[] data;
            synchronized (encoderLock) {
                encoder.encodeTable(table, false);
                data = encodeData(encoder.getOutput().array());
            }
            thread.broadcastQueue.offer(data);
        }
    }

    private byte[] encodeData(byte[] data) {
        byte[] lengthBytes = ByteBuffer.allocate(Integer.BYTES).putInt(data.length).array();
        byte[] fullData = new byte[lengthBytes.length + data.length];
        System.arraycopy(lengthBytes, 0, fullData, 0, lengthBytes.length);
        System.arraycopy(data, 0, fullData, lengthBytes.length, data.length);
        return fullData;
    }

    private class ServerThread extends Thread {
        private static final double heartbeatTimeoutSecs =
                3.0; // Close connection if heartbeat not received for this
        // length

        ServerSocket server;
        Thread broadcastThread;

        ArrayBlockingQueue<byte[]> broadcastQueue = new ArrayBlockingQueue<>(500);
        List<Socket> sockets = new ArrayList<>();
        List<Double> lastHeartbeats = new ArrayList<>();

        public ServerThread(int port) {
            super("Chrono_RLOGServer");
            this.setDaemon(true);
            try {
                server = new ServerSocket();
                server.setReuseAddress(true);
                server.bind(new InetSocketAddress(port));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            if (server == null) {
                return;
            }

            // Start broadcast thread
            broadcastThread = new Thread(this::runBroadcast);
            broadcastThread.setName("Chrono_RLOGServerBroadcast");
            broadcastThread.setDaemon(true);
            broadcastThread.start();

            // Wait for clients
            while (running) {
                try {
                    Socket socket = server.accept();
                    byte[] data;
                    synchronized (encoderLock) {
                        data = encodeData(encoder.getNewcomerData().array());
                    }
                    socket.getOutputStream().write(data);
                    synchronized (socketsLock) {
                        sockets.add(socket);
                        lastHeartbeats.add(System.nanoTime() / 1_000_000_000.0);
                    }
                    System.out.println(
                            "[Chrono] Connected to RLOG server client - "
                                    + socket.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    if (!running) {
                        System.out.println("[Chrono] RLOG server successfully stopped.");
                        return;
                    }
                    e.printStackTrace();
                }
            }
        }

        public void runBroadcast() {
            while (running) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    return;
                }

                // Get queue data
                List<byte[]> broadcastData = new ArrayList<>();
                broadcastQueue.drainTo(broadcastData);

                // Broadcast to each client
                synchronized (socketsLock) {
                    for (int i = 0; i < sockets.size(); i++) {
                        Socket socket = sockets.get(i);
                        if (socket.isClosed()) {
                            continue;
                        }

                        try {
                            // Read heartbeat
                            InputStream inputStream = socket.getInputStream();
                            if (inputStream.available() > 0) {
                                inputStream.skip(inputStream.available());
                                lastHeartbeats.set(i, System.nanoTime() / 1_000_000_000.0);
                            }

                            // Close connection if socket timed out
                            if (System.nanoTime() / 1_000_000_000.0 - lastHeartbeats.get(i)
                                    > heartbeatTimeoutSecs) {
                                socket.close();
                                printDisconnectMessage(socket, "timeout");
                                continue;
                            }

                            // Send message to stay alive
                            java.io.OutputStream outputStream = socket.getOutputStream();
                            outputStream.write(new byte[4]);

                            // Send broadcast data
                            for (byte[] data : broadcastData) {
                                outputStream.write(data);
                            }
                        } catch (IOException e) {
                            try {
                                socket.close();
                                printDisconnectMessage(socket, "IOException");
                            } catch (IOException a) {
                                a.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        private void printDisconnectMessage(Socket socket, String reason) {
            System.out.println(
                    "[Chrono] Disconnected from RLOG server client ("
                            + reason
                            + ") - "
                            + socket.getInetAddress().getHostAddress());
        }

        public void close() {
            if (server != null) {
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (broadcastThread != null) {
                broadcastThread.interrupt();
            }
            this.interrupt();
        }
    }
}