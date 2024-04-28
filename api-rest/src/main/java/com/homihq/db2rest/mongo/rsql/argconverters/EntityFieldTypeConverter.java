package com.homihq.db2rest.mongo.rsql.argconverters;

import com.homihq.db2rest.mongo.rsql.structs.ConversionInfo;
import com.homihq.db2rest.mongo.rsql.structs.Lazy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class EntityFieldTypeConverter implements StringToQueryValueConverter {

    private final ConversionService conversionService;
    private final MongoMappingContext mongoMappingContext;

    @Override
    public Lazy<Object> convert(ConversionInfo info) {
        return Lazy.fromFunc(() -> {

            PersistentPropertyPath<MongoPersistentProperty> property
                    = mongoMappingContext.getPersistentPropertyPath(info.pathToField(), info.targetEntityClass());

            MongoPersistentProperty leaf = Objects.requireNonNull(property.getLeafProperty(), "Leaf can't be null");

            Class<?> targetTypeDeterminedFromEntityField;

            if (leaf.isCollectionLike()) {
                targetTypeDeterminedFromEntityField = leaf.getComponentType();
            } else {
                targetTypeDeterminedFromEntityField = leaf.getType();
            }

            return convert(info, targetTypeDeterminedFromEntityField);
        });
    }


    private Object convert(ConversionInfo info, Class<?> targetType) {
        if (!conversionService.canConvert(info.argument().getClass(), targetType)) {
            throw new UnsupportedOperationException("Cannot convert " + info.argument() + "into type " + targetType.getSimpleName());
        }
        try {
            return conversionService.convert(info.argument(), targetType);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Cannot convert " + info.argument() + "into type " + targetType.getSimpleName());
        }
    }

}