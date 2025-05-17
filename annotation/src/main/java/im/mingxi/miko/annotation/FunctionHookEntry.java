package im.mingxi.miko.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface FunctionHookEntry {
    // 项目内部名称，可不填
    String itemName() default "NonName";

    // 项目类型，默认为通用项目
    int itemType() default 0;

    int COMMON_ITEM = 0;
    int WECHAT_ITEM = 1;
    int QQ_ITEM = 2;
    int TIM_ITEM = 3;
    int COMMON_QQ_TIM_ITEM = 4;
}
