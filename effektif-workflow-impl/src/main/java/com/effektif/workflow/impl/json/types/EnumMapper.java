/* Copyright (c) 2014, Effektif GmbH.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package com.effektif.workflow.impl.json.types;

import java.lang.reflect.Type;

import com.effektif.workflow.impl.json.JsonReader;
import com.effektif.workflow.impl.json.JsonTypeMapper;
import com.effektif.workflow.impl.json.JsonWriter;


/**
 * Maps a {@link Number} to a JSON number value for serialisation and deserialisation.
 *
 * @author Tom Baeyens
 */
public class EnumMapper extends AbstractTypeMapper<Enum> implements JsonTypeMapper<Enum> {

  Class<Enum> enumClass;
  
  public EnumMapper(Type type) {
    if (! (type instanceof Class)
        || !((Enum.class.isAssignableFrom((Class)type)))) {
      throw new RuntimeException("Expected number class, but was "+type);
    }
    enumClass = (Class<Enum>) type;
  }

  @Override
  public Class<Enum> getMappedClass() {
    return enumClass;
  }

  @Override
  public void write(Enum objectValue, JsonWriter jsonWriter) {
    jsonWriter.writeString(objectValue.toString());
  }

  @Override
  public Enum read(Object jsonValue, JsonReader jsonReader) {
    return Enum.valueOf(enumClass, (String) jsonValue);
  }
}
