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
import com.intellij.openapi.vcs.VcsRoot
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ConcurrentWeakHashMap
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.ui.untrackFiles.UntrackFilesDialog
import mobi.hsz.idea.gitignore.util.CommonDataKeys
import mobi.hsz.idea.gitignore.util.Icons
import java.util.concurrent.ConcurrentMap

/**
 * Action that invokes [UntrackFilesDialog] dialog.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.7.2
 */
/** Builds a new instance of [HandleTrackedIgnoredFilesAction].  */
class HandleTrackedIgnoredFilesAction : AnAction(
        IgnoreBundle.message("action.handleTrackedIgnoredFiles"),
        IgnoreBundle.message("action.handleTrackedIgnoredFiles.description"),
        Icons.IGNORE
) {
    /**
     * Toggles [mobi.hsz.idea.gitignore.settings.IgnoreSettings.hideIgnoredFiles] value.
     *
     * @param e action event
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        UntrackFilesDialog(project, getTrackedIgnoredFiles(e)).show()
    }

    /**
     * Shows action in the context menu.
     *
     * @param e action event
     */
    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = !getTrackedIgnoredFiles(e).isEmpty()
    }

    /**
     * Helper method to return tracked and ignored files map.
     *
     * @param event current event
     * @return map of files
     */
    private fun getTrackedIgnoredFiles(event: AnActionEvent): ConcurrentMap<VirtualFile, VcsRoot> =
            if (event.project != null) {
                IgnoreManager.getInstance(event.project!!).confirmedIgnoredFiles
            } else ConcurrentWeakHashMap()
}
