/*
 *  Copyright © 2020 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */
package io.cdap.directives.datamodel;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.RecipeException;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.datamodel.DataModelGlossary;
import io.cdap.wrangler.utils.AvroSchemaGlossary;
import org.apache.avro.Schema;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

/**
 * Tests {@link DataModelMapColumn}
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"unchecked", "unnecessary"})
public class DataModelMapColumnTest {

  @Rule
  public MockitoRule mockito = MockitoJUnit.rule().strictness(Strictness.LENIENT);

  private static final String SCHEMA = "{\n"
    + "    \"type\": \"record\",\n"
    + "    \"name\": \"TEST_DATA_MODEL\",\n"
    + "    \"namespace\": \"google.com.datamodels\",\n"
    + "    \"_revision\": \"1\",    \n"
    + "    \"fields\": [\n"
    + "        {\n"
    + "            \"name\": \"TEST_MODEL\",\n"
    + "            \"type\": [\n"
    + "                \"null\", {\n"
    + "                \"type\": \"record\",\n"
    + "                \"name\": \"TEST_MODEL\",\n"
    + "                \"namespace\": \"google.com.datamodels.Model\",\n"
    + "                \"fields\": [\n"
    + "                    {\n"
    + "                        \"name\": \"int_field\",\n"
    + "                        \"type\": [\"int\"]\n"
    + "                    },\n"
    + "                    {\n"
    + "                        \"name\": \"missing_field_type\",\n"
    + "                        \"type\": [\"null\"]\n"
    + "                    }\n"
    + "                ]}\n"
    + "            ]\n"
    + "        }\n"
    + "    ]\n"
    + "}";

  @Mock
  private AvroSchemaGlossary mockGlossary;

  @Before
  public void setup() throws Exception {
    // Configure the mock glossary
    DataModelMapColumn.setGlossary("http://test-url.com", mockGlossary);
    
    // Configure lenient stubbing for the mock glossary
    Mockito.lenient().when(mockGlossary.get(Mockito.anyString(), Mockito.anyLong()))
        .thenReturn(null);
  }

  @Test(expected = RecipeException.class)
  public void testInitialize_unknownDataModel_directiveException() throws Exception {
    // No need to set up the mock here as it's already set up in the @Before method
    String[] directives = new String[]{
      "data-model-map-column 'http://test-url.com' 'UNKNOWN_DATA_MODEL' 1 'TEST_MODEL' 'int_field' :dummy_col_1",
    };

    List<Row> rows = Arrays.asList(
      new Row("dummy_col_1", "1")
        .add("dummy_col_2", "2")
        .add("dummy_col_3", "3")
        .add("dummy_col_4", "4")
        .add("dummy_col_5", "5")
    );

    TestingRig.execute(directives, rows);
  }

  @Test(expected = RecipeException.class)
  public void testInitialize_unknownRevision_directiveException() throws Exception {
    // No need to set up the mock here as it's already set up in the @Before method
    String[] directives = new String[]{
      "data-model-map-column 'http://test-url.com' 'google.com.datamodels.TEST_DATA_MODEL' 0 'TEST_MODEL' "
        + "'int_field' :dummy_col_1",
    };

    List<Row> rows = Arrays.asList(
      new Row("dummy_col_1", "1")
        .add("dummy_col_2", "2")
        .add("dummy_col_3", "3")
        .add("dummy_col_4", "4")
        .add("dummy_col_5", "5")
    );

    TestingRig.execute(directives, rows);
  }

  @Test(expected = RecipeException.class)
  public void testInitialize_unknownModel_directiveException() throws Exception {
    Schema.Parser parser = new Schema.Parser().setValidate(false);
    Mockito.when(mockGlossary.get(Mockito.anyString(), Mockito.anyLong())).thenReturn(parser.parse(SCHEMA));

    String[] directives = new String[]{
      "data-model-map-column 'http://test-url.com' 'google.com.datamodels.TEST_DATA_MODEL' 1 'UNKNOWN_MODEL' "
        + "'int_field' :dummy_col_1",
    };

    List<Row> rows = Arrays.asList(
      new Row("dummy_col_1", "1")
        .add("dummy_col_2", "2")
        .add("dummy_col_3", "3")
        .add("dummy_col_4", "4")
        .add("dummy_col_5", "5")
    );

    TestingRig.execute(directives, rows);
  }

  @Test(expected = RecipeException.class)
  public void testInitialize_unknownTargetField_directiveException() throws Exception {
    Schema.Parser parser = new Schema.Parser().setValidate(false);
    Mockito.when(mockGlossary.get(Mockito.anyString(), Mockito.anyLong())).thenReturn(parser.parse(SCHEMA));

    String[] directives = new String[]{
      "data-model-map-column 'http://test-url.com' 'google.com.datamodels.TEST_DATA_MODEL' 1 'TEST_MODEL' "
        + "'_unknown_field' :dummy_col_1",
    };

    List<Row> rows = Arrays.asList(
      new Row("dummy_col_1", "1")
        .add("dummy_col_2", "2")
        .add("dummy_col_3", "3")
        .add("dummy_col_4", "4")
        .add("dummy_col_5", "5")
    );

    TestingRig.execute(directives, rows);
  }

  @Test(expected = RecipeException.class)
  public void testInitialize_targetFieldMissingType_directiveException() throws Exception {
    Schema.Parser parser = new Schema.Parser().setValidate(false);
    Mockito.when(mockGlossary.get(Mockito.anyString(), Mockito.anyLong())).thenReturn(parser.parse(SCHEMA));

    String[] directives = new String[]{
      "data-model-map-column 'http://test-url.com' 'google.com.datamodels.TEST_DATA_MODEL' 1 'TEST_MODEL' "
        + "'missing_field_type' :dummy_col_1",
    };

    List<Row> rows = Arrays.asList(
      new Row("dummy_col_1", "1")
        .add("dummy_col_2", "2")
        .add("dummy_col_3", "3")
        .add("dummy_col_4", "4")
        .add("dummy_col_5", "5")
    );

    TestingRig.execute(directives, rows);
  }

  @Test
  public void testExecute_row_successful() throws Exception {
    Schema.Parser parser = new Schema.Parser().setValidate(false);
    Mockito.when(mockGlossary.get(Mockito.anyString(), Mockito.anyLong())).thenReturn(parser.parse(SCHEMA));

    String[] directives = new String[]{
      "data-model-map-column 'http://test-url.com' 'google.com.datamodels.TEST_DATA_MODEL' 1 'TEST_MODEL' "
        + "'int_field' :dummy_col_1",
    };

    List<Row> rows = Arrays.asList(
      new Row("dummy_col_1", "1")
        .add("dummy_col_2", "2")
        .add("dummy_col_3", "3")
        .add("dummy_col_4", "4")
        .add("dummy_col_5", "5")
    );

    List<Row> results = TestingRig.execute(directives, rows);
    Assert.assertEquals(1, results.size());
    int columnIndex = results.get(0).find("int_field");
    Assert.assertNotEquals(-1, columnIndex);

    Object value = results.get(0).getValue(columnIndex);
    Assert.assertTrue(value instanceof Integer);
    Assert.assertEquals(1, ((Integer) value).intValue());
  }
}
