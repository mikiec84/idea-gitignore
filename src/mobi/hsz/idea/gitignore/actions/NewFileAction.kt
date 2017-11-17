/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 hsz Jakub Chrzanowski <jakub@hsz.mobi>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mobi.hsz.idea.gitignore.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.command.CreateFileCommandAction
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.ui.GeneratorDialog
import mobi.hsz.idea.gitignore.util.CommonDataKeys
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Creates new file or returns existing one.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @author Alexander Zolotov <alexander.zolotov@jetbrains.com>
 * @since 0.1
 */
open class NewFileAction(
        private val fileType: IgnoreFileType
) : AnAction(), DumbAware {
    /**
     * Creates new Gitignore file if it does not exist or uses an existing one and opens [GeneratorDialog].
     *
     * @param e action event
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(CommonDataKeys.PROJECT)
        val view = e.getRequiredData(LangDataKeys.IDE_VIEW)

        val fixedDirectory = fileType.ignoreLanguage.getFixedDirectory(project)
        val directory: PsiDirectory?

        directory = if (fixedDirectory != null) {
            PsiManager.getInstance(project).findDirectory(fixedDirectory)
        } else {
            view.orChooseDirectory
        }

        if (directory == null) {
            return
        }

        val dialog: GeneratorDialog
        val filename = fileType.ignoreLanguage.filename
        var file = directory.findFile(filename)
        val virtualFile = if (file == null) directory.virtualFile.findChild(filename) else file.virtualFile

        if (file == null && virtualFile == null) {
            val action = CreateFileCommandAction(project, directory, fileType)
            dialog = GeneratorDialog(project, action)
        } else {
            Notifications.Bus.notify(Notification(
                    fileType.languageName,
                    IgnoreBundle.message("action.newFile.exists", fileType.languageName),
                    IgnoreBundle.message("action.newFile.exists.in", virtualFile!!.path),
                    NotificationType.INFORMATION
            ), project)

            if (file == null) {
                file = Utils.getPsiFile(project, virtualFile)
            }

            dialog = GeneratorDialog(project, file)
        }

        dialog.show()
        file = dialog.file

        if (file != null) {
            Utils.openFile(project, file)
        }
    }

    /**
     * Updates visibility of the action presentation in various actions list.
     *
     * @param e action event
     */
    override fun update(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val view = e.getData(LangDataKeys.IDE_VIEW)

        val directory = view?.directories
        if (directory == null || directory.isEmpty() || project == null || !this.fileType.ignoreLanguage.isNewAllowed) {
            e.presentation.isVisible = false
        }
    }
}
