package com.xupoh.collator.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.xupoh.collator.parts.common.GlobalConstants;
import com.xupoh.collator.parts.common.HousingInfo;
import com.xupoh.collator.parts.common.PinInfo;
import com.xupoh.collator.parts.common.PinModeInfo;
import com.xupoh.collator.parts.common.PinType;

public class DocumentLoader {
	public static InputStream getResourceStream(String path) {
		return DocumentLoader.class.getResourceAsStream(path);
	}

	public static HousingInfo parseHousing(String path)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		db = dbf.newDocumentBuilder();

		Document doc = db.parse(getResourceStream(path));

		NodeList nodeList = doc.getElementsByTagName("pin");

		List<PinInfo> array = new ArrayList<>();

		HashMap<String, List<Integer>> interfacesToPins = new HashMap<>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node pinNode = nodeList.item(i);
			NodeList modes = pinNode.getChildNodes();
			NamedNodeMap pinAttrs = pinNode.getAttributes();

			PinInfo pinInfo = new PinInfo(
					Integer.parseInt(pinAttrs.getNamedItem("id").getNodeValue()),
					pinAttrs.getNamedItem("analog").getNodeValue());

			int n = 0;
			for (int j = 0; j < modes.getLength(); j++) {
				Node modeNode = modes.item(j);
				NamedNodeMap modeAttrs = modeNode.getAttributes();

				if (modeAttrs == null)
					continue;

				int index = n;
				n++;

				for (int k = 0; k < modeAttrs.getLength(); k++) {
					if (modeAttrs.item(k).toString().contains("id"))
						index = Integer
								.parseInt(modeAttrs.item(k).toString().split("=")[1]
										.replaceAll("\"", ""));
				}

				PinModeInfo info = new PinModeInfo(index,
						modeAttrs.getNamedItem("sign").getNodeValue(),
						PinType.valueOf(modeAttrs.getNamedItem("type").getNodeValue()),
						modeNode.getTextContent());

				// I2C - contains I2C{NUM}
				// UART - contains UART{NUM}
				// SPI - contains SPI{NUM}
				// Timer32 - contains Timer32_{NUM}
				// Timer16 - contains Timer16_{NUM}
				// SPIFI - contains SPIFI
				// JTAG - TDO, TRSTn, TMS, TCK, TDI

				String key = null;

				if (info.designation.contains("I2C") || info.designation.contains("UART")
						|| info.designation.contains("SPI")
						|| info.designation.contains("Timer32")
						|| info.designation.contains("Timer16")) {
					key = info.designation.split("_")[0];
				} else if (GlobalConstants.JTAG_NAMES.contains(info.designation)) {
					key = "JTAG";
				}

				if (key != null) {
					interfacesToPins.putIfAbsent(key, new ArrayList<Integer>());
					interfacesToPins.get(key).add(pinInfo.getId());
				}

				pinInfo.addMode(info);
			}

			array.add(pinInfo);
		}

		HousingInfo result = new HousingInfo();
		result.pinsInfo = array;
		result.interfacesToPins = interfacesToPins;

		return result;
	}
}
