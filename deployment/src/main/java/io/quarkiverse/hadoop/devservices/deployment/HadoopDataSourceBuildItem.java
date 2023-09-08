/*
 * @package
 *
 * @file
 * @copyright 2023-present Christoph Kappel <christoph@unexist.dev>
 * @version \$Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 */

package io.quarkiverse.hadoop.devservices.deployment;

import io.quarkus.builder.item.MultiBuildItem;

import java.util.Objects;

public final class HadoopDataSourceBuildItem extends MultiBuildItem {
    private final String defaultFS;
    private final String filePath;

    public HadoopDataSourceBuildItem(String defaultFS, String filePath) {
        this.defaultFS = defaultFS;
        this.filePath = filePath;
    }

    public String getDefaultFS() {
        return defaultFS;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HadoopDataSourceBuildItem that = (HadoopDataSourceBuildItem) o;
        return Objects.equals(defaultFS, that.defaultFS) && Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultFS, filePath);
    }
}