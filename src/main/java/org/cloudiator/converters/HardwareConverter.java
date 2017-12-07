package org.cloudiator.converters;

import cloudiator.CloudiatorFactory;
import cloudiator.CloudiatorPackage;
import cloudiator.Hardware;
import de.uniulm.omi.cloudiator.util.TwoWayConverter;
import java.math.BigInteger;
import org.cloudiator.messages.entities.IaasEntities;
import org.cloudiator.messages.entities.IaasEntities.HardwareFlavor;
import org.cloudiator.messages.entities.IaasEntities.HardwareFlavor.Builder;

public class HardwareConverter implements TwoWayConverter<IaasEntities.HardwareFlavor, Hardware> {

  private static final LocationConverter LOCATION_CONVERTER = new LocationConverter();
  private static final CloudiatorFactory CLOUDIATOR_FACTORY = CloudiatorPackage.eINSTANCE
      .getCloudiatorFactory();

  @Override
  public HardwareFlavor applyBack(Hardware hardware) {

    final Builder builder = HardwareFlavor.newBuilder().setCores(hardware.getCores().intValue())
        .setId(hardware.getId()).setProviderId(hardware.getProviderId())
        .setRam(hardware.getRam().intValue());
    if (hardware.getLocation() != null) {
      builder.setLocation(LOCATION_CONVERTER.applyBack(hardware.getLocation()));
    }
    if (hardware.getDisk() != null) {
      builder.setDisk(
          hardware.getDisk());
    }
    return builder.build();
  }

  @Override
  public Hardware apply(HardwareFlavor hardwareFlavor) {
    final Hardware hardware = CLOUDIATOR_FACTORY.createHardware();
    if (hardwareFlavor.hasLocation()) {
      hardware.setLocation(LOCATION_CONVERTER.apply(hardwareFlavor.getLocation()));
    }
    hardware.setProviderId(hardwareFlavor.getProviderId());
    hardware.setName(hardwareFlavor.getName());
    hardware.setId(hardwareFlavor.getId());
    hardware.setCores(BigInteger.valueOf(hardwareFlavor.getCores()));
    hardware.setRam(BigInteger.valueOf(hardwareFlavor.getRam()));
    hardware.setDisk(hardwareFlavor.getDisk());

    return hardware;
  }
}