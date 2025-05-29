// This file is auto-generated, don't edit it. Thanks.
package ops.example.backend.aliyun;

import com.aliyun.rds20140815.Client;
import com.aliyun.rds20140815.models.DescribeDBInstancesRequest;
import com.aliyun.rds20140815.models.DescribeDBInstancesResponse;
import com.aliyun.rds20140815.models.DescribeDBInstancesResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import ops.example.backend.aliyun.entity.RdsDbInstance;
import ops.example.backend.aliyun.mapper.RdsDbInstanceMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Sample {

    /**
     * <b>description</b> :
     * <p>使用凭据初始化账号Client</p>
     * @return Client
     *
     * @throws Exception
     */
    public static com.aliyun.rds20140815.Client createClient() throws Exception {
        // 工程代码建议使用更安全的无AK方式，凭据配置方式请参见：https://help.aliyun.com/document_detail/378657.html。
        com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credential);
        // Endpoint 请参考 https://api.aliyun.com/product/Rds
        config.endpoint = "rds.aliyuncs.com";
        return new com.aliyun.rds20140815.Client(config);
    }

    /**
     * 将ISO 8601格式的日期字符串转换为MySQL datetime格式
     */
    private static String convertToMySQLDateTime(String isoDateTime) {
        if (isoDateTime == null) {
            return null;
        }
        Instant instant = Instant.parse(isoDateTime);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static void main(String[] args_) throws Exception {
        // 初始化Spring上下文
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        RdsDbInstanceMapper rdsDbInstanceMapper = context.getBean(RdsDbInstanceMapper.class);

        Client client = Sample.createClient();
        DescribeDBInstancesRequest describeDBInstancesRequest = new DescribeDBInstancesRequest()
                .setRegionId("cn-heyuan");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 调用API获取数据
            DescribeDBInstancesResponse response = client.describeDBInstancesWithOptions(describeDBInstancesRequest, runtime);
            DescribeDBInstancesResponseBody body = response.getBody();
            List<DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance> items = body.getItems().getDBInstance();

            // 打印JSON数据
            // ObjectMapper mapper = new ObjectMapper();
            // System.out.println("API返回的JSON数据：");
            // System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(items));

            // 转换为实体对象并保存到数据库
            List<RdsDbInstance> instances = new ArrayList<>();
            for (DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance item : items) {
                RdsDbInstance instance = new RdsDbInstance();
                instance.setDbInstanceId(item.getDBInstanceId());
                instance.setDescription(item.getDBInstanceDescription());
                instance.setRegionId(item.getRegionId());
                instance.setStatus(item.getDBInstanceStatus());
                instance.setEngine(item.getEngine());
                instance.setEngineVersion(item.getEngineVersion());
                // 转换日期格式
                instance.setCreatedAt(convertToMySQLDateTime(item.getCreateTime()));
                instance.setUpdatedAt(convertToMySQLDateTime(item.getCreateTime()));
                instances.add(instance);
            }

            // 批量插入数据库
            int count = rdsDbInstanceMapper.batchInsert(instances);
            System.out.println("成功写入数据库 " + count + " 条记录");

        } catch (TeaException error) {
            System.out.println("阿里云API调用错误：");
            System.out.println("错误信息：" + error.getMessage());
            if (error.getData() != null) {
                System.out.println("诊断信息：" + error.getData().get("Recommend"));
            }
        } catch (Exception _error) {
            System.out.println("程序执行错误：");
            System.out.println("错误信息：" + _error.getMessage());
            _error.printStackTrace();
        }
    }
}
