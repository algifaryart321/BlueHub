package com.bluehub.app.data.local.preferences;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppSettingsDataStore_Factory implements Factory<AppSettingsDataStore> {
  private final Provider<Context> contextProvider;

  public AppSettingsDataStore_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppSettingsDataStore get() {
    return newInstance(contextProvider.get());
  }

  public static AppSettingsDataStore_Factory create(Provider<Context> contextProvider) {
    return new AppSettingsDataStore_Factory(contextProvider);
  }

  public static AppSettingsDataStore newInstance(Context context) {
    return new AppSettingsDataStore(context);
  }
}
