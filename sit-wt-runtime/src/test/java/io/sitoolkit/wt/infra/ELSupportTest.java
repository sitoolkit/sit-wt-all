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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.sitoolkit.wt.infra.ELSupport;

/**
 *
 * @author yuichi.kuwahara
 */
public class ELSupportTest {

    RootObject rootObject = new RootObject();
    ELSupport el = new ELSupport(rootObject);

    @Test
    public void testEvaluate() {
        assertEquals("3abc", el.evaluate("#{1 + 2}abc"));
        assertEquals("a3bc", el.evaluate("a#{1 + 2}bc"));
        assertEquals("abc3", el.evaluate("abc#{1 + 2}"));

        assertEquals("37abc", el.evaluate("#{1 + 2}#{3 + 4}abc"));
        assertEquals("abc37", el.evaluate("abc#{1 + 2}#{3 + 4}"));

        assertEquals("{1 + 2}abc", el.evaluate("{1 + 2}abc"));

        assertEquals("", el.evaluate(null));

        assertEquals("true",
                el.evaluate("#{T(org.apache.commons.lang3.math.NumberUtils).isNumber('1')}"));

    }

    @Test
    public void testEvaluateContext() {

        rootObject.params.put("key1", "value1");
        assertEquals("value1", el.evaluate("#{params['key1']}"));

    }

    static class RootObject {
        Map<String, Object> params = new HashMap<>();

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }

}