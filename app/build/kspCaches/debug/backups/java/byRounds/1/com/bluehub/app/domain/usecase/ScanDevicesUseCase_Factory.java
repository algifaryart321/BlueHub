package com.bluehub.app.domain.usecase;

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
public final class ScanDevicesUseCase_Factory implements Factory<ScanDevicesUseCase> {
  private final Provider<BluetoothRepository> repositoryProvider;

  public ScanDevicesUseCase_Factory(Provider<BluetoothRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ScanDevicesUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ScanDevicesUseCase_Factory create(
      Provider<BluetoothRepository> repositoryProvider) {
    return new ScanDevicesUseCase_Factory(repositoryProvider);
  }

  public static ScanDevicesUseCase newInstance(BluetoothRepository repository) {
    return new ScanDevicesUseCase(repository);
  }
}
