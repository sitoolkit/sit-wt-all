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

import org.junit.Test;
import org.sitoolkit.wt.infra.RegexHelper;

import static org.junit.Assert.*;

/**
 *
 * @author yuichi.kuwahara
 */
public class RegexHelperTest {
	
	@Test
	public void testMatches() {
		assertTrue(RegexHelper.matches("abc", "abc"));
		assertTrue(RegexHelper.matches("regexp:[a-d]{4}", "abcd"));
		assertFalse(RegexHelper.matches("regexp:[a-d]{5}", "abcde"));
	}
}