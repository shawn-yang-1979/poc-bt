package com.shawnyang.poc.bt.bluetooth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.shawnyang.poc.bt.boot.Boot;
import com.shawnyang.poc.bt.boot.dto.BootInfo;
import com.shawnyang.poc.bt.boot.dto.BootResult;

@Component
public class BluetoothServer {

	private static final Logger log = LoggerFactory.getLogger(BluetoothServer.class);

	@Autowired
	private BluetoothServerProperties bluetoothServerProperties;

	@Autowired
	private Boot boot;

	@Autowired
	private Gson gson;

	private StreamConnectionNotifier connection;

	@PostConstruct
	public void init() throws IOException {
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		localDevice.setDiscoverable(DiscoveryAgent.GIAC);
		log.info("Local device bluetooth address: " + localDevice.getBluetoothAddress());
		log.info("Local device friendly name: " + localDevice.getFriendlyName());

		// Create a server connection (a notifier)
		UUID uuid = new UUID(bluetoothServerProperties.getUuid(), false);
		String name = bluetoothServerProperties.getName();
		String connection_url = "btspp://localhost:" + uuid.toString() + ";name=" + name
				+ ";authenticate=false;encrypt=false";
		connection = (StreamConnectionNotifier) Connector.open(connection_url);
	}

	@EventListener(ContextRefreshedEvent.class)
	@Async
	public void listen() {
		while (true) {
			StreamConnection streamConnection = null;
			try {
				// Accept a new client connection
				log.info("Waiting for remote device.");
				streamConnection = connection.acceptAndOpen();
				RemoteDevice remoteDevice = RemoteDevice.getRemoteDevice(streamConnection);
				log.info("Remote device bluetooth address: " + remoteDevice.getBluetoothAddress());
				log.info("Remote device friendly name: " + remoteDevice.getFriendlyName(true));

				ExecutorService executor = Executors.newCachedThreadPool();

				Callable<Void> task = new Process(streamConnection);
				Future<Void> future = executor.submit(task);
				try {
					future.get(1200, TimeUnit.SECONDS);
				} finally {
					future.cancel(true); // may or may not desire this
				}

			} catch (Exception e) {
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

	private class Process implements Callable<Void> {

		private StreamConnection streamConnection;

		public Process(StreamConnection streamConnection) {
			super();
			this.streamConnection = streamConnection;
		}

		@Override
		public Void call() throws Exception {
			process();
			return null;
		}

		private void process() throws IOException {

			BufferedReader br = new BufferedReader(new InputStreamReader(streamConnection.openInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(streamConnection.openOutputStream()));

			while (true) {
				HttpMethod httpMethod = null;
				String resource = null;
				String requestMessage;

				String inputLine;
				boolean isReadingMessage = false;
				StringBuilder messageBuffer = new StringBuilder();
				while ((inputLine = br.readLine()) != null) {
					log.debug("inputLine=" + inputLine);
					if (isReadingMessage) {
						if (StringUtils.isEmpty(inputLine)) {// empty means done
							log.debug("comment=message read complete");
							break;
						} else {
							messageBuffer.append(inputLine);
						}
					} //
					else {// not reading messages means waiting for request
						String[] request = StringUtils.split(inputLine);
						if (request.length != 2) {
							log.debug("comment=Not a request line.");
							continue;
						}
						httpMethod = HttpMethod.resolve(request[0]);
						if (httpMethod == null) {
							log.debug("comment=Unknown http method: " + request[0]);
							continue;
						}
						String requestLine = inputLine;
						log.info("requestLine=" + requestLine);
						resource = request[1];
						isReadingMessage = true;
						log.debug("comment=message read start");
					}
				}
				requestMessage = messageBuffer.toString();
				log.info("requestMessage=" + requestMessage);

				HttpStatus status = HttpStatus.OK;
				String responseMessage = "";
				if ("/bootInfo".equals(resource)) {
					if (HttpMethod.PUT == httpMethod) {
						try {
							BootInfo request = gson.fromJson(requestMessage, BootInfo.class);
							BootResult response = boot.putBootInfo(request);
							responseMessage = gson.toJson(response);
						} catch (JsonSyntaxException e) {
							status = HttpStatus.BAD_REQUEST;
							responseMessage = e.toString() + "\n" + e.getMessage();
						} catch (Exception e) {
							status = HttpStatus.INTERNAL_SERVER_ERROR;
							responseMessage = e.toString() + "\n" + e.getMessage();
						}
					} else {
						status = HttpStatus.METHOD_NOT_ALLOWED;
					}
				} else {
					status = HttpStatus.NOT_FOUND;
				}

				String statusLine = status.value() + " " + status.getReasonPhrase();
				log.info("statusLine=" + statusLine);
				log.info("responseMessage=" + responseMessage);

				bw.write(statusLine + "\n");
				if (StringUtils.isNotEmpty(responseMessage)) {
					bw.write(responseMessage + "\n");
				}
				bw.write("\n");// empty line means done.
				bw.flush();
			}
		}

	}

	// private boolean dummySuccess = false;
	//
	// private BootInfoResponse bootInfo(BootInfoRequest request) throws
	// Exception {
	// // do something
	// if (dummySuccess) {
	// BootInfoResponse response = new BootInfoResponse();
	// response.setSuccess(dummySuccess);
	// for (Device device : request.getDevices()) {
	// response.getSuccessDeviceIds().add(device.getId());
	// }
	// dummySuccess = false;
	// return response;
	// } else {
	// BootInfoResponse response = new BootInfoResponse();
	// response.setSuccess(dummySuccess);
	// response.setMessage("Fail to complete the boot.");
	// for (int i = 0; i < request.getDevices().size(); i++) {
	// Device device = request.getDevices().get(i);
	// if (i == 0) {
	// response.getErrorDeviceIds().add(device.getId());
	// } else {
	// response.getSuccessDeviceIds().add(device.getId());
	// }
	// }
	// dummySuccess = true;
	// return response;
	// }
	// }

	@PreDestroy
	public void cleanup() {
		try {
			connection.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
