package com.fphoenixcorneae.util

import java.lang.reflect.*
import java.util.*

/**
 * 反射工具类
 */
class ReflectUtil private constructor(
    private val type: Class<*>,
    private val `object`: Any? = type
) {
    /**
     * Create and initialize a new instance.
     *
     * @param args The args.
     * @return the single [ReflectUtil] instance
     */
    ///////////////////////////////////////////////////////////////////////////
    // newInstance
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Create and initialize a new instance.
     *
     * @return the single [ReflectUtil] instance
     */
    @JvmOverloads
    fun newInstance(vararg args: Any? = arrayOfNulls(0)): ReflectUtil? {
        val types = getArgsType(*args)
        return try {
            val constructor =
                type().getDeclaredConstructor(*types)
            newInstance(constructor, *args)
        } catch (e: NoSuchMethodException) {
            val list: MutableList<Constructor<*>> =
                ArrayList()
            for (constructor in type().declaredConstructors) {
                if (match(constructor.parameterTypes, types)) {
                    list.add(constructor)
                }
            }
            if (list.isEmpty()) {
                throw ReflectException(e)
            } else {
                sortConstructors(list)
                newInstance(list[0], *args)
            }
        }
    }

    private fun getArgsType(vararg args: Any?): Array<Class<*>> {
        val result: Array<Class<*>> = arrayOf(*args as Array<out Class<*>>)
        for (i in args.indices) {
            val value = args[i]
            result[i] =
                value.javaClass
        }
        return result
    }

    private fun sortConstructors(list: List<Constructor<*>>) {
        Collections.sort(
            list
        ) { o1: Constructor<*>, o2: Constructor<*> ->
            val types1 = o1.parameterTypes
            val types2 = o2.parameterTypes
            val len = types1.size
            for (i in 0 until len) {
                if (types1[i] != types2[i]) {
                    if (Objects.requireNonNull(wrapper(types1[i]))
                            .isAssignableFrom(
                                Objects.requireNonNull(
                                    wrapper(
                                        types2[i]
                                    )
                                )
                            )
                    ) {
                        return@sort 1
                    } else {
                        return@sort -1
                    }
                }
            }
            0
        }
    }

    private fun newInstance(
        constructor: Constructor<*>,
        vararg args: Any
    ): ReflectUtil {
        return try {
            ReflectUtil(
                constructor.declaringClass,
                accessible(constructor)!!.newInstance(*args)
            )
        } catch (e: Exception) {
            throw ReflectException(e)
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // field
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Get the field.
     *
     * @param name The name of field.
     * @return the single [ReflectUtil] instance
     */
    fun field(name: String): ReflectUtil {
        return try {
            val field = getField(name)
            ReflectUtil(field.type, field[`object`])
        } catch (e: IllegalAccessException) {
            throw ReflectException(e)
        }
    }

    /**
     * Set the field.
     *
     * @param name  The name of field.
     * @param value The value.
     * @return the single [ReflectUtil] instance
     */
    fun field(name: String, value: Any): ReflectUtil {
        return try {
            val field = getField(name)
            field[`object`] = unwrap(value)
            this
        } catch (e: Exception) {
            throw ReflectException(e)
        }
    }

    @Throws(IllegalAccessException::class)
    private fun getField(name: String): Field {
        val field = getAccessibleField(name)
        if (field.modifiers and Modifier.FINAL == Modifier.FINAL) {
            try {
                val modifiersField =
                    Field::class.java.getDeclaredField("modifiers")
                modifiersField.isAccessible = true
                modifiersField.setInt(
                    field,
                    field.modifiers and Modifier.FINAL.inv()
                )
            } catch (ignore: NoSuchFieldException) {
                // runs in android will happen
                field.isAccessible = true
            }
        }
        return field
    }

    private fun getAccessibleField(name: String): Field {
        var type: Class<*>? = type()
        return try {
            accessible(type!!.getField(name))!!
        } catch (e: NoSuchFieldException) {
            do {
                try {
                    return accessible(type!!.getDeclaredField(name))!!
                } catch (ignore: NoSuchFieldException) {
                }
                type = type!!.superclass
            } while (type != null)
            throw ReflectException(e)
        }
    }

    private fun unwrap(`object`: Any): Any {
        return if (`object` is ReflectUtil) {
            `object`.get<Any>()!!
        } else `object`
    }
    /**
     * Invoke the method.
     *
     * @param name The name of method.
     * @param args The args.
     * @return the single [ReflectUtil] instance
     * @throws ReflectException if reflect unsuccessfully
     */
    ///////////////////////////////////////////////////////////////////////////
    // method
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Invoke the method.
     *
     * @param name The name of method.
     * @return the single [ReflectUtil] instance
     * @throws ReflectException if reflect unsuccessfully
     */
    @JvmOverloads
    @Throws(ReflectException::class)
    fun method(
        name: String,
        vararg args: Any = arrayOf()
    ): ReflectUtil? {
        val types = getArgsType(*args)
        return try {
            val exactMethod = exactMethod(name, types)
            method(exactMethod, `object`, *args)
        } catch (e: NoSuchMethodException) {
            try {
                val similarMethod = similarMethod(name, types)
                method(similarMethod, `object`, *args)
            } catch (e1: NoSuchMethodException) {
                throw ReflectException(e1)
            }
        }
    }

    private fun method(
        method: Method,
        obj: Any?,
        vararg args: Any?
    ): ReflectUtil {
        return try {
            accessible(method)
            if (method.returnType == Void.TYPE) {
                method.invoke(obj, *args)
                reflect(obj)
            } else {
                reflect(method.invoke(obj, *args))
            }
        } catch (e: Exception) {
            throw ReflectException(e)
        }
    }

    @Throws(NoSuchMethodException::class)
    private fun exactMethod(
        name: String,
        types: Array<Class<*>>
    ): Method {
        var type: Class<*>? = type()
        return try {
            type!!.getMethod(name, *types)
        } catch (e: NoSuchMethodException) {
            do {
                try {
                    return type!!.getDeclaredMethod(name, *types)
                } catch (ignore: NoSuchMethodException) {
                }
                type = type!!.superclass
            } while (type != null)
            throw NoSuchMethodException()
        }
    }

    @Throws(NoSuchMethodException::class)
    private fun similarMethod(
        name: String,
        types: Array<Class<*>>
    ): Method {
        var type: Class<*>? = type()
        val methods: MutableList<Method> =
            ArrayList()
        for (method in type!!.methods) {
            if (isSimilarSignature(method, name, types)) {
                methods.add(method)
            }
        }
        if (methods.isNotEmpty()) {
            sortMethods(methods)
            return methods[0]
        }
        do {
            for (method in type!!.declaredMethods) {
                if (isSimilarSignature(method, name, types)) {
                    methods.add(method)
                }
            }
            if (methods.isNotEmpty()) {
                sortMethods(methods)
                return methods[0]
            }
            type = type.superclass
        } while (type != null)
        throw NoSuchMethodException(
            "No similar method " + name + " with params "
                    + types.contentToString() + " could be found on type " + type() + "."
        )
    }

    private fun sortMethods(methods: List<Method>) {
        Collections.sort(
            methods
        ) { o1: Method, o2: Method ->
            val types1 = o1.parameterTypes
            val types2 = o2.parameterTypes
            val len = types1.size
            for (i in 0 until len) {
                if (types1[i] != types2[i]) {
                    if (Objects.requireNonNull(wrapper(types1[i]))
                            .isAssignableFrom(
                                Objects.requireNonNull(
                                    wrapper(
                                        types2[i]
                                    )
                                )
                            )
                    ) {
                        return@sort 1
                    } else {
                        return@sort -1
                    }
                }
            }
            0
        }
    }

    private fun isSimilarSignature(
        possiblyMatchingMethod: Method,
        desiredMethodName: String,
        desiredParamTypes: Array<Class<*>>
    ): Boolean {
        return possiblyMatchingMethod.name == desiredMethodName && match(
            possiblyMatchingMethod.parameterTypes,
            desiredParamTypes
        )
    }

    private fun match(
        declaredTypes: Array<Class<*>>,
        actualTypes: Array<Class<*>>
    ): Boolean {
        return if (declaredTypes.size == actualTypes.size) {
            for (i in actualTypes.indices) {
                if (actualTypes[i] == NULL::class.java
                    || Objects.requireNonNull(wrapper(declaredTypes[i]))
                        .isAssignableFrom(Objects.requireNonNull(wrapper(actualTypes[i])))
                ) {
                    continue
                }
                return false
            }
            true
        } else {
            false
        }
    }

    private fun <T : AccessibleObject?> accessible(accessible: T?): T? {
        if (accessible == null) {
            return null
        }
        if (accessible is Member) {
            val member = accessible as Member
            if (Modifier.isPublic(member.modifiers)
                && Modifier.isPublic(
                    member.declaringClass.modifiers
                )
            ) {
                return accessible
            }
        }
        if (!accessible.isAccessible) {
            accessible.isAccessible = true
        }
        return accessible
    }
    ///////////////////////////////////////////////////////////////////////////
    // proxy
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Create a proxy for the wrapped object allowing to typesafely invoke
     * methods on it using a custom interface.
     *
     * @param proxyType The interface type that is implemented by the proxy.
     * @return a proxy for the wrapped object
     */
    fun <P> proxy(proxyType: Class<P>): P {
        val isMap = `object` is Map<*, *>
        val handler =
            kotlin.run {
                InvocationHandler { proxy: Any?, method: Method, args: Array<Any>? ->
                    val name = method.name
                    try {
                        reflect(`object`)
                            .method(name, *args!!)?.get<Any>()
                    } catch (e: ReflectException) {
                        if (isMap) {
                            val map =
                                `object` as MutableMap<String, Any>?
                            val length = args?.size ?: 0
                            when {
                                length == 0 && name.startsWith("get") -> {
                                    map!![property(
                                        name.substring(
                                            3
                                        )
                                    )]
                                }
                                length == 0 && name.startsWith("is") -> {
                                    map!![property(
                                        name.substring(
                                            2
                                        )
                                    )]
                                }
                                length == 1 && name.startsWith("set") -> {
                                    map!![property(name.substring(3))] = args!![0]
                                    null
                                }
                            }
                        }
                        throw e
                    }
                }
            }
        return Proxy.newProxyInstance(
            proxyType.classLoader, arrayOf<Class<*>>(proxyType),
            handler
        ) as P
    }

    private fun type(): Class<*> {
        return type
    }

    private fun wrapper(type: Class<*>): Class<*> {
        if (type.isPrimitive) {
            when {
                Boolean::class.javaPrimitiveType == type -> {
                    return Boolean::class.java
                }
                Int::class.javaPrimitiveType == type -> {
                    return Int::class.java
                }
                Long::class.javaPrimitiveType == type -> {
                    return Long::class.java
                }
                Short::class.javaPrimitiveType == type -> {
                    return Short::class.java
                }
                Byte::class.javaPrimitiveType == type -> {
                    return Byte::class.java
                }
                Double::class.javaPrimitiveType == type -> {
                    return Double::class.java
                }
                Float::class.javaPrimitiveType == type -> {
                    return Float::class.java
                }
                Char::class.javaPrimitiveType == type -> {
                    return Char::class.java
                }
                Void.TYPE == type -> {
                    return Void::class.java
                }
            }
        }
        return type
    }

    /**
     * Get the result.
     *
     * @param <T> The value type.
     * @return the result
    </T> */
    fun <T> get(): T? {
        return `object` as T?
    }

    override fun hashCode(): Int {
        return `object`.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        return obj is ReflectUtil && `object` == obj.get<Any>()
    }

    override fun toString(): String {
        return `object`.toString()
    }

    private class NULL
    class ReflectException : RuntimeException {
        constructor(message: String?) : super(message) {}
        constructor(message: String?, cause: Throwable?) : super(message, cause) {}
        constructor(cause: Throwable?) : super(cause) {}

        companion object {
            private const val serialVersionUID = 858774075258496016L
        }
    }

    companion object {
        ///////////////////////////////////////////////////////////////////////////
        // reflect
        ///////////////////////////////////////////////////////////////////////////
        /**
         * Reflect the class.
         *
         * @param className The name of class.
         * @return the single [ReflectUtil] instance
         * @throws ReflectException if reflect unsuccessfully
         */
        @Throws(ReflectException::class)
        fun reflect(className: String): ReflectUtil {
            return reflect(
                forName(
                    className
                )
            )
        }

        /**
         * Reflect the class.
         *
         * @param className   The name of class.
         * @param classLoader The loader of class.
         * @return the single [ReflectUtil] instance
         * @throws ReflectException if reflect unsuccessfully
         */
        @Throws(ReflectException::class)
        fun reflect(
            className: String,
            classLoader: ClassLoader
        ): ReflectUtil {
            return reflect(
                forName(
                    className,
                    classLoader
                )
            )
        }

        /**
         * Reflect the class.
         *
         * @param clazz The class.
         * @return the single [ReflectUtil] instance
         * @throws ReflectException if reflect unsuccessfully
         */
        @Throws(ReflectException::class)
        fun reflect(clazz: Class<*>): ReflectUtil {
            return ReflectUtil(clazz)
        }

        /**
         * Reflect the class.
         *
         * @param object The object.
         * @return the single [ReflectUtil] instance
         * @throws ReflectException if reflect unsuccessfully
         */
        @Throws(ReflectException::class)
        fun reflect(`object`: Any?): ReflectUtil {
            return ReflectUtil(
                `object`?.javaClass ?: Any::class.java,
                `object`
            )
        }

        private fun forName(className: String): Class<*> {
            return try {
                Class.forName(className)
            } catch (e: ClassNotFoundException) {
                throw ReflectException(e)
            }
        }

        private fun forName(
            name: String,
            classLoader: ClassLoader
        ): Class<*> {
            return try {
                Class.forName(name, true, classLoader)
            } catch (e: ClassNotFoundException) {
                throw ReflectException(e)
            }
        }

        /**
         * Get the POJO property name of an getter/setter
         */
        private fun property(string: String): String {
            return when (string.length) {
                0 -> {
                    ""
                }
                1 -> {
                    string.toLowerCase(Locale.getDefault())
                }
                else -> {
                    string.substring(0, 1).toLowerCase(Locale.getDefault()) + string.substring(1)
                }
            }
        }
    }

}