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

    public static com.aliyun.ens20171110.Client createEnsClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                .setAccessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"))
                .setEndpoint("ens.aliyuncs.com");
        return new com.aliyun.ens20171110.Client(config);
    }

    public static com.aliyun.slb20140515.Client createSlbClient() throws Exception {
        // 工程代码建议使用更安全的无AK方式，凭据配置方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credential);
        // Endpoint 请参考 https://api.aliyun.com/product/Slb
        config.endpoint = "slb.cn-heyuan.aliyuncs.com";
        return new com.aliyun.slb20140515.Client(config);
    }
}
