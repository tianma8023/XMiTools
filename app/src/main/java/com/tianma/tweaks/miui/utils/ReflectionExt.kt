package com.tianma.tweaks.miui.utils

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * desc: Extension functions about Xposed.
 * date: 2021/8/5
 */

// 获取指定类的 Field (包括父类的 & 非 public 的)
fun Class<*>.getExactFiled(fieldName: String): Field? = try {
    getDeclaredField(fieldName)
} catch (e: NoSuchFieldException) {
    superclass.getExactFiled(fieldName)
}

// 获取对象中指定 Field (包括父类的 & 非 public 的) 的值
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

// 获取指定类中的指定方法 (包含父类的 & 非 public 的)
fun Class<*>.getExactMethod(
    methodName: String,
    parameterTypes: Array<Class<*>> = arrayOf(),
): Method? {
    return try {
        ReflectionUtils.getDeclaredMethod(this, methodName, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        superclass.getExactMethod(methodName, parameterTypes)
    }
}

// 调用指定类or对象的指定方法 (包含父类方法 & 非 public 方法)
fun Any?.callMethod(
    methodName: String,
    parameterTypes: Array<Class<*>> = arrayOf(),
    args: Array<Any> = arrayOf()
): Any? {
    return when {
        this == null -> null
        this is Class<*> -> {
            try {
                val method = this.getExactMethod(methodName, parameterTypes)
                ReflectionUtils.invoke(method, null, args)
            } catch (e: NoSuchMethodException) {
                logE("", e)
                null
            }
        }
        else -> {
            try {
                val method = this::class.java.getExactMethod(methodName, parameterTypes)
                ReflectionUtils.invoke(method, this, *args)
            } catch (e: NoSuchMethodException) {
                logE("", e)
                null
            }
        }
    }
}


fun <T : Any> Any?.safeAs(): T? {
    return this as? T
}