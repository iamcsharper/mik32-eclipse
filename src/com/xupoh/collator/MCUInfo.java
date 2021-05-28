package com.xupoh.collator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xupoh.collator.models.Periphery;
import com.xupoh.collator.models.PeripheryType;
import com.xupoh.collator.models.PinInfo;
import com.xupoh.collator.services.PeripheryConfigurationService;

public class MCUInfo {
	private List<PinInfo> pins = Collections.synchronizedList(new ArrayList<PinInfo>());
	private Map<String, List<PinInfo>> modesToPins = Collections
			.synchronizedMap(new HashMap<String, List<PinInfo>>());

	private Map<String, Periphery> peripherals = Collections
			.synchronizedMap(new HashMap<String, Periphery>());
	
	private Map<Periphery, List<PinInfo>> peripheralsToPins = Collections
			.synchronizedMap(new HashMap<Periphery, List<PinInfo>>()); 

	public List<PinInfo> getPins() {
		return pins;
	}

	public Map<String, List<PinInfo>> getModesToPins() {
		return this.modesToPins;
	}

	public Set<String> getPeripheralsNames() {
		return this.peripherals.keySet();
	}
	
	public Map<String, Periphery> getPeripherals() {
		return this.peripherals;
	}
	
	public Map<Periphery, List<PinInfo>> getPeripheralsToPins() {
		return peripheralsToPins;
	}

	public Set<String> getModeNames() {
		return this.modesToPins.keySet();
	}

	public void addPin(PinInfo info) {
		this.pins.add(info);
	}

	public void registerPin(PinInfo info, String pinWithPeriphery) {
		final Periphery p = PeripheryConfigurationService.instance
				.parsePeripheryByDesignation(pinWithPeriphery);
		
		if (!p.getType().equals(PeripheryType.None)) {
			if (!peripheralsToPins.containsKey(p)) {
				peripheralsToPins.put(p, new ArrayList<PinInfo>());
			}
			
			peripheralsToPins.get(p).add(info);	
		}
		
		peripherals.put(pinWithPeriphery, p);
		
		if (!modesToPins.containsKey(pinWithPeriphery)) {
			modesToPins.put(pinWithPeriphery, new ArrayList<PinInfo>());
		}
		
		modesToPins.get(pinWithPeriphery).add(info);
	}

	public void flush() {
		this.pins.clear();
		this.modesToPins.clear();
	}
}
