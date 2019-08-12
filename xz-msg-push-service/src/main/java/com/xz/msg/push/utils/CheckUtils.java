package com.xz.msg.push.utils;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.xz.msg.push.exception.CommonErrCode;
import com.xz.msg.push.exception.CommonException;

/**
 * @author 作者:Yan,Email:yanlun0323@163.com
 * @version 创建时间:2017年5月23日 下午9:43:58
 */
public abstract class CheckUtils {

	public static Pattern TAGS_OR_ALIAS_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\u4e00-\\u9fa5@!#$&*+=.|￥]+$");

	public static void validateTags(Set<String> tags, Integer maxLength) {
		if (!CollectionUtils.isEmpty(tags)) {
			Boolean valid = tags.stream().anyMatch(tag -> {
				try {
					return TAGS_OR_ALIAS_PATTERN.matcher(tag).matches()
							&& new String(tag.getBytes(), "utf8").length() <= maxLength;
				} catch (UnsupportedEncodingException e) {
					return Boolean.FALSE;
				}
			});
			if (!valid) {
				throw new CommonException(CommonErrCode.ARGS_INVALID,
						"参数非法[有效的 tag 组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|￥],[限制：每一个 tag 的长度限制为 40 字节。（判断长度需采用UTF-8编码）]");
			}
		}
	}

	public static void validateAlias(String alias, Integer maxLength) {
		if (StringUtils.isNotBlank(alias)) {
			Boolean valid = Boolean.FALSE;
			try {
				valid = TAGS_OR_ALIAS_PATTERN.matcher(alias).matches()
						&& new String(alias.getBytes(), "utf8").length() <= maxLength;
			} catch (UnsupportedEncodingException e) {
				valid = Boolean.FALSE;
			}

			if (!valid) {
				throw new CommonException(CommonErrCode.ARGS_INVALID,
						"参数非法[有效的 alias 组成：字母（区分大小写）、数字、下划线、汉字、特殊字符@!#$&*+=.|￥],[限制：每一个 alias 的长度限制为 40 字节。（判断长度需采用UTF-8编码）]");
			}
		}
	}
}
