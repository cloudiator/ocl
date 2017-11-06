package experiment;

import cloudiator.Cloud;
import cloudiator.CloudType;
import cloudiator.CloudiatorFactory;
import cloudiator.CloudiatorModel;
import cloudiator.Hardware;
import cloudiator.Image;
import cloudiator.Location;
import cloudiator.OSFamily;
import cloudiator.OperatingSystem;
import cloudiator.Price;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.cloudiator.ocl.ModelGenerator;
import org.cloudiator.ocl.PriceFunction;

public class ExperimentModelGenerator implements ModelGenerator {

  private static final CloudiatorModel CLOUDIATOR_MODEL = CloudiatorFactory.eINSTANCE
      .createCloudiatorModel();
  private static final CloudiatorFactory CLOUDIATOR_FACTORY = CloudiatorFactory.eINSTANCE;
  private static final PriceFunction PRICE_FUNCTION = new ExperimentModelPriceFunction();

  private static final int NR = 9;


  @Override
  public CloudiatorModel generateModel(String userId) {

    for (int i = 0; i < NR; i++) {
      Cloud cloud = CLOUDIATOR_FACTORY.createCloud();
      cloud.setId(String.valueOf(i));
      cloud.setName(String.valueOf(i));
      cloud.setType(CloudType.PUBLIC);

      LocationGenerator locationGenerator = new LocationGenerator(cloud);
      for (Location location : locationGenerator.get()) {
        ImageGenerator imageGenerator = new ImageGenerator(cloud, location);
        HardwareGenerator hardwareGenerator = new HardwareGenerator(cloud, location);

        for (Image image : imageGenerator.get()) {
          for (Hardware hardware : hardwareGenerator.get()) {
            cloud.getLocations().add(location);
            cloud.getHardwareList().add(hardware);
            cloud.getImages().add(image);

            Price price = CLOUDIATOR_FACTORY.createPrice();
            price.setHardware(hardware);
            price.setImage(image);
            price.setLocation(location);
            price.setPrice(PRICE_FUNCTION.calculatePricing(cloud, hardware, location, image));
            cloud.getPrices().add(price);
          }
        }
      }

      CLOUDIATOR_MODEL.getClouds().add(cloud);

    }

    return CLOUDIATOR_MODEL;
  }

  public static class LocationGenerator implements Supplier<List<Location>> {

    private final Cloud cloud;

    public static final Map<String, Double> OPTIONS = new HashMap<String, Double>() {{
      put("DE", 1.00);
      put("US", 1.01);
      put("CH", 1.02);
      put("AU", 1.03);
      put("IE", 1.04);
      put("JP", 1.05);
      put("BE", 1.06);
      put("SG", 1.07);
      put("GB", 1.08);
      put("ZA", 1.09);
      put("PL", 1.10);
      put("NO", 1.11);
      put("KR", 1.12);
      put("CA", 1.13);
      put("IN", 1.14);
    }};

    private LocationGenerator(Cloud cloud) {
      this.cloud = cloud;
    }

    @Override
    public List<Location> get() {

      List<Location> locations = new ArrayList<>();

      for (String country : OPTIONS.keySet()) {
        Location location = CLOUDIATOR_FACTORY.createLocation();
        location.setName(country);
        location.setProviderId(country);
        location.setId(cloud.getId() + ":" + location.getProviderId());
        location.setCountry(country);
        locations.add(location);
      }
      return locations;
    }
  }

  private static class HardwareGenerator implements Supplier<List<Hardware>> {

    private static final Set<Integer> CORE_OPTIONS = new HashSet<Integer>() {{
      add(2);
      add(4);
      add(8);
      add(16);
      add(32);
      add(64);
    }};
    private static final Set<Integer> RAM_OPTIONS = new HashSet<Integer>() {{
      add(512);
      add(1024);
      add(2048);
      add(4096);
      add(8192);
      add(16384);
    }};

    private final Cloud cloud;
    private final Location location;

    private HardwareGenerator(Cloud cloud, Location location) {
      this.cloud = cloud;
      this.location = location;
    }

    @Override
    public List<Hardware> get() {

      List<Hardware> hardwareList = new ArrayList<>();

      for (Integer cores : CORE_OPTIONS) {
        for (Integer ram : RAM_OPTIONS) {
          Hardware hardware = CLOUDIATOR_FACTORY.createHardware();
          hardware.setProviderId(String.format("%s cores - %s ram", cores, ram));
          hardware.setName(hardware.getProviderId());
          hardware.setId(
              cloud.getId() + ":" + location.getProviderId() + ":" + hardware.getProviderId());
          hardware.setCores(BigInteger.valueOf(cores));
          hardware.setRam(BigInteger.valueOf(ram));
          hardware.setLocation(location);
          hardwareList.add(hardware);
        }
      }

      return hardwareList;
    }
  }

  private static class ImageGenerator implements Supplier<List<Image>> {

    private final Cloud cloud;
    private final Location location;

    private static final Map<String, OSFamily> OPTIONS = new HashMap<String, OSFamily>() {{
      put("ubuntu", OSFamily.UBUNTU);
      put("debian", OSFamily.DEBIAN);
      put("rhel", OSFamily.RHEL);
    }};

    private ImageGenerator(Cloud cloud, Location location) {
      this.cloud = cloud;
      this.location = location;
    }

    @Override
    public List<Image> get() {

      List<Image> images = new ArrayList<>();

      for (Map.Entry<String, OSFamily> entry : OPTIONS.entrySet()) {
        Image image = CLOUDIATOR_FACTORY.createImage();
        image.setProviderId(entry.getKey());
        image.setName(entry.getKey());
        image.setId(cloud.getId() + ":" + location.getProviderId() + ":" + image.getProviderId());
        image.setLocation(location);
        OperatingSystem os = CLOUDIATOR_FACTORY.createOperatingSystem();
        os.setFamily(entry.getValue());
        image.setOperatingSystem(os);
        images.add(image);
      }

      return images;
    }
  }

}
