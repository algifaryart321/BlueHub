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
public final class SendSerialCommandUseCase_Factory implements Factory<SendSerialCommandUseCase> {
  private final Provider<BluetoothRepository> repositoryProvider;

  public SendSerialCommandUseCase_Factory(Provider<BluetoothRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SendSerialCommandUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SendSerialCommandUseCase_Factory create(
      Provider<BluetoothRepository> repositoryProvider) {
    return new SendSerialCommandUseCase_Factory(repositoryProvider);
  }

  public static SendSerialCommandUseCase newInstance(BluetoothRepository repository) {
    return new SendSerialCommandUseCase(repository);
  }
}
