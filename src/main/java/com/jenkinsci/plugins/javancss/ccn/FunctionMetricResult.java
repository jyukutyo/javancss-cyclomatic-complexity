/*
 * (c)FURYU CORP. 2012. All rights reserved.
 *
 * $Id$
 */
package com.jenkinsci.plugins.javancss.ccn;

import java.io.Serializable;

public final class FunctionMetricResult implements Serializable, Comparable<FunctionMetricResult> {

	private final String fqcn;

	private final String methodName;

	private long ncss;

	private long ccn;

	private long javadocs;

	public FunctionMetricResult(String fqcn, String methodName) {
		this.fqcn = fqcn;
		this.methodName = methodName;
	}

	public String getFqcn() {
		return fqcn;
	}

	public long getNcss() {
		return ncss;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setNcss(long ncss) {
		this.ncss = ncss;
	}

	public long getCcn() {
		return ccn;
	}

	public void setCcn(long ccn) {
		this.ccn = ccn;
	}

	public long getJavadocs() {
		return javadocs;
	}

	public void setJavadocs(long javadocs) {
		this.javadocs = javadocs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FunctionMetricResult)) return false;

		FunctionMetricResult that = (FunctionMetricResult) o;

		if (ccn != that.ccn) return false;
		if (javadocs != that.javadocs) return false;
		if (ncss != that.ncss) return false;
		if (fqcn != null ? !fqcn.equals(that.fqcn) : that.fqcn != null) return false;
		if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = fqcn != null ? fqcn.hashCode() : 0;
		result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
		result = 31 * result + (int) (ncss ^ (ncss >>> 32));
		result = 31 * result + (int) (ccn ^ (ccn >>> 32));
		result = 31 * result + (int) (javadocs ^ (javadocs >>> 32));
		return result;
	}

	public int compareTo(FunctionMetricResult o) {
		if (this.ccn < o.getCcn()) return 1 ;
		if (this.ccn == o.getCcn()) return 0 ;

		return -1;
	}

	@Override
	public String toString() {
		return "FunctionMetricResult{" +
				"fqcn='" + fqcn + '\'' +
				", methodName='" + methodName + '\'' +
				", ncss=" + ncss +
				", ccn=" + ccn +
				", javadocs=" + javadocs +
				'}';
	}

}
