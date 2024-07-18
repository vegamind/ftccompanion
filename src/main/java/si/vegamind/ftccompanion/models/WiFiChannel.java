package si.vegamind.ftccompanion.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WiFiChannel {
	private String name;
	private String displayName;
	private String band;
	private boolean overlapsWithOtherChannels;

	public WiFiChannel() {
	}

	@Override
	public String toString() {
		return displayName;
	}
}
