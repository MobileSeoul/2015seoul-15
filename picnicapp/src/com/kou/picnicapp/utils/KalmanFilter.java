package com.kou.picnicapp.utils;

public class KalmanFilter {

	// Extended Kalmna Filter가 아님. 그냥 칼만필터.

	private double Q = 0.000091;
	private double R = 0.0076;

	private double X = 0, P = 1, K;

	public KalmanFilter(float initValue) {
		X = initValue;
	}

	private void measurementUpdate() {
		K = (P + Q) / (P + Q + R);
		P = R * (P + Q) / (R + P + Q);
	}

	public double update(double measurement) {
		measurementUpdate();
		X = X + (measurement - X) * K;

		return X;
	}

	public void init(float initValue) {
		X = initValue;
	}

	public double getData() {
		return X;
	}
}
