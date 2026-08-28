package com.pm.leetpain.judge;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import com.pm.leetpain.Domain.ExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class DockerExecutor {

    private final DockerClient dockerClient;

    public static final String JAVA_IMAGE = "eclipse-temurin:21-jdk-alpine";
    public DockerExecutor() {
        String dockerHost = resolveDockerHost();
        log.info("Using Docker host: {}", dockerHost);
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();

        ZerodepDockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .connectionTimeout(Duration.ofSeconds(5))
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    private String resolveDockerHost() {
        String envHost = System.getenv("DOCKER_HOST");
        if (envHost != null) {
            String normalized = envHost.trim();
            if (!normalized.isEmpty()
                    && !"unix://localhost:2375".equalsIgnoreCase(normalized)
                    && !"tcp://localhost:2375".equalsIgnoreCase(normalized)) {
                return normalized;
            }
        }

        Path dockerDesktopSocket = Path.of(System.getProperty("user.home"), ".docker", "run", "docker.sock");
        if (Files.exists(dockerDesktopSocket)) {
            return "unix://" + dockerDesktopSocket.toAbsolutePath();
        }
        return "unix:///var/run/docker.sock";
    }

    /**
     * Create a container that bind-mounts hostDir -> /app (optionally read-only), uses a tmpfs for /work,
     * and applies resource/security limits appropriate for running untrusted code.
     */
    public CreateContainerResponse createContainerWithBind(String imageName, String command, String hostDir, boolean hostDirReadOnly) throws InterruptedException {
        String bindSpec = hostDir + ":/app:" + (hostDirReadOnly ? "ro" : "rw");
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withBinds(Bind.parse(bindSpec))
                .withTmpFs(Collections.singletonMap("/work", "size=33554432")) // 32MB tmpfs
                .withMemory(256L * 1024L * 1024L) // 256MB
                .withMemorySwap(256L * 1024L * 1024L)
                .withNetworkMode("none")
                .withReadonlyRootfs(true)
                .withCapDrop(Capability.ALL)
                .withPidsLimit(64L);

        // check if image exists locally, if not pull it
        PullImageIfNotExists(imageName);

        CreateContainerResponse response = dockerClient.createContainerCmd(imageName)
                .withCmd("sh", "-c", command)
                .withHostConfig(hostConfig)
                .exec();
        return response;
    }

    public void PullImageIfNotExists(String imageName)  {
        List<Image> images = dockerClient.listImagesCmd().exec();
        boolean imageExists = images.stream()
                .filter(image -> image.getRepoTags() != null) // Avoid NPE on untagged images
                .flatMap(image -> Arrays.stream(image.getRepoTags()))
                .anyMatch(tag -> tag.equals(imageName));
        if(imageExists){
            log.debug("Image {} already exists locally, skipping pull.", imageName);
            return;
        }
        log.debug("Pulling image {} from Docker registry...", imageName);
        try{
            dockerClient.pullImageCmd(imageName).start().awaitCompletion();
            log.debug("Image {} pulled successfully.", imageName);
        } catch (InterruptedException e) {
            log.debug("Image pull interrupted for {}.", imageName);
            throw new RuntimeException(e);
        }

    }

    public void startContainer(String containerId) {
         dockerClient.startContainerCmd(containerId).exec();
    }


    public void killContainer(String containerId) {
        dockerClient.killContainerCmd(containerId).exec();
    }

    public void removeContainer(String containerId) {
        try {
            dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        } catch (Exception ignored) {}
    }

    /**
     * Wait for container to exit up to timeoutSeconds. Returns exit code or null if timed out.
     */
    public Integer waitForContainer(String containerId, long timeoutSeconds) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> f = es.submit(() -> dockerClient
                .waitContainerCmd(containerId)
                .exec(new WaitContainerResultCallback())
                .awaitStatusCode());
        try {
            return f.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException te) {
            f.cancel(true);
            return null;
        } catch (InterruptedException | ExecutionException e) {
            return -1;
        } finally {
            es.shutdownNow();
        }
    }

    /**
     * Collect container stdout/stderr as a single string. Blocks until logs stream completes.
     */
    public ExecutionResult getContainerLogs(String containerId) {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        LogContainerResultCallback callback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                try {
                    if(item.getStreamType() == StreamType.STDOUT) {
                        stdout.write(item.getPayload());
                    } else if(item.getStreamType() == StreamType.STDERR) {
                        stderr.write(item.getPayload());
                    }
                } catch (IOException ignored) {
                }
            }
        };

        try {
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withTailAll()
                    .exec(callback)
                    .awaitCompletion();
        } catch (InterruptedException | RuntimeException e) {
            // fall through and return what we have
        }

        return new ExecutionResult(
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8)
        );
        }

}
