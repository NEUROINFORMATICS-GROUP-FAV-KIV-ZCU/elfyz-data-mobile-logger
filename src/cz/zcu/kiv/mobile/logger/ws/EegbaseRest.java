package cz.zcu.kiv.mobile.logger.ws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import cz.zcu.kiv.mobile.logger.Application;
import cz.zcu.kiv.mobile.logger.ws.data.UserInfo;
import cz.zcu.kiv.mobile.logger.ws.data.wrappers.UserInfoWrapper;
import cz.zcu.kiv.mobile.logger.ws.exceptions.CommunicationException;
import cz.zcu.kiv.mobile.logger.ws.exceptions.WrongCredentialsException;


public class EegbaseRest {
  
  public static UserInfo login(String email, String password) throws WrongCredentialsException, CommunicationException {
    UserInfo userinfo = null;
    try {
      HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.setAuthorization(new HttpBasicAuthentication(email, password));
      requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      requestHeaders.setContentType(MediaType.APPLICATION_JSON);
      
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
      
      HttpEntity<Void> entity = new HttpEntity<Void>(requestHeaders);
      
      ResponseEntity<UserInfoWrapper> response = restTemplate.exchange(getURL(), HttpMethod.GET, entity, UserInfoWrapper.class);
      userinfo = response.getBody().getUserInfo();
    }
    catch (HttpClientErrorException e) {
      if(e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        throw new WrongCredentialsException(e);
      }
      else {
        throw new CommunicationException(e);
      }
    }
    catch (RestClientException e) {
      throw new CommunicationException(e);
    }
    return userinfo;
  }

  private static URI getURL() throws CommunicationException {
    String url = Application.getPreferences().getString("eegbase_url", null);
    try {
      return new URI(url);
    }
    catch (URISyntaxException e) {
      throw new CommunicationException("Unparsable EEGbase URL: " + url, e);
    }
  }
}
