package org.zhangqi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhangqi.service.LoadBalanceService;

@RestController
public class WebController {

	@Autowired
	LoadBalanceService loadBalanceService;

	@RequestMapping("/gateway")
	public String getLeisureGateway() {
		Integer leisureGatewayId = loadBalanceService.getLeisureGatewayId();
		return leisureGatewayId == null ? null : loadBalanceService.getOneGatewayIdToConnectPath(leisureGatewayId);
	}
}
