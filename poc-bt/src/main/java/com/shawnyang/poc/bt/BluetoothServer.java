package com.shawnyang.poc.bt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class BluetoothServer {

	private static final Logger log = LoggerFactory.getLogger(BluetoothServer.class);
	// Define the server connection URL
	private static final UUID MYSERVICEUUID_UUID = new UUID("e67eff96668c4f69a89b5b53e9336921", false);
	private static final String CONNECTION_URL = "btspp://localhost:" + MYSERVICEUUID_UUID.toString()
			+ ";name=POC_BT_SERVICE_NAME;authenticate=false;encrypt=false";

	private StreamConnectionNotifier connection;

	@PostConstruct
	public void init() throws IOException {
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		localDevice.setDiscoverable(DiscoveryAgent.GIAC);
		log.info("My bluetooth address: " + localDevice.getBluetoothAddress());
		log.info("My friendly name: " + localDevice.getFriendlyName());
		// Create a server connection (a notifier)
		connection = (StreamConnectionNotifier) Connector.open(CONNECTION_URL);
	}

	@Async
	public void listen() {
		while (true) {
			StreamConnection streamConnection = null;
			try {
				// Accept a new client connection
				streamConnection = connection.acceptAndOpen();
				RemoteDevice remoteDevice = RemoteDevice.getRemoteDevice(streamConnection);
				log.info("Remote device bluetooth address: " + remoteDevice.getBluetoothAddress());
				log.info("Remote device friendly name: " + remoteDevice.getFriendlyName(true));

				BufferedReader br = new BufferedReader(new InputStreamReader(streamConnection.openInputStream()));
				DataOutputStream outStream = streamConnection.openDataOutputStream();

				while (true) {

					boolean exit = false;
					StringBuilder input = new StringBuilder();
					String lineRead;
					while ((lineRead = br.readLine()) != null) {
						if ("$send".equals(lineRead)) {
							break;
						} else if ("$exit".equals(lineRead)) {
							exit = true;
							break;
						}
						input.append(lineRead).append("\n");
					}
					log.info(input.toString());

					// send response to spp client
					String response = "Hello, got your message: " + input.toString();
					log.info(response);
					outStream.writeUTF(response + "\n");

					if (exit) {
						String bye = "Bye.";
						log.info(bye);
						outStream.writeUTF(bye + "\n");
						break;
					}

				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			} finally {
				if (streamConnection != null) {
					try {
						streamConnection.close();
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	@PreDestroy
	public void cleanup() {
		try {
			connection.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
