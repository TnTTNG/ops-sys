package ops.example.backend.aliyun.utils;

import com.aliyun.ecs20140526.Client;
import com.aliyun.teaopenapi.models.Config;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-30-0:15
 */
public class ClientUtils {
    public static com.aliyun.rds20140815.Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                .setAccessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"))
                .setEndpoint("rds.aliyuncs.com");
        return new com.aliyun.rds20140815.Client(config);
    }

    public static Client createEcsClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                .setAccessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"))
                .setEndpoint("ecs.aliyuncs.com");
        return new Client(config);
    }
}
