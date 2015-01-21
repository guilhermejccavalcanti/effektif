/* Copyright 2014 Effektif GmbH.
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
package com.effektif.workflow.impl.plugin;

import com.effektif.workflow.api.types.Type;
import com.effektif.workflow.impl.types.InvalidValueException;


public interface DataType<T extends Type> extends Plugin {
  
  // TODO byte[] getIconBytes();
  // TODO String getIconMimeType();

  Class< ? > getValueClass();
  Class< ? > getApiClass();
  T toType();

  boolean isSerializeRequired();
  Object serialize(Object value);
  Object deserialize(Object value);

  void initialize(T typeApi, ServiceRegistry serviceRegistry);

  /** invoked to validate values submitted through the api. */
  void validateInternalValue(Object internalValue) throws InvalidValueException;

  /** only returns valid internal values. */
  Object convertJsonToInternalValue(Object jsonValue) throws InvalidValueException;

  Object convertInternalToJsonValue(Object internalValue);

  Object convertInternalToScriptValue(Object internalValue, String language);

  Object convertScriptValueToInternal(Object scriptValue, String language);
}
