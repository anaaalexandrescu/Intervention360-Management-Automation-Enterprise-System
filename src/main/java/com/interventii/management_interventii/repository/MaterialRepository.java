/** Clasa pentru gestionarea operatiunilor de baza de date referitoare la stocul de materiale.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.repository;

import com.interventii.management_interventii.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
}