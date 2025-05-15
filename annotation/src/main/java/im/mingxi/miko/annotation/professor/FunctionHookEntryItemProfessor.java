package im.mingxi.miko.annotation.professor;

import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
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
            var classNative = new StringBuilder("package im.mingxi.miko.annotation.result.FuncHookResult;\n");


        }
        return false;
    }
}
