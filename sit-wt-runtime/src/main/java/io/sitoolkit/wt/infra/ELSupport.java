/*
 * Copyright 2013 Monocrea Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.sitoolkit.wt.infra;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import io.sitoolkit.wt.infra.log.SitLogger;
import io.sitoolkit.wt.infra.log.SitLoggerFactory;

/**
 *
 * @author yuichi.kuwahara
 */
public class ELSupport {

  protected final SitLogger LOG = SitLoggerFactory.getLogger(getClass());

  protected ExpressionParser parser = new SpelExpressionParser();

  protected ParserContext parserContext = new TemplateParserContext();

  private EvaluationContext ctx;

  protected Object rootObject;

  public ELSupport(Object rootObject) {
    super();
    this.rootObject = rootObject;
    ctx = new StandardEvaluationContext(rootObject);
  }

  public String evaluate(String value) {
    if (StringUtils.isEmpty(value)) {
      return StringUtils.EMPTY;
    }
    Expression exp = parser.parseExpression(value, parserContext);
    return StringUtils
        .defaultString(ctx == null ? exp.getValue(String.class) : exp.getValue(ctx, String.class));
  }

  public Object getRootObject() {
    return rootObject;
  }

  public void setRootObject(Object rootObject) {
    this.rootObject = rootObject;
  }
}
