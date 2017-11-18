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

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import java.net.URI
import java.net.URISyntaxException

/**
 * QuickFix action that removes relative parts of the entry
 * [IgnoreRelativeEntryInspection].
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
class IgnoreRelativeEntryFix(entry: IgnoreEntry) : LocalQuickFixOnPsiElement(entry) {
    /**
     * Gets QuickFix name.
     *
     * @return QuickFix action name
     */
    override fun getText(): String = IgnoreBundle.message("quick.fix.relative.entry")

    /**
     * Handles QuickFix action invoked on [IgnoreEntry].
     *
     * @param project      the [Project] containing the working file
     * @param psiFile      the [PsiFile] containing handled entry
     * @param startElement the [IgnoreEntry] that will be removed
     * @param endElement   the [PsiElement] which is ignored in invoked action
     */
    override fun invoke(project: Project, psiFile: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        if (startElement is IgnoreEntry) {
            val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
            if (document != null) {
                val start = startElement.getStartOffsetInParent()
                val text = startElement.getText()
                val fixed = getFixedPath(text)
                document.replaceString(start, start + text.length, fixed)
            }
        }
    }

    /**
     * Removes relative parts from the given path.
     *
     * @param path element
     * @return fixed path
     */
    private fun getFixedPath(path: String): String {
        var path = path
        path = path.replace("\\/".toRegex(), "/").replace("\\\\\\.".toRegex(), ".")
        try {
            path = URI(path).normalize().path
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        return path.replace("/\\.{1,2}/".toRegex(), "/").replace("^\\.{0,2}/".toRegex(), "")
    }

    /**
     * Gets QuickFix family name.
     *
     * @return QuickFix family name
     */
    override fun getFamilyName(): String = IgnoreBundle.message("codeInspection.group")
}
