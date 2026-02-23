/** Clasa pentru modelarea entitatii Angajat si a calificarilor acestuia.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.model;

import jakarta.persistence.*;

@Entity
@Table(name = "angajati", schema = "dbo")
public class User {

    @Id
    @Column(name = "angajat_id")
    private Integer id;

    @Column(name = "nume")
    private String nume;

    @Column(name = "prenume")
    private String prenume;

    @Column(name = "cnp")
    private String cnp;

    @Column(name = "telefon")
    private String telefon;

    @Column(name = "email")
    private String email;

    @Column(name = "strada")
    private String strada;

    @Column(name = "numar")
    private String numar;

    @Column(name = "oras")
    private String oras;

    @Column(name = "judet")
    private String judet;

    @Column(name = "sex")
    private String sex;

    @Column(name = "calificare")
    private String calificare;

    @Column(name = "salariu_orar")
    private Double salariuOrar;

    @Column(name = "supervizor_id")
    private Integer supervizorId;

    @Column(name = "categorie_id")
    private Integer categorieId;

    public User() {}

    // getters su setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }

    public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStrada() { return strada; }
    public void setStrada(String strada) { this.strada = strada; }

    public String getNumar() { return numar; }
    public void setNumar(String numar) { this.numar = numar; }

    public String getOras() { return oras; }
    public void setOras(String oras) { this.oras = oras; }

    public String getJudet() { return judet; }
    public void setJudet(String judet) { this.judet = judet; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getCalificare() { return calificare; }
    public void setCalificare(String calificare) { this.calificare = calificare; }

    public Double getSalariuOrar() { return salariuOrar; }
    public void setSalariuOrar(Double salariuOrar) { this.salariuOrar = salariuOrar; }

    public Integer getSupervizorId() { return supervizorId; }
    public void setSupervizorId(Integer supervizorId) { this.supervizorId = supervizorId; }

    public Integer getCategorieId() { return categorieId; }
    public void setCategorieId(Integer categorieId) { this.categorieId = categorieId; }

    // metoda helper pentru nume complet
    public String getNumeComplet() {
        return nume + " " + prenume;
    }
}