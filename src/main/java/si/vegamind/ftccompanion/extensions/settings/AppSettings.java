package si.vegamind.ftccompanion.extensions.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

@State(
		name = "si.vegamind.ftccompanion.extensions.appservices.AppSettings",
		storages = {@Storage("FtcCompanion.xml")}
)
public class AppSettings implements PersistentStateComponent<AppSettings.State> {
	private State myState = new State();

	@Override
	public State getState() {
		return myState;
	}

	@Override
	public void loadState(@NotNull State state) {
		myState = state;
	}

	public static class State {
		@NonNls
		public String robotIp = "192.168.49.1";
	}
}
