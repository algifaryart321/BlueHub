package com.bluehub.app.presentation.screens.gamepad;

import com.bluehub.app.core.hid.GamepadInputManager;
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
public final class GamepadViewModel_Factory implements Factory<GamepadViewModel> {
  private final Provider<GamepadInputManager> gamepadInputManagerProvider;

  public GamepadViewModel_Factory(Provider<GamepadInputManager> gamepadInputManagerProvider) {
    this.gamepadInputManagerProvider = gamepadInputManagerProvider;
  }

  @Override
  public GamepadViewModel get() {
    return newInstance(gamepadInputManagerProvider.get());
  }

  public static GamepadViewModel_Factory create(
      Provider<GamepadInputManager> gamepadInputManagerProvider) {
    return new GamepadViewModel_Factory(gamepadInputManagerProvider);
  }

  public static GamepadViewModel newInstance(GamepadInputManager gamepadInputManager) {
    return new GamepadViewModel(gamepadInputManager);
  }
}
