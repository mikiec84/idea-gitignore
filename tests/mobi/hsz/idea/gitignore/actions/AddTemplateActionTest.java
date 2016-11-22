/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 hsz Jakub Chrzanowski <jakub@hsz.mobi>
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
import com.intellij.testFramework.TestActionEvent;
import mobi.hsz.idea.gitignore.Common;
import mobi.hsz.idea.gitignore.IgnoreBundle;
import mobi.hsz.idea.gitignore.IgnoreTestModule;
import mobi.hsz.idea.gitignore.file.type.IgnoreFileType;
import mobi.hsz.idea.gitignore.ui.GeneratorDialog;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.5
 */
public class AddTemplateActionTest extends Common<AddTemplateAction> {

    @Mock
    private GeneratorDialog dialog;

    @InjectMocks
    private AddTemplateAction action;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        action = IgnoreTestModule.getInstance(AddTemplateAction.class, getProject());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddTemplateActionInvocation() {
        Presentation presentation = myFixture.testAction(action);
        
        assertEquals(IgnoreBundle.message("action.addTemplate"), presentation.getText());
        assertEquals(IgnoreBundle.message("action.addTemplate.description"), presentation.getDescription());
        assertFalse("Action is not visible if there is no Ignore file context", presentation.isEnabledAndVisible());

        AnActionEvent e = new TestActionEvent();
        action.actionPerformed(e);
        myFixture.configureByText(IgnoreFileType.INSTANCE, "foo");
        presentation = myFixture.testAction(action);
        
        assertTrue("Action is visible if there is Ignore file context", presentation.isEnabledAndVisible());
        verify(dialog, times(1)).setFile(myFixture.getFile());
        verify(dialog, times(1)).show();
    }
}
