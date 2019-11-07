/**
 * Copyright (C) 2015 ogaclejapan
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wkz.adapter.app

import android.annotation.TargetApi
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.util.SparseArray

import androidx.fragment.app.Fragment

import java.io.Serializable
import java.util.ArrayList

class Bundler private constructor(b: Bundle?) {

    private val bundle: Bundle = if (b == null) Bundle() else Bundle(b)

    /**
     * Constructs a new, empty Bundle.
     */
    constructor() : this(null)

    /**
     * Inserts all mappings from the given Bundle into this Bundle.
     *
     * @param bundle a Bundle
     * @return this
     */
    fun putAll(bundle: Bundle): Bundler {
        this.bundle.putAll(bundle)
        return this
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a byte
     * @return this
     */
    fun putByte(key: String, value: Byte): Bundler {
        bundle.putByte(key, value)
        return this
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a char, or null
     * @return this
     */
    fun putChar(key: String, value: Char): Bundler {
        bundle.putChar(key, value)
        return this
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a short
     * @return this
     */
    fun putShort(key: String, value: Short): Bundler {
        bundle.putShort(key, value)
        return this
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a float
     * @return this
     */
    fun putFloat(key: String, value: Float): Bundler {
        bundle.putFloat(key, value)
        return this
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence, or null
     * @return this
     */
    fun putCharSequence(key: String, value: CharSequence): Bundler {
        bundle.putCharSequence(key, value)
        return this
    }

    /**
     * Inserts a Parcelable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Parcelable object, or null
     * @return this
     */
    fun putParcelable(key: String, value: Parcelable): Bundler {
        bundle.putParcelable(key, value)
        return this
    }

    /**
     * Inserts a Size value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Size object, or null
     * @return this
     */
    @TargetApi(21)
    fun putSize(key: String, value: Size): Bundler {
        bundle.putSize(key, value)
        return this
    }

    /**
     * Inserts a SizeF value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a SizeF object, or null
     * @return this
     */
    @TargetApi(21)
    fun putSizeF(key: String, value: SizeF): Bundler {
        bundle.putSizeF(key, value)
        return this
    }

    /**
     * Inserts an array of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an array of Parcelable objects, or null
     * @return this
     */
    fun putParcelableArray(key: String, value: Array<Parcelable>): Bundler {
        bundle.putParcelableArray(key, value)
        return this
    }

    /**
     * Inserts a List of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList of Parcelable objects, or null
     * @return this
     */
    fun putParcelableArrayList(
        key: String,
        value: ArrayList<out Parcelable>
    ): Bundler {
        bundle.putParcelableArrayList(key, value)
        return this
    }

    /**
     * Inserts a SparceArray of Parcelable values into the mapping of this
     * Bundle, replacing any existing value for the given key.  Either key
     * or value may be null.
     *
     * @param key   a String, or null
     * @param value a SparseArray of Parcelable objects, or null
     * @return this
     */
    fun putSparseParcelableArray(
        key: String,
        value: SparseArray<out Parcelable>
    ): Bundler {
        bundle.putSparseParcelableArray(key, value)
        return this
    }

    /**
     * Inserts an ArrayList<Integer> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<Integer> object, or null
     * @return this
    </Integer></Integer> */
    fun putIntegerArrayList(key: String, value: ArrayList<Int>): Bundler {
        bundle.putIntegerArrayList(key, value)
        return this
    }

    /**
     * Inserts an ArrayList<String> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<String> object, or null
     * @return this
    </String></String> */
    fun putStringArrayList(key: String, value: ArrayList<String>): Bundler {
        bundle.putStringArrayList(key, value)
        return this
    }

    /**
     * Inserts an ArrayList<CharSequence> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<CharSequence> object, or null
     * @return this
    </CharSequence></CharSequence> */
    @TargetApi(8)
    fun putCharSequenceArrayList(key: String, value: ArrayList<CharSequence>): Bundler {
        bundle.putCharSequenceArrayList(key, value)
        return this
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Serializable object, or null
     * @return this
     */
    fun putSerializable(key: String, value: Serializable): Bundler {
        bundle.putSerializable(key, value)
        return this
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a byte array object, or null
     * @return this
     */
    fun putByteArray(key: String, value: ByteArray): Bundler {
        bundle.putByteArray(key, value)
        return this
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a short array object, or null
     * @return this
     */
    fun putShortArray(key: String, value: ShortArray): Bundler {
        bundle.putShortArray(key, value)
        return this
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a char array object, or null
     * @return this
     */
    fun putCharArray(key: String, value: CharArray): Bundler {
        bundle.putCharArray(key, value)
        return this
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a float array object, or null
     * @return this
     */
    fun putFloatArray(key: String, value: FloatArray): Bundler {
        bundle.putFloatArray(key, value)
        return this
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence array object, or null
     * @return this
     */
    @TargetApi(8)
    fun putCharSequenceArray(key: String, value: Array<CharSequence>): Bundler {
        bundle.putCharSequenceArray(key, value)
        return this
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Bundle object, or null
     * @return this
     */
    fun putBundle(key: String, value: Bundle): Bundler {
        bundle.putBundle(key, value)
        return this
    }

    /**
     * Inserts an [android.os.IBinder] value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     *
     * You should be very careful when using this function.  In many
     * places where Bundles are used (such as inside of Intent objects), the Bundle
     * can live longer inside of another process than the process that had originally
     * created it.  In that case, the IBinder you supply here will become invalid
     * when your process goes away, and no longer usable, even if a new process is
     * created for you later on.
     *
     * @param key   a String, or null
     * @param value an IBinder object, or null
     * @return this
     */
    @TargetApi(18)
    fun putBinder(key: String, value: IBinder): Bundler {
        bundle.putBinder(key, value)
        return this
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Boolean, or null
     * @return this
     */
    fun putBoolean(key: String, value: Boolean): Bundler {
        bundle.putBoolean(key, value)
        return this
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value an int, or null
     * @return this
     */
    fun putInt(key: String, value: Int): Bundler {
        bundle.putInt(key, value)
        return this
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a long
     * @return this
     */
    fun putLong(key: String, value: Long): Bundler {
        bundle.putLong(key, value)
        return this
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a double
     * @return this
     */
    fun putDouble(key: String, value: Double): Bundler {
        bundle.putDouble(key, value)
        return this
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     * @return this
     */
    fun putString(key: String, value: String): Bundler {
        bundle.putString(key, value)
        return this
    }

    /**
     * Inserts a boolean array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean array object, or null
     * @return this
     */
    fun putBooleanArray(key: String, value: BooleanArray): Bundler {
        bundle.putBooleanArray(key, value)
        return this
    }

    /**
     * Inserts an int array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an int array object, or null
     * @return this
     */
    fun putIntArray(key: String, value: IntArray): Bundler {
        bundle.putIntArray(key, value)
        return this
    }

    /**
     * Inserts a long array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a long array object, or null
     * @return this
     */
    fun putLongArray(key: String, value: LongArray): Bundler {
        bundle.putLongArray(key, value)
        return this
    }

    /**
     * Inserts a double array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a double array object, or null
     * @return this
     */
    fun putDoubleArray(key: String, value: DoubleArray): Bundler {
        bundle.putDoubleArray(key, value)
        return this
    }

    /**
     * Inserts a String array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String array object, or null
     * @return this
     */
    fun putStringArray(key: String, value: Array<String>): Bundler {
        bundle.putStringArray(key, value)
        return this
    }

    /**
     * Get the bundle.
     *
     * @return a bundle
     */
    fun get(): Bundle {
        return bundle
    }

    /**
     * Set the argument of Fragment.
     *
     * @param fragment a fragment
     * @return a fragment
     */
    fun <T : Fragment> into(fragment: T): T {
        fragment.arguments = get()
        return fragment
    }

    companion object {

        /**
         * Constructs a Bundle containing a copy of the mappings from the given
         * Bundle.
         *
         * @param b a Bundle to be copied.
         */
        fun of(b: Bundle): Bundler {
            return Bundler(b)
        }
    }
}
