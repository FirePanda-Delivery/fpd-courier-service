package ru.diplom.fpd.courier.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import ru.diplom.fpd.courier.dto.Coordinates;

@Data
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@ProtoFactory))
@Builder
@Proto
public final class ActiveCourierLocation {

    protected Long courierId;
    protected Coordinates currentCoordinates;
    protected Coordinates previousCoordinates;

}
