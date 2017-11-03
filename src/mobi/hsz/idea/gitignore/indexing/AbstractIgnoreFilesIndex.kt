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

package mobi.hsz.idea.gitignore.indexing

import com.intellij.openapi.project.DumbAware
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.io.KeyDescriptor

/**
 * Abstract class of [FileBasedIndexExtension] that contains base configuration for [IgnoreFilesIndex].
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
abstract class AbstractIgnoreFilesIndex<K, V> : FileBasedIndexExtension<K, V>(), KeyDescriptor<K>,
        DataIndexer<K, V, FileContent>, FileBasedIndex.InputFilter, DumbAware {
    /**
     * Returns [DataIndexer] implementation.
     *
     * @return [DataIndexer] instance.
     */
    override fun getIndexer(): DataIndexer<K, V, FileContent> = this

    /**
     * Returns [KeyDescriptor] implementation.
     *
     * @return [KeyDescriptor] instance.
     */
    override fun getKeyDescriptor(): KeyDescriptor<K> = this

    /**
     * Checks if given types objects are equal.
     *
     * @param val1 object to compare
     * @param val2 object to compare
     * @return objects are equal
     */
    override fun isEqual(val1: K, val2: K): Boolean = val1 == val2

    /**
     * Returns hashCode for given type object.
     *
     * @param value type object
     * @return object's hashCode
     */
    override fun getHashCode(value: K): Int = value!!.hashCode()

    /**
     * Current [AbstractIgnoreFilesIndex] depends on the file content.
     *
     * @return depends on file content
     */
    override fun dependsOnFileContent(): Boolean = true

    /**
     * Returns [FileBasedIndex.InputFilter] implementation.
     *
     * @return [FileBasedIndex.InputFilter] instance.
     */
    override fun getInputFilter(): FileBasedIndex.InputFilter = this
}
