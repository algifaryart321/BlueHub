package com.bluehub.app.presentation.screens.printer;

import com.bluehub.app.domain.usecase.ConnectDeviceUseCase;
import com.bluehub.app.domain.usecase.PrintThermalDataUseCase;
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
public final class PrinterViewModel_Factory implements Factory<PrinterViewModel> {
  private final Provider<PrintThermalDataUseCase> printThermalDataUseCaseProvider;

  private final Provider<ConnectDeviceUseCase> connectDeviceUseCaseProvider;

  public PrinterViewModel_Factory(Provider<PrintThermalDataUseCase> printThermalDataUseCaseProvider,
      Provider<ConnectDeviceUseCase> connectDeviceUseCaseProvider) {
    this.printThermalDataUseCaseProvider = printThermalDataUseCaseProvider;
    this.connectDeviceUseCaseProvider = connectDeviceUseCaseProvider;
  }

  @Override
  public PrinterViewModel get() {
    return newInstance(printThermalDataUseCaseProvider.get(), connectDeviceUseCaseProvider.get());
  }

  public static PrinterViewModel_Factory create(
      Provider<PrintThermalDataUseCase> printThermalDataUseCaseProvider,
      Provider<ConnectDeviceUseCase> connectDeviceUseCaseProvider) {
    return new PrinterViewModel_Factory(printThermalDataUseCaseProvider, connectDeviceUseCaseProvider);
  }

  public static PrinterViewModel newInstance(PrintThermalDataUseCase printThermalDataUseCase,
      ConnectDeviceUseCase connectDeviceUseCase) {
    return new PrinterViewModel(printThermalDataUseCase, connectDeviceUseCase);
  }
}
