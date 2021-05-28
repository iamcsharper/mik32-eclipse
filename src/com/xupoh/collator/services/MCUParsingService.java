package com.xupoh.collator.services;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.xupoh.collator.MCUInfo;
import com.xupoh.collator.models.PinInfo;
import com.xupoh.collator.models.PinModeInfo;
import com.xupoh.collator.models.PinType;

public class MCUParsingService implements Callable<MCUInfo> {
	private MCUInfo result = null;

	private String path;

	public static final MCUParsingService instance = new MCUParsingService();

	private static InputStream getResourceStream(String path) {
		return MCUParsingService.class.getResourceAsStream(path);
	}

	public FutureTask<MCUInfo> parse(String path) {
		this.path = path;

		return new FutureTask<MCUInfo>(this);
	}

	@Override
	public MCUInfo call() throws Exception {
		if (this.result != null)
			this.result.flush();
		else
			this.result = new MCUInfo();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		Document doc = db.parse(getResourceStream(path));

		NodeList nodeList = doc.getElementsByTagName("pin");

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

				String key = PeripheryConfigurationService.instance
						.parsePeripheryByDesignation(info.designation).getCanonicalName();

				pinInfo.addMode(info);

				if (key != null) {
					this.result.registerPin(pinInfo, key);
				}
			}

			this.result.addPin(pinInfo);
		}

		return this.result;
	}

}
