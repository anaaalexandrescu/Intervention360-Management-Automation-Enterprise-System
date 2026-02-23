/** Clasa pentru modelarea entitatii Interventie si a detaliilor de programare.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interventii", schema = "dbo")
public class Interventie {

    @Id
    @Column(name = "interventie_id")
    private Integer id;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "serviciu_id")
    private Integer serviciuId;

    @Column(name = "data")
    private LocalDateTime data;

    @Column(name = "adresa")
    private String adresa;

    @Column(name = "status")
    private String status;

    @Column(name = "categorie_id")
    private Integer categorieId;

    @Column(name = "descriere")
    private String descriere;

    public Interventie() {}

    // getters su setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getServiciuId() { return serviciuId; }
    public void setServiciuId(Integer serviciuId) { this.serviciuId = serviciuId; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public String getAdresa() { return adresa; }
    public void setAdresa(String adresa) { this.adresa = adresa; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCategorieId() { return categorieId; }
    public void setCategorieId(Integer categorieId) { this.categorieId = categorieId; }

    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
}