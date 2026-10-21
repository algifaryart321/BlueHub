package com.bluehub.app.di;

import com.bluehub.app.data.local.db.AppDatabase;
import com.bluehub.app.data.local.db.TelemetryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class AppModule_ProvideTelemetryDaoFactory implements Factory<TelemetryDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideTelemetryDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public TelemetryDao get() {
    return provideTelemetryDao(databaseProvider.get());
  }

  public static AppModule_ProvideTelemetryDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideTelemetryDaoFactory(databaseProvider);
  }

  public static TelemetryDao provideTelemetryDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTelemetryDao(database));
  }
}
