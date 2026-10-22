package com.yourname.studyroom.web.app.custom.converter;

import com.yourname.studyroom.model.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/**
 * 自定义类型转换器工厂：数字字符串 -> BaseEnum 子类。
 * <p>
 * 用户端接口（如 /app/order/updateStatusById）以数字码传递枚举查询参数，
 * 而 Spring 默认只按枚举「名称」转换，缺少本工厂时会直接 400。
 * 后台模块已有同款转换器，此处补齐以保持两端一致。
 */
@Component
public class StringToBaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new Converter<String, T>() {
            @Override
            public T convert(String code) {
                if (code == null || code.trim().isEmpty()) {
                    return null;
                }
                Integer target;
                try {
                    target = Integer.valueOf(code.trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("code" + code + "非法");
                }
                for (T constant : targetType.getEnumConstants()) {
                    if (constant.getCode().equals(target)) {
                        return constant;
                    }
                }
                throw new IllegalArgumentException("code" + code + "非法");
            }
        };
    }
}
