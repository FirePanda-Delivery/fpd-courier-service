package ru.diplom.fpd.courier.configuration;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;
import ru.diplom.fpd.courier.dto.Coordinates;
import ru.diplom.fpd.courier.model.cache.ActiveCourierLocation;

@ProtoSchema(
        schemaFileName = "library.proto",
        schemaFilePath = "proto/",
        includeClasses = {ActiveCourierLocation.class, Coordinates.class})
public interface ActiveCourierLocationSchemaBuilder extends GeneratedSchema {

}


