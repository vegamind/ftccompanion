package si.vegamind.ftccompanion.actions.filetree;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import si.vegamind.ftccompanion.utils.OpModeClassUtils;

public class NewTeleOp extends AnAction {
	@Override
	public void actionPerformed(AnActionEvent e) {
		PsiDirectory directory = OpModeClassUtils.isCreateActionValid(e);
		if(directory == null) return;

		Project project = e.getProject();

		String opModeName = Messages.showInputDialog(
				e.getProject(),
				"New TeleOp",
				"Choose new TeleOp name",
				null
		);
		if(opModeName == null) return;

		WriteCommandAction.runWriteCommandAction(
				project,
				OpModeClassUtils.fillOpModeClass(
						project,
						opModeName,
						OpModeClassUtils.createOpModeClass(directory, opModeName),
						"TeleOp"
				)
		);
	}
}