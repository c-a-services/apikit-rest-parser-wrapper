/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.apikit.implv2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mule.apikit.common.ApiSyncUtils.isSyncProtocol;

import org.mule.apikit.ParserUtils;
import org.mule.apikit.implv2.loader.ApiSyncResourceLoader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Test;
import org.raml.v2.api.loader.ResourceLoader;

public class ParserV2UtilsTestCase {

  @Test
  public void chooseWhichParserToUseWithoutSystemProperty() {
    assertTrue(ParserV2Utils.useParserV2("#%RAML 1.0 this is an api definition"));
    assertFalse(ParserV2Utils.useParserV2("#%RAML 0.8 this is an api definition"));
  }

  @Test
  public void chooseWhichParserToUseWithSystemPropertyInTrue() {
    System.setProperty(ParserV2Utils.PARSER_V2_PROPERTY, "true");
    assertTrue(ParserV2Utils.useParserV2("#%RAML 0.8 this is an api definition"));
    assertTrue(ParserV2Utils.useParserV2("#%RAML 1.0 this is an api definition"));
  }

  @Test
  public void chooseWhichParserToUseWithSystemPropertyInFalse() {
    System.setProperty(ParserV2Utils.PARSER_V2_PROPERTY, "false");
    assertTrue(ParserV2Utils.useParserV2("#%RAML 1.0 this is an api definition"));
    assertFalse(ParserV2Utils.useParserV2("#%RAML 0.8 this is an api definition"));
  }

  @Test
  public void chooseWhichParserToUseWithSystemPropertyInANonBooleanValue() {
    System.setProperty(ParserV2Utils.PARSER_V2_PROPERTY, "non-boolean-value");
    assertTrue(ParserV2Utils.useParserV2("#%RAML 1.0 this is an api definition"));
    assertFalse(ParserV2Utils.useParserV2("#%RAML 0.8 this is an api definition"));
  }

  @Test
  public void findIncludesWithApiSyncAPI() throws URISyntaxException, IOException {
    System.setProperty(ParserV2Utils.PARSER_V2_PROPERTY, "non-boolean-value");
    ParserV2Utils.findIncludeNodes(new URI("resource::com.juani.test:raml-test:1.0.0:raml-test-1.0.0-raml.zip"),
                                   new ApiSyncResourceLoader("resource::com.juani.test:raml-test:1.0.0:raml-test-1.0.0-raml.zip"));
    int asda = 0;
  }

  @After
  public void after() {
    System.clearProperty(ParserV2Utils.PARSER_V2_PROPERTY);
  }
}
