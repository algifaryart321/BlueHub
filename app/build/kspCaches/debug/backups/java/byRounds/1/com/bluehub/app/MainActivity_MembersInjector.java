package com.bluehub.app;

import com.bluehub.app.core.hid.GamepadInputManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<GamepadInputManager> gamepadInputManagerProvider;

  public MainActivity_MembersInjector(Provider<GamepadInputManager> gamepadInputManagerProvider) {
    this.gamepadInputManagerProvider = gamepadInputManagerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<GamepadInputManager> gamepadInputManagerProvider) {
    return new MainActivity_MembersInjector(gamepadInputManagerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectGamepadInputManager(instance, gamepadInputManagerProvider.get());
  }

  @InjectedFieldSignature("com.bluehub.app.MainActivity.gamepadInputManager")
  public static void injectGamepadInputManager(MainActivity instance,
      GamepadInputManager gamepadInputManager) {
    instance.gamepadInputManager = gamepadInputManager;
  }
}
