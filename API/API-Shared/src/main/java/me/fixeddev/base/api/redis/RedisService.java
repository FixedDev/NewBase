package me.fixeddev.base.api.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.fixeddev.base.api.service.AbstractService;
import me.fixeddev.minecraft.config.Configuration;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.SingleServerConfig;

import java.util.concurrent.atomic.AtomicBoolean;

public class RedisService implements AbstractService {

    private Configuration config;
    private ObjectMapper mapper;

    private RedissonClient redisson;
    private ListeningExecutorService executorService;

    private AtomicBoolean started;

    @Inject
    protected RedisService(ListeningExecutorService executorService, Configuration configuration, ObjectMapper mapper) {
        this.executorService = executorService;
        config = configuration;
        this.mapper = mapper;

        this.started = new AtomicBoolean(false);
    }

    public RedissonClient getRedisson() {
        if(!started.get()){
            throw new IllegalStateException("The service must be initialized first!");
        }

        return redisson;
    }

    @Override
    public void doStart() {
        String password = config.getString("redis.password", "");

        org.redisson.config.Config redissonConfig = new org.redisson.config.Config()
                .setExecutor(executorService);

        SingleServerConfig serverConfig = redissonConfig.useSingleServer()
                .setAddress("redis://" + config.getString("redis.host", "localhost") + ":" + config.getInt("redis.port", 6379));

        redissonConfig.setCodec(new RedissonJacksonCodec(mapper, false));

        if (!password.trim().isEmpty()) {
            serverConfig.setPassword(password);
        }

        redisson = Redisson.create(redissonConfig);
    }

    @Override
    public void doStop() {
        redisson.shutdown();
    }

    @Override
    public void start() throws Exception {
        if (!isStarted().compareAndSet(false, true)) {
            return;
        }

        doStart();
    }

    @Override
    public AtomicBoolean isStarted() {
        return started;
    }
}
