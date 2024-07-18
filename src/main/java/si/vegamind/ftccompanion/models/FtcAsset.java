package si.vegamind.ftccompanion.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FtcAsset {
	private String name;
	private String escapedName;
	private long dateModifiedMillis;

	public FtcAsset() {
	}
}
