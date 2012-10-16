/*
 * (c)FURYU CORP. 2012. All rights reserved.
 *
 * $Id$
 */
package com.jenkinsci.plugins.javancss.ccn;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hudson.util.IOException2;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class JavaNcssRawReportXmlParser {

	public static List<FunctionMetricResult> parse(File inFile, int threshold) throws IOException {

		List<FunctionMetricResult> results = new ArrayList<FunctionMetricResult>();
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			fis = new FileInputStream(inFile);
			bis = new BufferedInputStream(fis);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(bis, null);

			// check that the first tag is <javancss>
			String tag = "javancss";
			while (true) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					parser.next();
					continue;
				}
				if (parser.getName().equals(tag)) {
					break;
				}
				throw new IOException("Expecting tag " + tag);
			}

			// skip until we get to the <packages> tag
			while (parser.getDepth() > 0 && (parser.getEventType() != XmlPullParser.START_TAG || !"functions".equals(parser.getName()))) {
				parser.next();
			}
			while (parser.getDepth() > 0 && (parser.getEventType() != XmlPullParser.START_TAG || !"function".equals(parser.getName()))) {
				parser.next();
			}
			while (parser.getDepth() >= 2 && parser.getEventType() == XmlPullParser.START_TAG && "function".equals(parser.getName())) {
				Map<String, String> data = new HashMap<String, String>();
				String lastTag = null;
				String lastText = null;
				int depth = parser.getDepth();
				while (parser.getDepth() >= depth) {
					parser.next();
					switch (parser.getEventType()) {
						case XmlPullParser.START_TAG:
							lastTag = parser.getName();
							break;
						case XmlPullParser.TEXT:
							lastText = parser.getText();
							break;
						case XmlPullParser.END_TAG:
							if (parser.getDepth() == 4 && lastTag != null && lastText != null) {
								data.put(lastTag, lastText);
							}
							lastTag = null;
							lastText = null;
							break;
					}
				}
				if (data.containsKey("name") && threshold < Long.parseLong(data.get("ccn"))) {
					String name = data.get("name");
					int index = name.lastIndexOf('.');
					FunctionMetricResult functionMetricResult = new FunctionMetricResult(name.substring(0, index), name.substring(index + 1));
					functionMetricResult.setCcn(Long.valueOf(data.get("ccn")));
					functionMetricResult.setNcss(Long.valueOf(data.get("ncss")));
					functionMetricResult.setJavadocs(Long.valueOf(data.get("javadocs")));
					results.add(functionMetricResult);
				}
				parser.next();
			}

		} catch (XmlPullParserException e) {
			throw new IOException2(e);
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (fis != null) {
				fis.close();
			}
		}

		return results;
	}
}
