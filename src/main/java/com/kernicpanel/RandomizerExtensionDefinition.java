package com.kernicpanel;

import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class RandomizerExtensionDefinition extends ControllerExtensionDefinition {
  private static final UUID DRIVER_ID = UUID.fromString("5c4b743f-cc91-41cf-be42-092ecac4a64b");

  public RandomizerExtensionDefinition() {}

  @Override
  public String getName() {
    return "Randomizer";
  }

  @Override
  public String getAuthor() {
    return "kernicPanel";
  }

  @Override
  public String getVersion() {
    return "2.0";
  }

  @Override
  public UUID getId() {
    return DRIVER_ID;
  }

  @Override
  public String getHardwareVendor() {
    return "kernicPanel";
  }

  @Override
  public String getHardwareModel() {
    return "Randomizer";
  }

  @Override
  public int getRequiredAPIVersion() {
    return 17;
  }

  @Override
  public int getNumMidiInPorts() {
    return 0;
  }

  @Override
  public int getNumMidiOutPorts() {
    return 0;
  }

  @Override
  public void listAutoDetectionMidiPortNames(
      final AutoDetectionMidiPortNamesList list, final PlatformType platformType) {}

  @Override
  public RandomizerExtension createInstance(final ControllerHost host) {
    return new RandomizerExtension(this, host);
  }
}
