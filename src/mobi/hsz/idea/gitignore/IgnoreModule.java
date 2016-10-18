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

package mobi.hsz.idea.gitignore;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;
import mobi.hsz.idea.gitignore.util.CacheMap;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.6
 */
public class IgnoreModule extends AbstractModule {
    private static final LoadingCache<Project, Injector> injector = CacheBuilder.newBuilder().build(
        new CacheLoader<Project, Injector>() {
            public Injector load(@NotNull Project project) {
                return Guice.createInjector(new IgnoreModule(project));
            }
        }
    );
    
    @NotNull
    private final Project project;

    public static <T> T getInstance(@NotNull Project project, Class<T> type) throws ExecutionException {
        return injector.get(project).getInstance(type);
    }

    private IgnoreModule(@NotNull Project project) {
        this.project = project;
    }

    @Override
    protected void configure() {
        installOpenIdeDependenciesModule();

        bind(CacheMap.class).toInstance(new CacheMap(project));
        
        bind(IgnoreManager.class);
    }

    @Provides
    Project provideProject() {
        return project;
    }
    
    private void installOpenIdeDependenciesModule() {
        bind(VirtualFileManager.class).toInstance(VirtualFileManager.getInstance());
        bind(PsiManagerImpl.class).toInstance((PsiManagerImpl) PsiManager.getInstance(project));
    }
}
