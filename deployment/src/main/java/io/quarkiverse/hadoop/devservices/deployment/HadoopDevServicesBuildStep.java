/*
 * @package
 *
 * @file
 * @copyright 2023-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 */

package io.quarkiverse.hadoop.devservices.deployment;

import io.quarkus.datasource.deployment.spi.DevServicesDatasourceResultBuildItem;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CuratedApplicationShutdownBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HadoopDevServicesBuildStep {
    private static final Logger LOGGER = LoggerFactory.getLogger(HadoopDevServicesBuildStep.class);

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("devservices-hadoop");
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    HadoopDataSourceBuildItem generateDevServiceDataSource(
            Optional<DevServicesDatasourceResultBuildItem> item)
    {
        if (item.isEmpty()) {
            return null;
        }

        DevServicesDatasourceResultBuildItem.DbResult defaultDatasource = item.get().getDefaultDatasource();

        if (defaultDatasource == null) {
            return null;
        }

        Map<String, String> configProperties = defaultDatasource.getConfigProperties();

        return new HadoopDataSourceBuildItem(
                configProperties.getOrDefault("hadoop.defaultFS", "hdfs://localhost:9000"),
                configProperties.getOrDefault("hadoop.defaultFS", "hdfs://localhost:9000"));
    }

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    ServiceStartBuildItem enableHadoop(List<HadoopDataSourceBuildItem> dataSources,
                                       CuratedApplicationShutdownBuildItem closeBuildItem)
    {
        File baseDir;
        MiniDFSCluster cluster;
        Configuration configuration = new Configuration();

        try {
            this.baseDir = Files.createTempDirectory("test_hdfs").toFile().getAbsoluteFile();

            configuration.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, this.baseDir.getAbsolutePath());

            MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(configuration);

            this.cluster = builder.build();

            retVal = Map.of("hadoop.defaultFS",
                    String.format("hdfs://localhost:%d", this.cluster.getNameNodePort()));

            LOGGER.info(String.format("\n---\nCluster is ready\n URL = %s\nPath = %s\n---\n",
                    this.cluster.getHttpUri(0), this.cluster.getDataDirectory()));
        } catch (IOException e) {
            LOGGER.error("Cannot create mini cluster ", e);
        }

        closeBuildItem.addCloseTask(new Runnable() {
            @Override
            public void run() {
                log.info("Closing SqlPad!");
                container.stop();
            }
        }, false);
        return new ServiceStartBuildItem("hadoopp");
    }
}




    @Override
    public Map<String, String> start() {
        Map<String, String> retVal = Collections.emptyMap();



        return retVal;
    }

    @Override
    public void stop() {
        if (null != this.cluster) {
            this.cluster.shutdown();
        }

        if (null != this.baseDir) {
            try {
                FileUtils.deleteDirectory(this.baseDir);
            } catch (IOException e) {
                /* Do nothing */
            }
        }
    }
