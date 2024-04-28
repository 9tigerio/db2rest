package com.homihq.db2rest.mongo.rsql.visitor;

import com.homihq.db2rest.mongo.rsql.argconverters.EntityFieldTypeConverter;
import com.homihq.db2rest.mongo.rsql.argconverters.FieldSpecificConverter;
import com.homihq.db2rest.mongo.rsql.argconverters.NoOpConverter;
import com.homihq.db2rest.mongo.rsql.argconverters.OperatorSpecificConverter;
import com.homihq.db2rest.mongo.rsql.argconverters.StringToQueryValueConverter;
import com.homihq.db2rest.mongo.rsql.operator.Operator;
import com.homihq.db2rest.mongo.rsql.structs.ConversionInfo;
import com.homihq.db2rest.mongo.utils.LazyUtils;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
@Slf4j
public class ComparisonToCriteriaConverter {

    private final List<StringToQueryValueConverter> converters;

    public ComparisonToCriteriaConverter(ConversionService conversionService, MongoMappingContext mappingContext,
                                         FieldSpecificConverter... fieldSpecificConverters) {
        this.converters = new ArrayList<>();
        converters.add(new OperatorSpecificConverter());
        converters.addAll(List.of(fieldSpecificConverters));
        converters.add(new EntityFieldTypeConverter(conversionService, mappingContext));
        converters.add(new NoOpConverter());
    }

    public Criteria asCriteria(ComparisonNode node, Class<?> targetEntityClass) {
        Operator operator = Operator.toOperator(node.getOperator());
        List<Object> arguments = mapArgumentsToAppropriateTypes(operator, node, targetEntityClass);
        return makeCriteria(node.getSelector(), operator, arguments);
    }

    private List<Object> mapArgumentsToAppropriateTypes(Operator operator, ComparisonNode node, Class<?> targetEntityClass) {
        return node.getArguments().stream()
                .map(arg -> convert(new ConversionInfo(node.getSelector(), arg, targetEntityClass, operator)))
                .toList();
    }

    private Object convert(ConversionInfo conversionInfo) {
        return LazyUtils.firstThatReturnsNonNull(converters.stream()
                .map(converter -> converter.convert(conversionInfo))
                .toList());
    }

    private static Criteria makeCriteria(String selector, Operator operator, List<Object> arguments) {
        return switch (operator) {
            case EQUAL -> where(selector).is(getFirst(operator, arguments));
            case NOT_EQUAL -> where(selector).ne(getFirst(operator, arguments));
            case GREATER_THAN -> where(selector).gt(getFirst(operator, arguments));
            case GREATER_THAN_OR_EQUAL -> where(selector).gte(getFirst(operator, arguments));
            case LESS_THAN -> where(selector).lt(getFirst(operator, arguments));
            case LESS_THAN_OR_EQUAL -> where(selector).lte(getFirst(operator, arguments));
            case REGEX -> where(selector).regex((String) getFirst(operator, arguments));
            case EXISTS -> where(selector).exists((Boolean) getFirst(operator, arguments));
            case IN -> where(selector).in(arguments);
            case NOT_IN -> where(selector).nin(arguments);
        };
    }

    private static Object getFirst(Operator operator, List<Object> arguments) {
        if (arguments != null && arguments.size() == 1) {
            return arguments.getFirst();
        } else {
            throw new UnsupportedOperationException("You cannot perform the query operation " + operator.name()
                    + " with anything except a single value.");
        }
    }

}