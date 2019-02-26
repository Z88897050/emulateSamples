/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import api.CreateIndex;
import bean.Constants;
import crud.SearchFiles;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class LuceneSsdb {

    private Server httpServer;

    private void stopHttpServer() {
        try {
            if (httpServer != null) httpServer.stop();
        } catch (Exception ignored) {
        }
    }

    private void listenApi() throws Exception {
        httpServer = new Server(Constants.httpPort);
        ServletHandler handler = new ServletHandler();
        httpServer.setHandler(handler);
        handler.addServletWithMapping(CreateIndex.class,"/createIndex");
        httpServer.start();
        httpServer.join();
    }

    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            System.err.printf("command error...\n%s", "USAGE:*.sh start|stop");
            System.exit(-1);
        }
        if (!"start".equals(args[0]) && !"stop".equals(args[0])) {
            System.err.printf("param %s is not defined\n%s", args[1], "USAGE:*.sh start|stop");
            System.exit(-1);
        }
        Logger logger = Logger.getLogger(LuceneSsdb.class);
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            if ("start".equals(args[0])) {
                final JettyServer jettyServer = new JettyServer();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    jettyServer.stop();
                    latch.countDown();
                }));
                jettyServer.start();
                latch.await();
            } else {

            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            System.exit(-1);
        }
        System.exit(0);
    }

    private void testOneSearch(Path indexPath, String query, int expectedHitCount) throws Exception {
        PrintStream outSave = System.out;
        String output = "";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PrintStream fakeSystemOut = new PrintStream(bytes, false, Charset.defaultCharset().name());
            System.setOut(fakeSystemOut);
            SearchFiles.main(new String[]{"-query", query, "-index", indexPath.toString()});
            fakeSystemOut.flush();
            output = bytes.toString(Charset.defaultCharset().name()); // intentionally use default encoding
//      assertTrue("output=" + output, output.contains(expectedHitCount + " total matching documents"));
        } finally {
            System.setOut(outSave);
        }
        System.out.println(output);
    }

    public void testIndexSearch() throws Exception {
        Path dir = getDataPath("test-files/docs");
//    Path indexDir = createTempDir("ContribDemoTest");
        Path indexDir = Paths.get("/home/ylzhang/projects/lucene-solr/lucene/demo/src/test/org/apache/lucene/demo/ContribDemoTest");
//    WriteIndex.main(new String[] { "-create", "-docs", dir.toString(), "-index", indexDir.toString()});
        testOneSearch(indexDir, "apache", 3);
        testOneSearch(indexDir, "patent", 8);
        testOneSearch(indexDir, "lucene", 0);
        testOneSearch(indexDir, "gnu", 6);
        testOneSearch(indexDir, "derivative", 8);
        testOneSearch(indexDir, "license", 13);
    }

    private Path getDataPath(String s) {
        return null;
    }
}
