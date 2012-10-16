/*
 * (c)FURYU CORP. 2012. All rights reserved.
 *
 * $Id$
 */
package com.jenkinsci.plugins.javancss.ccn;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.xmlpull.v1.XmlPullParserException;

public class JavaNcssCyclomaticComplexityPublisher extends Notifier {

	private final Integer unstable;
	private final Integer failure;

	@DataBoundConstructor
	public JavaNcssCyclomaticComplexityPublisher(String unstable, String failure) {
		this.unstable = safeParse(unstable);
		this.failure = safeParse(failure);
	}

	public Integer getUnstable() {
		return unstable;
	}

	public Integer getFailure() {
		return failure;
	}

	private Integer safeParse(String value) {
		if (value == null) {
			return null;
		}
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

		File javaNcssXmlFile = new File(build.getWorkspace().getRemote().toString() + "/target/javancss-raw-report.xml");

		int threshold = -1;
		if (unstable != null) {
			threshold = unstable.intValue();
		} else if (failure != null) {
			threshold = failure.intValue();
		}

		List<FunctionMetricResult> functionMetricResults;
		try {
			functionMetricResults = JavaNcssRawReportXmlParser.parse(javaNcssXmlFile, threshold);
		} catch (XmlPullParserException e) {
			throw new IOException(e);
		}
		Collections.sort(functionMetricResults);

		build.addAction(new JavaNcssCyclomaticComplexityReport(functionMetricResults));

		if (!functionMetricResults.isEmpty()) {
			FunctionMetricResult result = functionMetricResults.get(0);
			long maxCcn = result.getCcn();

			if (failure != null && failure <= maxCcn) {
				build.setResult(Result.FAILURE);
			} else if (unstable != null && unstable <= maxCcn) {
				build.setResult(Result.UNSTABLE);
			}
		}

		return true;
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	@Override
	public BuildStepDescriptor<Publisher> getDescriptor() {
		return DESCRIPTOR;
	}

	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		/** Do not instantiate DescriptorImpl. */
		private DescriptorImpl() {
			super(JavaNcssCyclomaticComplexityPublisher.class);
		}

		/** {@inheritDoc} */
		public String getDisplayName() {
			return "循環的複雑度(McCabe)";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
			save();
			return true;
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}
	}

}
