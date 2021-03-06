package com.dragon.study.benchmark.utils.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Created by dragon on 16/4/1.
 */
public abstract class AbstractAdviceTransformer implements AgentBuilder.Transformer {


  protected abstract Class<?> builderAdviceTransform();

  protected abstract ElementMatcher<? super MethodDescription> description(
      ClassFileLocator.Compound compound);

  protected List<RhllorDynamicValue<?>> getDynamicValues() {
    return Collections.emptyList();
  }

  @Override
  public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
      TypeDescription typeDescription, ClassLoader classLoader) {

    ClassFileLocator.Compound compound = new ClassFileLocator.Compound(
        ClassFileLocator.ForClassLoader.of(classLoader),
        ClassFileLocator.ForClassLoader.ofClassPath());

    AsmVisitorWrapper.ForDeclaredMethods advice = registerDynamicValues()
        .to(builderAdviceTransform()).on(description(compound));

    return builder.visit(advice);
  }

  private Advice.WithCustomMapping registerDynamicValues() {
    List<RhllorDynamicValue<?>> dynamicValues = getDynamicValues();
    Advice.WithCustomMapping withCustomMapping = Advice.withCustomMapping();
    for (RhllorDynamicValue dynamicValue : dynamicValues) {
      withCustomMapping = withCustomMapping.bind(dynamicValue.getAnnotationClass(), dynamicValue);
    }
    return withCustomMapping;
  }

  public abstract static class RhllorDynamicValue<T extends Annotation> implements Advice.DynamicValue<T> {
    public abstract Class<T> getAnnotationClass();
  }

}
