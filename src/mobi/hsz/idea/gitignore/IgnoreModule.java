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
import com.google.inject.*;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.impl.DummyProject;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.ContainerUtil;
import mobi.hsz.idea.gitignore.settings.IgnoreSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Dependency injection module.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 3.0
 */
public class IgnoreModule extends AbstractModule {
    /** Injectors supplier. */
    @NotNull
    private final Supplier<Injector> injectorSupplier;

    /** Current project. Can be {@link DummyProject}. */
    @NotNull
    private Project project;

    /** Available injectors map. */
    @NotNull
    private static final Map<Project, Supplier<Injector>> INJECTOR_MAP = ContainerUtil.newHashMap();

    /**
     * Default constructor that initializes new supplier.
     *
     * @param project current project
     */
    private IgnoreModule(@NotNull final Project project) {
        this.project = project;
        this.injectorSupplier = Suppliers.memoize(new Supplier<Injector>() {
            @Override
            public Injector get() {
                return Guice.createInjector(IgnoreModule.this);
            }
        });

        INJECTOR_MAP.put(project, injectorSupplier);
    }

    /**
     * Constructor.
     *
     * @param project          current project
     * @param injectorSupplier existing injector
     */
    private IgnoreModule(@NotNull Project project, @NotNull Supplier<Injector> injectorSupplier) {
        this.project = project;
        this.injectorSupplier = injectorSupplier;
    }

    /**
     * Returns instance for given project.
     *
     * @param project current project
     * @return {@link IgnoreModule} instance
     */
    public static IgnoreModule get(@NotNull Project project) {
        if (INJECTOR_MAP.containsKey(project)) {
            return new IgnoreModule(project, INJECTOR_MAP.get(project));
        }
        return new IgnoreModule(project);
    }

    /**
     * Injects DI members.
     *
     * @param object to handle
     */
    public static void injectMembers(@NotNull Object object) {
        injectMembers(object, DummyProject.getInstance());
    }

    /**
     * Injects DI members.
     *
     * @param object to handle
     */
    public static void injectMembers(@NotNull Object object, @NotNull Project project) {
        get(project).injectorSupplier.get().injectMembers(object);
    }

    /**
     * Injects DI members.
     *
     * @param clz class to inject
     */
    public static <T> T inject(@NotNull Class<T> clz) {
        return inject(clz, DummyProject.getInstance());
    }

    /**
     * Injects DI members.
     *
     * @param clz     class to inject
     * @param project current project
     */
    public static <T> T inject(@NotNull Class<T> clz, @NotNull Project project) {
        return get(project).injectorSupplier.get().getInstance(clz);
    }

    /** Injections setup. */
    @Override
    protected void configure() {
        // plugin
        bind(IgnoreSettings.class).toInstance(ServiceManager.getService(IgnoreSettings.class));

        // IDE
        bind(Application.class).toInstance(ApplicationManager.getApplication());
        bind(FileTypeManager.class).toInstance(FileTypeManager.getInstance());
        bind(VirtualFileManager.class).toInstance(VirtualFileManager.getInstance());

        if (project instanceof DummyProject) {
            return;
        }

        // plugin

        // IDE
        bind(FileIndex.class).toInstance(ProjectRootManager.getInstance(project).getFileIndex());
        bind(FileStatusManager.class).toInstance(FileStatusManager.getInstance(project));
        bind(ProjectLevelVcsManager.class).toInstance(ProjectLevelVcsManager.getInstance(project));
        bind(ProjectView.class).toProvider(new Provider<ProjectView>() {
            @Override
            public ProjectView get() {
                return ProjectView.getInstance(project);
            }
        });
    }

    /**
     * Returns current project.
     *
     * @return project
     */
    @NotNull
    @Provides
    public Project getProject() {
        return project;
    }
}
