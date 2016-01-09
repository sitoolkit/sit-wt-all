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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.sitoolkit.wt.domain.tester.ELSupport;
import org.sitoolkit.wt.domain.tester.TestContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author yuichi.kuwahara
 */
public class ELSupportTest {

	ELSupport el = new ELSupport();

	@Test
	public void testEvaluate() {
		assertEquals("3abc", el.evaludate("#{1 + 2}abc"));
		assertEquals("a3bc", el.evaludate("a#{1 + 2}bc"));
		assertEquals("abc3", el.evaludate("abc#{1 + 2}"));

		assertEquals("37abc", el.evaludate("#{1 + 2}#{3 + 4}abc"));
		assertEquals("abc37", el.evaludate("abc#{1 + 2}#{3 + 4}"));

		assertEquals("abc#{1 + 2", el.evaludate("abc#{1 + 2"));
		assertEquals("{1 + 2}abc", el.evaludate("{1 + 2}abc"));

		assertEquals("", el.evaludate(null));
	}

	@Test
	public void testEvaluateContext() {
		TestContext current = new TestContext();
		el.current = current;
		el.init();

		current.addParam("idx", 10);
		assertEquals("10", el.evaludate("#{params['idx']}"));
	}

}