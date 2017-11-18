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

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.TreeUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreEntry
import mobi.hsz.idea.gitignore.psi.IgnoreTypes

/**
 * QuickFix action that removes specified entry handled by code inspections like
 * [IgnoreCoverEntryInspection],
 * [IgnoreDuplicateEntryInspection],
 * [IgnoreUnusedEntryInspection].
 *
 * @author Alexander Zolotov <alexander.zolotov@jetbrains.com>
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.4
 */
class IgnoreRemoveEntryFix(entry: IgnoreEntry) : LocalQuickFixAndIntentionActionOnPsiElement(entry) {
    /**
     * Gets QuickFix name.
     *
     * @return QuickFix action name
     */
    override fun getText(): String = IgnoreBundle.message("quick.fix.remove.entry")

    /**
     * Handles QuickFix action invoked on [IgnoreEntry].
     *
     * @param project      the [Project] containing the working file
     * @param file         the [PsiFile] containing handled entry
     * @param startElement the [IgnoreEntry] that will be removed
     * @param endElement   the [PsiElement] which is ignored in invoked action
     */
    override fun invoke(project: Project, file: PsiFile, editor: Editor?,
                        startElement: PsiElement, endElement: PsiElement) {
        if (startElement is IgnoreEntry) {
            removeCrlf(startElement)
            startElement.delete()
        }
    }

    /**
     * Shorthand method for removing CRLF element.
     *
     * @param startElement working PSI element
     */
    private fun removeCrlf(startElement: PsiElement) {
        var node = TreeUtil.findSibling(startElement.node, IgnoreTypes.CRLF)
        if (node == null) {
            node = TreeUtil.findSiblingBackward(startElement.node, IgnoreTypes.CRLF)
        }
        if (node != null) {
            node.psi.delete()
        }

    }

    /**
     * Gets QuickFix family name.
     *
     * @return QuickFix family name
     */
    override fun getFamilyName(): String = IgnoreBundle.message("codeInspection.group")
}
