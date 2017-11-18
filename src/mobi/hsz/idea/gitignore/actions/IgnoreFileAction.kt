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
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreBundle.BUNDLE_NAME
import mobi.hsz.idea.gitignore.command.AppendFileCommandAction
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.util.CommonDataKeys
import mobi.hsz.idea.gitignore.util.Utils
import org.jetbrains.annotations.PropertyKey

/**
 * Action that adds currently selected [VirtualFile] to the specified Ignore [VirtualFile].
 * Action is added to the IDE context menus not directly but with [IgnoreFileGroupAction] action.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
open class IgnoreFileAction @JvmOverloads constructor(
        /** Current ignore file type.  */
        private val fileType: IgnoreFileType?,
        /** Ignore [VirtualFile] that will be used for current action.  */
        private val ignoreFile: VirtualFile?,
        @PropertyKey(resourceBundle = BUNDLE_NAME) textKey: String = "action.addToIgnore",
        @PropertyKey(resourceBundle = BUNDLE_NAME) descriptionKey: String = "action.addToIgnore.description"
) : DumbAwareAction(
        IgnoreBundle.message(textKey, fileType?.ignoreLanguage?.filename),
        IgnoreBundle.message(descriptionKey, fileType?.ignoreLanguage?.filename),
        fileType?.icon
) {
    /**
     * Builds a new instance of [IgnoreFileAction].
     * Describes action's presentation.
     *
     * @param virtualFile Gitignore file
     */
    @JvmOverloads constructor(virtualFile: VirtualFile? = null) : this(Utils.getFileType(virtualFile), virtualFile)

    /**
     * Adds currently selected [VirtualFile] to the [.ignoreFile].
     * If [.ignoreFile] is null, default project's Gitignore file will be used.
     * Files that cannot be covered with Gitignore file produces error notification.
     * When action is performed, Gitignore file is opened with additional content added
     * using [AppendFileCommandAction].
     *
     * @param e action event
     */
    override fun actionPerformed(e: AnActionEvent) {
        val files = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        val project = e.getRequiredData(CommonDataKeys.PROJECT)

        var ignore: PsiFile? = null
        if (ignoreFile != null) {
            ignore = Utils.getPsiFile(project, ignoreFile)
        }
        if (ignore == null && fileType != null) {
            ignore = Utils.getIgnoreFile(project, fileType, null, true)
        }

        if (ignore != null) {
            val paths = ContainerUtil.newHashSet<String>()
            for (file in files) {
                val path = getPath(ignore.virtualFile.parent, file)
                if (path.isEmpty()) {
                    val baseDir = project.baseDir
                    if (baseDir != null) {
                        Notifications.Bus.notify(Notification(IgnoreLanguage.GROUP,
                                IgnoreBundle.message("action.ignoreFile.addError",
                                        Utils.getRelativePath(baseDir, file)),
                                IgnoreBundle.message("action.ignoreFile.addError.to",
                                        Utils.getRelativePath(baseDir, ignore.virtualFile)),
                                NotificationType.ERROR), project)
                    }
                } else {
                    paths.add(path)
                }
            }
            Utils.openFile(project, ignore)
            AppendFileCommandAction(project, ignore, paths, false, false).execute()
        }
    }

    /**
     * Shows action in the context menu if current file is covered by the specified [.ignoreFile].
     *
     * @param e action event
     */
    override fun update(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        val project = e.project

        if (project == null || files == null || files.size == 1 && files[0] == project.baseDir) {
            e.presentation.isVisible = false
        }
    }

    /**
     * Gets the file's path relative to the specified root directory.
     *
     * @param root root directory
     * @param file file used for generating output path
     * @return relative path
     */
    protected open fun getPath(root: VirtualFile, file: VirtualFile): String {
        var path = StringUtil.notNullize(Utils.getRelativePath(root, file))
        path = Utils.escapeChar(path, '[')
        path = Utils.escapeChar(path, ']')
        path = Utils.trimLeading(path, '/')
        return if (path.isEmpty()) path else '/' + path
    }
}
