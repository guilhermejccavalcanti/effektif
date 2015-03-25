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
package com.effektif.workflow.impl.file;

import java.io.InputStream;

import com.effektif.workflow.api.model.Attachment;


/**
 * @author Tom Baeyens
 */
public class FileAttachment implements Attachment {
  
  protected File file;
  protected InputStream inputStream;
  
  protected FileAttachment() {
  }

  protected FileAttachment(File file, InputStream inputStream) {
    super();
    this.file = file;
    this.inputStream = inputStream;
  }

  public static FileAttachment createFileAttachment(File file, FileService fileService) {
    String fileStreamId = file!=null ? file.getStreamId() : null;
    if (fileStreamId==null) {
      return null;
    }
    InputStream inputStream = fileService.getFileStream(file.getStreamId());
    if (inputStream==null) {
      return null;
    }
    return new FileAttachment(file, inputStream);
  }

  @Override
  public String getFileName() {
    return file.getFileName();
  }

  @Override
  public String getContentType() {
    return file.getContentType();
  }

  @Override
  public InputStream getInputStream() {
    return inputStream;
  }

}
