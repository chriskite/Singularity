package com.hubspot.singularity.s3.base.config;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.hubspot.singularity.runner.base.configuration.BaseRunnerConfiguration;
import com.hubspot.singularity.runner.base.configuration.Configuration;
import com.hubspot.singularity.runner.base.constraints.DirectoryExists;

@Configuration("/etc/singularity.s3base.yaml")
public class SingularityS3Configuration extends BaseRunnerConfiguration {
  public static final String ARTIFACT_CACHE_DIRECTORY = "artifact.cache.directory";

  public static final String S3_ACCESS_KEY = "s3.access.key";
  public static final String S3_SECRET_KEY = "s3.secret.key";

  public static final String S3_CHUNK_SIZE = "s3.downloader.chunk.size";
  public static final String S3_DOWNLOAD_TIMEOUT_MILLIS = "s3.downloader.timeout.millis";

  public static final String LOCAL_DOWNLOAD_HTTP_PORT = "s3.downloader.http.port";
  public static final String LOCAL_DOWNLOAD_HTTP_DOWNLOAD_PATH = "s3.downloader.http.download.path";

  @NotEmpty
  @DirectoryExists
  @JsonProperty
  private String cacheDirectory;

  @NotNull
  @JsonProperty
  private SingularityS3Credentials defaultCredentials;

  @NotNull
  @JsonProperty
  private Map<String, SingularityS3Credentials> bucketCredentials = Collections.emptyMap();

  @Min(1)
  @JsonProperty
  private long s3ChunkSize = 104857600;

  @Min(1)
  @JsonProperty
  private long s3DownloadTimeoutMillis = TimeUnit.MINUTES.toMillis(1);

  @Min(0)
  @JsonProperty
  private int localDownloadHttpPort = 7070;

  @NotEmpty
  @JsonProperty
  private String localDownloadPath = "/download";

  public SingularityS3Configuration() {
    super(Optional.<String>absent());
  }

  public String getCacheDirectory() {
    return cacheDirectory;
  }

  public void setCacheDirectory(String cacheDirectory) {
    this.cacheDirectory = cacheDirectory;
  }

  public SingularityS3Credentials getDefaultCredentials() {
    return defaultCredentials;
  }

  public void setDefaultCredentials(SingularityS3Credentials defaultCredentials) {
    this.defaultCredentials = defaultCredentials;
  }

  public Map<String, SingularityS3Credentials> getBucketCredentials() {
    return bucketCredentials;
  }

  public void setBucketCredentials(Map<String, SingularityS3Credentials> bucketCredentials) {
    this.bucketCredentials = bucketCredentials;
  }

  public long getS3ChunkSize() {
    return s3ChunkSize;
  }

  public void setS3ChunkSize(long s3ChunkSize) {
    this.s3ChunkSize = s3ChunkSize;
  }

  public long getS3DownloadTimeoutMillis() {
    return s3DownloadTimeoutMillis;
  }

  public void setS3DownloadTimeoutMillis(long s3DownloadTimeoutMillis) {
    this.s3DownloadTimeoutMillis = s3DownloadTimeoutMillis;
  }

  public int getLocalDownloadHttpPort() {
    return localDownloadHttpPort;
  }

  public void setLocalDownloadHttpPort(int localDownloadHttpPort) {
    this.localDownloadHttpPort = localDownloadHttpPort;
  }

  public String getLocalDownloadPath() {
    return localDownloadPath;
  }

  public void setLocalDownloadPath(String localDownloadPath) {
    this.localDownloadPath = localDownloadPath;
  }

  @JsonIgnore
  public SingularityS3Credentials getCredentialsForBucket(String bucketName) {
    return bucketCredentials.containsKey(bucketName) ? bucketCredentials.get(bucketName) : defaultCredentials;
  }

  @Override
  public String toString() {
    return "SingularityS3Configuration[" +
            "cacheDirectory='" + cacheDirectory + '\'' +
            ", defaultCredentials=" + defaultCredentials +
            ", bucketCredentials=" + bucketCredentials +
            ", s3ChunkSize=" + s3ChunkSize +
            ", s3DownloadTimeoutMillis=" + s3DownloadTimeoutMillis +
            ", localDownloadHttpPort=" + localDownloadHttpPort +
            ", localDownloadPath='" + localDownloadPath + '\'' +
            ']';
  }

  @Override
  public void updateFromProperties(Properties properties) {
    if (properties.containsKey(ARTIFACT_CACHE_DIRECTORY)) {
      setCacheDirectory(properties.getProperty(ARTIFACT_CACHE_DIRECTORY));
    }

    if (properties.containsKey(S3_ACCESS_KEY) && properties.containsKey(S3_SECRET_KEY)) {
      setDefaultCredentials(new SingularityS3Credentials(properties.getProperty(S3_ACCESS_KEY), properties.getProperty(S3_SECRET_KEY)));
    }

    if (properties.containsKey(S3_CHUNK_SIZE)) {
      setS3ChunkSize(Long.parseLong(properties.getProperty(S3_CHUNK_SIZE)));
    }

    if (properties.containsKey(S3_DOWNLOAD_TIMEOUT_MILLIS)) {
      setS3DownloadTimeoutMillis(Long.parseLong(properties.getProperty(S3_DOWNLOAD_TIMEOUT_MILLIS)));
    }

    if (properties.containsKey(LOCAL_DOWNLOAD_HTTP_DOWNLOAD_PATH)) {
      setLocalDownloadPath(properties.getProperty(LOCAL_DOWNLOAD_HTTP_DOWNLOAD_PATH));
    }

    if (properties.containsKey(LOCAL_DOWNLOAD_HTTP_PORT)) {
      setLocalDownloadHttpPort(Integer.parseInt(properties.getProperty(LOCAL_DOWNLOAD_HTTP_PORT)));
    }
  }
}
