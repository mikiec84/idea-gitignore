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

package mobi.hsz.idea.gitignore.outer

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.labels.LinkLabel
import com.intellij.ui.components.labels.LinkListener
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.UIUtil
import mobi.hsz.idea.gitignore.IgnoreBundle
import mobi.hsz.idea.gitignore.lang.IgnoreLanguage
import mobi.hsz.idea.gitignore.settings.IgnoreSettings
import mobi.hsz.idea.gitignore.util.Utils
import java.awt.BorderLayout
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants

/**
 * Wrapper that creates bottom editor component for displaying outer ignore rules.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.1
 */
class OuterIgnoreWrapper(project: Project, language: IgnoreLanguage, outerFiles: List<VirtualFile>) : Disposable {
    /** Main wrapper panel.  */
    private val panel: JPanel = JPanel(BorderLayout())

    /** List of outer editors in the wrapper.  */
    private val outerEditors = ContainerUtil.newArrayList<Editor>()

    /** The settings storage object.  */
    private val settings: IgnoreSettings = IgnoreSettings.getInstance()

    /** Current panel's height.  */
    private var dragPanelHeight: Int = 0

    /** Y position of the drag event.  */
    private var dragYOnScreen: Int = 0

    /** Obtains if it's in drag mode.  */
    private var drag: Boolean = false

    /**
     * Returns outer panel.
     *
     * @return outer panel
     */
    val component: JComponent get() = panel

    init {
        panel.border = BorderFactory.createEmptyBorder(0, 10, 5, 10)

        val northPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 5))
        val label = JBLabel(
                IgnoreBundle.message("outer.label"),
                UIUtil.ComponentStyle.REGULAR,
                UIUtil.FontColor.BRIGHTER
        )
        label.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
        northPanel.add(label)

        val tabbedPanel = TabbedPaneWrapper(project)
        val tabbedPanelComponent = tabbedPanel.component
        val linkLabel = LinkLabel(outerFiles[0].path, null, LinkListener<Any> { aSource, aLinkData ->
            Utils.openFile(project, outerFiles[tabbedPanel.selectedIndex])
        })
        val userHomeDir = VfsUtil.getUserHomeDir()

        outerFiles.forEach { outerFile ->
            val document = FileDocumentManager.getInstance().getDocument(outerFile)
            val outerEditor = if (document != null) Utils.createPreviewEditor(document, project, true) else null

            if (outerEditor != null) {
                val scrollPanel = ScrollPaneFactory.createScrollPane(outerEditor.component)
                scrollPanel.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
                scrollPanel.preferredSize = Dimension(0, settings.outerIgnoreWrapperHeight)

                var path: String? = outerFile.path
                if (userHomeDir != null) {
                    path = path!!.replace(userHomeDir.path, "~")
                }

                if (path != null) {
                    tabbedPanel.addTab(path, language.icon, scrollPanel, outerFile.canonicalPath)
                    outerEditors.add(outerEditor)
                }
            }
        }

        northPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                if (e!!.point.getY() <= DRAG_OFFSET) {
                    dragPanelHeight = tabbedPanelComponent.height
                    dragYOnScreen = e.yOnScreen
                    drag = true
                }
            }

            override fun mouseReleased(e: MouseEvent?) {
                drag = false
                settings.outerIgnoreWrapperHeight = tabbedPanelComponent.height
            }
        })
        northPanel.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent?) {
                val cursor = when {
                    e!!.point.getY() <= DRAG_OFFSET -> Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)
                    else -> Cursor.getDefaultCursor()
                }
                panel.cursor = cursor
            }

            override fun mouseDragged(e: MouseEvent?) {
                if (drag) {
                    var height = dragPanelHeight - e!!.yOnScreen + dragYOnScreen
                    if (height > MAX_HEIGHT) {
                        height = MAX_HEIGHT
                    }
                    tabbedPanelComponent.preferredSize = Dimension(0, height)
                    panel.revalidate()
                }
            }
        })

        tabbedPanel.addChangeListener { linkLabel.setText(outerFiles[tabbedPanel.selectedIndex].path) }

        panel.add(northPanel, BorderLayout.NORTH)
        panel.add(tabbedPanelComponent, BorderLayout.CENTER)
        panel.add(linkLabel, BorderLayout.SOUTH)
    }
    /** Constructor.  */

    /** Disposes all outer editors stored in [.outerEditors].  */
    override fun dispose() {
        outerEditors.forEach { outerEditor -> EditorFactory.getInstance().releaseEditor(outerEditor) }
    }

    companion object {
        /** Pixels offset to handle drag event.  */
        private val DRAG_OFFSET = 10

        /** Maximum height of the component to avoid losing it from view.  */
        private val MAX_HEIGHT = 300
    }
}
