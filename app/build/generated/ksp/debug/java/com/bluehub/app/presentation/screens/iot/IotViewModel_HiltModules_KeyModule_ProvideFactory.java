package com.bluehub.app.presentation.screens.iot;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.internal.lifecycle.HiltViewModelMap.KeySet")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class IotViewModel_HiltModules_KeyModule_ProvideFactory implements Factory<Boolean> {
  @Override
  public Boolean get() {
    return provide();
  }

  public static IotViewModel_HiltModules_KeyModule_ProvideFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static boolean provide() {
    return IotViewModel_HiltModules.KeyModule.provide();
  }

  private static final class InstanceHolder {
    private static final IotViewModel_HiltModules_KeyModule_ProvideFactory INSTANCE = new IotViewModel_HiltModules_KeyModule_ProvideFactory();
  }
}
