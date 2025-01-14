/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.amf.impl.model;

import static java.util.Arrays.asList;
import static org.mule.amf.impl.model.MediaType.APPLICATION_YAML;
import static org.mule.amf.impl.model.MediaType.getMimeTypeForValue;

import org.mule.amf.impl.exceptions.UnsupportedSchemaException;
import org.mule.apikit.model.QueryString;
import org.mule.apikit.model.parameter.Parameter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import amf.client.model.domain.AnyShape;
import amf.client.model.domain.ArrayShape;
import amf.client.model.domain.NodeShape;
import amf.client.model.domain.PropertyShape;
import amf.client.model.domain.Shape;
import amf.client.validate.PayloadValidator;
import amf.client.validate.ValidationReport;

public class QueryStringImpl implements QueryString {

  private AnyShape schema;
  private Collection<String> scalarTypes;

  private final Map<String, Optional<PayloadValidator>> payloadValidatorMap = new HashMap<>();
  private final String defaultMediaType = APPLICATION_YAML;

  public QueryStringImpl(AnyShape anyShape) {
    this.schema = anyShape;

    final List<ScalarType> typeIds = asList(ScalarType.values());

    this.scalarTypes = typeIds.stream().map(ScalarType::getName).collect(Collectors.toList());
  }

  @Override
  public String getDefaultValue() {
    return schema.defaultValueStr().option().orElse(null);
  }

  @Override
  public boolean isArray() {
    return schema instanceof ArrayShape;
  }

  @Override
  public boolean validate(final String value) {
    return validatePayload(value).conforms();
  }

  private ValidationReport validatePayload(String value) {
    final String mimeType = getMimeTypeForValue(value);

    Optional<PayloadValidator> payloadValidator;
    if (!payloadValidatorMap.containsKey(mimeType)) {
      payloadValidator = schema.payloadValidator(mimeType);

      if (!payloadValidator.isPresent()) {
        payloadValidator = schema.payloadValidator(defaultMediaType);
      }

      payloadValidatorMap.put(mimeType, payloadValidator);
    } else {
      payloadValidator = payloadValidatorMap.get(mimeType);
    }

    if (payloadValidator.isPresent()) {
      try {
        return payloadValidator.get().validate(mimeType, value).get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException("Unexpected Error validating request", e);
      }
    } else {
      throw new RuntimeException("Unexpected Error validating request");
    }
  }

  @Override
  public boolean isScalar() {
    return scalarTypes.contains(schema.id());
  }

  @Override
  public boolean isFacetArray(String facet) {
    if (schema instanceof NodeShape) {
      for (PropertyShape type : ((NodeShape) schema).properties()) {
        if (facet.equals(type.name().value()))
          return type.range() instanceof ArrayShape;
      }
    }
    return false;
  }

  @Override
  public Map<String, Parameter> facets() {
    HashMap<String, Parameter> result = new HashMap<>();

    if (schema instanceof NodeShape) {
      for (PropertyShape type : ((NodeShape) schema).properties())
        result.put(type.name().value(), new ParameterImpl(type));
    }
    return result;
  }
}
