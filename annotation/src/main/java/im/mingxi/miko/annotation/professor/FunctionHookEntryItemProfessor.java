package im.mingxi.miko.annotation.professor;

import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import im.mingxi.miko.annotation.FunctionHookEntry;

@AutoService(Processor.class)
@SupportedAnnotationTypes("im.mingxi.miko.annotation.FunctionHookEntry")
public class FunctionHookEntryItemProfessor extends AbstractProcessor {
    private Filer mFiler;

    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (TypeElement element : set
        ) {
            var classNative = new StringBuilder("package im.mingxi.miko.annotation.result;\n");
            classNative.append("import java.util.ArrayList;\n");
            classNative.append("public class FuncHookResult {\n");
            classNative.append("public static ArrayList<Object> MMFuncHooks = new ArrayList<>();\n");
            classNative.append("public static ArrayList<Object> mobileQQFuncHooks = new ArrayList<>();\n");
            classNative.append("static {\n");
            for (Element anno : roundEnvironment.getElementsAnnotatedWith(element)) {
                boolean isNeedProvideFinder = false;
                String itemName = anno.toString();
                int itemType = anno.getAnnotation(FunctionHookEntry.class).itemType();
                System.out.println("Loading XPFunction: " + anno);
                switch (itemType) {
                    case FunctionHookEntry.COMMON_ITEM -> {
                        classNative.append("MMFuncHooks.add(new " + itemName + ");\n");
                        classNative.append("mobileQQFuncHooks.add(new " + itemName + ");\n");
                    }
                    case FunctionHookEntry.WECHAT_ITEM -> {
                        classNative.append("MMFuncHooks.add(new " + itemName + ");\n");
                    }
                    case FunctionHookEntry.QQ_ITEM, FunctionHookEntry.COMMON_QQ_TIM_ITEM,
                         FunctionHookEntry.TIM_ITEM -> {
                        classNative.append("mobileQQFuncHooks.add(new " + itemName + ");\n");
                    }
                    default -> {
                        throw new RuntimeException("Unknown item type: " + itemType + " in " + itemName);
                    }
                }
            }
            classNative.append("}\n");
            classNative.append("}");

        }
        return false;
    }
}
