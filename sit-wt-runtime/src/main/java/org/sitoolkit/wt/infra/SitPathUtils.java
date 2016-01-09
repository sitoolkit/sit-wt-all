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
package org.sitoolkit.wt.infra;

import java.io.File;

/**
 *
 * @author yuichi.kuwahara
 */
public class SitPathUtils {

    public static String relatvePath(File parent, File child) {
        return parent.toURI().relativize(child.toURI()).getPath();
    }

    public static String relatvePath(String parent, String child) {
        return relatvePath(new File(parent), new File(child));
    }
}
