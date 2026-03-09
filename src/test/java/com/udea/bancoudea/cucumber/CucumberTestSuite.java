package com.udea.bancoudea.cucumber;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@Cucumber
public class CucumberTestSuite {
    // Test suite para ejecutar todos los escenarios de Cucumber
}
