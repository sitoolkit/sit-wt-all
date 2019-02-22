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
package io.sitoolkit.wt.domain.operation;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

/**
 *
 * @author yuichi.kuwahara
 */
public abstract class OperationConverter {

    @Resource
    ApplicationContext appCtx;

    public abstract Operation convert(String name);

    public abstract List<String> getOperationNames();

    protected Operation convertByPackage(String operationName, String... packages) {
        String beanName = OperationCatalog.getBeanName(operationName, packages);
        return (Operation) appCtx.getBean(beanName);
    }

    protected List<String> getOperationNamesByPackage(String... packages) {
        return OperationCatalog.getOperationNames(packages);
    }

}
