package ru.diplom.fpd.courier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.diplom.fpd.courier.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}