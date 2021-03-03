package com.tianma.tweaks.miui.xp.wrapper;

import androidx.annotation.Nullable;

import com.tianma.tweaks.miui.utils.XLog;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Xposed Wrapper Utils
 */
public class XposedWrapper {

    private static final HashMap<String, Method> methodCache = new HashMap<String, Method>();

    private XposedWrapper() {
    }

    public static Class<?> findClass(String className, ClassLoader classLoader) {
        try {
            return XposedHelpers.findClass(className, classLoader);
        } catch (Throwable t) {
            XLog.e("Class not found: %s", className);
            return null;
        }
    }

    public static XC_MethodHook.Unhook findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        try {
            return XposedHelpers.findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
        } catch (Throwable t) {
            XLog.e("Error in hook %s#%s", className, methodName, t);
            return null;
        }
    }

    public static XC_MethodHook.Unhook findAndHookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        try {
            return XposedHelpers.findAndHookMethod(clazz, methodName, parameterTypesAndCallback);
        } catch (Throwable t) {
            XLog.e("Error in hook %s#%s", clazz.getName(), methodName, t);
            return null;
        }
    }

    public static Set<XC_MethodHook.Unhook> hookAllConstructors(Class<?> hookClass, XC_MethodHook callback) {
        try {
            return XposedBridge.hookAllConstructors(hookClass, callback);
        } catch (Throwable t) {
            XLog.e("Error in hookAllConstructors: %s", hookClass.getName(), t);
            return null;
        }
    }

    public static XC_MethodHook.Unhook findAndHookConstructor(Class<?> clazz, Object... parameterTypesAndCallback) {
        try {
            return XposedHelpers.findAndHookConstructor(clazz, parameterTypesAndCallback);
        } catch (Throwable t) {
            XLog.e("Error in findAndHookConstructor: %s", clazz.getName(), t);
            return null;
        }
    }

    public static XC_MethodHook.Unhook findAndHookConstructor(String className, ClassLoader classLoader, Object... parameterTypesAndCallback) {
        try {
            return XposedHelpers.findAndHookConstructor(className, classLoader, parameterTypesAndCallback);
        } catch (Throwable t) {
            XLog.e("Error in findAndHookConstructor: %s", className, t);
            return null;
        }
    }

    // 通过方法名查找方法，模糊查询
    @Nullable
    public static Method findMethodByNameIfExists(String className, ClassLoader classLoader, String methodName) {
        return findMethodByNameIfExists(findClass(className, classLoader), methodName);
    }

    // 通过方法名查找方法，模糊查询
    public static Method findMethodByNameIfExists(Class<?> clazz, String methodName) {
        if (clazz == null) {
            return null;
        }
        String fullMethodName = clazz.getName() + '#' + methodName + "#fuzzyMatch";

        if (methodCache.containsKey(fullMethodName)) {
            Method method = methodCache.get(fullMethodName);
            if (method == null)
                throw new NoSuchMethodError(fullMethodName);
            return method;
        }

        Method fuzzyMatch = null;
        Class<?> clz = clazz;
        boolean considerPrivateMethods = true;
        do {
            for (Method method : clz.getDeclaredMethods()) {
                // don't consider private methods of superclasses
                if (!considerPrivateMethods && Modifier.isPrivate(method.getModifiers()))
                    continue;

                // compare name and parameters
                if (method.getName().equals(methodName)) {
                    // get accessible version of method
                    fuzzyMatch = method;
                    break;
                }
            }

            if (fuzzyMatch != null) {
                break;
            }

            considerPrivateMethods = false;
        } while ((clz = clz.getSuperclass()) != null);

        if (fuzzyMatch != null) {
            fuzzyMatch.setAccessible(true);
        }
        methodCache.put(fullMethodName, fuzzyMatch);
        return fuzzyMatch;
    }

    private static String getParametersString(Class<?>... clazzes) {
        StringBuilder sb = new StringBuilder("(");
        boolean first = true;
        for (Class<?> clazz : clazzes) {
            if (first)
                first = false;
            else
                sb.append(",");

            if (clazz != null)
                sb.append(clazz.getCanonicalName());
            else
                sb.append("null");
        }
        sb.append(")");
        return sb.toString();
    }

}
