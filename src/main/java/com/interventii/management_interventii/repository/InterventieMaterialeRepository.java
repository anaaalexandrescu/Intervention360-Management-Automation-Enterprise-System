/** Clasa pentru gestionarea relatiei dintre interventii si materialele utilizate in baza de date.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.repository;

import com.interventii.management_interventii.model.InterventieMateriale;
import com.interventii.management_interventii.model.InterventieMaterialeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface InterventieMaterialeRepository extends JpaRepository<InterventieMateriale, InterventieMaterialeId> {

    @Modifying
    @Query(value = "DELETE FROM dbo.interventie_materiale WHERE interventie_id = :interventieId", nativeQuery = true)
    void deleteByInterventieId(@Param("interventieId") Integer interventieId);

    @Modifying
    @Query(value = "INSERT INTO dbo.interventie_materiale (interventie_id, material_id, cantitate_utilizata) " +
            "VALUES (:interventieId, :materialId, :cantitate)", nativeQuery = true)
    void insertMaterial(@Param("interventieId") Integer interventieId,
                        @Param("materialId") Integer materialId,
                        @Param("cantitate") Integer cantitate);

    @Query(value = """
        SELECT M.denumire, IM.cantitate_utilizata, M.pret_achizitie,
               (IM.cantitate_utilizata * M.pret_achizitie) as subtotal,
               M.unitate_masura
        FROM dbo.interventie_materiale IM
        JOIN dbo.materiale M ON IM.material_id = M.material_id
        WHERE IM.interventie_id = :interventieId
        ORDER BY M.denumire
    """, nativeQuery = true)
    List<Map<String, Object>> findMaterialeByInterventieId(@Param("interventieId") Integer interventieId);
}