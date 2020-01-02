package com.yicj.rpc.common.transport.body;

import java.util.List;

import com.yicj.rpc.common.exception.RemotingCommmonCustomException;
import com.yicj.rpc.common.metrics.ServiceMetrics;

public class MetricsCustomBody implements CommonCustomBody {
	
	private List<ServiceMetrics> serviceMetricses;

	@Override
	public void checkFields() throws RemotingCommmonCustomException {
	}

	public List<ServiceMetrics> getServiceMetricses() {
		return serviceMetricses;
	}

	public void setServiceMetricses(List<ServiceMetrics> serviceMetricses) {
		this.serviceMetricses = serviceMetricses;
	}

}