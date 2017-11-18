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

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.psi.IgnoreSyntax

/**
 * QuickFix action that invokes [mobi.hsz.idea.gitignore.codeInsight.SyntaxCompletionContributor]
 * on the given [IgnoreSyntax] element.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.0
 */
class IgnoreSyntaxEntryFix(syntax: IgnoreSyntax) : LocalQuickFixAndIntentionActionOnPsiElement(syntax) {
    /**
     * Gets QuickFix name.
     *
     * @return QuickFix action name
     */
    override fun getText(): String = IgnoreBundle.message("quick.fix.syntax.entry")

    /**
     * Handles QuickFix action invoked on [IgnoreSyntax].
     *
     * @param project      the [Project] containing the working file
     * @param file         the [PsiFile] containing handled entry
     * @param startElement the [IgnoreSyntax] that will be selected and replaced
     * @param endElement   the [PsiElement] which is ignored in invoked action
     */
    override fun invoke(project: Project, file: PsiFile, editor: Editor?,
                        startElement: PsiElement, endElement: PsiElement) {
        if (startElement is IgnoreSyntax) {
            val value = startElement.value
            editor?.selectionModel?.setSelection(
                    value.textOffset,
                    value.textOffset + value.textLength
            )
            CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(project, editor)
        }
    }

    /**
     * Run in read action because of completion invoking.
     *
     * @return `false`
     */
    override fun startInWriteAction(): Boolean = false

    /**
     * Gets QuickFix family name.
     *
     * @return QuickFix family name
     */
    override fun getFamilyName(): String = IgnoreBundle.message("codeInspection.group")
}
