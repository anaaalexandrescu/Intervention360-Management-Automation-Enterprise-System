/** Clasa pentru gestionarea catalogului de servicii in baza de date.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.repository;

import com.interventii.management_interventii.model.Serviciu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ServiciuRepository extends JpaRepository<Serviciu, Integer> {

    // servicii populare
    @Query(value = """
        SELECT S.denumire, COUNT(I.interventie_id) as numar_interventii, 
               ISNULL(AVG(S.pret_standard), 0) as pret_mediu
        FROM dbo.servicii S
        LEFT JOIN dbo.interventii I ON S.serviciu_id = I.serviciu_id
        GROUP BY S.serviciu_id, S.denumire
        HAVING COUNT(I.interventie_id) >= :numarMinim
        ORDER BY numar_interventii DESC
    """, nativeQuery = true)
    List<Map<String, Object>> getServiciiPopulare(@Param("numarMinim") Integer numarMinim);
}