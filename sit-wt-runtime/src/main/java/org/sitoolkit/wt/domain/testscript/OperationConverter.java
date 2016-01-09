/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sitoolkit.wt.domain.testscript;

import org.apache.commons.beanutils.Converter;
import javax.annotation.Resource;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author yuichi.kuwahara
 */
public class OperationConverter implements Converter {

    @Resource
    ApplicationContext appCtx;

    public Object convert(Class type, Object o) {
        if (o == null || o.toString().isEmpty()) {
            return null;
        }
        return appCtx.getBean(o.toString() + "Operation");
    }
}
