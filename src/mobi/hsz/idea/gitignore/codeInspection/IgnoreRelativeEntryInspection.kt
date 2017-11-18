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

package mobi.hsz.idea.gitignore.codeInspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreFile
import mobi.hsz.idea.gitignore.psi.IgnoreVisitor

/**
 * Inspection tool that checks if entry is relative.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
class IgnoreRelativeEntryInspection : LocalInspectionTool() {
    /**
     * Checks if entries are relative.
     *
     * @param file       current working file yo check
     * @param manager    [InspectionManager] to ask for [ProblemDescriptor]'s from
     * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action
     * otherwise
     * @return `null` if no problems found or not applicable at file level
     */
    override fun checkFile(file: PsiFile,
                           manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is IgnoreFile) {
            return null
        }

        val problemsHolder = ProblemsHolder(manager, file, isOnTheFly)

        file.acceptChildren(object : IgnoreVisitor() {
            override fun visitEntry(entry: IgnoreEntry) {
                val path = entry.text.replace("\\\\(.)".toRegex(), "$1")
                if (path.contains("./")) {
                    problemsHolder.registerProblem(entry, IgnoreBundle.message("codeInspection.relativeEntry.message"),
                            IgnoreRelativeEntryFix(entry))
                }
                super.visitEntry(entry)
            }
        })

        return problemsHolder.resultsArray
    }

    /**
     * Forces checking every entry in checked file.
     *
     * @return `true`
     */
    override fun runForWholeFile(): Boolean = true
}
