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

import com.intellij.openapi.vfs.VirtualFile
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import mobi.hsz.idea.gitignore.util.Utils

/**
 * Action that adds currently selected [VirtualFile] to the specified Ignore [VirtualFile] as unignored.
 * Action is added to the IDE context menus not directly but with [UnignoreFileGroupAction] action.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.6
 */
class UnignoreFileAction(
        fileType: IgnoreFileType?,
        virtualFile: VirtualFile?
) : IgnoreFileAction(fileType, virtualFile, "action.addToUnignore", "action.addToUnignore.description") {
    /**
     * Builds a new instance of [IgnoreFileAction].
     * Describes action's presentation.
     *
     * @param virtualFile Gitignore file
     */
    @JvmOverloads constructor(virtualFile: VirtualFile? = null) : this(Utils.getFileType(virtualFile), virtualFile)

    /**
     * Gets the file's path relative to the specified root directory.
     * Returns string with negation.
     *
     * @param root root directory
     * @param file file used for generating output path
     * @return relative path
     */
    override fun getPath(root: VirtualFile, file: VirtualFile): String {
        val path = super.getPath(root, file)
        return if (path.isEmpty()) path else '!' + path
    }
}
