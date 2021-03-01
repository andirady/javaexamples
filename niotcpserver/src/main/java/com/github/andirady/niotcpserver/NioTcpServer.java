package com.github.andirady.niotcpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NioTcpServer {
	
	private static final Logger LOG = Logger.getLogger("main");

	public static void main(String[] args) throws IOException {
		try (var serverSocketChannel = ServerSocketChannel.open()) {
			var selector = Selector.open();
			serverSocketChannel.bind(new InetSocketAddress("0.0.0.0", 5000));
			LOG.info("Server listening on port 5000");
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			var nioTcpServer = new NioTcpServer();
			while (true) {
				selector.select(nioTcpServer::handleKey, 0);
			}
		}
	}

	public void handleKey(SelectionKey key) {
		if (!key.isValid()) {
			return;
		}
		
		if (key.isAcceptable()) {
			var server = (ServerSocketChannel) key.channel();
			try {
				var client = server.accept();
				client.configureBlocking(false);
				client.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
				LOG.info("Client connected: " + client);
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Failed to accept client", e);
			}
		}
		
		if (key.isReadable()) {
			readRequest(key);
		}
	}
	
	private void readRequest(SelectionKey key) {
		var buffer = (ByteBuffer) key.attachment();
		try (var channel = (SocketChannel) key.channel()) {
			var size = channel.read(buffer);
			var data = new byte[size];
			System.arraycopy(buffer.array(), 0, data, 0, size);
			buffer.clear();

            var req = new String(data);
            var resp = req.getBytes();
            buffer = ByteBuffer.allocate(resp.length);
            System.arraycopy(resp, 0, buffer.array(), 0, resp.length);
            channel.write(buffer);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failure", e);
		}
	}
}
