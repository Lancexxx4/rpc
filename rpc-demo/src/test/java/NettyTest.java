import com.example.netty.AppClient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NettyTest {
    @Test
    public void testByteBuf(){
        ByteBuf header = Unpooled.buffer();
        ByteBuf body = Unpooled.buffer();

        //通过逻辑组装而不是物理拷贝，实现在jvm中
        CompositeByteBuf byteBuf = Unpooled.compositeBuffer();
        byteBuf.addComponents(header,body);
    }

    @Test
    public void testWrapper(){
        byte[] by1 = new byte[1024];
        byte[] by2 = new byte[1024];
        ByteBuf byteBuf = Unpooled.wrappedBuffer(by1, by2);
    }

    @Test
    public void testSlice(){
        ByteBuf byteBuf = Unpooled.compositeBuffer();
        ByteBuf byteBuf1 = byteBuf.slice(0,5);
        ByteBuf byteBuf2 = byteBuf.slice(6,10);
    }

    @Test
    public void testMessage() throws IOException {
        ByteBuf message = Unpooled.buffer();
        message.writeBytes("rpc".getBytes(StandardCharsets.UTF_8));
        message.writeByte(1);
        message.writeShort(125);
        message.writeInt(256);
        message.writeByte(1);
        message.writeByte(0);
        message.writeByte(2);
        message.writeLong(251455L);
        //用对象流转化为字节数组
        AppClient appClient = new AppClient();

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
        objectOutputStream.writeObject(appClient);
        byte[] bytes = byteOutputStream.toByteArray();
        message.writeBytes(bytes);

        printAsBinary(message);
    }

    public static void printAsBinary(ByteBuf byteBuf){
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(),bytes);

        String binaryString = ByteBufUtil.hexDump(bytes);
        StringBuilder formattedBinary = new StringBuilder();

        for (int i = 0;i<binaryString.length();i+=2){
            formattedBinary.append(binaryString.substring(i,i+2)).append(" ");
        }

        System.out.println("Binary representation: "+ formattedBinary.toString());
    }

    //压缩byte数组
    @Test
    public void testCompress() throws IOException {
        byte[] byteBuf = new byte[]{12,24,24,32,24,23,12,24,24,32,24,23,12,24,24,32,24,23,12,24,24,32,24,23,12,24,24,32,24,23,12,24,24,32,24,23,12,24,24,32,24,23,12,24,24,32,24,23,12,24,24,32,24,23};

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);

        gzip.write(byteBuf);
        gzip.finish();

        byte[] temp = bos.toByteArray();
        System.out.println(byteBuf.length+"->"+temp.length);
        System.out.println(Arrays.toString(temp));
    }

    //解压byte数组，byte格式有要求
    @Test
    public void testDeCompress() throws IOException {
        byte[] byteBuf = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, -1, -29, -111, -112, 80, -112, 16, -25, 33, -111, 4, 0, 45, -45, -64, 29, 54, 0, 0, 0};

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuf);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

        byte[] temp = gzipInputStream.readAllBytes();
        System.out.println(byteBuf.length+"->"+temp.length);
        System.out.println(Arrays.toString(temp));
    }
}
