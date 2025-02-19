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

package com.perl5.lang.perl.idea.completion.util;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ObjectUtils;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.fileTypes.PerlFileTypePackage;
import com.perl5.lang.perl.idea.PerlCompletionWeighter;
import com.perl5.lang.perl.internals.PerlFeaturesTable;
import com.perl5.lang.perl.internals.PerlVersion;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.impl.PerlFileImpl;
import com.perl5.lang.perl.psi.references.PerlBuiltInNamespacesService;
import com.perl5.lang.perl.util.PerlPackageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Map;

import static com.perl5.PerlIcons.PACKAGE_GUTTER_ICON;
import static com.perl5.lang.perl.util.PerlPackageUtil.__PACKAGE__;


public class PerlPackageCompletionUtil {
  public static final InsertHandler<LookupElement> COMPLETION_REOPENER = (context, item) -> {
    if (context.getCompletionChar() != Lookup.AUTO_INSERT_SELECT_CHAR) {
      AutoPopupController.getInstance(context.getProject()).scheduleAutoPopup(context.getEditor());
    }
  };

  @NotNull
  public static LookupElementBuilder getNamespaceLookupElement(@NotNull PerlNamespaceDefinitionElement namespaceDefinition) {
    return getPackageLookupElement(
      namespaceDefinition, ObjectUtils.notNull(namespaceDefinition.getNamespaceName(), "unnamed"), namespaceDefinition.getIcon(0));
  }

  /**
   * @return package lookup element by package name
   */
  @NotNull
  public static LookupElementBuilder getPackageLookupElement(@Nullable Object lookupObject,
                                                             @NotNull String packageName,
                                                             @Nullable Icon icon) {
    LookupElementBuilder result = LookupElementBuilder.create(ObjectUtils.notNull(lookupObject, packageName), packageName);

    if (PerlPackageUtil.isBuiltIn(packageName)) {
      result = result.withBoldness(true);
    }

    if (PerlPackageUtil.isPragma(packageName)) {
      result = result.withIcon(PerlIcons.PRAGMA_GUTTER_ICON);
    }
    else {
      result = result.withIcon(icon == null ? PACKAGE_GUTTER_ICON : icon);
    }
    // fixme this should be adjusted in #954
    //		if (PerlPackageUtil.isDeprecated(project, packageName))
    //			result = result.withStrikeoutness(true);

    return result;
  }

  /**
   * @return package lookup element with automatic re-opening autocompeltion
   */
  public static LookupElementBuilder getPackageLookupElementWithAutocomplete(@Nullable PerlNamespaceDefinitionElement namespaceDefinitionElement,
                                                                             @NotNull String packageName,
                                                                             @Nullable Icon icon) {
    return getPackageLookupElement(namespaceDefinitionElement, packageName, icon)
      .withInsertHandler(COMPLETION_REOPENER)
      .withTailText("...");
  }

  public static void fillWithAllNamespacesNames(@NotNull PsiElement element, @NotNull final CompletionResultSet result) {
    final Project project = element.getProject();
    GlobalSearchScope resolveScope = element.getResolveScope();

    PerlBuiltInNamespacesService.getInstance(project).processNamespaces(namespace -> {
      result.addElement(PerlPackageCompletionUtil.getNamespaceLookupElement(namespace));
      return true;
    });

    result.addElement(PerlPackageCompletionUtil.getPackageLookupElement(null, __PACKAGE__, PACKAGE_GUTTER_ICON));
    for (String packageName : PerlPackageUtil.getKnownNamespaceNames(project)) {
      PerlPackageUtil.processNamespaces(packageName, project, resolveScope, namespace -> {
        String name = namespace.getNamespaceName();
        if (StringUtil.isNotEmpty(name)) {
          char firstChar = name.charAt(0);
          if (firstChar == '_' || Character.isLetterOrDigit(firstChar)) {
            result.addElement(PerlPackageCompletionUtil.getPackageLookupElement(namespace, name, namespace.getIcon(0)));
            return false;
          }
        }
        return true;
      });
    }
  }

  public static void fillWithAllNamespacesNamesWithAutocompletion(@NotNull PsiElement element, @NotNull final CompletionResultSet result) {
    final Project project = element.getProject();
    final String prefix = result.getPrefixMatcher().getPrefix();
    GlobalSearchScope resolveScope = element.getResolveScope();

    PerlBuiltInNamespacesService.getInstance(project).processNamespaces(namespace -> {
      addExpandablePackageElement(namespace, namespace.getNamespaceName(), result, prefix);
      return true;
    });

    for (String packageName : PerlPackageUtil.getKnownNamespaceNames(project)) {
      PerlPackageUtil.processNamespaces(packageName, project, resolveScope, namespace -> {
        String name = namespace.getNamespaceName();
        if (StringUtil.isNotEmpty(name)) {
          char firstChar = name.charAt(0);
          if (firstChar == '_' || Character.isLetterOrDigit(firstChar)) {
            addExpandablePackageElement(namespace, packageName, result, prefix);
            return false;
          }
        }
        return true;
      });
    }
  }

  protected static void addExpandablePackageElement(@NotNull PerlNamespaceDefinitionElement namespaceDefinitionElement,
                                                    @Nullable String packageName,
                                                    @NotNull CompletionResultSet result,
                                                    String prefix) {
    String name = packageName + PerlPackageUtil.NAMESPACE_SEPARATOR;
    if (!StringUtil.equals(prefix, name)) {
      LookupElementBuilder newElement = PerlPackageCompletionUtil.getPackageLookupElementWithAutocomplete(
        namespaceDefinitionElement, name, namespaceDefinitionElement.getIcon(0));
      newElement.putUserData(PerlCompletionWeighter.WEIGHT, -1);
      result.addElement(newElement);
    }
    name = packageName + PerlPackageUtil.DEREFERENCE_OPERATOR;
    if (!StringUtil.equals(prefix, name)) {
      LookupElementBuilder newElement = PerlPackageCompletionUtil.getPackageLookupElementWithAutocomplete(
        namespaceDefinitionElement, name, namespaceDefinitionElement.getIcon(0));
      newElement.putUserData(PerlCompletionWeighter.WEIGHT, -1);
      result.addElement(newElement);
    }
  }


  public static void fillWithAllPackageFiles(@NotNull PsiElement element, @NotNull final CompletionResultSet result) {
    PerlPackageUtil.processPackageFilesForPsiElement(element, (packageName, file) -> {
      result.addElement(PerlPackageCompletionUtil.getPackageLookupElement(file, packageName, null));
      return true;
    });
  }

  public static void fillWithVersionNumbers(@NotNull PsiElement element, @NotNull final CompletionResultSet result) {
    for (Map.Entry<String, List<String>> entry : PerlFeaturesTable.AVAILABLE_FEATURES_BUNDLES.entrySet()) {
      String version = entry.getKey();
      if (StringUtil.startsWith(version, "5")) {
        result.addElement(
          LookupElementBuilder.create(new PerlVersion("v" + version), "v" + version)
            .withTypeText(StringUtil.join(entry.getValue(), " "))
        );
      }
    }
  }

  public static void fillWithNamespaceNameSuggestions(@NotNull PsiElement element, @NotNull final CompletionResultSet result) {
    PsiFile file = element.getContainingFile().getOriginalFile();

    VirtualFile virtualFile = file.getViewProvider().getVirtualFile();
    if (virtualFile.getFileType() == PerlFileTypePackage.INSTANCE) {
      result.addElement(LookupElementBuilder.create(virtualFile, virtualFile.getNameWithoutExtension()));
      if (file instanceof PerlFileImpl) {
        String packageName = ((PerlFileImpl)file).getFilePackageName();
        if (packageName != null) {
          result.addElement(LookupElementBuilder.create(file, packageName));
        }
      }
    }
  }
}
