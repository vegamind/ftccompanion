package si.vegamind.ftccompanion.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RcInfo {
	private boolean appUpdateRequiresReboot;
	private List<WiFiChannel> availableChannels;
	private WiFiChannel currentChannel;
	private String deviceName;
	private String ftcUserAgentCategory;
	private String includedFirmwareFileVersion;
	private boolean isREVControlHub;
	private String networkName;
	private String passphrase;
	private String rcVersion;
	private boolean serverIsAlive;
	private String serverUrl;
	private boolean supports5GhzAp;
	private boolean supportsOtaUpdate;
	private String timeServerStarted;
	private long timeServerStartedMillis;

	public RcInfo() {
	}
}
