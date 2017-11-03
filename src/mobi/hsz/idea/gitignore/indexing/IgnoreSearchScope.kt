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

package mobi.hsz.idea.gitignore.indexing

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType

/**
 * Provides extended [GlobalSearchScope] with additional ignore files (i.e. outer gitignore files).
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
class IgnoreSearchScope private constructor(project: Project) : GlobalSearchScope(project) {
    override fun compare(file1: VirtualFile, file2: VirtualFile): Int = 0

    override fun contains(file: VirtualFile): Boolean = file.fileType is IgnoreFileType

    override fun isSearchInLibraries(): Boolean = true

    override fun isForceSearchingInLibrarySources(): Boolean = true

    override fun isSearchInModuleContent(aModule: Module): Boolean = true

    override fun isSearchOutsideRootModel(): Boolean = true

    override fun union(scope: SearchScope): GlobalSearchScope = this

    override fun intersectWith(scope: SearchScope): SearchScope = scope

    companion object {
        /**
         * Returns [GlobalSearchScope.projectScope] instance united with additional files.
         *
         * @param project current project
         * @return extended instance of [GlobalSearchScope]
         */
        operator fun get(project: Project): GlobalSearchScope {
            val scope = IgnoreSearchScope(project)
            val files = ExternalIndexableSetContributor.getAdditionalFiles(project)
            return scope.uniteWith(GlobalSearchScope.filesScope(project, files))
        }
    }
}
