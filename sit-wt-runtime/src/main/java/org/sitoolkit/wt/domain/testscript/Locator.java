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

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.sitoolkit.wt.domain.tester.ELSupport;

/**
 *
 * @author yuichi.kuwahara
 */
public class Locator {

    @Resource
    ELSupport el;

    private static final Locator NA = new Locator();

    /**
     * 形式
     */
    private String type = Type.na.name();

    /**
     * 値
     */
    private String value = StringUtils.EMPTY;

    /**
     * 属性名
     */
    private String attributeName = StringUtils.EMPTY;

    public Locator() {

    }

    private Locator(Type type, String value) {
        this.type = type.name();
        setValue(value);
    }

    public Type getTypeVo() {
        return Type.decode(getType());
    }

    public void setTypeVo(Type type) {
        setType(type == null ? Type.na.name() : type.name());
    }

    public String getValue() {
        return el == null ? value : el.evaludate(value);
    }

    public void setValue(String value) {
        String attrName = StringUtils.substringAfterLast(value, "@");

        if (StringUtils.isNotEmpty(attrName) && !StringUtils.containsAny(attrName, "/", "[", "(")) {
            value = StringUtils.substringBeforeLast(value, "@");
            this.attributeName = attrName;
        }

        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public enum Type {
        id, css, name, xpath, link, tag, title, na;

        public static Type decode(String code) {
            for (Type type : values()) {
                if (type.name().equals(code)) {
                    return type;
                }
            }
            return na;
        }
    }

    public boolean isNa() {
        return Type.na.equals(getTypeVo());
    }

    @Override
    public String toString() {
        return Type.na.name().equals(getType()) ? getValue() : getType() + "=" + getValue();
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public static Locator build(String type, String value) {

        return new Locator(Type.decode(type), value);
    }

    public static Locator build(String str) {

        if (StringUtils.isBlank(str)) {
            return NA;
        }

        if (str.startsWith("//")) {
            return build(Type.xpath.name(), str);
        }

        Locator locator = buidlByPrefix(str, "=");

        if (locator == null) {
            locator = buidlByPrefix(str, " ");
        }

        if (locator == null) {
            locator = build(Type.na.name(), str);
        }

        return locator;
    }

    private static Locator buidlByPrefix(String str, String _prefix) {
        for (Type type : Type.values()) {
            String prefix = type.name() + _prefix;

            if (str.startsWith(prefix)) {
                return build(type.name(), StringUtils.substringAfter(str, prefix));
            }
        }
        return null;
    }

    /**
     * 形式と値がともに空の場合にtrueを返します。
     *
     * @return 形式と値がともに空の場合にtrue
     */
    public boolean isEmpty() {
        return StringUtils.isEmpty(type) && StringUtils.isEmpty(value);
    }
}
