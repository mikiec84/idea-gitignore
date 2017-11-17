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

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.indexing.IndexableSetContributor
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.IgnoreManager
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType
import java.util.*

/**
 * IndexedRootsProvider implementation that provides additional paths to index - like external/global ignore files.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
class ExternalIndexableSetContributor : IndexableSetContributor() {
    /**
     * @return an additional project-dependent set of [VirtualFile] instances to index, the returned set should
     * not contain nulls or invalid files
     */
    override fun getAdditionalProjectRootsToIndex(project: Project): Set<VirtualFile> = getAdditionalFiles(project)

    /**
     * Not implemented. Returns [.EMPTY_SET].
     *
     * @return [.EMPTY_SET]
     */
    override fun getAdditionalRootsToIndex(): Set<VirtualFile> = EMPTY_SET

    companion object {
        /** Empty set.  */
        private val EMPTY_SET = emptySet<VirtualFile>()

        /** Cached additional paths set.  */
        private val CACHE = ContainerUtil.newConcurrentMap<Project, HashSet<VirtualFile>>()

        /**
         * Returns additional files located outside of the current project that should be indexed.
         *
         * @param project current project
         * @return additional files
         */
        fun getAdditionalFiles(project: Project): HashSet<VirtualFile> {
            val files = ContainerUtil.newHashSet<VirtualFile>()

            if (CACHE.containsKey(project)) {
                files.addAll(CACHE[project]?.filter { file -> file.isValid }!!)
            } else {
                for (language in IgnoreBundle.LANGUAGES) {
                    val fileType = language.fileType
                    if (language.isOuterFileSupported) {
                        for (file in language.getOuterFiles(project, true)) {
                            if (file == null || !file.isValid) {
                                continue
                            }
                            if (file.fileType !is IgnoreFileType && file.fileType != fileType) {
                                IgnoreManager.associateFileType(file.name, fileType)
                            }

                            files.add(file)
                        }
                    }
                }
            }

            CACHE.put(project, files)
            return files
        }

        /**
         * Removes cached files for the given project.
         *
         * @param project current project
         */
        fun invalidateCache(project: Project) {
            CACHE.remove(project)
        }
    }
}
