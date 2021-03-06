package com.intellij.lang.javascript.flex.projectStructure.model;

import com.intellij.flex.model.bc.BuildConfigurationNature;
import com.intellij.flex.model.bc.OutputType;
import com.intellij.flex.model.bc.TargetPlatform;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * User: ksafonov
 */
public interface ModifiableFlexBuildConfiguration extends FlexBuildConfiguration {

  @NotNull
  @Override
  ModifiableDependencies getDependencies();

  @NotNull
  @Override
  ModifiableCompilerOptions getCompilerOptions();

  @NotNull
  @Override
  ModifiableAirDesktopPackagingOptions getAirDesktopPackagingOptions();

  @NotNull
  @Override
  ModifiableAndroidPackagingOptions getAndroidPackagingOptions();

  @NotNull
  @Override
  ModifiableIosPackagingOptions getIosPackagingOptions();

  void setTargetPlatform(@NotNull TargetPlatform targetPlatform);

  void setNature(BuildConfigurationNature nature);

  void setPureAs(boolean pureAs);

  void setOutputType(@NotNull OutputType outputType);

  void setOptimizeFor(@NotNull String optimizeFor);

  void setMainClass(@NotNull String mainClass);

  void setOutputFileName(@NotNull String outputFileName);

  void setOutputFolder(@NotNull String outputFolder);

  void setUseHtmlWrapper(boolean useHtmlWrapper);

  void setWrapperTemplatePath(@NotNull String wrapperTemplatePath);

  void setRLMs(Collection<RLMInfo> rlms);

  void setCssFilesToCompile(Collection<String> cssFilesToCompile);

  void setSkipCompile(boolean skipCompile);

  void setName(@NotNull String name);
}
