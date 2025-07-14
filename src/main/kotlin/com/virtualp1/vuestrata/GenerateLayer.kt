package com.virtualp1.vuestrata

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiManager
import com.intellij.openapi.command.WriteCommandAction

class GenerateLayer : AnAction() {
  var layers = arrayOf("app", "pages", "widgets", "features", "entities", "shared")

  override fun actionPerformed(p0: AnActionEvent) {
    val project = p0.project ?: return
    val virtualFile = p0.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
    val dir = PsiManager.getInstance(project).findDirectory(virtualFile) ?: return

    WriteCommandAction.runWriteCommandAction(project) {
      for (layer in layers) {
        val layerDir = dir.findSubdirectory(layer)

        if (layerDir == null) {
          dir.createSubdirectory(layer)
        } else {
          Messages.showErrorDialog(
            project,
            "Layer $layer already created",
            "VueStrata"
          )
        }
      }
    }
  }
}