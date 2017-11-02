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

package mobi.hsz.idea.gitignore;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 1.6
 */
public class IgnoreModule extends AbstractModule {
//    private static final LoadingCache<Project, Injector> INJECTOR = CacheBuilder.newBuilder().build(
//            new CacheLoader<Project, Injector>() {
//                public Injector load(@NotNull Project project) {
//                    return Guice.createInjector(new IgnoreModule(project));
//                }
//            }
//    );

    protected final Supplier<Injector> injectorSupplier;

    @NotNull
    private final Project project;

    private static final Map<Project, Supplier<Injector>> INJECTOR_MAP = ContainerUtil.newHashMap();

    private IgnoreModule(@NotNull final Project project) {

        this.project = project;

//        ConnectionSettingsProvider connectionSettingsProvider = ServiceManager.getService(project, IdePersistentConnectionSettingsProvider.class);

        injectorSupplier = Suppliers.memoize(new Supplier<Injector>() {
            @Override
            public Injector get() {
                return Guice.createInjector(new IgnoreModule(project));
            }
        });

        INJECTOR_MAP.put(project, injectorSupplier);

//        getInstance(ToolbarActiveItem.class);
    }

    /**
     * Create an instance from an already existing PluginModule
     *
     * @param project          current project
     * @param injectorSupplier supplier
     */
    private IgnoreModule(@NotNull Project project, Supplier<Injector> injectorSupplier) {
        this.project = project;
        this.injectorSupplier = injectorSupplier;
    }

    /**
     * Checks if {@link #INJECTOR_MAP} has given project.
     *
     * @param project current project
     * @return project is present
     */
    public static boolean hasProject(@NotNull Project project) {
        return INJECTOR_MAP.containsKey(project);
    }

    /**
     * Get DI module with project injected.
     *
     * @param project current project
     * @return {@link IgnoreModule} instance
     */
    public static IgnoreModule withProject(@NotNull Project project) {
        if (hasProject(project)) {
            return new IgnoreModule(project, INJECTOR_MAP.get(project));
        }
        return new IgnoreModule(project);
    }

    /**
     * Returns instance of given type.
     *
     * @param type class to get
     * @param <T>  type
     * @return instance of given class
     */
    public <T> T getInstance(Class<T> type) {
        return injectorSupplier.get().getInstance(type);
    }

    /**
     * CAREFUL: if there's a possibility that the module with the project does not exist yet, this must be run on
     * dispatch thread
     *
     * @param project
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Project project, Class<T> type) {
        if (!INJECTOR_MAP.containsKey(project)) {
            //Constructor changes static field event tho instance is not used anywhere
            new IgnoreModule(project);
        }
        return INJECTOR_MAP.get(project).get().getInstance(type);
    }

    @Override
    protected void configure() {
        bind(VirtualFileManager.class).toInstance(VirtualFileManager.getInstance());
        bind(FileIndex.class).toInstance(ProjectRootManager.getInstance(project).getFileIndex());
    }

//
//
//
//    public static <T> T getInstance(@NotNull Project project, Class<T> type) throws ExecutionException {
//        return INJECTOR.get(project).getInstance(type);
//    }
//
//    private IgnoreModule(@NotNull Project project) {
//        this.project = project;
//    }
//
//    @Override
//    protected void configure() {
//        installOpenIdeDependenciesModule();
//    }
//
//    @Provides
//    Project provideProject() {
//        return project;
//    }
//
//    private void installOpenIdeDependenciesModule() {
//        bind(VirtualFileManager.class).toInstance(VirtualFileManager.getInstance());
//        bind(PsiManagerImpl.class).toInstance((PsiManagerImpl) PsiManager.getInstance(project));
//    }
}
