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

package completion;


import base.PerlLightTestCase;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.perl5.lang.perl.idea.configuration.settings.PerlSharedSettings;
import com.perl5.lang.perl.idea.project.PerlNamesCache;
import com.perl5.lang.perl.internals.PerlVersion;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.function.BiPredicate;

public class PerlCompletionTest extends PerlLightTestCase {

  @NotNull
  private static BiPredicate<LookupElement, LookupElementPresentation> withType(@NotNull String type){
    return (__, presentation) -> StringUtil.contains(StringUtil.notNullize(presentation.getTypeText()), type);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    PerlNamesCache.getInstance(getProject()).forceCacheUpdate();
  }

  @Override
  protected String getBaseDataPath() {
    return "testData/completion/perl";
  }

  @Test
  public void testHashSlices520LexicalDisabled() {
    doTestHashSlices520Lexical();
  }

  @Test
  public void testHashSlices520LexicalEnabled() {
    PerlSharedSettings.getInstance(getProject()).setTargetPerlVersion(PerlVersion.V5_20);
    doTestHashSlices520Lexical();
  }

  private void doTestHashSlices520Lexical() {
    initWithFileSmart("hashSlices520lexical");
    doTestCompletionCheck();
  }

  @Test
  public void testHashSlices520GlobalDisabled() {
    doTestHashSlices520Global();
  }

  @Test
  public void testHashSlices520GlobalEnabled() {
    PerlSharedSettings.getInstance(getProject()).setTargetPerlVersion(PerlVersion.V5_20);
    doTestHashSlices520Global();
  }

  private void doTestHashSlices520Global() {
    initWithFileSmart("hashSlices520global");
    doTestCompletionCheck();
  }

  @Test
  public void testIssue2024() {
    String fileName = "MyCustomPackage.pm";
    VirtualFile package1 = myFixture.copyFileToProject(fileName, "lib/" + fileName);
    assertNotNull(package1);
    markAsLibRoot(package1.getParent(), true);
    VirtualFile package2 = myFixture.copyFileToProject(fileName, "lib/something/lib/" + fileName);
    assertNotNull(package2);
    markAsLibRoot(package2.getParent(), true);
    initWithTextSmart("use <caret>");
    doTestCompletionCheck("", (element, __) -> element.getLookupString().contains("MyCustomPackage"));
  }

  @Test
  public void testMainCompletionAll(){
    doTestMainCompletion(false);
  }

  @Test
  public void testMainCompletionSimple(){
    doTestMainCompletion(true);
  }

  private void doTestMainCompletion(boolean value) {
    PerlSharedSettings.getInstance(getProject()).SIMPLE_MAIN_RESOLUTION = value;
    myFixture.copyFileToProject("second_app.pl");
    initWithTextSmartWithoutErrors("use Mojolicious::Lite;\n" +
                                   "<caret>");
    doTestCompletionCheck("", withType("main"));
  }

  @Test
  public void testMainImportCompletionAll(){
    doTestMainImportCompletion(false);
  }

  @Test
  public void testMainImportCompletionSimple(){
    doTestMainImportCompletion(true);
  }

  private void doTestMainImportCompletion(boolean value) {
    PerlSharedSettings.getInstance(getProject()).SIMPLE_MAIN_RESOLUTION = value;
    myFixture.copyFileToProject("mainImporter.pl");
    initWithTextSmartWithoutErrors("<caret>");
    doTestCompletionCheck("", withType("MyTest::Some::Package"));
  }

  @Test
  public void testMultipleNamespaces(){
    doTestCompletion((__, presentation) -> StringUtil.startsWith(StringUtil.notNullize(presentation.getItemText()), "Some::Thing"));
  }

  @Test
  public void testMojoLite(){
    doTestCompletion(withType("main"));}

  @Test
  public void testBlessedInference() {doTest();}

  @Test
  public void testBlessedInferenceContext() {doTest();}

  @Test
  public void testPodLiveTemplates() {
    doTest();
  }

  @Test
  public void testImportInSuperClass() {
    doTest();
  }

  @Test
  public void testTypeGlobAsMethod() {
    doTest();
  }

  @Test
  public void testTypeGlobAsStatic() {
    doTest();
  }

  @Test
  public void testFileSpec() {
    withFileSpec();
    doTest();
  }

  @Test
  public void testTestMoreParameters() {
    withTestMore();
    doTest();
  }

  @Test
  public void testCpanfile() {
    withCpanfile();
    initWithCpanFile();
    doTestCompletionCheck();
  }

  @Test
  public void testLog4perlOptions() {doTest();}

  @Test
  public void testLog4perlEasy() {doTestLog4Perl();}

  @Test
  public void testLog4perlEasyScalar() {doTestLog4Perl();}

  @Test
  public void testLog4perlGetLogger() {doTestLog4Perl();}

  @Test
  public void testLog4perlLevels() {doTestLog4Perl();}

  private void doTestLog4Perl() {
    doTestCompletion((lookup, presentation) -> StringUtil.contains(StringUtil.notNullize(presentation.getTypeText()), "Log4perl"));
  }

  @Test
  public void testReadonlyBare() {doTest();}

  @Test
  public void testReadonlyImport() {doTest();}

  @Test
  public void testReadonlyImported() {doTest();}

  @Test
  public void testReadonlyMethod() {doTest();}

  @Test
  public void testTypesStandard() {doTest();}

  @Test
  public void testVariableSubSignature() {doTest();}

  @Test
  public void testVariableAnonSubSignature() {doTest();}

  @Test
  public void testVariableMethodSignature() {doTest();}

  @Test
  public void testVariableFuncSignature() {doTest();}

  @Test
  public void testDataPrinter() {doTest();}

  @Test
  public void testListMoreUtilsParams() {doTest();}

  @Test
  public void testBuiltInPackageExtension() {doTest();}

  @Test
  public void testAnnotatedRefMethod() {doTest();}

  @Test
  public void testWildCardReturns() {
    //doTest();
  }

  @Test
  public void testVariableArrayInArray() {doTest();}

  @Test
  public void testVariableArrayInScalar() {doTest();}

  @Test
  public void testVariableHashInArray() {doTest();}

  @Test
  public void testVariableHashInHash() {doTest();}

  @Test
  public void testVariableHashInScalar() {doTest();}

  @Test
  public void testVariableScalarLocal() {doTest();}

  @Test
  public void testVariableScalarMy() {doTest();}

  @Test
  public void testVariableScalarOur() {doTest();}

  @Test
  public void testVariableScalarState() {doTest();}

  @Test
  public void testNotOverridenSubs() {doTest();}

  @Test
  public void testMooseImports() {doTest();}

  @Test
  public void testMooseXMethodAttributes() {doTest();}

  @Test
  public void testMooseXMethodAttributesRole() {doTest();}

  @Test
  public void testMooseXRoleWithOverloading() {doTest();}

  @Test
  public void testMooseXRoleParametrized() {doTest();}

  @Test
  public void testMooseXClassAttribute() {doTest();}

  @Test
  public void testMooseXTypesCheckedUtilExports() {doTest();}

  @Test
  public void testMooseUtilTypeConstraints() {doTest();}

  @Test
  public void testMooseRoleImports() {doTest();}

  @Test
  public void testMooseAttrs() {doTest();}

  @Test
  public void testMojoAttrs() {doTest();}


  @Test
  public void testClassAccessor() {
    doTest();
  }

  @Test
  public void testExceptionClassAliasLocal() {
    doTest();
  }

  @Test
  public void testExceptionClassAliasStatic() {
    doTest();
  }

  @Test
  public void testExceptionClassAliasMethod() {
    doTest();
  }

  @Test
  public void testLibraryConstants() {
    doTest();
  }

  @Test
  public void testUsePackageInCurrentDir510() {
    doTest(PerlVersion.V5_10);
  }

  @Test
  public void testUsePackageInCurrentDir512() {
    doTest(PerlVersion.V5_12);
  }

  @Test
  public void testUsePackageInCurrentDir514() {
    doTest(PerlVersion.V5_14);
  }

  @Test
  public void testUsePackageInCurrentDir516() {
    doTest(PerlVersion.V5_16);
  }

  @Test
  public void testUsePackageInCurrentDir518() {
    doTest(PerlVersion.V5_18);
  }

  @Test
  public void testUsePackageInCurrentDir520() {
    doTest(PerlVersion.V5_20);
  }

  @Test
  public void testUsePackageInCurrentDir522() {
    doTest(PerlVersion.V5_22);
  }

  @Test
  public void testUsePackageInCurrentDir524() {
    doTest(PerlVersion.V5_24);
  }

  @Test
  public void testUsePackageInCurrentDir526() {
    doTest(PerlVersion.V5_26);
  }

  @Test
  public void testUsePackageInCurrentDir528() {
    doTest(PerlVersion.V5_28);
  }

  public void doTest(@NotNull PerlVersion version) {
    addCustomPackage();
    setTargetPerlVersion(version);
    initWithFileSmart("usePackageInCurrentDir");
    doTestCompletionCheck("", (__, presentation) -> "MyCustomPackage".equals(presentation.getItemText()));
  }

  @Test
  public void testExceptionClass() {
    doTest();
  }

  @Test
  public void testReferenceCompletion() {doTest();}

  @Test
  public void testCaptureScalar() {doTest();}

  @Test
  public void testCaptureArray() {doTest();}

  @Test
  public void testCaptureHash() {doTest();}

  @Test
  public void testAnnotationReturnsPackage() {
    doTest();
  }

  @Test
  public void testAnnotationTypePackage() {
    doTest();
  }

  @Test
  public void testImportSubsParam() {
    doTest();
  }

  @Test
  public void testImportSubsParamWithoutExport() {
    doTest();
  }

  @Test
  public void testImportSubsParamWithoutExportOk() {
    doTest();
  }

  @Test
  public void testImportParamWithoutOption() {
    doTest();
  }

  @Test
  public void testImportParamWithoutOptionBundles() {
    doTest();
  }

  @Test
  public void testImportParamWithoutParent() {
    doTest();
  }

  private void doTestExportArray() {
    doTest();
  }

  @Test
  public void testExportArrayEmpty() {
    doTestExportArray();
  }

  @Test
  public void testExportArrayNonEmpty() {
    doTestExportArray();
  }

  @Test
  public void testExportOkArrayEmpty() {
    doTestExportArray();
  }

  @Test
  public void testExportOkArrayNonEmpty() {
    doTestExportArray();
  }

  @Test
  public void testPackageToStringQ() {
    doTestStringCompletion();
  }

  @Test
  public void testPackageToStringQPartial() {
    doTest();
  }

  @Test
  public void testPackageToStringQQ() {
    doTestStringCompletion();
  }

  @Test
  public void testPackageToStringQWFirst() {
    doTestStringCompletion();
  }

  @Test
  public void testPackageToStringQWNonFirst() {
    doTestStringCompletion();
  }

  private void doTestStringCompletion() {
    doTest();
  }

  @Test
  public void testUnresolvedSubDeclaration() {
    doTest();
  }

  @Test
  public void testUnresolvedSubDefinition() {
    doTest();
  }

  @Test
  public void testConstants() {
    doTest();
  }

  @Test
  public void testConstantsWithPackage() {
    doTest();
  }

  @Test
  public void testVariableInDeclaration() {
    doTest();
  }

  @Test
  public void testImportSubs() {
    doTest();
  }

  @Test
  public void testImportHashes() {
    doTest();
  }

  @Test
  public void testImportArrays() {
    doTest();
  }

  @Test
  public void testImportScalars() {
    doTest();
  }

  @Test
  public void testImportDancer() {
    doTest();
  }

  @Test
  public void testImportDancer2() {
    doTest();
  }

  @Test
  public void testImportPosixOk() {
    doTest();
  }

  @Test
  public void testImportPosix() {
    doTest();
  }

  @Test
  public void testImportPosixVar() {
    doTest();
  }

  @Test
  public void testLexicalMy() {
    doTest();
  }

  @Test
  public void testLexicalState() {
    doTest();
  }

  @Test
  public void testLexicalOur() {
    doTest();
  }

  @Test
  public void testSameStatementSimple() {
    doTest();
  }

  @Test
  public void testSameStatementMap() {
    doTest();
  }

  @Test
  public void testHeredocOpenerBare() {
    doTestHeredoc();
  }

  @Test
  public void testHeredocOpenerBackref() {
    doTestHeredoc();
  }

  @Test
  public void testHeredocOpenerQQ() {
    doTestHeredoc();
  }

  @Test
  public void testHeredocOpenerSQ() {
    doTestHeredoc();
  }

  @Test
  public void testHeredocOpenerXQ() {
    doTestHeredoc();
  }

  private void doTestHeredoc() {
    doTest();
  }

  @Test
  public void testRefTypes() {
    doTestCompletionFromText("my $var = '<caret>'");
  }

  @Test
  public void testHashIndexBare() {
    doTestCompletionFromText("$$a{testindex}; $b->{<caret>}");
  }


  @Test
  public void testAnnotation() {
    doTest();
  }

  @Test
  public void testInjectMarkers() {
    doTest();
  }


  @Test
  public void testNextLabels() {
    doTest();
  }

  @Test
  public void testGotoLabels() {
    doTest();
  }

  @Test
  public void testPackageUse() {
    doTest();
  }

  @Test
  public void testPackageNo() {
    doTest();
  }

  @Test
  public void testPackageRequire() {
    doTest();
  }

  @Test
  public void testPackageMy() {
    doTest();
  }

  @Test
  public void testPackageOur() {
    doTest();
  }

  @Test
  public void testPackageState() {
    doTest();
  }

  @Test
  public void testTryCatch() {
    doTest();
  }

  @Test
  public void testReturnsPackageTag() {doTest();}

  @Test
  public void testPackageVarTypePerl() {doTest();}

  @Test
  public void testPackageVarTypeAnnotation() {doTest();}

  @Test
  public void testLazyExportOkQw() {doTest();}

  @Test
  public void testGlobSlots() {doTest();}

  private void doTest() {
    doTestCompletion();
  }
}
