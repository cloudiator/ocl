/*
 * Copyright 2017 University of Ulm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudiator.matchmaking.domain;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public class IdRequirementImpl implements IdRequirement {

  private final String hardwareId;
  private final String locationId;
  private final String imageId;

  protected IdRequirementImpl(String hardwareId, String locationId, String imageId) {
    this.hardwareId = hardwareId;
    this.locationId = locationId;
    this.imageId = imageId;
  }

  @Override
  public String hardwareId() {
    return hardwareId;
  }

  @Override
  public String locationId() {
    return locationId;
  }

  @Override
  public String imageId() {
    return imageId;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("hardwareId", hardwareId)
        .add("locationId", locationId).add("imageId", imageId).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IdRequirementImpl that = (IdRequirementImpl) o;
    return Objects.equals(hardwareId, that.hardwareId) &&
        Objects.equals(locationId, that.locationId) &&
        Objects.equals(imageId, that.imageId);
  }

  @Override
  public int hashCode() {

    return Objects.hash(hardwareId, locationId, imageId);
  }
}
