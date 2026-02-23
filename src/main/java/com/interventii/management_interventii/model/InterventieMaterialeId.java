/** Clasa pentru definirea cheii compuse a entitatii InterventieMateriale.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.model;

import java.io.Serializable;
import java.util.Objects;

public class InterventieMaterialeId implements Serializable {
    private Integer interventieId;
    private Integer materialId;

    public InterventieMaterialeId() {}

    public InterventieMaterialeId(Integer interventieId, Integer materialId) {
        this.interventieId = interventieId;
        this.materialId = materialId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterventieMaterialeId that = (InterventieMaterialeId) o;
        return Objects.equals(interventieId, that.interventieId) &&
                Objects.equals(materialId, that.materialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interventieId, materialId);
    }
}