package cn.yeezi.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 集合字段提取工具类
 * 用于从集合中批量提取指定字段值，避免重复的Stream操作
 *
 * @author luoguoliang
 * @since 2025-09-15
 */
public class CollExtractUtil {

    /**
     * 从集合中提取指定字段的非空值集合
     *
     * @param collection 源集合
     * @param extractor  字段提取器（方法引用）
     * @param <T>        源对象类型
     * @param <R>        提取字段类型
     * @return 提取的非空字段值集合
     */
    public static <T, R> Set<R> extractNonNullSet(Collection<T> collection, Function<T, R> extractor) {
        if (CollUtil.isEmpty(collection)) {
            return Set.of();
        }
        return collection.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 从集合中提取Long类型字段的非空值集合
     * 针对ID字段的常用场景进行优化
     *
     * @param collection 源集合
     * @param extractor  字段提取器（方法引用）
     * @param <T>        源对象类型
     * @return 提取的非空Long字段值集合
     */
    public static <T> Set<Long> extractLongSet(Collection<T> collection, Function<T, Long> extractor) {
        return extractNonNullSet(collection, extractor);
    }

    /**
     * 从集合中提取Integer类型字段的非空值集合
     * 针对ID字段的常用场景进行优化
     *
     * @param collection 源集合
     * @param extractor  字段提取器（方法引用）
     * @param <T>        源对象类型
     * @return 提取的非空Long字段值集合
     */
    public static <T> Set<Integer> extractIntegerSet(Collection<T> collection, Function<T, Integer> extractor) {
        return extractNonNullSet(collection, extractor);
    }

    /**
     * 从集合中提取String类型字段的非空值集合
     *
     * @param collection 源集合
     * @param extractor  字段提取器（方法引用）
     * @param <T>        源对象类型
     * @return 提取的非空String字段值集合
     */
    public static <T> Set<String> extractStringSet(Collection<T> collection, Function<T, String> extractor) {
        if (CollUtil.isEmpty(collection)) {
            return Set.of();
        }
        return collection.stream()
                .map(extractor)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
    }

    /**
     * 从集合中提取Boolean类型字段的非空值集合
     *
     * @param collection 源集合
     * @param extractor  字段提取器（方法引用）
     * @param <T>        源对象类型
     * @return 提取的非空Boolean字段值集合
     */
    public static <T> Set<Boolean> extractBooleanSet(Collection<T> collection, Function<T, Boolean> extractor) {
        return extractNonNullSet(collection, extractor);
    }

    /**
     * 从集合中提取指定字段的非空值列表
     *
     * @param collection 源集合
     * @param extractor  字段提取器（方法引用）
     * @param <T>        源对象类型
     * @param <R>        提取字段类型
     * @return 提取的非空字段值列表
     */
    public static <T, R> List<R> extractNonNullList(Collection<T> collection, Function<T, R> extractor) {
        if (CollUtil.isEmpty(collection)) {
            return List.of();
        }
        return collection.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 从集合中提取Long类型字段的非空值列表
     * 针对ID字段的常用场景进行优化
     *
     * @param collection 源集合
     * @param extractor  字段提取器（方法引用）
     * @param <T>        源对象类型
     * @return 提取的非空Long字段值列表
     */
    public static <T> List<Long> extractLongList(Collection<T> collection, Function<T, Long> extractor) {
        return extractNonNullList(collection, extractor);
    }

    /**
     * 从集合中提取String类型字段的非空值列表
     *
     * @param collection 源集合
     * @param extractor  字段提取器（方法引用）
     * @param <T>        源对象类型
     * @return 提取的非空String字段值列表
     */
    public static <T> List<String> extractStringList(Collection<T> collection, Function<T, String> extractor) {
        if (CollUtil.isEmpty(collection)) {
            return List.of();
        }
        return collection.stream()
                .map(extractor)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }

    /**
     * 从集合中提取Boolean类型字段的非空值列表
     *
     * @param collection 源集合
     * @param extractor  字段提取器（方法引用）
     * @param <T>        源对象类型
     * @return 提取的非空Boolean字段值列表
     */
    public static <T> List<Boolean> extractBooleanList(Collection<T> collection, Function<T, Boolean> extractor) {
        return extractNonNullList(collection, extractor);
    }
}