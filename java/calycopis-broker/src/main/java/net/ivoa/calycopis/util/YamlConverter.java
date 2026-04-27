/*
 *
 * <meta:header>
 *   <meta:licence>
 *     Copyright (C) 2024 University of Manchester.
 *
 *     This information is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This information is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   </meta:licence>
 * </meta:header>
 *
 * https://yaml.org/spec/1.2.2/#tags
 * https://stackoverflow.com/questions/74743065/unable-to-remove-tag-when-serializing-object-to-yaml-using-jackson-dataformat-ya
 * https://stackoverflow.com/a/74747017
 *
 */

package net.ivoa.calycopis.util;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature ;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class YamlConverter extends AbstractJackson2HttpMessageConverter {

  YamlConverter() {
    super(
        new ObjectMapper(
            new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID)
                )
            .setSerializationInclusion(
                Include.NON_NULL
                )
            .registerModule(
                new JavaTimeModule()
                )
            .disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                ),

        new MediaType("application", "yaml", StandardCharsets.UTF_8),
        new MediaType("text", "yaml", StandardCharsets.UTF_8),
        new MediaType("application", "*+yaml", StandardCharsets.UTF_8),
        new MediaType("text", "*+yaml", StandardCharsets.UTF_8),
        new MediaType("application", "yml", StandardCharsets.UTF_8),
        new MediaType("text", "yml", StandardCharsets.UTF_8),
        new MediaType("application", "*+yaml", StandardCharsets.UTF_8),
        new MediaType("text", "*+yaml", StandardCharsets.UTF_8)
        );
  }

  @Override
  public void setObjectMapper(final ObjectMapper objectMapper) {
    if (!(objectMapper.getFactory() instanceof YAMLFactory)) {
      // Sanity check to make sure we do have an ObjectMapper configured
      // with YAML support, just in case someone attempts to call
      // this method elsewhere.
      throw new IllegalArgumentException(
          "ObjectMapper must be configured with an instance of YAMLFactory");
    }
    super.setObjectMapper(objectMapper);
  }
}

