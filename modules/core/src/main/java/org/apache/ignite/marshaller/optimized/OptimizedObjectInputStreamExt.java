/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.marshaller.optimized;

import org.apache.ignite.internal.util.io.*;

import java.io.*;

import static org.apache.ignite.marshaller.optimized.OptimizedMarshallerExt.*;


/**
 * TODO: IGNITE-950
 */
public class OptimizedObjectInputStreamExt extends OptimizedObjectInputStream {
    /** {@inheritDoc} */
    public OptimizedObjectInputStreamExt(GridDataInput in) throws IOException {
        super(in);
    }

    /** {@inheritDoc} */
    @Override protected boolean hasFooter(Class<?> cls) throws IOException {
        return fieldsIndexingSupported(cls, metaHandler, ctx, clsMap, mapper);
    }

    /** {@inheritDoc} */
    @Override protected void skipFooter() throws IOException {
        short footerLen = in.readShort();

        if (footerLen != EMPTY_FOOTER)
            in.skipBytes(footerLen - 2);
    }

    /** {@inheritDoc} */
    @Override protected int readFieldType() throws IOException {
        return in.readByte();
    }
}
