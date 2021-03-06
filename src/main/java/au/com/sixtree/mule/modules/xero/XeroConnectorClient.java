package au.com.sixtree.mule.modules.xero;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.scribe.exceptions.OAuthException;

/**
 * A helper class which encapsulates client logic (connection & 
 * authentication) for comms to Xero.  
 * 
 * @author Sixtree 
 */
public class XeroConnectorClient {
	
    private String xeroApiUrl;
    private String consumerKey;
    private String consumerSecret;
    private String accessKey;
    private String accessSecret;
    private Token accessToken;

    public XeroConnectorClient(String xeroApiUrl, String consumerKey, String consumerSecret, String accessKey, String accessSecret) {
        this.xeroApiUrl = xeroApiUrl;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
        this.accessToken = new Token(this.accessKey, this.accessSecret);
    }

	/**
	 * getXeroObjectList retrieves a list of xero objects based on the 'objectType' parameter
	 * 
	 * @param objectType the Xero object type
	 * @return a Xero xml response string
	 * @throws XeroConnectorClientException if an error is returned from the Xero API
	 * @throws XeroConnectorClientUnexpectedException if an unexpected exception occurs
	 */
	public String getXeroObjectList(String objectType) throws XeroConnectorClientException, XeroConnectorClientUnexpectedException
	{
		return callXero(Verb.GET, buildRequestUri(objectType), null, null, null);
	}
	
	/**
	 * getXeroObjectList retrieves a list of xero objects based on the 'objectType' parameter. Method overloaded to allow: 
	 * 	- an optional 'where' clause to be passed for filtering of the response list
	 * 
	 * @param objectType the Xero object type
	 * @param whereClause a 'where' clause passed for filtering of the response list 
	 * @return a Xero xml response string
	 * @throws XeroConnectorClientException if an error is returned from the Xero API
	 * @throws XeroConnectorClientUnexpectedException if an unexpected exception occurs
	 */
	public String getXeroObjectList(String objectType, String whereClause) throws XeroConnectorClientException, XeroConnectorClientUnexpectedException
	{
		Map<String,String> queryStringParameters = new HashMap<String,String>();
		queryStringParameters.put("where", whereClause);
		return callXero(Verb.GET, buildRequestUri(objectType), queryStringParameters, null, null);
	}
	
	/**
	 * getXeroObjectList retrieves a list of xero objects based on the 'objectType' parameter. Method overloaded to allow: 
	 * 	- an optional 'where' clause to be passed for filtering of the response list
	 * 	- an optional 'order' clause for ordering the response list
	 * 
	 * @param objectType the Xero object type
	 * @param whereClause a 'where' clause passed for filtering of the response list 
	 * @param orderClause an 'order' clause for ordering the response list
	 * @return a Xero xml response string
	 * @throws XeroConnectorClientException if an error is returned from the Xero API
	 * @throws XeroConnectorClientUnexpectedException if an unexpected exception occurs
	 */
	public String getXeroObjectList(String objectType, String whereClause, String orderClause) throws XeroConnectorClientException, XeroConnectorClientUnexpectedException
	{
		Map<String,String> queryStringParameters = new HashMap<String,String>();
		queryStringParameters.put("where", whereClause);
		queryStringParameters.put("order", orderClause);
		return callXero(Verb.GET, buildRequestUri(objectType), queryStringParameters, null, null);
	}
	
	/**
	 * getXeroObject method retrieves an individual xero object based on the given 'objectType' parameter
	 * 
	 * @param objectType the Xero object type
	 * @return a Xero xml response string
	 * @throws XeroConnectorClientException if an error is returned from the Xero API
	 * @throws XeroConnectorClientUnexpectedException if an unexpected exception occurs
	 */
	public String getXeroObject(String objectType) throws XeroConnectorClientException, XeroConnectorClientUnexpectedException
	{
		return callXero(Verb.GET, buildRequestUri(objectType), null, null, null);
	}

	/**
	 * getXeroObject method retrieves an individual xero object based on the given 'objectType' parameter. Method overloaded to allow:
	 * 	- an optional 'objectId' field to identify a unique Xero object
	 * 
	 * @param objectType the Xero object type
	 * @param objectId unique Xero object identifier
	 * @return a Xero xml response string
	 * @throws XeroConnectorClientException if an error is returned from the Xero API
	 * @throws XeroConnectorClientUnexpectedException if an unexpected exception occurs
	 */
	public String getXeroObject(String objectType, String objectId) throws XeroConnectorClientException, XeroConnectorClientUnexpectedException
	{
		return callXero(Verb.GET, buildRequestUri(objectType) + "/" + objectId, null, null, null);
	}
	
	/**
	 * updateXeroObject method updates a xero object based on the given 'objectType' and 'payload' parameters. 
	 * 
	 * @param objectType the Xero object type
	 * @param payload xml string request payload
	 * @return a Xero xml response string
	 * @throws XeroConnectorClientException if an error is returned from the Xero API
	 * @throws XeroConnectorClientUnexpectedException if an unexpected exception occurs
	 */
	public String updateXeroObject(XeroObjectTypes.XeroPostTypes objectType,
						 String payload) throws XeroConnectorClientException, XeroConnectorClientUnexpectedException
	{
		Map<String,String> bodyParameters = new HashMap<String,String>();
		bodyParameters.put("xml", payload);
		return callXero(Verb.POST, buildRequestUri(objectType.toString()), null, bodyParameters, null);
	}
	
	
	/**
	 * createXeroObject method creates a xero object based on the given 'objectType' and 'payload' parameters. 
	 * 
	 * @param objectType the Xero object type
	 * @param payload xml string request payload
	 * @return a Xero xml response string
	 * @throws XeroConnectorClientException if an error is returned from the Xero API
	 * @throws XeroConnectorClientUnexpectedException if an unexpected exception occurs
	 */
	public String createXeroObject(XeroObjectTypes.XeroPutTypes objectType,
			 String payload) throws XeroConnectorClientException, XeroConnectorClientUnexpectedException
	{
		Map<String,String> bodyParameters = new HashMap<String,String>();
		bodyParameters.put("xml", payload);
		return callXero(Verb.POST, buildRequestUri(objectType.toString()), null, null, payload);
	}
	
	/**
	 * buildRequestUri builds the full target URI for the Xero request to be directed to
	 * 
	 * @param objectType to be appended to the base Xero API URI
	 * @return full Xero API URI
	 */
	public String buildRequestUri(String objectType)
	{
		String fullApiUri = xeroApiUrl + objectType;	
		
		return fullApiUri;
	}

	public String callXero(Verb verb, String url, Map<String,String> queryStringParameters, Map<String,String> bodyParameters, String payload)
			throws XeroConnectorClientException, XeroConnectorClientUnexpectedException
	{
		String responseString = null;
		
		try {
			OAuthService service = new ServiceBuilder()
			.provider(XeroApi.class)
			.apiKey(this.consumerKey)
			.apiSecret(this.consumerSecret)
			.build();

			OAuthRequest request = new OAuthRequest(verb, url);
			for (Entry<String,String> entry : queryStringParameters.entrySet()) {
				request.addQuerystringParameter(entry.getKey(), entry.getValue());
			}
			for (Entry<String,String> entry : bodyParameters.entrySet()) {
				request.addBodyParameter(entry.getKey(), entry.getValue());
			}
			if (payload != null) {
				request.addPayload(payload.getBytes("UTF-8"));
			}
			service.signRequest(this.accessToken, request);

			Response response = request.send();
			responseString = response.getBody();
		} catch (OAuthException e) {
			throw new XeroConnectorClientException(e);
		} catch (Exception e) {
			throw new XeroConnectorClientUnexpectedException(e);
		}
		
		return responseString;		
	}
}
