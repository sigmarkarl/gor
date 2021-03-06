/*
 *  BEGIN_COPYRIGHT
 *
 *  Copyright (C) 2011-2013 deCODE genetics Inc.
 *  Copyright (C) 2013-2019 WuXi NextCode Inc.
 *  All Rights Reserved.
 *
 *  GORpipe is free software: you can redistribute it and/or modify
 *  it under the terms of the AFFERO GNU General Public License as published by
 *  the Free Software Foundation.
 *
 *  GORpipe is distributed "AS-IS" AND WITHOUT ANY WARRANTY OF ANY KIND,
 *  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 *  NON-INFRINGEMENT, OR FITNESS FOR A PARTICULAR PURPOSE. See
 *  the AFFERO GNU General Public License for the complete license terms.
 *
 *  You should have received a copy of the AFFERO GNU General Public License
 *  along with GORpipe.  If not, see <http://www.gnu.org/licenses/agpl-3.0.html>
 *
 *  END_COPYRIGHT
 */

package org.gorpipe.gor.driver;

import org.gorpipe.gor.driver.DataSource;
import org.gorpipe.gor.driver.GorDriverFactory;
import org.gorpipe.gor.driver.PluggableGorDriver;
import org.gorpipe.gor.driver.meta.SourceReference;
import gorsat.TestUtils;
import org.gorpipe.model.genome.files.gor.GenomicIterator;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class UTestPluggableGorDriver {

    private static PluggableGorDriver gorDriver = (PluggableGorDriver) GorDriverFactory.fromConfig();

    @Test
    public void testFileSourcesAreClosed() throws IOException, InterruptedException {
        File gorfile = File.createTempFile("testfile", ".gor");
        File linkfile = File.createTempFile(gorfile.getAbsolutePath(), ".link");
        linkfile.deleteOnExit();
        gorfile.deleteOnExit();
        Files.write(linkfile.toPath(), gorfile.getAbsolutePath().getBytes());

        long count = TestUtils.countOpenFiles();

        for (int i = 0; i < 1000; i++) {
            DataSource source = gorDriver.getDataSource(new SourceReference(linkfile.getAbsolutePath()));
            Assert.assertTrue(source.exists());
        }

        long newCount = TestUtils.countOpenFiles();
        Assert.assertTrue("Open files now " + newCount + ", was " + count, newCount <= count + 10);
    }

    @Test
    public void noGuice() throws IOException {
        PluggableGorDriver pd = PluggableGorDriver.instance();
        GenomicIterator iterator = pd.createIterator(new SourceReference("1.mem"));
        Assert.assertNotNull(iterator);
    }

}