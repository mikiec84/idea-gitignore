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

package mobi.hsz.idea.gitignore.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import mobi.hsz.idea.gitignore.Common;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.5
 */
public class CloseIgnoredEditorsActionTest extends Common {
    @Mock
    private ProjectLevelVcsManager projectLevelVcsManager;

    @InjectMocks
    private CloseIgnoredEditorsAction action;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

//        when(dialog.setFile(any(PsiFile.class))).thenReturn(dialog);
//
//        AbstractModule module = new TestsModule(getProject());
//        IgnoreModule.EXTERNAL_MODULES.add(module);
//        System.out.println("x");
    }

    public void testCloseIgnoredEditorsActionInvocation() {
        Presentation presentation = myFixture.testAction(action);
        assertEquals("Close Ignored", presentation.getText());
        assertNull(presentation.getDescription());
        assertFalse("Action is not visible if there is no Ignore file context", presentation.isEnabledAndVisible());

        assertEquals("Close _Ignored", action.getPresentationText(false));
        assertEquals("Close _Ignored In Group", action.getPresentationText(true));
    }

    public void testIsActionEnabled() {
        Presentation presentation = myFixture.testAction(action);
        Assert.assertFalse("Action is not enabled", presentation.isEnabled());


        when(action.isActionEnabled(getProject(), any(AnActionEvent.class))).thenReturn(true);
        when(projectLevelVcsManager.getAllActiveVcss()).thenReturn(new AbstractVcs[2]);
        presentation = myFixture.testAction(action);
        Assert.assertTrue("Action is enabled", presentation.isEnabled());

    }
}
