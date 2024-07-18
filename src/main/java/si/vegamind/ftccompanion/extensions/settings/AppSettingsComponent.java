package si.vegamind.ftccompanion.extensions.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AppSettingsComponent {
	private final JPanel panel;
	private final JBTextField robotIpText = new JBTextField();

	public AppSettingsComponent() {
		panel = FormBuilder.createFormBuilder()
				.addLabeledComponent(new JBLabel("Control Hub IP:"), robotIpText, 1, false)
				.addComponentFillVertically(new JPanel(), 0)
				.getPanel();
	}

	public JPanel getPanel() {
		return panel;
	}

	public JComponent getPreferredFocusedComponent() {
		return robotIpText;
	}

	@NotNull
	public String getRobotIpText() {
		return robotIpText.getText();
	}

	public void setRobotIpText(@NotNull String newText) {
		robotIpText.setText(newText);
	}
}