/*
 * (c)FURYU CORP. 2012. All rights reserved.
 *
 * $Id$
 */
package com.jenkinsci.plugins.javancss.ccn;

import java.io.Serializable;
import java.util.List;

import hudson.model.Action;

public class JavaNcssCyclomaticComplexityReport implements Action, Serializable {

	private final List<FunctionMetricResult> results;

	public JavaNcssCyclomaticComplexityReport(List<FunctionMetricResult> results) {
		this.results = results;
	}

	public List<FunctionMetricResult> getResults() {
		return results;
	}

	@Override
	public String getIconFileName() {
		return "graph.gif";
	}

	@Override
	public String getDisplayName() {
		return "循環的複雑度(McCabe)";
	}

	@Override
	public String getUrlName() {
		return "javancss-ccn";
	}

}
