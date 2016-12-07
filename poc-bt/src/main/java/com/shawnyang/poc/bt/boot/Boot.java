package com.shawnyang.poc.bt.boot;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.shawnyang.poc.bt.boot.dto.BootInfo;
import com.shawnyang.poc.bt.boot.dto.BootResult;
import com.shawnyang.poc.bt.boot.dto.Device;
import com.shawnyang.poc.bt.boot.dto.Type;

@Component
public class Boot {

	private int fakestate = 0;

	public BootResult putBootInfo(BootInfo bootInfo) throws ConfigurationException {

		BootResult result = new BootResult();
		StringBuilder message = new StringBuilder();
		result.setId(bootInfo.getId());
		fakestate++;
		if (fakestate == 1 || fakestate == 2) {
			for (Device device : bootInfo.getDevices()) {
				result.getSuccessDeviceIds().add(device.getId() + "\n");
			}
		} else if (fakestate == 3 || fakestate == 4) {
			for (Device device : bootInfo.getDevices()) {
				if (Type.DEVICE_RFID_READER.equals(device.getType())) {
					message.append("Fail to boot reader component.\n");
					result.getErrorDeviceIds().add(device.getId());
				} else if (Type.DEVICE_ZIGBEE.equals(device.getType())) {
					message.append("Fail to boot zigbee door sensor component.\n");
					result.getErrorDeviceIds().add(device.getId());
				} else {
					message.append("Not supported device type: " + device.getType().toString() + "\n");
					result.getErrorDeviceIds().add(device.getId());
				}
			}
		} else if (fakestate == 5 || fakestate == 6) {
			for (Device device : bootInfo.getDevices()) {
				if (Type.DEVICE_RFID_READER.equals(device.getType())) {
					result.getSuccessDeviceIds().add(device.getId());
				} else if (Type.DEVICE_ZIGBEE.equals(device.getType())) {
					message.append("Fail to boot zigbee door sensor component.\n");
					result.getErrorDeviceIds().add(device.getId());
				} else {
					message.append("Not supported device type: " + device.getType().toString() + "\n");
					result.getErrorDeviceIds().add(device.getId());
				}
			}
		}
		if (fakestate == 6) {
			fakestate = 0;
		}
		if (!StringUtils.isEmpty(message.toString())) {
			result.setMessage(message.toString());
		}
		if (result.getErrorDeviceIds().isEmpty()) {
			result.setSuccess(true);
		}
		return result;
	}
}
