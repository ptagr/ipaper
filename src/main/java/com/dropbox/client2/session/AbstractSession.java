/*
 * Copyright (c) 2009-2011 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.dropbox.client2.session;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.dropbox.client2.DropboxAPI;

/**
 * Keeps track of a logged in user and contains configuration options for the
 * {@link DropboxAPI}. This is a base class to use for creating your own
 * {@link Session}s.
 */
public abstract class AbstractSession implements Session {

    private static final String API_SERVER = "api.dropbox.com";
    private static final String CONTENT_SERVER = "api-content.dropbox.com";
    private static final String WEB_SERVER = "www.dropbox.com";

    /** How long connections are kept alive. */
    private static final int KEEP_ALIVE_DURATION_SECS = 20;

    /** How often the monitoring thread checks for connections to close. */
    private static final int KEEP_ALIVE_MONITOR_INTERVAL_SECS = 5;

    /** The default timeout for client connections. */
    private static final int DEFAULT_TIMEOUT_MILLIS = 30000; // 30 seconds

    private final AccessType accessType;
    private final AppKeyPair appKeyPair;
    private AccessTokenPair accessTokenPair = null;

    private HttpClient client = null;

    /**
     * Creates a new session with the given app key and secret, and access
     * type. The session will not be linked because it has no access token pair.
     */
    public AbstractSession(AppKeyPair appKeyPair, AccessType type) {
        this(appKeyPair, type, null);
    }

    /**
     * Creates a new session with the given app key and secret, and access
     * type. The session will be linked to the account corresponding to the
     * given access token pair.
     */
    public AbstractSession(AppKeyPair appKeyPair, AccessType type,
            AccessTokenPair accessTokenPair) {
        if (appKeyPair == null) throw new IllegalArgumentException("'appKeyPair' must be non-null");
        if (type == null) throw new IllegalArgumentException("'type' must be non-null");

        this.appKeyPair = appKeyPair;
        this.accessType = type;
        this.accessTokenPair = accessTokenPair;
    }

    /**
     * Links the session with the given access token and secret.
     */
    public void setAccessTokenPair(AccessTokenPair accessTokenPair) {
        if (accessTokenPair == null) throw new IllegalArgumentException("'accessTokenPair' must be non-null");
        this.accessTokenPair = accessTokenPair;
    }

    
    public AppKeyPair getAppKeyPair() {
        return appKeyPair;
    }

    
    public AccessTokenPair getAccessTokenPair() {
        return accessTokenPair;
    }

    
    public AccessType getAccessType() {
        return accessType;
    }

    /**
     * {@inheritDoc}
     * <br/><br/>
     * The default implementation always returns {@code Locale.ENLISH}, but you
     * are highly encouraged to localize your application and return the system
     * locale instead. Note: as of the time this was written, Dropbox supports
     * the de, en, es, fr, and ja locales - if you use a locale other than
     * these, messages will be returned in English. However, it is good
     * practice to pass along the correct locale as we will add more languages
     * in the future.
     */
    
    public Locale getLocale() {
        return Locale.ENGLISH;
    }

    
    public boolean isLinked() {
        return accessTokenPair != null;
    }

    
    public void unlink() {
        accessTokenPair = null;
    }

    /**
     * Signs the request by using's OAuth's HTTP header authorization scheme
     * and the PLAINTEXT signature method.  As such, this should only be used
     * over secure connections (i.e. HTTPS).  Using this over regular HTTP
     * connections is completely insecure.
     *
     * @see Session#sign
     */
    
    public void sign(HttpRequest request) {
        request.addHeader("Authorization",
                buildOAuthHeader(this.appKeyPair, this.accessTokenPair));
    }

    private static String buildOAuthHeader(AppKeyPair appKeyPair,
            AccessTokenPair signingTokenPair) {
        StringBuilder buf = new StringBuilder();
        buf.append("OAuth oauth_version=\"1.0\"");
        buf.append(", oauth_signature_method=\"PLAINTEXT\"");
        buf.append(", oauth_consumer_key=\"").append(
                encode(appKeyPair.key)).append("\"");

        /*
         * TODO: This is hacky.  The 'signingTokenPair' is null only in auth
         * step 1, when we acquire a request token.  We really should have two
         * different buildOAuthHeader functions for the two different
         * situations.
         */
        String sig;
        if (signingTokenPair != null) {
            buf.append(", oauth_token=\"").append(
                    encode(signingTokenPair.key)).append("\"");
            sig = encode(appKeyPair.secret) + "&"
                    + encode(signingTokenPair.secret);
        } else {
            sig = encode(appKeyPair.secret) + "&";
        }
        buf.append(", oauth_signature=\"").append(sig).append("\"");

        // Note: Don't need nonce or timestamp since we do everything over SSL.
        return buf.toString();
    }

    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            AssertionError ae = new AssertionError("UTF-8 isn't available");
            ae.initCause(ex);
            throw ae;
        }
    }

    /**
     * {@inheritDoc}
     * <br/><br/>
     * The default implementation always returns null.
     */
    
    public synchronized ProxyInfo getProxyInfo() {
        return null;
    }

    /**
     * {@inheritDoc}
     * <br/><br/>
     * The default implementation does all of this and more, including using
     * a connection pool and killing connections after a timeout to use less
     * battery power on mobile devices. It's unlikely that you'll want to
     * change this behavior.
     */
    
    public synchronized HttpClient getHttpClient() {
        if (client == null) {
            // Set up default connection params. There are two routes to
            // Dropbox - api server and content server.
            HttpParams connParams = new BasicHttpParams();
            ConnManagerParams.setMaxConnectionsPerRoute(connParams, new ConnPerRoute() {
                
                public int getMaxForRoute(HttpRoute route) {
                    return 10;
                }
            });
            ConnManagerParams.setMaxTotalConnections(connParams, 20);

            // Set up scheme registry.
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(
                    new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(
                    new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

            DBClientConnManager cm = new DBClientConnManager(connParams,
                    schemeRegistry);

            // Set up client params.
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
            HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            HttpProtocolParams.setUserAgent(httpParams,
                    "OfficialDropboxJavaSDK/" + DropboxAPI.SDK_VERSION);

            client = new DefaultHttpClient(cm, httpParams) {
                
                protected ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
                    return new DBKeepAliveStrategy();
                }
            };
        }

        return client;
    }

    /**
     * {@inheritDoc}
     * <br/><br/>
     * The default implementation always sets a 30 second timeout.
     */
    
    public void setRequestTimeout(HttpUriRequest request) {
        HttpParams reqParams = request.getParams();
        HttpConnectionParams.setSoTimeout(reqParams, DEFAULT_TIMEOUT_MILLIS);
        HttpConnectionParams.setConnectionTimeout(reqParams, DEFAULT_TIMEOUT_MILLIS);
    }

    
    public String getAPIServer() {
        return API_SERVER;
    }

    
    public String getContentServer() {
        return CONTENT_SERVER;
    }

    
    public String getWebServer() {
        return WEB_SERVER;
    }

    private static class DBKeepAliveStrategy implements ConnectionKeepAliveStrategy {
        
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            // Keep-alive for the shorter of 20 seconds or what the server specifies.
            long timeout = KEEP_ALIVE_DURATION_SECS * 1000;

            HeaderElementIterator i = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (i.hasNext()) {
                HeaderElement element = i.nextElement();
                String name = element.getName();
                String value = element.getValue();
                if (value != null && name.equalsIgnoreCase("timeout")) {
                    try {
                        timeout = Math.min(timeout, Long.parseLong(value) * 1000);
                    } catch (NumberFormatException e) {}
                }
            }

            return timeout;
        }
    }

    private static class DBClientConnManager extends ThreadSafeClientConnManager {
        public DBClientConnManager(HttpParams params, SchemeRegistry schreg) {
            super(params, schreg);
        }

        
        public ClientConnectionRequest requestConnection(HttpRoute route,
                Object state) {
            IdleConnectionCloserThread.ensureRunning(this, KEEP_ALIVE_DURATION_SECS, KEEP_ALIVE_MONITOR_INTERVAL_SECS);
            return super.requestConnection(route, state);
        }
    }

    private static class IdleConnectionCloserThread extends Thread {
        private final DBClientConnManager manager;
        private final int idleTimeoutSeconds;
        private final int checkIntervalMs;
        private static IdleConnectionCloserThread thread = null;

        public IdleConnectionCloserThread(DBClientConnManager manager,
                int idleTimeoutSeconds, int checkIntervalSeconds) {
            super();
            this.manager = manager;
            this.idleTimeoutSeconds = idleTimeoutSeconds;
            this.checkIntervalMs = checkIntervalSeconds * 1000;
        }

        public static synchronized void ensureRunning(
                DBClientConnManager manager, int idleTimeoutSeconds,
                int checkIntervalSeconds) {
            if (thread == null) {
                thread = new IdleConnectionCloserThread(manager,
                        idleTimeoutSeconds, checkIntervalSeconds);
                thread.start();
            }
        }

        
        public void run() {
            try {
                while (true) {
                    synchronized (this) {
                        wait(checkIntervalMs);
                    }
                    manager.closeExpiredConnections();
                    manager.closeIdleConnections(idleTimeoutSeconds, TimeUnit.SECONDS);
                    synchronized (IdleConnectionCloserThread.class) {
                        if (manager.getConnectionsInPool() == 0) {
                            thread = null;
                            return;
                        }
                    }
                }
            } catch (InterruptedException e) {
                thread = null;
            }
        }
    }
}
