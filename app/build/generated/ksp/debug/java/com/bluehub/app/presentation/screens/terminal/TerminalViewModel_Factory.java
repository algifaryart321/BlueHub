package com.bluehub.app.presentation.screens.terminal;

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
public final class TerminalViewModel_Factory implements Factory<TerminalViewModel> {
  private final Provider<BluetoothRepository> repositoryProvider;

  public TerminalViewModel_Factory(Provider<BluetoothRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public TerminalViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static TerminalViewModel_Factory create(Provider<BluetoothRepository> repositoryProvider) {
    return new TerminalViewModel_Factory(repositoryProvider);
  }

  public static TerminalViewModel newInstance(BluetoothRepository repository) {
    return new TerminalViewModel(repository);
  }
}
