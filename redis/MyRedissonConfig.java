/**
 * @author long
 * @date 2020/12/10
 */
@Configuration
public class MyRedissonConfig {

    @Autowired
    private SpcConfig spcConfig;

    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + spcConfig.getRedisHost() + ":" + spcConfig.getRedisPort());
        return Redisson.create(config);
    }
}