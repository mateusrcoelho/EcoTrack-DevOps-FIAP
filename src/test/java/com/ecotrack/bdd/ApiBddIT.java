package com.ecotrack.bdd;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.ecotrack.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,html:target/bdd-report.html,json:target/bdd-report.json,junit:target/bdd-report.xml")
public class ApiBddIT {}
