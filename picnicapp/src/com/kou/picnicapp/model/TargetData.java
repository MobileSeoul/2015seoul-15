package com.kou.picnicapp.model;

public class TargetData {

	public static enum CHECK_STATE {
		UNKNOWN, FOUND
	};

	private int sequence_id; // not used
	private String number;
	private String name;
	private long lastCheckedTime = 0;
	private CHECK_STATE checkState = CHECK_STATE.UNKNOWN;
	private String phoneNumber;
	private String uuid;
	private int major;
	private int minor;
	private String rssi;

	public int getSequence_id() {
		return sequence_id;
	}

	public void setSequence_id(int sequence_id) {
		this.sequence_id = sequence_id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getLastCheckedTime() {
		return lastCheckedTime;
	}

	public void setLastCheckedTime(long lastCheckedTime) {
		this.lastCheckedTime = lastCheckedTime;
	}

	public CHECK_STATE getCheckState() {
		return checkState;
	}

	public void setCheckState(CHECK_STATE checkState) {
		this.checkState = checkState;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getRssi() {
		return rssi;
	}

	public void setRssi(String rssi) {
		this.rssi = rssi;
	}

}
