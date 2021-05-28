package com.xupoh.collator.models;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.xupoh.collator.MCUInfo;
import com.xupoh.collator.services.MCUParsingService;

public class Mik32 {
	public MCUInfo info;

	public Mik32() {
		try {
			FutureTask<MCUInfo> task = MCUParsingService.instance
					.parse("/mik32_description.xml");
			task.run();
			this.info = task.get();
			task.cancel(true);

			if (this.info == null) {
				System.err.println("Null info. exit.");
				System.exit(0);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public class UnimplementedFeatureException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4315832766880210168L;
	}

	public void parsePort(int code) throws UnimplementedFeatureException {
		int port = code;

		for (int i = 0; i < 16; ++i) {
			int mode = port & 3;
			System.out.println("GPIO #" + i + " has a mode of " + mode);
			port >>= 2;
		}

		throw new UnimplementedFeatureException();
	}

	public int generatePort(int portNum) {
		int result = 0;

		for (PinInfo pin : this.info.getPins()) {
			if (pin.getPortNum() != portNum)
				continue;

			final PinModeInfo selectedMode = pin.getSelectedMode();

			if (selectedMode.type == PinType.GND || selectedMode.type == PinType.Power)
				continue;

			result |= selectedMode.id << (pin.getGpioId() * 2);
		}

		return result;
	}

	public interface PinModeUpdateListener {
		void call(int id, int mode, PinInfo info);
	}

	public void updatePinMode(int pinId, int modeId) {
		final PinInfo p = this.info.getPins().get(pinId);
		p.setSelectedModeId(modeId);

		for (PinModeUpdateListener listener : listeners) {
			listener.call(pinId, modeId, p);
		}
	}

	private HashSet<PinModeUpdateListener> listeners = new HashSet<>();

	public void addOnPinModeUpdateListener(PinModeUpdateListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
		else {
			System.err.println("[Mik32] Warning, duplicate addOnPinModeUpdate listener!");
		}
	}

	public void removeOnPinModeUpdateListener(PinModeUpdateListener listener) {
		this.listeners.remove(listener);
	}
}
