package com.example.accesswebcontentwithaselfsignedcertificate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class ConnectToHostWithSelfSignedSSL {

	private InputStream hostCertificate;
	private String hostURL;
	private SSLSocketFactory sslSocketFactory;

	public ConnectToHostWithSelfSignedSSL(InputStream hostCertificate,
			String hostURL) {
		this.hostCertificate = hostCertificate;
		this.hostURL = hostURL;
	}

	// Run GET request using HTTPS
	public String executeUsingHTTPS() {
		String executeResult = null;
		try {
			// Generate certificate from input stream
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate ca;
			ca = cf.generateCertificate(hostCertificate);

			// Create a KeyStore containing our trusted CAs
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			// Create an SSLContext that uses our TrustManager
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, tmf.getTrustManagers(), null);

			// Create SSLSocketFactory
			sslSocketFactory = new SSLSocketFactory(keyStore);

			// run connection
			try {
				executeResult = new connectUsingHTTPS().execute(hostURL).get();
				if (executeResult != null) {
					return executeResult;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		return executeResult;
	}

	// Background worker for HTTPS connection
	private class connectUsingHTTPS extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... uri) {
			HttpClient httpclient = new DefaultHttpClient();

			// Enable HttpClient to use SSL
			Scheme scheme = new Scheme("https", sslSocketFactory, 443);
			httpclient.getConnectionManager().getSchemeRegistry()
					.register(scheme);

			HttpResponse response;
			String responseString = null;
			try {
				response = httpclient.execute(new HttpGet(uri[0]));
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
				} else {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
			return responseString;
		}
	}

}
