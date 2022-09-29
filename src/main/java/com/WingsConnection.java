package main.java.com;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class WingsConnection {
    public final String server = "https://wings.disk.isi.edu/wings-portal";
    public final String username = "admin";
    public final String domain = "Enigma";
    public String password; 
    private CookieStore cookieStore;

    public WingsConnection (String password) {
        this.cookieStore = new BasicCookieStore();
        this.password = password;
        this.login();
    }

    private boolean login() {
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultCookieStore(this.cookieStore).build();
		HttpClientContext context = HttpClientContext.create();
		try {
			// Get a default domains page
			HttpGet securedResource = new HttpGet(this.server + "/users/" + this.username + "/domains");
			HttpResponse httpResponse = client.execute(securedResource, context);
			HttpEntity responseEntity = httpResponse.getEntity();
			String strResponse = EntityUtils.toString(responseEntity);
			// If it doesn't ask for a username/password form, then we are already logged in
			if (!strResponse.contains("j_security_check")) {
				return true;
			}

			// Login with the username/password
			HttpPost authpost = new HttpPost(this.server + "/j_security_check");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("j_username", this.username));
			nameValuePairs.add(new BasicNameValuePair("j_password", this.password));
			authpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			try {
				httpResponse = client.execute(authpost);
				responseEntity = httpResponse.getEntity();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			httpResponse = client.execute(securedResource);
			responseEntity = httpResponse.getEntity();
			EntityUtils.consume(responseEntity);

			// Check for Session ID to make sure we've logged in
			for (Cookie cookie : context.getCookieStore().getCookies()) {
				if (cookie.getName().equalsIgnoreCase("JSESSIONID")) {
                    System.out.println("TOKEN: " + cookie.getValue());
					return true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
