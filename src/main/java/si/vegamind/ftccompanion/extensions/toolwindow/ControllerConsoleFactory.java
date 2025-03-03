package si.vegamind.ftccompanion.extensions.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import si.vegamind.ftccompanion.extensions.settings.AppSettings;
import si.vegamind.ftccompanion.models.FtcAsset;
import si.vegamind.ftccompanion.models.RcInfo;
import si.vegamind.ftccompanion.models.WiFiChannel;
import si.vegamind.ftccompanion.services.FtcAssetService;
import si.vegamind.ftccompanion.services.RcInfoService;
import si.vegamind.ftccompanion.services.RobotLogsService;
import si.vegamind.ftccompanion.services.WiFiService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Objects;

public class ControllerConsoleFactory implements ToolWindowFactory {
	private boolean connected = false;
	private AppSettings.State settings;

	@Override
	public void createToolWindowContent(Project project, ToolWindow toolWindow) {
		settings = Objects.requireNonNull(project.getService(AppSettings.class)).getState();

		// Create content for tool window
		ContentManager contentManager = toolWindow.getContentManager();
		ContentFactory contentFactory = contentManager.getFactory();

		AnActionButton connectButton = new AnActionButton("Connect to Control Hub", null, AllIcons.Actions.Execute) {
			@Override
			public void actionPerformed(@NotNull AnActionEvent e) {
				if(!connected) {
					RcInfo rcInfo = RcInfoService.getRcInfo(settings.robotIp);

					if(rcInfo == null) {
						Messages.showErrorDialog("Couldn't connect to control Hub", "Connection Error");
						return;
					}

					contentManager.addContent(contentFactory.createContent(createMainLayout(rcInfo), "Manage", false));
					contentManager.addContent(contentFactory.createContent(createAssetsTable("SOUNDS"), "Sounds", false));

					e.getPresentation().setIcon(AllIcons.Actions.Suspend);
					connected = true;
				} else {
					contentManager.removeAllContents(true);

					e.getPresentation().setIcon(AllIcons.Actions.Execute);
					connected = false;
				}
			}

			@Override
			public @NotNull ActionUpdateThread getActionUpdateThread() {
				return ActionUpdateThread.EDT;
			}
		};

		toolWindow.setTitleActions(List.of(connectButton));
	}

	private JPanel createMainLayout(RcInfo rcInfo) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Info panel
		JPanel infoPanel = createSettingsPanel("Robot Controller Connection Info");
		infoPanel.add(new JBLabel("Robot Controller version:"));
		infoPanel.add(new JBLabel(rcInfo.getRcVersion()));
		infoPanel.add(new JBLabel("The connected robot controller resides on the wireless network named:"));
		infoPanel.add(new JBLabel(rcInfo.getNetworkName()));
		infoPanel.add(new JBLabel("The passphrase for this network is:"));
		infoPanel.add(new JBLabel(rcInfo.getPassphrase()));

		// Wi-Fi panel
		ComboBox<WiFiChannel> comboChannels = new ComboBox<>();
		rcInfo.getAvailableChannels().forEach(comboChannels::addItem);

		JBTextField deviceName = new JBTextField();
		deviceName.setText(rcInfo.getDeviceName());

		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(e -> WiFiService.applySettings(
				settings.robotIp,
				deviceName.getText(),
				(WiFiChannel) comboChannels.getSelectedItem(),
				rcInfo.getPassphrase()
		));

		JPanel wifiPanel = createSettingsPanel("Wi-Fi Settings");
		wifiPanel.add(
				FormBuilder.createFormBuilder()
						.addLabeledComponent(new JBLabel("Name"), deviceName, 1, false)
						.addLabeledComponent(new JBLabel("Wi-Fi Channel"), comboChannels, 1, false)
						.addComponent(submitButton)
						.addComponentFillVertically(new JPanel(), 0)
						.getPanel()
		);

		// Logs panel
		JPanel logsPanel = createSettingsPanel("Download Robot Controller Logs");
		logsPanel.add(new JLabel("Examination of activity logs from the robot controller can sometimes help diagnose problems and bugs."));
		JButton downloadLogs = new JButton("Download Logs");
		downloadLogs.addActionListener(e -> selectAndDownloadLogs());
		logsPanel.add(downloadLogs);

		panel.add(infoPanel);
		panel.add(wifiPanel);
		panel.add(logsPanel);

		return panel;
	}

	private JPanel createSettingsPanel(String title) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setBorder(new TitledBorder(title));
		panel.add(Box.createHorizontalGlue());

		return panel;
	}

	private JPanel createAssetsTable(String fmname) {
		AssetsTableModel model = new AssetsTableModel(FtcAssetService.listAssets(settings.robotIp, fmname));
		JBTable assetsTable = new JBTable(model);

		// Callback to refresh the table
		Runnable refreshTable = () -> {
			model.setAssets(FtcAssetService.listAssets(settings.robotIp, fmname));
			model.fireTableDataChanged();
		};

		ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(assetsTable)
				.setAddAction(button -> selectAndUploadAsset(fmname, refreshTable))
				.setRemoveAction(button -> {
					if(Messages.showYesNoDialog(
							"Are you sure you want to delete selected asset?",
							"Delete Asset",
							Messages.getQuestionIcon()
					) != 0) return;

					FtcAssetService.deleteAsset(
							settings.robotIp,
							fmname,
							model.getAssets()[assetsTable.getSelectedRow()]
					);
					refreshTable.run();
				})
				.setEditAction(button -> {
					FtcAsset selectedAsset = model.getAssets()[assetsTable.getSelectedRow()];

					String assetName = Messages.showInputDialog(
							"Rename Selected File",
							"Rename asset",
							null,
							selectedAsset.getName(),
							null
					);
					if(assetName == null) return;

					FtcAssetService.renameAsset(settings.robotIp, fmname, selectedAsset, assetName);
					refreshTable.run();
				})
				.setEditActionName("Rename")
				.addExtraAction(new AnAction("Download", null, AllIcons.Actions.Download) {
					@Override
					public void actionPerformed(@NotNull AnActionEvent e) {
						// TODO: Disable button when no row selected rather than performing a check, just like remove and rename buttons (help needed)
						if(assetsTable.getSelectedRow() != -1) {
							selectAndDownloadAsset(fmname, model.getAssets()[assetsTable.getSelectedRow()]);
						}
					}

					@Override
					public void update(@NotNull AnActionEvent e) {
						Presentation presentation = e.getPresentation();
						presentation.setEnabled(assetsTable.getSelectedRow() != -1);
					}

					@Override
					public @NotNull ActionUpdateThread getActionUpdateThread() {
						return ActionUpdateThread.EDT;
					}
				});

		return toolbarDecorator.createPanel();
	}

	/**
	 * Prompt user to select file and upload it
	 *
	 * @param fmname   Asset type
	 * @param callback Callback (usually to refresh table) that is called after file is uploaded
	 */
	private void selectAndUploadAsset(String fmname, Runnable callback) {
		SwingUtilities.invokeLater(() -> {
			JFileChooser fileChooser = new JFileChooser();

			if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				FtcAssetService.uploadAsset(settings.robotIp, fmname, fileChooser.getSelectedFile());
			}

			callback.run();
		});
	}

	private void selectAndDownloadAsset(String fmname, FtcAsset asset) {
		SwingUtilities.invokeLater(() -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(asset.getName()));

			if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				FtcAssetService.downloadAsset(settings.robotIp, fmname, asset, fileChooser.getSelectedFile());
			}
		});
	}

	private void selectAndDownloadLogs() {
		SwingUtilities.invokeLater(() -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				RobotLogsService.downloadLogs(settings.robotIp, fileChooser.getSelectedFile());
			}
		});
	}
}