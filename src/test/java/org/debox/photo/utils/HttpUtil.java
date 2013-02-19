package org.debox.photo.utils;

/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 - 2013 Debox
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class HttpUtil {
    
    protected static final HttpClient HTTPCLIENT = new DefaultHttpClient();
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    
    public static String getString(String url) throws IOException {
        String targetUrl = getURL(url);
        HttpGet get = new HttpGet(targetUrl);
        HttpResponse response = HTTPCLIENT.execute(get);
        String str = null;
        try {
            str = EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
        } finally {
            get.releaseConnection();
        }
        return str;
    }
    
    public static String postAndGetStringResult(String url, String... parameters) throws IOException {
        String targetUrl = getURL(url);
        HttpPost post = new HttpPost(targetUrl);
        String result = null;
        try {
            HttpResponse response = post(post, parameters);
            result = EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
        } finally {
            post.releaseConnection();
        }
        return result;
    }
    
    public static JsonNode getJson(String url) throws IOException {
        String str = getString(url);
        JsonNode node = stringToJsonNode(str);
        return node;
    }
    
    public static JsonNode postAndGetJson(String url, String... param) throws IOException {
        String str = postAndGetStringResult(url);
        JsonNode node = MAPPER.readTree(str);
        return node;
    }
    
    public static int postAndGetHttpStatus(String url, String... param) throws IOException {
        String targetUrl = getURL(url);
        HttpPost post = new HttpPost(targetUrl);
        int result = -1;
        try {
            HttpResponse response = post(post, param);
            result = response.getStatusLine().getStatusCode();
        } finally {
            post.releaseConnection();
        }
        return result;
    }
    
        
    protected static JsonNode stringToJsonNode(String str) throws IOException {
        return MAPPER.readTree(str);
    }
    
    protected static HttpResponse post(HttpPost post, String... parameters) throws IOException {
        if (parameters.length % 2 != 0) {
            throw new IllegalArgumentException("You must give one parameter value for each parameter key");
        }
        
        List <NameValuePair> postParameters = new ArrayList<>(parameters.length / 2);
        for (int i = 0 ; i < parameters.length ; i += 2) {
            postParameters.add(new BasicNameValuePair(parameters[i], parameters[i+1]));
        }
        post.setEntity(new UrlEncodedFormEntity(postParameters));
        
        HttpResponse response = HTTPCLIENT.execute(post);
        return response;
    }
    
    protected static String getURL(String urlContext) {
        if (urlContext.startsWith("/")) {
            return ApplicationContextUtil.getBaseUrl() + urlContext;
        }
        return urlContext;
    }
    
}
