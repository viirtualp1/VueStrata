package com.virtualp1.cli

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiManager
import com.intellij.openapi.command.WriteCommandAction

class GenerateDomain : AnAction() {
  var layers = arrayOf("entities", "features", "widgets", "page-widgets", "pages", "app")

  override fun actionPerformed(p0: AnActionEvent) {
    val project = p0.project ?: return
    val virtualFile = p0.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
    val psiDir = PsiManager.getInstance(project).findDirectory(virtualFile) ?: return

    val name = Messages.showInputDialog(
      project,
      "Enter new domain name",
      "@frontend/cli",
      AllIcons.Nodes.Folder,
    )

    if (name == null || name.isEmpty()) {
      Messages.showErrorDialog(
        project,
        "Domain name cannot be empty",
        "@frontend/cli"
      )

      return
    }

    val existingDir = psiDir.findSubdirectory(name)
    if (existingDir != null) {
      Messages.showErrorDialog(
        project,
        "Directory '$name' already exists",
        "@frontend/cli"
      )
      return
    }

    WriteCommandAction.runWriteCommandAction(project) {
      psiDir.createSubdirectory(name)
      val domainDir = psiDir.findSubdirectory(name)

      for (layer in layers) {
        val layerDir = domainDir?.findSubdirectory(layer)

        if (layerDir == null) {
          domainDir?.createSubdirectory(layer)
        } else {
          Messages.showErrorDialog(
            project,
            "Layer $layer already created",
            "My CLI Plugin"
          )
        }
      }
    }
  }
}