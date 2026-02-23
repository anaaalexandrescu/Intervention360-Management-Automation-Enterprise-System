/** Clasa pentru modelarea consumului de materiale per interventie.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.model;

import jakarta.persistence.*;

@Entity
@Table(name = "interventie_materiale", schema = "dbo")
@IdClass(InterventieMaterialeId.class)
public class InterventieMateriale {

    @Id
    @Column(name = "interventie_id")
    private Integer interventieId;

    @Id
    @Column(name = "material_id")
    private Integer materialId;

    @Column(name = "cantitate_utilizata")
    private Integer cantitateUtilizata;

    public InterventieMateriale() {}

    // getters su setters
    public Integer getInterventieId() { return interventieId; }
    public void setInterventieId(Integer interventieId) { this.interventieId = interventieId; }

    public Integer getMaterialId() { return materialId; }
    public void setMaterialId(Integer materialId) { this.materialId = materialId; }

    public Integer getCantitateUtilizata() { return cantitateUtilizata; }
    public void setCantitateUtilizata(Integer cantitateUtilizata) { this.cantitateUtilizata = cantitateUtilizata; }
}