package com.virtualp1.vuestrata

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiManager
import com.intellij.openapi.command.WriteCommandAction

class GenerateSlice : AnAction() {
  var segments = arrayOf("ui", "api", "model", "lib", "config")

  override fun actionPerformed(p0: AnActionEvent) {
    val project = p0.project ?: return
    val virtualFile = p0.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
    val dir = PsiManager.getInstance(project).findDirectory(virtualFile) ?: return

    val name = Messages.showInputDialog(
      project,
      "Enter new slice name",
      "VueStrata",
      AllIcons.Nodes.Folder,
    )

    if (name == null || name.isEmpty()) {
      Messages.showErrorDialog(
        project,
        "Slice name cannot be empty",
        "VueStrata"
      )

      return
    }

    val existingDir = dir.findSubdirectory(name)
    if (existingDir != null) {
      Messages.showErrorDialog(
        project,
        "Slice '$name' already exists",
        "VueStrata"
      )
      return
    }

    WriteCommandAction.runWriteCommandAction(project) {
      dir.createSubdirectory(name)
      val sliceDir = dir.findSubdirectory(name)

      for (segment in segments) {
        val segmentDir = sliceDir?.findSubdirectory(segment)

        if (segmentDir == null) {
          sliceDir?.createSubdirectory(segment)
        } else {
          Messages.showErrorDialog(
            project,
            "Segment $segment already created",
            "VueStrata"
          )
        }
      }
    }
  }
}
