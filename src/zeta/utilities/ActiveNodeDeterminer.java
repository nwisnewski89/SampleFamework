package zeta.utilities;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.openqa.selenium.remote.SessionId;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActiveNodeDeterminer {
    private String gridHostName;
    private int gridPort;
 
    private static final Logger LOGGER = Logger.getLogger(ActiveNodeDeterminer.class.getCanonicalName());
 
    /**
     *
     * @param gridHostName - The host where the Grid Hub is running.
     * @param gridPort - The port on which the Grid port is listening to.
     */
    public ActiveNodeDeterminer(String gridHostName, int gridPort) {
        this.gridHostName = gridHostName;
        this.gridPort = gridPort;
    }
 
    /**
     * @param sessionId - A {@link SessionId} object that represents a valid session.
     * @return - A {@link GridNode} object that represents the node to which the session was routed to.
     */
    public String getNodeInfoForSession(SessionId sessionId) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        String node = null;
        try {
            URL url = new URL("http://" + gridHostName + ":" + gridPort + "/grid/api/testsession?session=" + sessionId);
            BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST", url.toExternalForm());
            response = client.execute(new HttpHost(gridHostName, gridPort), r);
            JsonObject object = extractJson(response.getEntity());
            node = object.get("proxyId").getAsString();
        } catch (Exception e) {
            String errorMsg = "Failed to acquire remote webdriver node and port info. Root cause: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        LOGGER.info("Session " + sessionId + " was routed to " + node);
        return node;
    }
 
    private JsonObject extractJson(HttpEntity entity) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return new JsonParser().parse(builder.toString()).getAsJsonObject();
        }
    }
 
  
}
