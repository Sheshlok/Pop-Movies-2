package com.example.android.popmovies.utils;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by sheshloksamal on 12/03/16. This contains code to include all Test Classes and is
 * packaged into a suite of tests that jUnit will run.
 */

public class FullTestSuite extends TestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere()
                .build();
    }

    public FullTestSuite(){
        super();
    }

}
