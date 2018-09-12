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
package io.sitoolkit.wt.infra;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author yuichi.kuwahara
 */
public class SitPathUtils {

    private static final String LOCAL_BASE_URL = "src/main/webapp";

    public static String relatvePath(File parent, File child) {
        return parent.toURI().relativize(child.toURI()).getPath();
    }

    public static String relatvePath(String parent, String child) {
        return relatvePath(new File(parent), new File(child));
    }

    /**
     * オープン先となるURLを構築します。
     *
     * @param baseUrl
     *            基準となるURL
     * @param path
     *            基準となるURLからの相対パス
     * @return オープン先となるURLの文字列
     */
    public static String buildUrl(String baseUrl, String path) {
        if (path.startsWith("http:") || path.startsWith("https:")) {
            return path;
        }
        if (StringUtils.isEmpty(baseUrl)) {
            return file2url(concatPath(LOCAL_BASE_URL, path));
        } else {
            if (baseUrl.startsWith("http:") || baseUrl.startsWith("https:")) {
                return concatPath(baseUrl, path);
            } else {
                return concatPath(file2url(baseUrl), path);
            }
        }
    }

    public static String file2url(String path) {
        try {
            return new File(path).toURI().toURL().toString();
        } catch (MalformedURLException e) {
            throw new TestException(e);
        }
    }

    public static String concatPath(String a, String b) {
        return a.endsWith("/") ? a + b : a + "/" + b;
    }

}
