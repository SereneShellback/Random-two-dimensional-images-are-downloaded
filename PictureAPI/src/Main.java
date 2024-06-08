import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    private static final String[] API_URLS = {
            // "https://img.xjh.me/random_img.php",
            "https://api.yimian.xyz/img",
            "https://www.loliapi.com/bg/",
            "https://api.yimian.xyz/img",
            // "https://t.mwm.moe/mp",
            "https://t.mwm.moe/fj",
            //"http://www.98qy.com/sjbz/api.php",
            "https://sex.nyan.xyz/api/v2/img",
            "https://imgapi.xl0408.top/index.php",
            "https://api.dujin.org/pic/yuanshen/",
            "https://img.paulzzh.com/touhou/random",
            "https://moe.jitsu.top/r18",
            "https://api.oick.cn/random/api.php",
            "https://api.lolimi.cn/API/dmt/api.php?type=image",
           // "https://api.lolimi.cn/API/yuan/?type=image",
            "https://api.asxe.vip/random.php",
            "https://api.asxe.vip/scenery.php",
            "https://tuapi.eees.cc/api.php?category=dongman&type=302",
            "https://api.horosama.com/random.php",
            "https://api.likepoems.com/img/pc"
    };

    public static void main(String[] args) {
        try {
            int numIterations = 5;  // 循环次数
            for (int i = 0; i < numIterations; i++) {
                String apiUrl = selectRandomApi();
                System.out.println("Using API: " + apiUrl);

                byte[] imageData = getImageDataFromApi(apiUrl);
                String contentType = "image/jpeg"; // 替换为实际的内容类型
                String fileName = determineImageTypeAndSave(imageData, contentType);
                System.out.println("Image downloaded as: " + fileName);

                // 休眠1秒
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error downloading image.");
        }
    }

    private static String selectRandomApi() {
        Random random = new Random();
        return API_URLS[random.nextInt(API_URLS.length)];
    }

    private static byte[] getImageDataFromApi(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // 获取 Content-Type
        String contentType = connection.getHeaderField("Content-Type");

        // 处理压缩内容
        InputStream inputStream;
        if ("gzip".equalsIgnoreCase(connection.getContentEncoding())) {
            inputStream = new GZIPInputStream(connection.getInputStream());
        } else {
            inputStream = connection.getInputStream();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();

        return outputStream.toByteArray();
    }

    private static String determineImageTypeAndSave(byte[] imageData, String contentType) throws IOException {
        if (contentType == null || contentType.isEmpty()) {
            throw new IllegalArgumentException("Content-Type cannot be null or empty");
        }

        String extension = contentType.split("/")[1].toLowerCase();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);
        String fileName = "image_" + timestamp + "." + extension;
        Files.write(Paths.get(fileName), imageData);
        return fileName;
    }
}
