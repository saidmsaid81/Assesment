package com.said.assesment.repositories;

import com.said.assesment.models.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstitutionRepository extends JpaRepository<Institution, String> {

    Institution getInstitutionByNameIgnoreCase(String name);

    @Query(value = "SELECT EXISTS(SELECT * FROM institution WHERE lower(name) = lower(?1))", nativeQuery = true)
    boolean checkIfInstitutionExistsByName(String name);

    @Query(
            "SELECT institution FROM Institution institution WHERE lower(institution.name) LIKE lower(concat('%', ?1,'%'))"
    )
    List<Institution> search(String keyword);

    @Modifying
    @Query("UPDATE Institution institution SET institution.name = ?1 WHERE lower(institution.name) = lower(?2)")
    void updateInstitutionName(String newName, String oldName);

}
