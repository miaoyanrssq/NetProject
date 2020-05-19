package cn.zgy.net.utils;


import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型 - 工具类
 */
public final class GenericUtils {

    /**
     * 获取类申明的泛型
     *
     * @param clazz 目标类
     * @return 返回泛型对应type {@link Type}, 获取失败返回null.
     */
    public static @Nullable
    Type getGenericType(Class clazz) {
        while (clazz != Object.class) {
            Type type;
            Type[] interfaces = clazz.getGenericInterfaces();
            if (interfaces != null && interfaces.length > 0) { // 接口上的实际类型参数
                type = interfaces[0];
            } else { // 类/抽象类上的实际类型参数
                type = clazz.getGenericSuperclass();
            }
            if (type instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) type).getActualTypeArguments();
                if (args[0] instanceof Class) {
                    return args[0];
                } else if (args[0] instanceof ParameterizedType) {
                    return args[0];
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

}
