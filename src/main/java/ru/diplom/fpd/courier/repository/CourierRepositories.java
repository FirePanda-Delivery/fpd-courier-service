package ru.diplom.fpd.courier.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.diplom.fpd.courier.model.Courier;

public interface CourierRepositories extends JpaRepository<Courier, Long>, JpaSpecificationExecutor<Courier> {

    List<Courier> findByIsDeletedFalse();

    @Query("""
              SELECT DISTINCT c FROM Courier c
              LEFT JOIN Order o ON o.orderId = c.id
              WHERE c.isDeleted = FALSE AND c.city = ?1 AND c.status = 'ACTIVE' AND (o IS NULL OR o.onActive = FALSE)
              """)
    List<Courier> findAllActiveFreeByCity(String city);

    @Modifying
    @Query("UPDATE Courier c SET c.status = 'NOT_ACTIVE' WHERE c.id = ?1")
    void setNotActiveStatus(Long id);

    @Modifying
    @Query("UPDATE Courier c SET c.status = 'ACTIVE' WHERE c.id = ?1")
    void setActiveStatus(Long id);
}
