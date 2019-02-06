package org.xito.test;

import org.junit.Test;
import org.xito.boot.Boot;

import java.io.File;


/**
 * Copyright 2017
 * Licensed by Apache License 2.0
 * https://www.apache.org/licenses/LICENSE-2.0.txt
 */
public class BootTest {

    @Test
    public void testBoot() throws Exception {

        String buildDir = System.getProperty("buildDir");
        File bootDir = new File(buildDir);
        System.out.println(bootDir.getCanonicalPath());
        String[] args = new String[]{"-bootdir", buildDir+"/loader_test/test1"};
        Boot.main(args);

    }
}
