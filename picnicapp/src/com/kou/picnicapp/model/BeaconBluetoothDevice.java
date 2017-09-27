package com.kou.picnicapp.model;

import android.bluetooth.BluetoothDevice;

public class BeaconBluetoothDevice {

	private BluetoothDevice bluetoothDevice = null;
	private String macAddress;
	private String UUID;
	private int major = 0;
	private int minor = 0;
	private int decryptedMajor = 0;
	private int decryptedMinor = 0;
	private int measuredPower = 0;
	private byte[] scanRecord;
	private int rssi = 0;

	public BeaconBluetoothDevice(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
		this.bluetoothDevice = bluetoothDevice;
		this.scanRecord = scanRecord;
		this.macAddress = bluetoothDevice.getAddress();
		this.rssi = rssi;
		if (scanRecord == null)
			return;
		if ((scanRecord[5] == 76) && (scanRecord[6] == 0) && (scanRecord[7] == 2) && (scanRecord[8] == 21)) {
			this.UUID = String.format(
					"%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
					new Object[] { Byte.valueOf(scanRecord[9]), Byte.valueOf(scanRecord[10]), Byte.valueOf(scanRecord[11]), Byte.valueOf(scanRecord[12]), Byte.valueOf(scanRecord[13]),
							Byte.valueOf(scanRecord[14]), Byte.valueOf(scanRecord[15]), Byte.valueOf(scanRecord[16]), Byte.valueOf(scanRecord[17]), Byte.valueOf(scanRecord[18]),
							Byte.valueOf(scanRecord[19]), Byte.valueOf(scanRecord[20]), Byte.valueOf(scanRecord[21]), Byte.valueOf(scanRecord[22]), Byte.valueOf(scanRecord[23]),
							Byte.valueOf(scanRecord[24]) });

			this.major = (scanRecord[25] << 8 & 0xFF00 | scanRecord[26] & 0xFF);
			this.minor = (scanRecord[27] << 8 & 0xFF00 | scanRecord[28] & 0xFF);

			this.measuredPower = scanRecord[29];

		} else if ((scanRecord[6] == 76) && (scanRecord[7] == 0) && (scanRecord[8] == 2) && (scanRecord[9] == 21)) {
			this.UUID = String.format(
					"%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
					new Object[] { Byte.valueOf(scanRecord[10]), Byte.valueOf(scanRecord[11]), Byte.valueOf(scanRecord[12]), Byte.valueOf(scanRecord[13]), Byte.valueOf(scanRecord[14]),
							Byte.valueOf(scanRecord[15]), Byte.valueOf(scanRecord[16]), Byte.valueOf(scanRecord[17]), Byte.valueOf(scanRecord[18]), Byte.valueOf(scanRecord[19]),
							Byte.valueOf(scanRecord[20]), Byte.valueOf(scanRecord[21]), Byte.valueOf(scanRecord[22]), Byte.valueOf(scanRecord[23]), Byte.valueOf(scanRecord[24]),
							Byte.valueOf(scanRecord[25]) });

			this.major = (scanRecord[26] << 8 & 0xFF00 | scanRecord[27] & 0xFF);
			this.minor = (scanRecord[28] << 8 & 0xFF00 | scanRecord[29] & 0xFF);

			this.measuredPower = scanRecord[30];
		}

	}

	public BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
		this.bluetoothDevice = bluetoothDevice;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getDecryptedMajor() {
		return decryptedMajor;
	}

	public void setDecryptedMajor(int decryptedMajor) {
		this.decryptedMajor = decryptedMajor;
	}

	public int getDecryptedMinor() {
		return decryptedMinor;
	}

	public void setDecryptedMinor(int decryptedMinor) {
		this.decryptedMinor = decryptedMinor;
	}

	public int getMeasuredPower() {
		return measuredPower;
	}

	public void setMeasuredPower(int measuredPower) {
		this.measuredPower = measuredPower;
	}

	public byte[] getScanRecord() {
		return scanRecord;
	}

	public void setScanRecord(byte[] scanRecord) {
		this.scanRecord = scanRecord;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

}
