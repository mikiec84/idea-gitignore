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

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.ui.GeneratorDialog
import mobi.hsz.idea.gitignore.util.CommonDataKeys

/**
 * Action that initiates adding new template to the selected .gitignore file.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5.3
 */
/** Builds a new instance of [AddTemplateAction].  */
class AddTemplateAction : AnAction(
        IgnoreBundle.message("action.addTemplate"),
        IgnoreBundle.message("action.addTemplate.description"),
        null
) {
    /**
     * Handles an action of adding new template.
     * Ignores action if selected file is not a [IgnoreFile] instance, otherwise shows [GeneratorDialog].
     *
     * @param e action event
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val file = e.getData(CommonDataKeys.PSI_FILE)

        if (project == null || file == null || file !is IgnoreFile) {
            return
        }

        GeneratorDialog(project, file).show()
    }

    /**
     * Updates visibility of the action presentation in various actions list.
     * Visible only for [IgnoreFile] context.
     *
     * @param e action event
     */
    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)

        if (file == null || file !is IgnoreFile) {
            e.presentation.isVisible = false
            return
        }
        templatePresentation.icon = file.fileType.icon
    }
}
