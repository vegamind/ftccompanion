package si.vegamind.ftccompanion.extensions.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class AppSettingsConfigurable implements Configurable {
	private final AppSettings settings;
	private AppSettingsComponent mySettingsComponent;

	public AppSettingsConfigurable(Project project) {
		this.settings = project.getService(AppSettings.class);
	}

	@Nls(capitalization = Nls.Capitalization.Title)
	@Override
	public String getDisplayName() {
		return "FTC Companion";
	}

	@Override
	public JComponent getPreferredFocusedComponent() {
		return mySettingsComponent.getPreferredFocusedComponent();
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		mySettingsComponent = new AppSettingsComponent();
		return mySettingsComponent.getPanel();
	}

	@Override
	public boolean isModified() {
		AppSettings.State state = Objects.requireNonNull(settings.getState());
		return !mySettingsComponent.getRobotIpText().equals(state.robotIp);
	}

	@Override
	public void apply() {
		AppSettings.State state = Objects.requireNonNull(settings.getState());
		state.robotIp = mySettingsComponent.getRobotIpText();
	}

	@Override
	public void reset() {
		AppSettings.State state = Objects.requireNonNull(settings.getState());
		mySettingsComponent.setRobotIpText(state.robotIp);
	}

	@Override
	public void disposeUIResources() {
		mySettingsComponent = null;
	}
}
