package com.virtualp1.cli

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiManager
import kotlin.text.isEmpty
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.toDocument(): Document? =
  FileDocumentManager.getInstance().getDocument(this)

class GenerateComponent : AnAction() {
  override fun actionPerformed(p0: AnActionEvent) {
    val project = p0.project ?: return
    val virtualFile = p0.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
    val psiDir = PsiManager.getInstance(project).findDirectory(virtualFile) ?: return

    val name = Messages.showInputDialog(
      project,
      "Enter component name",
      "VueStrata",
      AllIcons.Nodes.Folder,
    )

    if (name == null || name.isEmpty()) {
      Messages.showErrorDialog(
        project,
        "Component name cannot be empty",
        "VueStrata"
      )

      return
    }

    val existingDir = psiDir.findSubdirectory(name)
    if (existingDir != null) {
      Messages.showErrorDialog(
        project,
        "Component '$name' already exists",
        "VueStrata"
      )
      return
    }

    WriteCommandAction.runWriteCommandAction(project) {
      val subDir = psiDir.createSubdirectory(name)

      val indexFile = subDir.createFile("index.ts")
      val vueFile = subDir.createFile("$name.vue")
      subDir.createFile("$name.scss")
      subDir.createFile("locales.json")

      val vueContent = """
        <template></template>
        
        <script setup lang="ts"></script>
        
        <style lang="scss" src="./$name.scss"></style>
      """.trimIndent()
      val vueDocument = vueFile.virtualFile.toDocument()
      vueDocument?.setText(vueContent)

      val indexContent = """
        export { default as $name } from './$name.vue'
      """.trimIndent()
      val indexDocument = indexFile.virtualFile.toDocument()
      indexDocument?.setText(indexContent)
    }
  }
}
