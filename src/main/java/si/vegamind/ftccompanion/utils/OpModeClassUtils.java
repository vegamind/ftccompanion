package si.vegamind.ftccompanion.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class OpModeClassUtils {
	private OpModeClassUtils() {
	}

	public static PsiDirectory isCreateActionValid(AnActionEvent e) {
		// Check project
		Project project = e.getProject();
		if(project == null) return null;

		// Check if directory was clicked
		PsiDirectory directory = e.getData(CommonDataKeys.PSI_ELEMENT) instanceof PsiDirectory ? (PsiDirectory) e.getData(CommonDataKeys.PSI_ELEMENT) : null;
		if(directory == null) return null;

		// Check if directory was a Java package
		PsiPackage javaPackage = JavaDirectoryService.getInstance().getPackage(directory);
		if(javaPackage == null) return null;

		return directory;
	}

	public static PsiClass createOpModeClass(PsiDirectory directory, String opModeName) {
		return JavaDirectoryService.getInstance().createClass(directory, opModeName);
	}

	public static Runnable fillOpModeClass(Project project, String opModeName, PsiClass opModeClass, String teleopAnnotation) {
		return () -> {
			PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();

			// Add imports
			opModeClass.addBefore(
					elementFactory.createImportStatementOnDemand("com.qualcomm.robotcore.eventloop.opmode"),
					opModeClass
			);

			// Extend LinearOpMode
			PsiJavaCodeReferenceElement superClassReference = elementFactory.createReferenceFromText("LinearOpMode", opModeClass);
			opModeClass.getExtendsList().add(superClassReference);

			// Annotate class
			PsiAnnotation teleOpAnnotation = elementFactory.createAnnotationFromText(
					"@" + teleopAnnotation + "(name=\"" + opModeName + "\")",
					opModeClass
			);
			opModeClass.addBefore(teleOpAnnotation, opModeClass.getFirstChild());

			// Create runOpMode method
			PsiMethod runOpMode = elementFactory.createMethod("runOpMode", PsiTypes.voidType());
			PsiAnnotation overrideAnnotation = elementFactory.createAnnotationFromText("@Override", runOpMode);
			runOpMode.addBefore(overrideAnnotation, runOpMode.getFirstChild());

			// Add wait and while loop
			PsiCodeBlock runOpModeBody = runOpMode.getBody();
			runOpModeBody.add(elementFactory.createStatementFromText("waitForStart();", runOpMode));
			runOpModeBody.add(
					elementFactory.createStatementFromText(
							"""
									if (opModeIsActive()) {
										// Pre-run
										while (opModeIsActive()) {
											// OpMode loop
										}
									}
									""",
							runOpMode
					)
			);

			// Add to class
			opModeClass.add(runOpMode);
		};
	}
}
