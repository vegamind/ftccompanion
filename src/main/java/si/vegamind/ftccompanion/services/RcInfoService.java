package si.vegamind.ftccompanion.services;

import si.vegamind.ftccompanion.models.RcInfo;
import si.vegamind.ftccompanion.utils.WebUtils;

public class RcInfoService {
	private RcInfoService() {
	}

	public static RcInfo getRcInfo(String robotIp) {
		return WebUtils.get("http://" + robotIp + ":8080/js/rcInfo.json", RcInfo.class);
	}
}
