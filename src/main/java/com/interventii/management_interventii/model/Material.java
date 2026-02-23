/** Clasa pentru modelarea entitatii Material si a costurilor de achizitie.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.model;

import jakarta.persistence.*;

@Entity
@Table(name = "materiale", schema = "dbo")
public class Material {

    @Id
    @Column(name = "material_id")
    private Integer id;

    @Column(name = "denumire")
    private String denumire;

    @Column(name = "pret_achizitie")
    private Double pretAchizitie;

    @Column(name = "unitate_masura")
    private String unitateMasura;

    public Material() {}

    // getters su setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDenumire() { return denumire; }
    public void setDenumire(String denumire) { this.denumire = denumire; }

    public Double getPretAchizitie() { return pretAchizitie; }
    public void setPretAchizitie(Double pretAchizitie) { this.pretAchizitie = pretAchizitie; }

    public String getUnitateMasura() { return unitateMasura; }
    public void setUnitateMasura(String unitateMasura) { this.unitateMasura = unitateMasura; }
}