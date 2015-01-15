package com.android.vantage.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vantage.Fragments.HomeFragment;
import com.android.vantage.ModelClasses.EmpRecord;
import com.android.vantage.ModelClasses.RoomBeaconObject;
import com.android.vantageLogManager.Logger;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

public class XebiaBeaconService extends Service {

	private static final Region ALL_ESTIMOTE_BEACONS = new Region("rid", null,
			null, null);

	private final String MY_BEACON_MAC_ADDRESS = "";
	public static boolean isStarted = false;

	private final int MAX_READINGS = 7;

	private int counter;

	private static HashMap<Beacon, ArrayList<Double>> readings = new HashMap<Beacon, ArrayList<Double>>();
	private static HashMap<Beacon, Double> meanReadings = new HashMap<Beacon, Double>();
	private BeaconManager beaconManager = new BeaconManager(this);
	String TAG = this.getClass().getSimpleName();

	Beacon currentBeacon;
	String currentRoomName;
	HashSet<Beacon> entryBeaconSet = new HashSet<Beacon>();
	private NotificationManager notificationManager;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Should be invoked in #onCreate.
		isStarted = true;
		Log.e(TAG, TAG + "created");

		beaconManager
				.setMonitoringListener(new BeaconManager.MonitoringListener() {

					@Override
					public void onExitedRegion(Region region) {
						Log.e(TAG, "Exited Region");
						if (currentBeacon != null) {
							Logger.info(TAG, "Updating User with Out Of Range "
									+ currentBeacon);
							// EmpRecord.updateRegion(currentBeacon,
							// "Out Of Range");
							EmpRecord.updateOutTimeAnalytics(currentBeacon,
									currentRoomName);
							currentBeacon = null;

						} else {
							Logger.info(TAG, "Updating User with Out Of Range");
							//EmpRecord.updateRegion(null, "Out Of Range");
						}

					}

					@Override
					public void onEnteredRegion(Region r, List<Beacon> beacons) {
						Log.e(TAG, "Entered Region");
						if (beacons != null && beacons.size() > 0) {
							Collections.sort(beacons, distanceSorter);
							for (Beacon b : beacons) {
								Log.e("Beacon:",
										"MAC:" + b.getMacAddress()
												+ " Distance: "
												+ Utils.computeAccuracy(b)
												+ " RSSI" + b.getRssi());
							}
							Beacon beacon = beacons.get(0);
							if (beacon.equals(currentBeacon)) {
								Logger.info(TAG, "Same Room Area");
								return;
							} else if (currentBeacon != null) {
								EmpRecord.updateOutTimeAnalytics(currentBeacon,
										currentRoomName);

							}

							currentBeacon = beacon;
							currentRoomName = RoomBeaconObject
									.getRoomNameFromBeaconMac(currentBeacon
											.getMacAddress());

							EmpRecord.updateRegion(currentBeacon,
									currentRoomName);
							EmpRecord.updateInTimeAnalytics(currentBeacon,
									currentRoomName);
						} else {
							// EmpRecord.updateRegion(currentBeacon,
							// "Out Of Range");
							Logger.error(TAG, "No Beacon Found");
						}

					}
				});

//		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
//			@Override
//			public void onBeaconsDiscovered(Region region,
//					final List<Beacon> rangedBeacons) {
//
//				if (rangedBeacons != null && rangedBeacons.size() > 0) {
//
//				} else {
//					EmpRecord.updateRegion(null, HomeFragment.OUT_OF_RANGE);
//				}
//
//				/*
//				 * if (counter == MAX_READINGS) { Log.i(TAG,
//				 * "Counter == MAX_READINGS"); counter = 0; Iterator<Beacon>
//				 * iterator = readings.keySet().iterator();
//				 * 
//				 * } else { counter++; for (Beacon rangedBeacon : rangedBeacons)
//				 * { ArrayList<Double> values; if
//				 * (readings.containsKey(rangedBeacon)) { values =
//				 * readings.get(rangedBeacon);
//				 * 
//				 * } else { values = new ArrayList<Double>(); }
//				 * values.add(Utils.computeAccuracy(rangedBeacon));
//				 * readings.put(rangedBeacon, values); Log.e("Ranged Beacon",
//				 * rangedBeacon.getMacAddress() + " D:" +
//				 * Utils.computeAccuracy(rangedBeacon) + " RSSI-" +
//				 * rangedBeacon.getRssi()); } }
//				 */
//
//			}
//		});
	}

	private Double findMeanValue(ArrayList<Double> values) {
		Double sum = 0D;
		int size = values.size();
		for (Double value : values) {
			sum += value;
		}
		return sum / size;
	}

	public Comparator<Beacon> distanceSorter = new Comparator<Beacon>() {

		@Override
		public int compare(Beacon lhs, Beacon rhs) {
			// TODO Auto-generated method stub

			Double d1 = Utils.computeAccuracy(lhs);
			Double d2 = Utils.computeAccuracy(rhs);
			return d1.compareTo(d2);
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		EmpRecord.updateOnlineStatus(beaconManager.isBluetoothEnabled());
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override
			public void onServiceReady() {
				try {
					// if(currentRoomName == null){
					// Logger.info(TAG,
					// "Started Beacon Monitoring Updating User with Out Of Range");
					// EmpRecord.updateRegion(null, HomeFragment.OUT_OF_RANGE);
					// }
					Log.i(TAG, "Started Beacon Monitoring");

					beaconManager.startMonitoring(ALL_ESTIMOTE_BEACONS);

				} catch (RemoteException e) {
					Log.e(TAG, "Cannot start ranging", e);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e(TAG, "Destroying Service");
		super.onDestroy();
		isStarted = false;
		try {
			Log.e(TAG, "Destroying Service");
			EmpRecord.updateOnlineStatus(false);
			if (currentBeacon != null && currentRoomName != null
					&& !currentRoomName.isEmpty()) {
				EmpRecord
						.updateOutTimeAnalytics(currentBeacon, currentRoomName);
			}
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
		} catch (RemoteException e) {
			Log.e(TAG, "Cannot stop but it does not matter now", e);
		}
		beaconManager.disconnect();
	}

}
