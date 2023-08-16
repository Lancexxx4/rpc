import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ZookeeperTest {
    ZooKeeper zooKeeper;

    @Before
    public void createZK(){
        //参数，connectionString 和 sessionTimeout
        String connectionString = "127.0.0.1:2181";
        int sessionTimeout = 10000;

        try {
            zooKeeper = new ZooKeeper(connectionString,sessionTimeout,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreatePNode(){
        String result = null;
        try {
            result = zooKeeper.create("/rpc", "hello".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(zooKeeper != null){
                    zooKeeper.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(result);
    }

    @Test
    public void testDeletePNode(){
        String result = null;
        try {
            //version: 乐观锁  -1表示无视版本号
            zooKeeper.delete("/rpc", -1);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(result);
    }
}
