/*
 * Copyright 2015-2019 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perl5.lang.perl.psi;

import com.intellij.psi.PsiElement;


public interface PerlStringContentElement extends PsiElement {

  /**
   * Returns continuos string for this element
   *
   * @return text till space or end before and after this element
   * @deprecated use {@link com.intellij.psi.ElementManipulators#getValueText(PsiElement)} instead
   */
  @Deprecated
  String getContinuosText();
}