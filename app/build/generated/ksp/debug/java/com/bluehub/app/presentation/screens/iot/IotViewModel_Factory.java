package com.bluehub.app.presentation.screens.iot;

import com.bluehub.app.domain.repository.BluetoothRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class IotViewModel_Factory implements Factory<IotViewModel> {
  private final Provider<BluetoothRepository> repositoryProvider;

  public IotViewModel_Factory(Provider<BluetoothRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public IotViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static IotViewModel_Factory create(Provider<BluetoothRepository> repositoryProvider) {
    return new IotViewModel_Factory(repositoryProvider);
  }

  public static IotViewModel newInstance(BluetoothRepository repository) {
    return new IotViewModel(repository);
  }
}
