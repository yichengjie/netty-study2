package com.yicj.rpc.common.transport.body;

import java.util.List;
import com.yicj.rpc.common.exception.RemotingCommmonCustomException;
import com.yicj.rpc.common.rpc.MetricsReporter;

/**
 * 
 * @author BazingaLyn
 * @description 管理员发送给监控中心的信息
 * @time 2016年9月1日
 * @modifytime
 */
public class ProviderMetricsCustomBody implements CommonCustomBody {
	private List<MetricsReporter> metricsReporter;

	@Override
	public void checkFields() throws RemotingCommmonCustomException {
	}

	public List<MetricsReporter> getMetricsReporter() {
		return metricsReporter;
	}

	public void setMetricsReporter(List<MetricsReporter> metricsReporter) {
		this.metricsReporter = metricsReporter;
	}
}