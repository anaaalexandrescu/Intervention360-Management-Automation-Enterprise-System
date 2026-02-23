/** Clasa pentru modelarea tipurilor de servicii oferite si a preturilor standard.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicii", schema = "dbo")
public class Serviciu {

    @Id
    @Column(name = "serviciu_id")
    private Integer id;

    @Column(name = "denumire")
    private String denumire;

    @Column(name = "pret_standard")
    private Double pretStandard;

    public Serviciu() {}

    // getters su setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDenumire() { return denumire; }
    public void setDenumire(String denumire) { this.denumire = denumire; }

    public Double getPretStandard() { return pretStandard; }
    public void setPretStandard(Double pretStandard) { this.pretStandard = pretStandard; }
}
