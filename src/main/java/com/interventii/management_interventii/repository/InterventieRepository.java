/** Clasa pentru gestionarea interogarilor si operatiunilor de baza de date pentru interventii.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.repository;

import com.interventii.management_interventii.model.Interventie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface InterventieRepository extends JpaRepository<Interventie, Integer> {

    @Query(value = "SELECT ISNULL(MAX(interventie_id), 0) FROM dbo.interventii", nativeQuery = true)
    Integer findMaxId();

    @Modifying
    @Query(value = "INSERT INTO dbo.interventii (interventie_id, client_id, serviciu_id, data, adresa, status, categorie_id, descriere) " +
            "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)", nativeQuery = true)
    void insertWithId(Integer id, Integer clientId, Integer serviciuId, LocalDateTime data,
                      String adresa, String status, Integer categorieId, String descriere);

    @Modifying
    @Query(value = "INSERT INTO dbo.interventii (interventie_id, client_id, serviciu_id, data, adresa, status, categorie_id, descriere) " +
            "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)", nativeQuery = true)
    void insertWithFullDetails(Integer id, Integer clientId, Integer serviciuId, LocalDateTime data,
                               String adresa, String status, Integer categorieId, String descriere);

    // gaseste dupa status
    @Query("SELECT i FROM Interventie i WHERE i.status = :status ORDER BY i.data")
    List<Interventie> findByStatus(@Param("status") String status);

    // gaseste dupa client
    @Query("SELECT i FROM Interventie i WHERE i.clientId = :clientId")
    List<Interventie> findByClientId(@Param("clientId") Integer clientId);

    // sterge dupa client
    @Modifying
    @Query(value = "DELETE FROM dbo.interventii WHERE client_id = :clientId", nativeQuery = true)
    void deleteByClientId(@Param("clientId") Integer clientId);

    // sterge alocari angajați
    @Modifying
    @Query(value = "DELETE FROM dbo.alocare_angajati WHERE interventie_id = :interventieId", nativeQuery = true)
    void deleteAlocariAngajati(@Param("interventieId") Integer interventieId);

    // update status
    @Modifying
    @Query(value = "UPDATE dbo.interventii SET status = :status WHERE interventie_id = :id", nativeQuery = true)
    void updateStatus(@Param("id") Integer id, @Param("status") String status);

    // detalii intervenție
    @Query(value = """
        SELECT I.interventie_id, I.data, I.status, C.nume, C.prenume, C.telefon, C.email, S.denumire
        FROM dbo.interventii I
        JOIN dbo.clienti C ON I.client_id = C.client_id
        JOIN dbo.servicii S ON I.serviciu_id = S.serviciu_id
        WHERE I.interventie_id = :id
    """, nativeQuery = true)
    Map<String, Object> findInterventieDetails(@Param("id") Integer id);

    // costuri materiale/ intervenție
    @Query(value = """
        SELECT I.interventie_id, C.nume, C.prenume, S.denumire, I.data, I.status,
               ISNULL(SUM(IM.cantitate_utilizata * M.pret_achizitie), 0) as cost_total_materiale
        FROM dbo.interventii I
        JOIN dbo.clienti C ON I.client_id = C.client_id
        JOIN dbo.servicii S ON I.serviciu_id = S.serviciu_id
        LEFT JOIN dbo.interventie_materiale IM ON I.interventie_id = IM.interventie_id
        LEFT JOIN dbo.materiale M ON IM.material_id = M.material_id
        GROUP BY I.interventie_id, C.nume, C.prenume, S.denumire, I.data, I.status
        ORDER BY cost_total_materiale DESC
    """, nativeQuery = true)
    List<Map<String, Object>> getCosturiInterventii();

    // interventii cu cost peste medie
    @Query(value = """
        SELECT I.interventie_id, C.nume, C.prenume, S.denumire,
               SUM(IM.cantitate_utilizata * M.pret_achizitie) as cost_total
        FROM dbo.interventii I
        JOIN dbo.clienti C ON I.client_id = C.client_id
        JOIN dbo.servicii S ON I.serviciu_id = S.serviciu_id
        JOIN dbo.interventie_materiale IM ON I.interventie_id = IM.interventie_id
        JOIN dbo.materiale M ON IM.material_id = M.material_id
        GROUP BY I.interventie_id, C.nume, C.prenume, S.denumire
        HAVING SUM(IM.cantitate_utilizata * M.pret_achizitie) > (
            SELECT AVG(cost)
            FROM (
                SELECT SUM(IM2.cantitate_utilizata * M2.pret_achizitie) as cost
                FROM dbo.interventie_materiale IM2
                JOIN dbo.materiale M2 ON IM2.material_id = M2.material_id
                GROUP BY IM2.interventie_id
            ) AS costuri_medii
        )
        ORDER BY cost_total DESC
    """, nativeQuery = true)
    List<Map<String, Object>> getInterventiiPesteMedie();
}