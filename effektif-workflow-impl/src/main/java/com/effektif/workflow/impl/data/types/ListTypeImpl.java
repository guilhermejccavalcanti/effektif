/*
 * Copyright 2014 Effektif GmbH.
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
 * limitations under the License.
 */
package com.effektif.workflow.impl.data.types;

import java.util.ArrayList;
import java.util.List;

import com.effektif.workflow.api.Configuration;
import com.effektif.workflow.api.types.DataType;
import com.effektif.workflow.api.types.ListType;
import com.effektif.workflow.impl.data.AbstractDataType;
import com.effektif.workflow.impl.data.DataTypeImpl;
import com.effektif.workflow.impl.data.DataTypeService;
import com.effektif.workflow.impl.data.TypedValueImpl;
import com.effektif.workflow.impl.template.Hints;


/**
 * @author Tom Baeyens
 */
public class ListTypeImpl extends AbstractDataType<ListType> {
  
  public DataTypeImpl elementType;
  
  public ListTypeImpl() {
    this(new ListType());
  }
  
  public ListTypeImpl(DataTypeImpl elementType) {
    this(new ListType(elementType.serialize()));
    this.elementType = elementType;
  }
  
  public ListTypeImpl(ListType listType) {
    super(listType);
  }
  
  @Override
  public void setConfiguration(Configuration configuration) {
    super.setConfiguration(configuration);
    DataType elementType = type.getElementType();
    if (elementType!=null) {
      DataTypeService dataTypeService = configuration.get(DataTypeService.class);
      this.elementType = dataTypeService.createDataType(elementType);
    }
  }

  @Override
  public boolean isStatic() {
    return false;
  }

  @Override
  public TypedValueImpl dereference(Object value, String field) {
    List<Object> fieldValues = null;
    if (value instanceof List) {
      List<Object> values = (List<Object>) value;
      fieldValues = new ArrayList<>(); 
      for (Object elementValue: values) {
        TypedValueImpl elementFieldValue = elementType.dereference(elementValue, field);
        fieldValues.add(elementFieldValue.value);
      }
    }
    return new TypedValueImpl(this, fieldValues);
  }

  @Override
  public String convertInternalToText(Object value, Hints hints) {
    if (value==null) {
      return "";
    }
    if (value instanceof List) {
      StringBuilder text = null;
      List<Object> list = (List<Object>) value;
      for (Object element: list) {
        if (text==null) {
          text = new StringBuilder();
        } else {
          text.append(", ");
        }
        String elementText = elementType.convertInternalToText(element, hints);
        text.append(elementText);
      }
      return text.toString();
    }
    return value.toString();
  }
}
