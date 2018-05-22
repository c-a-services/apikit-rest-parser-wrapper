/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.amf.impl;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mule.raml.implv1.ParserV1Utils;
import org.mule.raml.interfaces.model.IRaml;
import org.mule.raml.interfaces.model.parameter.IParameter;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


public class CompatibilityRaml08TestCase {

  private static IRaml amf;
  private static IRaml raml;

  @BeforeClass
  public static void beforeClass() {

    amf = ParserApiUtils.build("sanity-08.raml");
    raml = buildRaml08("sanity-08.raml");

    assertNotNull(amf);
    assertNotNull(raml);
  }

  @Test
  public void baseUri() {
    final String ramlBaseUri = raml.getBaseUri();
    final String amfBaseUri = amf.getBaseUri();

    assertThat(ramlBaseUri, is(equalTo(amfBaseUri)));
  }

  @Ignore
  public void uri() {
    final String ramlUri = raml.getBaseUri();
    final String amfUri = amf.getBaseUri();
    assertThat(ramlUri, is(equalTo(amfUri)));
  }

  @Test
  public void version() {
    final String ramlVersion = raml.getVersion();
    final String amfVersion = amf.getVersion();
    assertThat(ramlVersion, is(equalTo(amfVersion)));
  }

  @Test
  public void baseUriParameters() {
    final Map<String, IParameter> ramlBaseUriParameters = raml.getBaseUriParameters();
    final Map<String, IParameter> amfBaseUriParameters = amf.getBaseUriParameters();

    assertThat(ramlBaseUriParameters.size(), is(amfBaseUriParameters.size()));

    ramlBaseUriParameters.forEach((k, v) -> {
      assertThat(amfBaseUriParameters.containsKey(k), is(true));
      assertEqual(v, amfBaseUriParameters.get(k));

    });
  }

  private static void assertEqual(final IParameter expected, final IParameter actual) {
    // TODO
    assertThat(expected.getDefaultValue(), is(equalTo(actual.getDefaultValue())));
  }

  private static IRaml buildRaml08(final String resource) {
    final URL url = ParserApiUtils.class.getResource(resource);
    final File file = new File(url.getFile());

    try {
      final FileReader fileReader = new FileReader(file);
      final String content = IOUtils.toString(fileReader);
      fileReader.close();

      String rootRamlName = file.getName();
      String ramlFolderPath = null;
      if (file.getParentFile() != null) {
        ramlFolderPath = file.getParentFile().getPath();
      }
      return ParserV1Utils.build(content, ramlFolderPath, rootRamlName);
    } catch (Exception e) {
      Assert.fail(e.getMessage());
    }
    return null;
  }

}