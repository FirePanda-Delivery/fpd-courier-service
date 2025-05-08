package ru.diplom.fpd.courier.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.diplom.fpd.courier.model.Courier;
import ru.diplom.fpd.courier.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(Long id);

    List<Order> findAllByCourierIsNull();

    @Query("select o from Order o where o.courier.id = ?1 and o.onActive = true")
    Optional<Order> findActiveByCourierId(Long id);

    @Query("select (count(o) > 0) from Order o where o.courier.id = ?1 and o.onActive = true")
    boolean existActiveByCourierId(Long id);

}