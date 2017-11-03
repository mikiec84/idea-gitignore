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

package mobi.hsz.idea.gitignore.highlighter

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.ContainerUtil
import mobi.hsz.idea.gitignore.lang.IgnoreParserDefinition
import mobi.hsz.idea.gitignore.lexer.IgnoreLexerAdapter

/**
 * Syntax highlighter definition.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 0.8
 */
class IgnoreHighlighter(
        private val project: Project?,
        private val virtualFile: VirtualFile?
) : SyntaxHighlighterBase() {
    /**
     * Creates lexer adapter.
     *
     * @return lexer adapter
     */
    override fun getHighlightingLexer(): Lexer = IgnoreLexerAdapter(project, virtualFile)

    /**
     * Gets highlighter text [TextAttributesKey] list using [IElementType] token.
     *
     * @param tokenType element type
     * @return attributes list
     */
    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
            SyntaxHighlighterBase.pack(ATTRIBUTES[tokenType])

    companion object {
        /** Attributes map.  */
        private val ATTRIBUTES = ContainerUtil.newHashMap<IElementType, TextAttributesKey>()

        /** Binds parser definitions with highlighter colors.  */
        init {
            fillMap(ATTRIBUTES, IgnoreParserDefinition.COMMENTS, IgnoreHighlighterColors.COMMENT)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.SECTIONS, IgnoreHighlighterColors.SECTION)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.HEADERS, IgnoreHighlighterColors.HEADER)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.NEGATIONS, IgnoreHighlighterColors.NEGATION)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.BRACKETS, IgnoreHighlighterColors.BRACKET)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.SLASHES, IgnoreHighlighterColors.SLASH)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.SYNTAXES, IgnoreHighlighterColors.SYNTAX)
            fillMap(ATTRIBUTES, IgnoreParserDefinition.VALUES, IgnoreHighlighterColors.VALUE)
        }
    }
}
