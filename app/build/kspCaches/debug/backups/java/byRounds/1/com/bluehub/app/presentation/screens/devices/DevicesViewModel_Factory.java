package com.bluehub.app.presentation.screens.devices;

import com.bluehub.app.domain.usecase.ConnectDeviceUseCase;
import com.bluehub.app.domain.usecase.ScanDevicesUseCase;
import com.bluehub.app.domain.usecase.SendSerialCommandUseCase;
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
public final class DevicesViewModel_Factory implements Factory<DevicesViewModel> {
  private final Provider<ScanDevicesUseCase> scanDevicesUseCaseProvider;

  private final Provider<ConnectDeviceUseCase> connectDeviceUseCaseProvider;

  private final Provider<SendSerialCommandUseCase> sendSerialCommandUseCaseProvider;

  public DevicesViewModel_Factory(Provider<ScanDevicesUseCase> scanDevicesUseCaseProvider,
      Provider<ConnectDeviceUseCase> connectDeviceUseCaseProvider,
      Provider<SendSerialCommandUseCase> sendSerialCommandUseCaseProvider) {
    this.scanDevicesUseCaseProvider = scanDevicesUseCaseProvider;
    this.connectDeviceUseCaseProvider = connectDeviceUseCaseProvider;
    this.sendSerialCommandUseCaseProvider = sendSerialCommandUseCaseProvider;
  }

  @Override
  public DevicesViewModel get() {
    return newInstance(scanDevicesUseCaseProvider.get(), connectDeviceUseCaseProvider.get(), sendSerialCommandUseCaseProvider.get());
  }

  public static DevicesViewModel_Factory create(
      Provider<ScanDevicesUseCase> scanDevicesUseCaseProvider,
      Provider<ConnectDeviceUseCase> connectDeviceUseCaseProvider,
      Provider<SendSerialCommandUseCase> sendSerialCommandUseCaseProvider) {
    return new DevicesViewModel_Factory(scanDevicesUseCaseProvider, connectDeviceUseCaseProvider, sendSerialCommandUseCaseProvider);
  }

  public static DevicesViewModel newInstance(ScanDevicesUseCase scanDevicesUseCase,
      ConnectDeviceUseCase connectDeviceUseCase,
      SendSerialCommandUseCase sendSerialCommandUseCase) {
    return new DevicesViewModel(scanDevicesUseCase, connectDeviceUseCase, sendSerialCommandUseCase);
  }
}
