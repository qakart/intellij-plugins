package com.intellij.lang.javascript.uml.actions;

import com.intellij.lang.javascript.JSBundle;
import com.intellij.lang.javascript.JavaScriptSupportLoader;
import com.intellij.lang.javascript.psi.JSElementFactory;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.JSFunction;
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList;
import com.intellij.lang.javascript.psi.ecmal4.JSClass;
import com.intellij.lang.javascript.psi.resolve.JSInheritanceUtil;
import com.intellij.lang.javascript.refactoring.changeSignature.JSChangeSignatureDialog;
import com.intellij.lang.javascript.refactoring.changeSignature.JSChangeSignatureProcessor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.BaseRefactoringProcessor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JSCreateMethodDialog extends JSChangeSignatureDialog {

  private CreateAction myCreateAction;
  private final JSClass myTargetClass;
  private JCheckBox myStaticCb;

  public JSCreateMethodDialog(final JSClass targetClass, final JSFunction method, boolean forceConstructor) {
    super(targetClass.getProject(), method, forceConstructor, method);
    myTargetClass = targetClass;

    setTitle(JSBundle.message(forceConstructor ? "create.constructor.dialog.title" : "create.method.dialog.title"));
  }

  static JSFunction createFakeMethod(JSClass clazz, String text, boolean inClass) {
    if (inClass) {
      JSFile file = JSElementFactory.createExpressionCodeFragment(clazz.getProject(),
                                                                  (clazz.isInterface() ? "interface" : "class") + " Dummy {" + text + "}",
                                                                  clazz, true, JavaScriptSupportLoader.ECMA_SCRIPT_L4, null,
                                                                  JSElementFactory.TopLevelCompletion.NO, null);
      JSClass aClass = PsiTreeUtil.findChildOfType(file, JSClass.class);
      return aClass.getFunctions()[0];
    }
    else {
      JSFile file = JSElementFactory.createExpressionCodeFragment(clazz.getProject(), text, clazz, true,
                                                                  JavaScriptSupportLoader.ECMA_SCRIPT_L4, null,
                                                                  JSElementFactory.TopLevelCompletion.NO, null);
      return PsiTreeUtil.findChildOfType(file, JSFunction.class);
    }
  }

  @Override
  protected String getDefaultValueColumnTitle() {
    return null;
  }

  @Override
  protected void createDefaultActions() {
    super.createDefaultActions();
    myCreateAction = new CreateAction();
  }

  @Override
  protected Action[] createActions() {
    return new Action[]{myCreateAction, getCancelAction()};
  }

  @Override
  protected boolean hasPreviewButton() {
    return false;
  }

  @Override
  protected void invokeRefactoring(BaseRefactoringProcessor processor) {
    close(OK_EXIT_CODE);
  }

  @Override
  protected JSChangeSignatureProcessor createRefactoringProcessor() {
    return null; // ignore
  }

  @Override
  public String getMethodName() {
    return super.getMethodName();
  }

  public String getReturnTypeText() {
    return myReturnTypeCodeFragment != null ? myReturnTypeCodeFragment.getText() : "";
  }

  @Override
  protected String getHelpId() {
    return null;
  }

  @Override
  protected String validateAndCommitData() {
    String error = super.validateAndCommitData();
    if (error != null) {
      return error;
    }

    JSFunction existingMethod = JSInheritanceUtil.findMethodInClass(createMethod(), myTargetClass, true);
    if (existingMethod != null &&
        existingMethod.getAttributeList().hasModifier(JSAttributeList.ModifierType.STATIC) ==
        (myStaticCb != null && myStaticCb.isSelected())) {
      boolean contains = existingMethod.getParent() == myTargetClass;
      String message = JSBundle.message("class.already.contains.method.warning", myTargetClass.getQualifiedName(),
                                        Integer.valueOf(contains ? 1 : 2), getMethodName());
      if (Messages.showYesNoDialog(myProject, message, getTitle(), Messages.getQuestionIcon()) != 0) {
        return EXIT_SILENTLY;
      }
    }
    return null;
  }

  @Override
  protected String calculateModifiers() {
    String result = super.calculateModifiers();
    if (myStaticCb != null && myStaticCb.isSelected()) {
      result += " static ";
    }
    return result;
  }

  public JSFunction createMethod() {
    String methodText = calculateSignature() + (myTargetClass.isInterface() ? ";" : "{}");
    return createFakeMethod(myTargetClass, methodText, true);
  }

  @Override
  protected JPanel createVisibilityPanel() {
    JPanel visibilityPanel = super.createVisibilityPanel();
    if (myMethod.isConstructor()) {
      return visibilityPanel;
    }

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(visibilityPanel, BorderLayout.NORTH);
    myStaticCb = new JCheckBox(JSBundle.message("declare.static"));
    myStaticCb.addChangeListener(new ChangeListener() {
      public void stateChanged(final ChangeEvent e) {
        updateSignature();
      }
    });
    panel.add(myStaticCb, BorderLayout.SOUTH);
    return panel;
  }

  private class CreateAction extends AbstractAction {
    public CreateAction() {
      putValue(NAME, JSBundle.message("create.button.text"));
      putValue(DEFAULT_ACTION, Boolean.TRUE);
    }

    public void actionPerformed(ActionEvent e) {
      doOKAction();
    }
  }
}
