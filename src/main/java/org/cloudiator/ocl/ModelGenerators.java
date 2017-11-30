package org.cloudiator.ocl;

import java.util.Optional;
import javax.annotation.Nullable;
import org.cloudiator.experiment.CloudHarmony;
import org.cloudiator.experiment.FileCachedModelGenerator;
import org.cloudiator.experiment.LargeModelGenerator;
import org.cloudiator.experiment.SmallExperimentModelGenerator;

public enum ModelGenerators {

  CLOUDHARMONY(CloudHarmony.class, FileCachedModelGenerator.class),
  SMALL(SmallExperimentModelGenerator.class, MemoryCachedModelGenerator.class),
  LARGE(LargeModelGenerator.class, MemoryCachedModelGenerator.class),
  DISCOVERY(DiscoveryBasedModelGenerator.class, MemoryCachedModelGenerator.class);

  private final Class<? extends ModelGenerator> modelGeneratorClass;
  @Nullable
  private final Class<? extends ModelGenerator> cacheClass;

  ModelGenerators(Class<? extends ModelGenerator> modelGeneratorClass) {
    this(modelGeneratorClass, null);
  }

  ModelGenerators(Class<? extends ModelGenerator> modelGeneratorClass,
      @Nullable Class<? extends ModelGenerator> cache) {
    this.modelGeneratorClass = modelGeneratorClass;
    this.cacheClass = cache;
  }

  public Class<? extends ModelGenerator> modelGeneratorClass() {
    return modelGeneratorClass;
  }

  public Optional<Class<? extends ModelGenerator>> cacheClass() {
    return Optional.ofNullable(cacheClass);
  }
}
