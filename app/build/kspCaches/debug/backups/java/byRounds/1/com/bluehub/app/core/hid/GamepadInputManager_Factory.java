package com.bluehub.app.core.hid;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class GamepadInputManager_Factory implements Factory<GamepadInputManager> {
  @Override
  public GamepadInputManager get() {
    return newInstance();
  }

  public static GamepadInputManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GamepadInputManager newInstance() {
    return new GamepadInputManager();
  }

  private static final class InstanceHolder {
    private static final GamepadInputManager_Factory INSTANCE = new GamepadInputManager_Factory();
  }
}
