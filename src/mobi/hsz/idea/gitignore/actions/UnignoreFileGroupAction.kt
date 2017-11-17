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

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.settings.IgnoreSettings

/**
 * Group action that unignores specified file or directory.
 * ActionGroup expands single action into a more child options to allow user specify
 * the IgnoreFile that will be used for file's path storage.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.6
 */
class UnignoreFileGroupAction : IgnoreFileGroupAction(
        "action.addToUnignore.group",
        "action.addToUnignore.group.description",
        "action.addToUnignore.group.noPopup"
) {
    /** Ignore settings instance.  */
    private val settings: IgnoreSettings = IgnoreSettings.getInstance()

    /**
     * Creates new [UnignoreFileAction] action instance.
     *
     * @param file current file
     * @return action instance
     */
    override fun createAction(file: VirtualFile): IgnoreFileAction = UnignoreFileAction(file)

    /**
     * Presents a list of suitable Gitignore files that can cover currently selected [VirtualFile].
     * Shows a subgroup with available files or one option if only one Gitignore file is available.
     * Group will be hidden if [IgnoreSettings.isUnignoreActions] is set to `false`.
     *
     * @param e action event
     */
    override fun update(e: AnActionEvent) {
        val status = settings.isUnignoreActions
        e.presentation.isVisible = status
        if (status) {
            super.update(e)
        }
    }
}
