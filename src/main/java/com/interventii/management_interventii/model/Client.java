/** Clasa pentru modelarea entitatii Client si a atributelor sale.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clienti", schema = "dbo")
public class Client {

    @Id
    @Column(name = "client_id")
    private Integer id;

    @Column(name = "nume")
    private String nume;

    @Column(name = "prenume")
    private String prenume;

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

    public Client() {}

    // getters su setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }

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

    public String getNumeComplet() {
        return nume + " " + prenume;
    }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
}