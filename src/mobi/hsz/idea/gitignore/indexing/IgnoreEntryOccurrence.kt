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

import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.containers.ContainerUtil
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.builder.HashCodeBuilder

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import java.io.Serializable
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Entry containing information about the [VirtualFile] instance of the ignore file mapped with the collection
 * of ignore entries converted to [Pattern] for better performance. Class is used for indexing.
 *
 * @author Jakub Chrzanowski <jakub@hsz.mobi>
 * @since 2.0
 */
class IgnoreEntryOccurrence(
        private val url: String,
        private var file: VirtualFile? = null
) : Serializable {
    /** Collection of ignore entries converted to [Pattern].  */
    private val items = ContainerUtil.newArrayList<Pair<Matcher, Boolean>>()

    /**
     * Constructor.
     *
     * @param file current file
     */
    constructor(file: VirtualFile) : this(file.url, file)

    /**
     * Calculates hashCode with [.file] and [.items] hashCodes.
     *
     * @return entry hashCode
     */
    override fun hashCode(): Int = HashCodeBuilder().append(url).append(items.toString()).toHashCode()

    /**
     * Checks if given object is equal to current [IgnoreEntryOccurrence] instance.
     *
     * @param other to check
     * @return objects are equal.
     */
    override fun equals(other: Any?): Boolean {
        if (other !is IgnoreEntryOccurrence) {
            return false
        }

        val entry = other as IgnoreEntryOccurrence?
        var equals = url == entry!!.url && items.size == entry.items.size
        items.indices.forEach { i -> equals = equals && items[i].toString() == entry.items[i].toString() }

        return equals
    }

    /**
     * Returns current [VirtualFile].
     *
     * @return current file
     */
    fun getFile(): VirtualFile? {
        if (file == null) {
            file = VirtualFileManager.getInstance().findFileByUrl(url)
        }
        return file
    }

    /**
     * Returns entries for current file.
     *
     * @return entries
     */
    fun getItems(): List<Pair<Matcher, Boolean>> = items

    /**
     * Adds new element to [.items].
     *
     * @param matcher   entry converted to [Matcher]
     * @param isNegated entry is negated
     */
    fun add(matcher: Matcher, isNegated: Boolean) {
        items.add(Pair.create(matcher, isNegated))
    }

    companion object {
        /**
         * Static helper to write given [IgnoreEntryOccurrence] to the output stream.
         *
         * @param out   output stream
         * @param entry entry to write
         * @throws IOException I/O exception
         */
        @Synchronized
        @Throws(IOException::class)
        fun serialize(out: DataOutput, entry: IgnoreEntryOccurrence) {
            out.writeUTF(entry.url)
            out.writeInt(entry.items.size)
            entry.items.forEach { item ->
                out.writeUTF(item.first.pattern().pattern())
                out.writeBoolean(item.second)
            }
        }

        /**
         * Static helper to read [IgnoreEntryOccurrence] from the input stream.
         *
         * @param in input stream
         * @return read [IgnoreEntryOccurrence]
         */
        @Synchronized
        fun deserialize(`in`: DataInput): IgnoreEntryOccurrence? {
            try {
                val url = `in`.readUTF()
                if (StringUtils.isEmpty(url)) {
                    return null
                }

                val entry = IgnoreEntryOccurrence(url)
                val size = `in`.readInt()
                for (i in 0 until size) {
                    val pattern = Pattern.compile(`in`.readUTF())
                    val isNegated = `in`.readBoolean()
                    entry.add(pattern.matcher(""), isNegated)
                }

                return entry
            } catch (ignored: IOException) {
            }

            return null
        }
    }
}
