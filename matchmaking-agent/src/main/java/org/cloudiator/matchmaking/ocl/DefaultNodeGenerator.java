package org.cloudiator.matchmaking.ocl;

import static com.google.common.base.Preconditions.checkNotNull;

import cloudiator.Cloud;
import cloudiator.CloudiatorModel;
import cloudiator.Hardware;
import cloudiator.Image;
import cloudiator.Location;
import cloudiator.Price;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.cloudiator.matchmaking.ocl.DefaultNodeGenerator.PriceCache.PriceKey;
import org.cloudiator.matchmaking.ocl.NodeCandidate.NodeCandidateFactory;

public class DefaultNodeGenerator implements NodeGenerator {

  private static final PriceCache PRICE_CACHE = new PriceCache();
  private final NodeCandidateFactory nodeCandidateFactory;
  private final CloudiatorModel cloudiatorModel;

  public DefaultNodeGenerator(NodeCandidateFactory nodeCandidateFactory,
      CloudiatorModel cloudiatorModel) {
    this.nodeCandidateFactory = nodeCandidateFactory;
    this.cloudiatorModel = cloudiatorModel;
    if (!PRICE_CACHE.exists(cloudiatorModel)) {
      PRICE_CACHE.load(cloudiatorModel);
    }
  }

  private static boolean isValidCombination(Image image, Hardware hardware, Location location) {
    checkNotNull(hardware, "hardware is null");
    checkNotNull(image, "image is null");
    checkNotNull(location, "location is null");

    //we have a valid combination if the location is assignable
    //and is in the scope of the image and the hardware

    if (!location.isAssignable()) {
      //if the location is not assignable, the combination is always invalid
      return false;
    }

    final Set<String> locationScope = locationIds(location);

    if (image.getLocation() != null) {
      //we have to check the image scope
      if (!locationScope.contains(image.getLocation().getId())) {
        return false;
      }
    } else {
      image.setLocation(location);
    }

    if (hardware.getLocation() != null) {
      //we have to check the hardware scope
      if (!locationScope.contains(hardware.getLocation().getId())) {
        return false;
      }
    } else {
      hardware.setLocation(location);
    }

    return true;
  }

  private static Set<String> locationIds(Location location) {
    Set<String> ids = new HashSet<>();
    for (Location i = location; i != null; i = i.getParent()) {
      ids.add(i.getId());
    }
    return ids;
  }

  /*
   * (non-Javadoc)
   *
   * @see ocl.NodeGenerator#getPossibleNodes()
   */
  @Override
  public NodeCandidates getPossibleNodes() {
    Set<NodeCandidate> nodeCandidates = new HashSet<>();
    for (Cloud cloud : cloudiatorModel.getClouds()) {
      for (Image image : cloud.getImages()) {
        for (Hardware hardware : cloud.getHardwareList()) {
          for (Location location : cloud.getLocations()) {
            //check if valid combination
            if (isValidCombination(image, hardware, location)) {
              Double price = PRICE_CACHE
                  .retrieve(cloudiatorModel, PriceKey.of(cloud, image, hardware, location));
              if (price == null) {
                price = Double.MAX_VALUE;
              }
              nodeCandidates.add(nodeCandidateFactory.of(cloud, hardware, image, location, price));
            }
          }
        }
      }
    }
    System.out
        .println(String.format("%s generated all possible nodes: %s", this, nodeCandidates.size()));
    return NodeCandidates.of(nodeCandidates);
  }

  static class PriceCache {

    private Map<CloudiatorModel, Map<PriceKey, Double>> cache = new HashMap<>();

    private boolean exists(CloudiatorModel cloudiatorModel) {
      return cache.containsKey(cloudiatorModel);
    }

    private void load(CloudiatorModel cloudiatorModel) {
      Map<PriceKey, Double> prices = new HashMap<>();
      for (Cloud cloud : cloudiatorModel.getClouds()) {
        for (Price price : cloud.getPrices()) {
          prices.put(PriceKey.of(cloud, price), price.getPrice());
        }
      }
      cache.put(cloudiatorModel, prices);
    }

    private Double retrieve(CloudiatorModel cloudiatorModel, PriceKey priceKey) {
      return cache.get(cloudiatorModel).get(priceKey);
    }

    static class PriceKey {

      private final String cloudId;
      private final String imageId;
      private final String hardwareId;
      private final String location;

      private PriceKey(String cloudId, String imageId, String hardwareId, String location) {
        this.cloudId = cloudId;
        this.imageId = imageId;
        this.hardwareId = hardwareId;
        this.location = location;
      }

      static PriceKey of(Cloud cloud, Image image, Hardware hardware, Location location) {
        checkNotNull(cloud, "cloud is null");
        checkNotNull(image, "image is null");
        checkNotNull(hardware, "hardware is null");
        checkNotNull(location, "location is null");
        return new PriceKey(cloud.getId(), image.getId(), hardware.getId(), location.getId());
      }

      static PriceKey of(Cloud cloud, Price price) {
        checkNotNull(cloud, "cloud is null");
        checkNotNull(price, "price is null");
        return PriceKey.of(cloud, price.getImage(), price.getHardware(),
            price.getLocation());
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (!(o instanceof PriceKey)) {
          return false;
        }

        PriceKey priceKey = (PriceKey) o;

        if (!cloudId.equals(priceKey.cloudId)) {
          return false;
        }
        if (!imageId.equals(priceKey.imageId)) {
          return false;
        }
        if (!hardwareId.equals(priceKey.hardwareId)) {
          return false;
        }
        return location.equals(priceKey.location);
      }

      @Override
      public int hashCode() {
        int result = cloudId.hashCode();
        result = 31 * result + imageId.hashCode();
        result = 31 * result + hardwareId.hashCode();
        result = 31 * result + location.hashCode();
        return result;
      }
    }

  }

}