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
package org.sitoolkit.wt.domain.tester;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuichi.kuwahara
 */
@Component
public class ELSupport {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected ExpressionParser parser = new SpelExpressionParser();
    private static Pattern p = Pattern.compile("#\\{(.+?\\})");

    private EvaluationContext ctx;

    @Resource
    protected TestContext current;

    @PostConstruct
    public void init() {
        ctx = new StandardEvaluationContext(current);
    }

    public String evaludate(String value) {
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }

        Matcher m = p.matcher(value);

        while (m.find()) {
            String expGroup = m.group();
            LOG.trace("value:{}, el:{}", value, expGroup);
            String expStr = expGroup.substring(2, expGroup.length() - 1);
            value = value.replace(expGroup, eval(expStr));
        }

        return value;
    }

    String eval(String expStr) {
        Expression exp = parser.parseExpression(expStr);
        return StringUtils.defaultString(ctx == null
            ? exp.getValue(String.class)
            : exp.getValue(ctx, String.class));

    }
}
