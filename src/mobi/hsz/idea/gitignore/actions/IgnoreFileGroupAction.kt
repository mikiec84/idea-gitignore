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

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreBundle.BUNDLE_NAME
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.util.CommonDataKeys
import mobi.hsz.idea.gitignore.util.ExternalFileException
import mobi.hsz.idea.gitignore.util.Utils
import org.jetbrains.annotations.PropertyKey
import java.util.*

/**
 * Group action that ignores specified file or directory. * [ActionGroup] expands single action into a more child
 * options to allow user specify the IgnoreFile that will be used for file's path storage.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.5
 */
open class IgnoreFileGroupAction @JvmOverloads constructor(
        @PropertyKey(resourceBundle = BUNDLE_NAME) textKey: String = "action.addToIgnore.group",
        @PropertyKey(resourceBundle = BUNDLE_NAME) descriptionKey: String = "action.addToIgnore.group.description",
        /** Action presentation's text for single element.  */
        @param:PropertyKey(resourceBundle = BUNDLE_NAME) @field:PropertyKey(resourceBundle = BUNDLE_NAME)
        private val presentationTextSingleKey: String = "action.addToIgnore.group.noPopup"
) : ActionGroup() {
    /** List of suitable Gitignore [VirtualFile]s that can be presented in an IgnoreFile action. */
    private val files = ContainerUtil.newHashMap<IgnoreFileType, List<VirtualFile>>()

    /** [Project]'s base directory.  */
    private var baseDir: VirtualFile? = null

    init {
        val p = templatePresentation
        p.text = IgnoreBundle.message(textKey)
        p.description = IgnoreBundle.message(descriptionKey)
    }

    /**
     * Presents a list of suitable Gitignore files that can cover currently selected [VirtualFile].
     * Shows a subgroup with available files or one option if only one Gitignore file is available.
     *
     * @param e action event
     */
    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val project = e.getData(CommonDataKeys.PROJECT)
        val presentation = e.presentation
        files.clear()

        if (project != null && file != null) {
            try {
                presentation.isVisible = true
                baseDir = project.baseDir

                for (language in IgnoreBundle.LANGUAGES) {
                    val fileType = language.fileType
                    val list = Utils.getSuitableIgnoreFiles(project, fileType, file)
                    Collections.reverse(list)
                    files.put(fileType, list)
                }
            } catch (e1: ExternalFileException) {
                presentation.isVisible = false
            }

        }

        isPopup = countFiles() > 1
    }

    /**
     * Creates subactions bound to the specified Gitignore [VirtualFile]s using [IgnoreFileAction].
     *
     * @param e action event
     * @return actions list
     */
    override fun getChildren(e: AnActionEvent?): Array<AnAction?> {
        val actions: Array<AnAction?>
        val count = countFiles()

        if (count == 0 || baseDir == null) {
            actions = arrayOfNulls(0)
        } else {
            actions = arrayOfNulls(count)

            var i = 0
            for ((key, value) in files) {
                for (file in value) {
                    val action = createAction(file)
                    actions[i++] = action

                    var name = Utils.getRelativePath(baseDir!!, file)
                    if (StringUtil.isNotEmpty(name)) {
                        name = StringUtil.shortenPathWithEllipsis(name!!, FILENAME_MAX_LENGTH)
                    }

                    if (count == 1) {
                        name = IgnoreBundle.message(presentationTextSingleKey, name)
                    }

                    val presentation = action.templatePresentation
                    presentation.setIcon(key.icon)
                    presentation.setText(name)
                }
            }
        }
        return actions
    }

    /**
     * Creates new [IgnoreFileAction] action instance.
     *
     * @param file current file
     * @return action instance
     */
    protected open fun createAction(file: VirtualFile): IgnoreFileAction = IgnoreFileAction(file)

    /**
     * Counts items in [.files] map.
     *
     * @return files amount
     */
    private fun countFiles(): Int = files.values.sumBy { it.size }

    companion object {
        /** Maximum filename length for the action name.  */
        private val FILENAME_MAX_LENGTH = 30
    }
}
