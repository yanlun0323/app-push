package com.xz.msg.push.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月23日 上午10:39:16
 */
public abstract class CollectionUtils {

	/**
	 * 快速构建Set集合
	 * @param t
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public static <T> Set<T> buildHashSet(@SuppressWarnings("unchecked") T... t) {
		Set<T> result = new HashSet<>();
		if(t != null){
			for (int i = 0; i < t.length; i++) {
				if(t[i] != null){//不允许NULL值存在
					result.add(t[i]);
				}
			}
		}
		return result;
	}

	public static boolean isEmpty(Collection<?> dataSource) {
		return null == dataSource || dataSource.isEmpty();
	}

	public static boolean isNotEmpty(Collection<?> dataSource) {
		return !isEmpty(dataSource);
	}

	/**
	 * 找到集合的第一个元素
	 * @param dataSource
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public static <T> T findFirst(Collection<T> dataSource) {
		return CollectionUtils.isNotEmpty(dataSource) ? dataSource.stream().findFirst().get() : null;
	}

	/**
	 * 找到第一个元素
	 * @param dataSource
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public static <T> T findFirst(List<T> dataSource) {
		return CollectionUtils.isNotEmpty(dataSource) ? dataSource.get(0) : null;
	}
	
	/**
	 * 集合拆分
	 * @param dataSource
	 * @return
	 * @author  作者:Yan,Email:yanlun0323@163.com
	 */
	public static <T> List<Collection<T>> split(Collection<T> dataSource, Integer size) {
		List<T> list = new ArrayList<>(dataSource);
		
		List<Collection<T>> splited = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dataSource)) {
			for (int i = 0; i < dataSource.size(); i = i + size) {
				splited.add(list.subList(i, Math.min(i + size, dataSource.size())));
			}
		}
		return splited;
	}
}
