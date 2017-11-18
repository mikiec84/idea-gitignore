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

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Icons

/**
 * Action that hides or show ignored files in the project tree view.
 *
 * @author Maximiliano Najle <maximilianonajle@gmail.com>
 * @since 1.7
 */
class HideIgnoredFilesAction : AnAction(text, "", Icons.IGNORE) {
    /**
     * Toggles [IgnoreSettings.hideIgnoredFiles] value.
     *
     * @param e action event
     */
    override fun actionPerformed(e: AnActionEvent) {
        SETTINGS.isHideIgnoredFiles = !SETTINGS.isHideIgnoredFiles

        val presentation = this.templatePresentation
        presentation.text = text
    }

    companion object {
        val SETTINGS: IgnoreSettings = IgnoreSettings.getInstance()

        /**
         * Returns proper action's presentation text depending on the [IgnoreSettings.hideIgnoredFiles] value.
         *
         * @return presentation text
         */
        private val text: String
            get() {
                val hideIgnoredFiles = SETTINGS.isHideIgnoredFiles
                return IgnoreBundle.message(when {
                    hideIgnoredFiles -> "action.showIgnoredVisibility"
                    else -> "action.hideIgnoredVisibility"
                })
            }
    }
}
