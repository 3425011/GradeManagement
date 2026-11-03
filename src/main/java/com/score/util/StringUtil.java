package com.score.util;

/**
 * 字符串工具类：空值判断与安全类型转换
 */
public class StringUtil {

    /** 判断字符串是否为空（null 或去除空格后为空） */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /** 判断字符串是否非空 */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /** 安全转换为 Integer，转换失败返回 null */
    public static Integer toInteger(String str) {
        if (isEmpty(str)) {
            return null;
        }
        try {
            return Integer.valueOf(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 安全转换为 Double，转换失败返回 null */
    public static Double toDouble(String str) {
        if (isEmpty(str)) {
            return null;
        }
        try {
            return Double.valueOf(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
