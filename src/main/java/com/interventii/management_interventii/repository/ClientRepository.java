/** Clasa pentru gestionarea operatiunilor de persistenta a datelor clientilor.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.repository;

import com.interventii.management_interventii.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    @Query(value = "SELECT ISNULL(MAX(client_id), 0) FROM dbo.clienti", nativeQuery = true)
    Integer findMaxId();

    // gaseste client după email
    @Query("SELECT c FROM Client c WHERE c.email = :email")
    Optional<Client> findByEmail(String email);

    // clienti cu statistici
    @Query(value = """
        SELECT C.client_id, C.nume, C.prenume, C.email, COUNT(I.interventie_id) as nr_lucrari
        FROM dbo.clienti C
        LEFT JOIN dbo.interventii I ON C.client_id = I.client_id
        GROUP BY C.client_id, C.nume, C.prenume, C.email
        ORDER BY nr_lucrari DESC
    """, nativeQuery = true)
    List<Map<String, Object>> findClientiCuStatistici();
}