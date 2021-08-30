package com.tianma.tweaks.miui.utils

import java.lang.reflect.Field

/**
 * desc: Extension functions about Xposed.
 * date: 2021/8/5
 */

fun Class<*>.getExactFiled(fieldName: String): Field? = try {
    getDeclaredField(fieldName)
} catch (e: NoSuchFieldException) {
    superclass.getExactFiled(fieldName)
}

operator fun Any?.get(propertyName: String): Any? = when {
    this == null -> null
    this is Class<*> -> {
        try {
            this.getExactFiled(propertyName)?.apply {
                isAccessible = true
            }?.get(null)
        } catch (e: NoSuchFieldException) {
            null
        }
    }
    else -> {
        try {
            this::class.java.getExactFiled(propertyName)?.apply {
                isAccessible = true
            }?.get(this)
        } catch (e: NoSuchFieldException) {
            null
        }
    }
}

// 反射调用方法
//fun Any?.callMethod(methodName: String, vararg args: Any) = when {
//    this == null -> null
//    this is Class<*> -> {
//        val method = this.getMethod(methodName, *XposedHelpers.getParameterTypes(*args))
//        method.invoke(null, *args)
//    }
//    else -> {
//        val method = this::class.java.getMethod(methodName, *XposedHelpers.getParameterTypes(*args))
//        XLog.d("method = $method")
//        method.invoke(this, *args)
//    }
//}

fun Any?.callMethod(methodName: String, parameterTypes: Array<Class<*>> = arrayOf(), args: Array<Any> = arrayOf()): Any? {
    return when {
        this == null -> null
        this is Class<*> -> {
            val method = ReflectionUtils.getMethod(this, methodName, *parameterTypes)
            return ReflectionUtils.invoke(method, null, args)
        }
        else -> {
            val method = ReflectionUtils.getMethod(this::class.java, methodName, *parameterTypes)
            return ReflectionUtils.invoke(method, this, *args)
        }
    }
}


fun <T : Any> Any?.safeAs(): T? {
    return this as? T
}