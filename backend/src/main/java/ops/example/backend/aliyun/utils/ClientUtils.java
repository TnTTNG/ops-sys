package ops.example.backend.aliyun.utils;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-29-18:56
 */
public class ClientUtils {
    public static com.aliyun.rds20140815.Client createClient() throws Exception {
        // 工程代码建议使用更安全的无AK方式，凭据配置方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credential);
        // Endpoint 请参考 https://api.aliyun.com/product/Rds
        config.endpoint = "rds.aliyuncs.com";
        return new com.aliyun.rds20140815.Client(config);
    }
}
